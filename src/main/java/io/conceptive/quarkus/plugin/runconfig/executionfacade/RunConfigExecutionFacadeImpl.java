package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.application.ApplicationManager;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import io.conceptive.quarkus.plugin.util.ExecutionUtility;
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
  public synchronized void executeNestedMavenRunConfig(@NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions)
  {
    if (mavenRunConfig == null)
      mavenRunConfig = new QuarkusMavenRunConfig(pSource.getProject());
    mavenRunConfig.setName(pSource.getName());
    mavenRunConfig.reinit(null, null);
    ExecutionUtility.execute(pSource.getProject(), mavenRunConfig, false);
  }

  @Override
  public synchronized void executeNestedMavenRunConfig(@NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions, @NotNull Integer pDebugPort)
  {
    if (mavenRunConfig == null)
      mavenRunConfig = new QuarkusMavenRunConfig(pSource.getProject());
    mavenRunConfig = new QuarkusMavenRunConfig(pSource.getProject());
    mavenRunConfig.setName(pSource.getName());
    mavenRunConfig.reinit(pDebugPort, (pMavenHandle) -> ApplicationManager.getApplication().invokeLater(() -> {
      if (debugRunConfig == null)
        debugRunConfig = new QuarkusDebugRunConfig(pSource.getProject());
      debugRunConfig.setName(pSource.getName());
      debugRunConfig.reinit(pMavenHandle, pDebugPort);
      ExecutionUtility.execute(pSource.getProject(), debugRunConfig, true);
    }));
    ExecutionUtility.execute(pSource.getProject(), mavenRunConfig, true);
  }

}
