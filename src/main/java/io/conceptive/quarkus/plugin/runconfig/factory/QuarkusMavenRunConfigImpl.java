package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.IRunConfigExecutionFacade;
import io.conceptive.quarkus.plugin.runconfig.options.QuarkusMavenRunConfigurationOptions;
import io.conceptive.quarkus.plugin.util.NetUtility;
import org.jetbrains.annotations.*;

/**
 * Provides implementation for intellijs RunConfigurationBase and Quarkus running with maven.
 *
 * @author w.glanzer, 12.06.2019
 */
class QuarkusMavenRunConfigImpl extends RunConfigurationBase<JavaRunConfigurationModule>
{

  @Inject
  private IRunConfigExecutionFacade executionFacade;

  @Inject
  QuarkusMavenRunConfigImpl(@Assisted @NotNull Project pProject, @Assisted @NotNull ConfigurationFactory pConfigurationFactory)
  {
    super(pProject, pConfigurationFactory, null);
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
  {
    SettingsEditorGroup<QuarkusMavenRunConfigImpl> group = new SettingsEditorGroup<>();
    group.addEditor("Parameters", new QuarkusMavenParametersSettingsEditorImpl(getProject()));
    return group;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    return (pExec, pRunner) -> {
      ApplicationManager.getApplication().invokeLater(() -> {
        RunnerAndConfigurationSettings settings = pExecutionEnvironment.getRunnerAndConfigurationSettings();
        if (settings != null) // how can this be null?
        {
          if (pExec instanceof DefaultDebugExecutor)
            executionFacade.executeNestedMavenRunConfig(settings, this, getOptions(), NetUtility.findAvailableSocketPortUnchecked());
          else
            executionFacade.executeNestedMavenRunConfig(settings, this, getOptions());
        }
      });

      return null;
    };
  }

  @NotNull
  @Override
  public QuarkusMavenRunConfigurationOptions getOptions()
  {
    return (QuarkusMavenRunConfigurationOptions) super.getOptions();
  }

}
