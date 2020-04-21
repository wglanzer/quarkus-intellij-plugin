package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.remote.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusRunConfigType;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * Part II: Connect debugger to started Quarkus instance
 *
 * @author w.glanzer, 13.06.2019
 */
class QuarkusDebugRunConfig extends RemoteConfiguration
{

  private ProcessHandler mavenProcessHandler;

  public QuarkusDebugRunConfig(@NotNull Project project)
  {
    super(project, new RemoteConfigurationType());
    HOST = "localhost";
    USE_SOCKET_TRANSPORT = true;
    SERVER_MODE = false;
  }

  @Nullable
  @Override
  public Icon getIcon()
  {
    return QuarkusRunConfigType.ICON;
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

    return new QuarkusDebugState(getProject(), createRemoteConnection(), AUTO_RESTART, mavenProcessHandler);
  }

}
