package io.conceptive.quarkus.plugin.runconfig.options;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

  @Nullable
  String getArguments();

  @Nullable
  String getJreName();

  @Nullable
  Map<String, String> getEnvVariables();

  boolean getPassParentEnvParameters();

  boolean getCompileBeforeLaunch();
}
