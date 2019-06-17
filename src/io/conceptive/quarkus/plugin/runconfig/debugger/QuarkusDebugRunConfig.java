package io.conceptive.quarkus.plugin.runconfig.debugger;

import com.intellij.debugger.engine.RemoteStateState;
import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.execution.*;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.remote.*;
import com.intellij.execution.runners.*;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.util.ForwardProcessListener;
import org.jetbrains.annotations.*;

/**
 * Part II: Connect debugger to started Quarkus instance
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusDebugRunConfig extends RemoteConfiguration
{

  private ProcessHandler mavenProcessHandler;

  public QuarkusDebugRunConfig(@NotNull Project project)
  {
    super(project, new RemoteConfigurationType());
    HOST = "localhost";
    USE_SOCKET_TRANSPORT = true;
    SERVER_MODE = false;
  }

  /**
   * Reinitializes this RunConfig with new settings
   *
   * @param pMavenProcessHandler ProcessHandler for the Maven Process
   * @param pPort                Debug-Port
   */
  public void reinit(@Nullable ProcessHandler pMavenProcessHandler, int pPort)
  {
    mavenProcessHandler = pMavenProcessHandler;
    PORT = String.valueOf(pPort);
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
  {
    GenericDebuggerRunnerSettings debuggerSettings = (GenericDebuggerRunnerSettings) env.getRunnerSettings();
    if (debuggerSettings != null)
    {
      debuggerSettings.LOCAL = false;
      debuggerSettings.setDebugPort(USE_SOCKET_TRANSPORT ? PORT : SHMEM_ADDRESS);
      debuggerSettings.setTransport(USE_SOCKET_TRANSPORT ? 0 : 1);
    }

    return new RemoteStateState(getProject(), createRemoteConnection(), AUTO_RESTART)
    {
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
    };
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
