package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.*;
import com.intellij.execution.impl.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.debugger.QuarkusDebugRunConfig;
import io.conceptive.quarkus.plugin.runconfig.maven.QuarkusMavenRunConfig;
import io.conceptive.quarkus.plugin.util.NetUtility;
import org.jetbrains.annotations.*;

import java.util.function.Consumer;

/**
 * RunConfig for Quarkus-Instances, started by maven
 *
 * Part I: Start quarkus maven goal
 * Part II: Attach remote debugger
 *
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusRunConfig extends LocatableConfigurationBase<JavaRunConfigurationModule>
{

  QuarkusRunConfig(@NotNull Project project, @NotNull ConfigurationFactory factory)
  {
    super(project, factory);
  }

  @NotNull
  @Override
  public SettingsEditor<QuarkusRunConfig> getConfigurationEditor()
  {
    return new QuarkusSettingsEditor();
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    return (pExec, pRunner) -> {
      RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(getProject());
      Integer port = pExecutor instanceof DefaultDebugExecutor ? NetUtility._findAvailableSocketPortUnchecked() : null;
      ApplicationManager.getApplication().invokeLater(() -> _startMavenConfiguration(runManager, port));
      return null;
    };
  }

  /**
   * Cares about the first part of this run config: Start the maven goal for quarkus and start attaching debugger if necessary
   *
   * @param pRunManager RunManager to run RunConfigs in it
   * @param pPort       Port to be used for debugging. NULL if no debugger should be attached
   */
  private void _startMavenConfiguration(@NotNull RunManagerImpl pRunManager, @Nullable Integer pPort)
  {
    Consumer<ProcessHandler> onReady = null;
    if (pPort != null)
      onReady = (pMavenHandle) -> ApplicationManager.getApplication()
          .invokeLater(() -> _startDebugConfiguration(pRunManager, pMavenHandle, pPort));

    QuarkusMavenRunConfig runConfig = new QuarkusMavenRunConfig(getProject(), pPort, onReady);

    // Define new displayname
    runConfig.setName("Maven - " + getName());

    // Execute
    ExecutionUtil.runConfiguration(new RunnerAndConfigurationSettingsImpl(pRunManager, runConfig), DefaultRunExecutor.getRunExecutorInstance());
  }

  /**
   * Attaches the debugger to the running quarkus instance
   *
   * @param pRunManager  RunManager to run RunConfigs in it
   * @param pMavenHandle ProcessHandle for the running maven instance
   * @param pPort        Debugger Port
   */
  private void _startDebugConfiguration(@NotNull RunManagerImpl pRunManager, @NotNull ProcessHandler pMavenHandle, int pPort)
  {
    QuarkusDebugRunConfig runConfig = new QuarkusDebugRunConfig(getProject(), pMavenHandle, pPort);

    // Define new displayname
    runConfig.setName("Debug - " + getName());

    // Execute
    ExecutionUtil.runConfiguration(new RunnerAndConfigurationSettingsImpl(pRunManager, runConfig), DefaultDebugExecutor.getDebugExecutorInstance());
  }

}
