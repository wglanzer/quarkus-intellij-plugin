package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.*;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.util.Key;
import com.intellij.util.io.*;
import org.jetbrains.annotations.*;

import java.util.function.Consumer;

/**
 * Implements the RunProfileState to execute a job with given JavaParameters.
 * Calls "pOnReady" if the Debugger is ready to connect
 *
 * @author w.glanzer, 13.06.2019
 */
class QuarkusMavenState extends JavaCommandLineState
{
  private static final String _DEBUGGER_READY_STRING = "Listening for transport dt_socket at address";
  private final QuarkusMavenRunConfig quarkusMavenRunConfig;
  private final boolean attachDebugger;
  private final Consumer<ProcessHandler> onReady;

  QuarkusMavenState(@NotNull QuarkusMavenRunConfig pQuarkusMavenRunConfig, @NotNull ExecutionEnvironment pEnv,
                    boolean pAttachDebugger, @Nullable Consumer<ProcessHandler> pOnReady)
  {
    super(pEnv);
    quarkusMavenRunConfig = pQuarkusMavenRunConfig;
    attachDebugger = pAttachDebugger;
    onReady = pOnReady;
  }

  @Override
  protected JavaParameters createJavaParameters() throws ExecutionException
  {
    return quarkusMavenRunConfig.createJavaParameters(null);
  }

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException
  {
    _ProcessHandler processHandler = startProcess();
    ConsoleView console = createConsole(executor);
    if (console != null)
      console.attachToProcess(processHandler);
    processHandler.addProcessListener(new _StartDebuggerListener(processHandler, pHandler -> {
      if (attachDebugger)
        processHandler.detachProcessSilently();

      // Delegate Handler
      if (onReady != null)
        onReady.accept(pHandler);
    }));
    return new DefaultExecutionResult(console, processHandler);
  }

  @NotNull
  @Override
  protected _ProcessHandler startProcess() throws ExecutionException
  {
    _ProcessHandler result = new _ProcessHandler(createCommandLine());
    result.setShouldDestroyProcessRecursively(true);
    return result;
  }

  /**
   * Listener which fires the "onReady" consumer when the Debugger is able to attach
   */
  private static class _StartDebuggerListener extends ProcessAdapter
  {
    private final ProcessHandler processHandler;
    private final Consumer<ProcessHandler> onReady;

    private _StartDebuggerListener(ProcessHandler pProcessHandler, Consumer<ProcessHandler> pOnReady)
    {
      processHandler = pProcessHandler;
      onReady = pOnReady;
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent pProcessEvent, @NotNull Key pKey)
    {
      String text = pProcessEvent.getText();
      if (text != null && text.startsWith(_DEBUGGER_READY_STRING))
      {
        processHandler.removeProcessListener(this);
        if (onReady != null)
          onReady.accept(processHandler);
      }
    }
  }

  /**
   * Simple ProcessHandler copied from original MavenRunConfiguration
   *
   * @see org.jetbrains.idea.maven.execution.MavenRunConfiguration
   */
  private static class _ProcessHandler extends ColoredProcessHandler
  {
    private _ProcessHandler(@NotNull GeneralCommandLine pCmdLine) throws ExecutionException
    {
      super(pCmdLine);
    }

    @Override
    public void destroyProcess()
    {
      // Skip destroyProcess checks, because this handler is ... not really dead (if detachProcessSilently called)
      executeTask(this::destroyProcessImpl);
    }

    /**
     * This method just calls the listeners so that all objects are thinking this process handler has detatched.
     * This will update the GUI and closes the ToolWindow-Content.
     * Do not really detach - we have to destroy the process later on, if the remote state has been killed!
     */
    protected void detachProcessSilently()
    {
      executeTask(this::notifyProcessDetached);
    }

    @NotNull
    @Override
    protected BaseOutputReader.Options readerOptions()
    {
      return new BaseOutputReader.Options()
      {
        @Override
        public BaseDataReader.SleepingPolicy policy()
        {
          return BaseDataReader.SleepingPolicy.BLOCKING;
        }

        @Override
        public boolean splitToLines()
        {
          return true;
        }

        @Override
        public boolean sendIncompleteLines()
        {
          return false;
        }
      };
    }
  }
}
