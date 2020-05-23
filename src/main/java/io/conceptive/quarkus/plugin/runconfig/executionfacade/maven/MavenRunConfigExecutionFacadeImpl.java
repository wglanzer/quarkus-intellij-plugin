package io.conceptive.quarkus.plugin.runconfig.executionfacade.maven;

import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.04.2020
 */
public class MavenRunConfigExecutionFacadeImpl extends AbstractExecutionFacadeImpl
{
  @NotNull
  @Override
  protected IInternalRunConfigs.IBuildRunConfig createBuildConfig(@NotNull Project pProject)
  {
    return new QuarkusMavenRunConfig(pProject);
  }
}
