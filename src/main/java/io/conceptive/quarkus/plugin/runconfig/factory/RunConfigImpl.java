package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.IRunConfigExecutionFacade;
import io.conceptive.quarkus.plugin.runconfig.options.QuarkusRunConfigurationOptions;
import io.conceptive.quarkus.plugin.util.NetUtility;
import org.jetbrains.annotations.*;

/**
 * Provides implementation for intellijs RunConfigurationBase.
 *
 * @author w.glanzer, 12.06.2019
 */
class RunConfigImpl extends RunConfigurationBase<JavaRunConfigurationModule> implements WithoutOwnBeforeRunSteps
{

  @Inject
  private IRunConfigExecutionFacade executionFacade;

  @Inject
  RunConfigImpl(@Assisted @NotNull Project pProject, @Assisted @NotNull ConfigurationFactory pConfigurationFactory)
  {
    super(pProject, pConfigurationFactory, null);
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
  {
    SettingsEditorGroup<RunConfigImpl> group = new SettingsEditorGroup<>();
    group.addEditor("Parameters", new ParametersSettingsEditorImpl(getProject()));
    return group;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    return (pExec, pRunner) -> {
      ApplicationManager.getApplication().invokeLater(() -> {
        if (pExec instanceof DefaultDebugExecutor)
          executionFacade.executeNestedMavenRunConfig(this, getOptions(), NetUtility.findAvailableSocketPortUnchecked());
        else
          executionFacade.executeNestedMavenRunConfig(this, getOptions());
      });

      return null;
    };
  }

  @NotNull
  @Override
  public QuarkusRunConfigurationOptions getOptions()
  {
    return (QuarkusRunConfigurationOptions) super.getOptions();
  }

}
