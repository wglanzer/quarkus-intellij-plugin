package io.conceptive.quarkus.plugin.runconfig.options;

import org.jetbrains.annotations.*;

import java.util.*;

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

  @NotNull
  List<String> getGoals();

  @NotNull
  List<String> getProfiles();
}
