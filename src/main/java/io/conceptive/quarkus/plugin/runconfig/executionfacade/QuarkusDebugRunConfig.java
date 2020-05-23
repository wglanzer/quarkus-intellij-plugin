package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.remote.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusMavenRunConfigType;
import org.jdom.Element;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

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
    return QuarkusMavenRunConfigType.ICON;
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

    // prepare environment
    _preventSettingsFromBeingSavedToDisk(env);

    // those settings have to be set, so that the runner is able to run correctly
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

  /**
   * If something gets set in the execution environments GenericDebuggerRunnerSettings, it will persisted to disk.
   * This is normally okay, because the user did those changes - here it is definitely not okay, because the user has changed nothing!
   * Especially the behaviour for "Random Ports" is ... bad, if it gets changed every run
   *
   * @param pEnvironment Environment to be prevented
   */
  private void _preventSettingsFromBeingSavedToDisk(@NotNull ExecutionEnvironment pEnvironment)
  {
    try
    {
      Field field = ExecutionEnvironment.class.getDeclaredField("myRunnerSettings");
      field.setAccessible(true);
      field.set(pEnvironment, new GenericDebuggerRunnerSettings()
      {
        @Override
        public void readExternal(Element element)
        {
          // nothing, do not change here
        }

        @Override
        public void writeExternal(Element element)
        {
          // nothing, to prevent writing
        }
      });
    }
    catch (Exception e)
    {
      throw new RuntimeException("_preventSettingsFromBeingSavedToDisk does not work anymore", e);
    }
  }

}
