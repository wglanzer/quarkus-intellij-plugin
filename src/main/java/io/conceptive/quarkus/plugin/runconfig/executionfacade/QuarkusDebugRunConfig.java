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
import java.lang.ref.WeakReference;

/**
 * Part II: Connect debugger to started Quarkus instance
 *
 * @author w.glanzer, 13.06.2019
 */
class QuarkusDebugRunConfig extends RemoteConfiguration
{

  private ProcessHandler mavenProcessHandler;
  private Runnable onRestart;
  private WeakReference<RunProfileState> stateRef = null;

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

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
  {
    // Rerun is not supported - it has to be initialized again
    if (stateRef != null && stateRef.get() != null)
    {
      if (onRestart != null)
        onRestart.run();
      return null;
    }

    GenericDebuggerRunnerSettings debuggerSettings = (GenericDebuggerRunnerSettings) env.getRunnerSettings();
    if (debuggerSettings != null)
    {
      debuggerSettings.LOCAL = false;
      debuggerSettings.setDebugPort(USE_SOCKET_TRANSPORT ? PORT : SHMEM_ADDRESS);
      debuggerSettings.setTransport(USE_SOCKET_TRANSPORT ? 0 : 1);
    }

    QuarkusDebugState state = new QuarkusDebugState(getProject(), createRemoteConnection(), AUTO_RESTART, mavenProcessHandler);
    stateRef = new WeakReference<>(state);
    return state;
  }

  /**
   * Reinitializes this RunConfig with new settings
   *
   * @param pMavenProcessHandler ProcessHandler for the Maven Process
   * @param pPort                Debug-Port
   * @param pOnRestart           Runnable that gets called, if this runconfig gets restartet
   */
  public void reinit(@Nullable ProcessHandler pMavenProcessHandler, int pPort, @Nullable Runnable pOnRestart)
  {
    mavenProcessHandler = pMavenProcessHandler;
    onRestart = pOnRestart;
    PORT = String.valueOf(pPort);
    stateRef = null;
  }

}
