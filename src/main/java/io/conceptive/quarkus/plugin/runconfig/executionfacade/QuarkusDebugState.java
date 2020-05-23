package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.engine.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.conceptive.quarkus.plugin.util.ForwardProcessListener;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author w.glanzer, 21.04.2020
 */
class QuarkusDebugState extends RemoteStateState
{
  private final Project project;
  private final ProcessHandler buildProcessHandler;
  private final Supplier<List<Map.Entry<String, Key<?>>>> previousMessageSupplier;
  private final List<Runnable> invalidationRunnables = new ArrayList<>();

  public QuarkusDebugState(@NotNull Project pProject, @NotNull RemoteConnection pConnection, boolean pRestart,
                           @NotNull ProcessHandler pBuildProcessHandler, @Nullable Supplier<List<Map.Entry<String, Key<?>>>> pPreviousMessageSupplier)
  {
    super(pProject, pConnection, pRestart);
    project = pProject;
    buildProcessHandler = pBuildProcessHandler;
    previousMessageSupplier = pPreviousMessageSupplier;
  }

  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException
  {
    ExecutionResult execute = super.execute(executor, runner);
    if (execute == null)
      return null;
    ProcessHandler debugHandler = execute.getProcessHandler();
    if (debugHandler != null && buildProcessHandler != null)
      debugHandler.addProcessListener(new _ReadinessProcessListener(project, debugHandler, () -> _onProcessHandlerCreated(buildProcessHandler, debugHandler), invalidationRunnables));

    return execute;
  }

  /**
   * Gets called, when the DebuggerProcessHandler was created and ready
   *
   * @param pBuildProcessHandle  ProcessHandler for Build Process
   * @param pDebugProcessHandler ProcessHandler for Debug Process
   */
  private void _onProcessHandlerCreated(@NotNull ProcessHandler pBuildProcessHandle, @NotNull ProcessHandler pDebugProcessHandler)
  {
    if (previousMessageSupplier != null)
    {
      List<Map.Entry<String, Key<?>>> messages = previousMessageSupplier.get();
      if (messages != null)
        for (Map.Entry<String, Key<?>> msg : messages)
          pDebugProcessHandler.notifyTextAvailable(msg.getKey(), msg.getValue());
    }

    ForwardProcessListener buildProcessListener = new ForwardProcessListener(pDebugProcessHandler, true);
    pBuildProcessHandle.addProcessListener(buildProcessListener);
    invalidationRunnables.add(() -> pBuildProcessHandle.removeProcessListener(buildProcessListener));

    ForwardProcessListener debugProcessListener = new ForwardProcessListener(pBuildProcessHandle, false);
    pDebugProcessHandler.addProcessListener(debugProcessListener);
    invalidationRunnables.add(() -> pDebugProcessHandler.removeProcessListener(debugProcessListener));
  }

  /**
   * Listener to hear on readiness of the debug config.
   * It first waits for the startNotified() command from the ProcessListener, then for processAttached() on the debugProcess
   */
  private static class _ReadinessProcessListener extends ProcessAdapter implements DebugProcessListener
  {
    private final Project project;
    private final ProcessHandler debugHandler;
    private final Runnable doOnReady;
    private final List<Runnable> doOnUnready;

    public _ReadinessProcessListener(@NotNull Project pProject, @NotNull ProcessHandler pDebugHandler, @NotNull Runnable pDoOnReady, @NotNull List<Runnable> pDoOnUnready)
    {
      project = pProject;
      debugHandler = pDebugHandler;
      doOnReady = pDoOnReady;
      doOnUnready = pDoOnUnready;

      if (debugHandler.isStartNotified())
        startNotified(new ProcessEvent(debugHandler));
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event)
    {
      DebugProcess process = DebuggerManager.getInstance(project).getDebugProcess(debugHandler);
      process.addDebugProcessListener(this);
      if (process.isAttached())
        processAttached(process);
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event)
    {
      DebugProcess process = DebuggerManager.getInstance(project).getDebugProcess(debugHandler);
      if (process != null)
        processDetached(process, false);
    }

    @Override
    public void processAttached(@NotNull DebugProcess process)
    {
      process.removeDebugProcessListener(this);
      ApplicationManager.getApplication().executeOnPooledThread(doOnReady);
    }

    @Override
    public void processDetached(@NotNull DebugProcess process, boolean closedByUser)
    {
      process.removeDebugProcessListener(this);
      doOnUnready.forEach(Runnable::run);
    }
  }

}
