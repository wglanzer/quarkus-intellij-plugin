package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.*;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.*;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 23.05.2020
 */
public abstract class AbstractExecutionFacadeImpl implements IRunConfigExecutionFacade
{

  // the runconfigs have to be saved here, so that "single instance" will kill those configs too
  private IInternalRunConfigs.IBuildRunConfig buildRunConfig;
  private IInternalRunConfigs.IDebugRunConfig debugRunConfig;

  @Override
  public synchronized void executeNestedRunConfigs(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions)
  {
    if (buildRunConfig == null)
      buildRunConfig = createBuildConfig(pSource.getProject());
    buildRunConfig.setName(pSource.getName());
    buildRunConfig.reinit(null, pOptions, null, () -> ExecutionUtil.runConfiguration(pSettings, DefaultRunExecutor.getRunExecutorInstance()));
    execute(pSettings, pSource, buildRunConfig, DefaultRunExecutor.getRunExecutorInstance());
  }

  @Override
  public synchronized void executeNestedRunConfigs(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions,
                                                   @NotNull Integer pDebugPort)
  {
    if (buildRunConfig == null)
      buildRunConfig = createBuildConfig(pSource.getProject());
    buildRunConfig.setName(pSource.getName());
    buildRunConfig.reinit(pDebugPort, pOptions, (pProcessHandle) -> {
      if (debugRunConfig == null)
        debugRunConfig = createDebugConfig(pSource.getProject());
      debugRunConfig.enableMessageCache(pProcessHandle);
      debugRunConfig.setName(pSource.getName());

      ApplicationManager.getApplication().invokeLater(() -> {
        debugRunConfig.reinit(pProcessHandle, pDebugPort, () -> ExecutionUtil.runConfiguration(pSettings, DefaultDebugExecutor.getDebugExecutorInstance()));
        execute(pSettings, pSource, debugRunConfig, DefaultDebugExecutor.getDebugExecutorInstance());
      });
    }, () -> ExecutionUtil.runConfiguration(pSettings, DefaultDebugExecutor.getDebugExecutorInstance()));
    execute(pSettings, pSource, buildRunConfig, DefaultRunExecutor.getRunExecutorInstance());
  }

  @NotNull
  protected abstract IInternalRunConfigs.IBuildRunConfig createBuildConfig(@NotNull Project pProject);

  @NotNull
  protected IInternalRunConfigs.IDebugRunConfig createDebugConfig(@NotNull Project pProject)
  {
    return new QuarkusDebugRunConfig(pProject);
  }

  /**
   * Executes the given runconfig in a project
   */
  private static void execute(@NotNull RunnerAndConfigurationSettings pSourceSettings, @NotNull RunConfiguration pSource, @NotNull RunConfiguration pConfig, Executor pExecutor)
  {
    boolean parallel = pSource.isAllowRunningInParallel();
    boolean parallelConfig = pConfig.isAllowRunningInParallel();

    try
    {
      if (pSourceSettings instanceof RunnerAndConfigurationSettingsImpl)
        ((RunnerAndConfigurationSettingsImpl) pSourceSettings).setConfiguration(pConfig);
      pSource.setAllowRunningInParallel(true);
      pConfig.setAllowRunningInParallel(true);
      ExecutionUtil.runConfiguration(pSourceSettings, pExecutor);
    }
    finally
    {
      if (pSourceSettings instanceof RunnerAndConfigurationSettingsImpl)
        ((RunnerAndConfigurationSettingsImpl) pSourceSettings).setConfiguration(pSource);
      pSource.setAllowRunningInParallel(parallel);
      pConfig.setAllowRunningInParallel(parallelConfig);
    }
  }

}
