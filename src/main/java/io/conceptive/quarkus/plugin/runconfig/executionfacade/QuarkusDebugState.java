package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.debugger.engine.RemoteStateState;
import com.intellij.execution.*;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.util.ForwardProcessListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.04.2020
 */
class QuarkusDebugState extends RemoteStateState
{
  private final ProcessHandler mavenProcessHandler;

  public QuarkusDebugState(@NotNull Project pProject, @NotNull RemoteConnection pConnection, boolean pRestart, @NotNull ProcessHandler pMavenProcessHandler)
  {
    super(pProject, pConnection, pRestart);
    mavenProcessHandler = pMavenProcessHandler;
  }

  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException
  {
    ExecutionResult execute = super.execute(executor, runner);
    if (execute == null)
      return null;
    ProcessHandler handler = execute.getProcessHandler();
    if (handler != null && mavenProcessHandler != null)
      _onProcessHandlerCreated(mavenProcessHandler, handler);
    return execute;
  }

  /**
   * Gets called, when the DebuggerProcessHandler was created and ready
   *
   * @param pMavenProcessHandler ProcessHandler for Maven Process
   * @param pDebugProcessHandler ProcessHandler for Debug Process
   */
  private void _onProcessHandlerCreated(@NotNull ProcessHandler pMavenProcessHandler, @NotNull ProcessHandler pDebugProcessHandler)
  {
    pMavenProcessHandler.addProcessListener(new ForwardProcessListener(pDebugProcessHandler, true));
    pDebugProcessHandler.addProcessListener(new ForwardProcessListener(pMavenProcessHandler, false));
  }

}
