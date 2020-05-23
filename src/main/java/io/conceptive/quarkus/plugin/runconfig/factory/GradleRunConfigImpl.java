package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.IRunConfigExecutionFacade;
import io.conceptive.quarkus.plugin.runconfig.options.GradleRunConfigurationOptions;
import io.conceptive.quarkus.plugin.util.NetUtility;
import org.jetbrains.annotations.*;

/**
 * Provides implementation for intellijs RunConfigurationBase and Quarkus running with gradle.
 *
 * @author w.glanzer, 23.05.2020
 */
class GradleRunConfigImpl extends RunConfigurationBase<RunProfileState>
{

  @Inject
  @Named("gradle")
  private IRunConfigExecutionFacade executionFacade;

  @Inject
  GradleRunConfigImpl(@Assisted @NotNull Project pProject, @Assisted @NotNull ConfigurationFactory pConfigurationFactory)
  {
    super(pProject, pConfigurationFactory, null);
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
  {
    SettingsEditorGroup<GradleRunConfigImpl> group = new SettingsEditorGroup<>();
    group.addEditor("Parameters", new GradleParametersSettingsEditorImpl(getProject()));
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
            executionFacade.executeNestedRunConfigs(settings, this, getOptions(), NetUtility.findAvailableSocketPortUnchecked());
          else
            executionFacade.executeNestedRunConfigs(settings, this, getOptions());
        }
      });

      return null;
    };
  }

  @NotNull
  @Override
  public GradleRunConfigurationOptions getOptions()
  {
    return (GradleRunConfigurationOptions) super.getOptions();
  }

}
