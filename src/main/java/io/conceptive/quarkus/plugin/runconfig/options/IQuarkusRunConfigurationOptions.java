package io.conceptive.quarkus.plugin.runconfig.options;

import org.jetbrains.annotations.Nullable;

/**
 * Information about all set options in a single run configuration
 *
 * @author w.glanzer, 21.04.2020
 */
public interface IQuarkusRunConfigurationOptions
{
  @Nullable
  String getWorkingDir();

  @Nullable
  String getVmOptions();
}
