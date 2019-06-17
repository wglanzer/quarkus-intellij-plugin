package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.*;
import com.intellij.execution.impl.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import io.conceptive.quarkus.plugin.runconfig.debugger.QuarkusDebugRunConfig;
import io.conceptive.quarkus.plugin.runconfig.maven.QuarkusMavenRunConfig;
import io.conceptive.quarkus.plugin.runconfig.settings.*;
import io.conceptive.quarkus.plugin.util.NetUtility;
import org.jdom.Element;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;

import java.util.function.Consumer;

/**
 * RunConfig for Quarkus-Instances, started by maven
 *
 * Part I: Start quarkus maven goal
 * Part II: Attach remote debugger
 *
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusRunConfig extends LocatableConfigurationBase<JavaRunConfigurationModule> implements WithoutOwnBeforeRunSteps
{

  private QuarkusDebugRunConfig debugRunConfig;
  private RunnerAndConfigurationSettings debugRunConfigSettings;
  private QuarkusMavenRunConfig mavenRunConfig;
  private RunnerAndConfigurationSettings mavenRunConfigSettings;
  private QuarkusSettings mySettings;

  QuarkusRunConfig(@NotNull Project pProject, @NotNull ConfigurationFactory factory)
  {
    super(pProject, factory);
    mySettings = new QuarkusSettings(pProject);
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
  {
    return new QuarkusSettingsEditor(getProject());
  }

  @Override
  public void readExternal(@NotNull Element element) throws InvalidDataException
  {
    super.readExternal(element);

    Element mavenSettingsElement = element.getChild(QuarkusSettings.TAG);
    if (mavenSettingsElement != null)
    {
      mySettings = XmlSerializer.deserialize(mavenSettingsElement, QuarkusSettings.class);
      if (mySettings.getMavenRunnerParameters() == null)
        mySettings.setMavenRunnerParameters(new MavenRunnerParameters());
    }
  }

  @Override
  public void writeExternal(@NotNull Element element) throws WriteExternalException
  {
    super.writeExternal(element);
    element.addContent(XmlSerializer.serialize(mySettings));
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

  @Override
  public RunConfiguration clone()
  {
    QuarkusRunConfig clone = (QuarkusRunConfig) super.clone();
    clone.mySettings = mySettings.clone();
    return clone;
  }

  public QuarkusSettings getMySettings()
  {
    return mySettings;
  }

  /**
   * Cares about the first part of this run config: Start the maven goal for quarkus and start attaching debugger if necessary
   *
   * @param pRunManager RunManager to run RunConfigs in it
   * @param pPort       Port to be used for debugging. NULL if no debugger should be attached
   */
  private synchronized void _startMavenConfiguration(@NotNull RunManagerImpl pRunManager, @Nullable Integer pPort)
  {
    Consumer<ProcessHandler> onReady = null;
    if (pPort != null)
      onReady = (pMavenHandle) -> ApplicationManager.getApplication()
          .invokeLater(() -> _startDebugConfiguration(pRunManager, pMavenHandle, pPort));

    if(mavenRunConfig == null)
    {
      mavenRunConfig = new QuarkusMavenRunConfig(getProject());
      mavenRunConfig.setName(pPort != null ? "Maven - " + getName() : getName());
    }

    mavenRunConfig.reinit(mySettings, pPort, onReady);

    // Execute
    if(mavenRunConfigSettings == null)
      mavenRunConfigSettings = new RunnerAndConfigurationSettingsImpl(pRunManager, mavenRunConfig);

    ExecutionUtil.runConfiguration(mavenRunConfigSettings, DefaultRunExecutor.getRunExecutorInstance());
  }

  /**
   * Attaches the debugger to the running quarkus instance
   *
   * @param pRunManager  RunManager to run RunConfigs in it
   * @param pMavenHandle ProcessHandle for the running maven instance
   * @param pPort        Debugger Port
   */
  private synchronized void _startDebugConfiguration(@NotNull RunManagerImpl pRunManager, @NotNull ProcessHandler pMavenHandle, int pPort)
  {
    if(debugRunConfig == null)
    {
      debugRunConfig = new QuarkusDebugRunConfig(getProject());
      debugRunConfig.setName(getName());
    }

    debugRunConfig.reinit(pMavenHandle, pPort);

    // Execute
    if(debugRunConfigSettings == null)
      debugRunConfigSettings = new RunnerAndConfigurationSettingsImpl(pRunManager, debugRunConfig);

    ExecutionUtil.runConfiguration(debugRunConfigSettings, DefaultDebugExecutor.getDebugExecutorInstance());
  }

}
