package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.*;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.*;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.application.ApplicationManager;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.04.2020
 */
public class RunConfigExecutionFacadeImpl implements IRunConfigExecutionFacade
{

  // the runconfigs have to be saved here, so that "single instance" will kill those configs too
  private QuarkusMavenRunConfig mavenRunConfig;
  private QuarkusDebugRunConfig debugRunConfig;

  @Override
  public synchronized void executeNestedMavenRunConfig(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions)
  {
    if (mavenRunConfig == null)
      mavenRunConfig = new QuarkusMavenRunConfig(pSource.getProject());
    mavenRunConfig.setName(pSource.getName());
    mavenRunConfig.reinit(null, pOptions, null, () -> ExecutionUtil.runConfiguration(pSettings, DefaultRunExecutor.getRunExecutorInstance()));
    execute(pSettings, pSource, mavenRunConfig, DefaultRunExecutor.getRunExecutorInstance());
  }

  @Override
  public synchronized void executeNestedMavenRunConfig(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions,
                                                       @NotNull Integer pDebugPort)
  {
    if (mavenRunConfig == null)
      mavenRunConfig = new QuarkusMavenRunConfig(pSource.getProject());
    mavenRunConfig.setName(pSource.getName());
    mavenRunConfig.reinit(pDebugPort, pOptions, (pMavenHandle) -> ApplicationManager.getApplication().invokeLater(() -> {
      if (debugRunConfig == null)
        debugRunConfig = new QuarkusDebugRunConfig(pSource.getProject());
      debugRunConfig.setName(pSource.getName());
      debugRunConfig.reinit(pMavenHandle, pDebugPort, () -> ExecutionUtil.runConfiguration(pSettings, DefaultDebugExecutor.getDebugExecutorInstance()));
      execute(pSettings, pSource, debugRunConfig, DefaultDebugExecutor.getDebugExecutorInstance());
    }), () -> ExecutionUtil.runConfiguration(pSettings, DefaultDebugExecutor.getDebugExecutorInstance()));
    execute(pSettings, pSource, mavenRunConfig, DefaultDebugExecutor.getDebugExecutorInstance());
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
