package io.conceptive.quarkus.plugin.runconfig.options;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredPropertyBase;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Serializable options for quarkus run configuration
 *
 * @author w.glanzer, 20.04.2020
 */
public class MavenRunConfigurationOptions extends RunConfigurationOptions implements IQuarkusRunConfigurationOptions
{

  public final StoredPropertyBase<String> workingDir;
  public final StoredPropertyBase<String> vmOptions;
  public final StoredPropertyBase<String> goals;
  public final StoredPropertyBase<String> profiles;
  public final StoredPropertyBase<String> jreName;
  public final StoredPropertyBase<Map<String, String>> envVariables;
  public final StoredPropertyBase<Boolean> passParentEnvParameters;

  public MavenRunConfigurationOptions()
  {
    workingDir = string("");
    workingDir.setName("workingDir");
    vmOptions = string("");
    vmOptions.setName("vmOptions");
    goals = string("clean compile quarkus:dev");
    goals.setName("goals");
    profiles = string("");
    profiles.setName("profiles");
    jreName = string("");
    jreName.setName("jreName");
    envVariables = map();
    envVariables.setName("envVariables");
    passParentEnvParameters = property(true);
    passParentEnvParameters.setName("passParentEnvParameters");
  }

  @Override
  @Nullable
  public String getWorkingDir()
  {
    return workingDir.getValue(this);
  }

  public void setWorkingDir(@Nullable String pWorkingDir)
  {
    workingDir.setValue(this, pWorkingDir);
  }

  @Nullable
  @Override
  public String getVmOptions()
  {
    return vmOptions.getValue(this);
  }

  public void setVmOptions(@Nullable String pOptions)
  {
    vmOptions.setValue(this, pOptions);
  }

  @Nullable
  @Override
  public String getArguments()
  {
    return null;
  }

  @Nullable
  @Override
  public String getJreName()
  {
    return jreName.getValue(this);
  }

  public void setJreName(@Nullable String pJreName)
  {
    jreName.setValue(this, pJreName);
  }

  @Nullable
  @Override
  public Map<String, String> getEnvVariables()
  {
    return envVariables.getValue(this);
  }

  public void setEnvVariables(@Nullable Map<String, String> pVariables)
  {
    envVariables.setValue(this, pVariables);
  }

  @Override
  public boolean getPassParentEnvParameters()
  {
    return passParentEnvParameters.getValue(this);
  }

  public void setPassParentEnvParameters(boolean pPass)
  {
    passParentEnvParameters.setValue(this, pPass);
  }

  @Nullable
  @Override
  public String getGoals()
  {
    return goals.getValue(this);
  }

  public void setGoals(@Nullable String pProfiles)
  {
    goals.setValue(this, pProfiles);
  }

  @Nullable
  @Override
  public String getProfiles()
  {
    return profiles.getValue(this);
  }

  public void setProfiles(@Nullable String pProfiles)
  {
    profiles.setValue(this, pProfiles);
  }
}
