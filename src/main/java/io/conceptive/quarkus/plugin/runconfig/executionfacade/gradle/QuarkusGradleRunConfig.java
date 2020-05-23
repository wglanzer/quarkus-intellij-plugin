package io.conceptive.quarkus.plugin.runconfig.executionfacade.gradle;

import com.google.common.base.Strings;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import io.conceptive.quarkus.plugin.runconfig.IQuarkusRunConfigType;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.IInternalRunConfigs;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jdom.Element;
import org.jetbrains.annotations.*;
import org.jetbrains.plugins.gradle.service.execution.*;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * Part I: Start Quarkus instance via Gradle task
 *
 * @author w.glanzer, 23.05.2020
 */
class QuarkusGradleRunConfig extends GradleRunConfiguration implements IInternalRunConfigs.IBuildRunConfig
{

  private int port;
  private Consumer<ProcessHandler> onRdy;
  private boolean attachDebugger;
  private IQuarkusRunConfigurationOptions options;
  private WeakReference<RunProfileState> stateRef = null;
  private Runnable onRestart;

  public QuarkusGradleRunConfig(@NotNull Project project)
  {
    super(project, GradleExternalTaskConfigurationType.getInstance().getConfigurationFactories()[0], "");
  }

  @Nullable
  @Override
  public Icon getIcon()
  {
    return IQuarkusRunConfigType.ICON;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    // Rerun is not supported - it has to be initialized again
    if (stateRef != null && stateRef.get() != null)
    {
      if (onRestart != null)
        onRestart.run();
      return null;
    }

    _updateSettings();
    QuarkusGradleState state = new QuarkusGradleState(getSettings(), getProject(), this, pExecutionEnvironment, attachDebugger, onRdy);
    copyUserDataTo(state);
    stateRef = new WeakReference<>(state);
    return state;
  }

  @Override
  public void readExternal(@NotNull Element element) throws InvalidDataException
  {
    _updateSettings();
  }

  @Override
  public void writeExternal(@NotNull Element element) throws WriteExternalException
  {
    // we do not persist any settings
  }

  @Override
  public void reinit(@Nullable Integer pPort, @NotNull IQuarkusRunConfigurationOptions pOptions, @Nullable Consumer<ProcessHandler> pOnRdy, @Nullable Runnable pOnRestart)
  {
    attachDebugger = pOnRdy != null && pPort != null;
    port = attachDebugger ? pPort : -1;
    options = pOptions;
    onRdy = pOnRdy;
    onRestart = pOnRestart;
    stateRef = null;
  }

  /**
   * Creates new settings and updates it with given quarkus settings
   */
  private void _updateSettings()
  {
    try
    {
      // Generate
      ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
      settings.setExternalSystemIdString(GradleConstants.SYSTEM_ID.getId());
      settings.setVmOptions(options.getVmOptions());
      settings.setScriptParameters(_getArguments());
      String workingDir = options.getWorkingDir();
      if (workingDir != null)
        settings.setExternalProjectPath(workingDir);
      Map<String, String> env = options.getEnvVariables();
      if (env != null)
        settings.setEnv(env);
      settings.setPassParentEnvs(options.getPassParentEnvParameters());
      settings.setTaskNames(_getTasks());

      // Set
      Field mySettings = ExternalSystemRunConfiguration.class.getDeclaredField("mySettings");
      mySettings.setAccessible(true);
      mySettings.set(this, settings);
    }
    catch (Exception e)
    {
      throw new InvalidDataException(e);
    }
  }

  @NotNull
  private String _getArguments()
  {
    String base = Strings.nullToEmpty(options.getArguments());
    if (attachDebugger)
      return "-Ddebug=" + port + " " + base;
    return base;
  }

  /**
   * @return tasks to be executed
   */
  @NotNull
  private List<String> _getTasks()
  {
    List<String> tasks = new ArrayList<>();
    if (options.getCompileBeforeLaunch())
    {
      tasks.add("clean");
      tasks.add("build");
    }
    tasks.add("quarkusDev");
    return tasks;
  }

}
