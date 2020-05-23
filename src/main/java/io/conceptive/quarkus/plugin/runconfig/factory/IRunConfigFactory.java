package io.conceptive.quarkus.plugin.runconfig.factory;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.04.2020
 */
public interface IRunConfigFactory
{

  /**
   * Creates a new quarkus run configuration for a specific project (with Maven as build tool)
   *
   * @param pProject Project to create run config for
   * @return the run config
   */
  @NotNull
  RunConfiguration createQuarkusMavenRunConfiguration(@NotNull Project pProject, @NotNull ConfigurationFactory pFactory);

}
