package io.conceptive.quarkus.plugin.runconfig.options;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredPropertyBase;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Serializable options for quarkus run configuration
 *
 * @author w.glanzer, 23.05.2020
 */
public class GradleRunConfigurationOptions extends RunConfigurationOptions implements IQuarkusRunConfigurationOptions
{
  public final StoredPropertyBase<String> workingDir;
  public final StoredPropertyBase<String> vmOptions;
  public final StoredPropertyBase<String> goals;
  public final StoredPropertyBase<String> arguments;
  public final StoredPropertyBase<Map<String, String>> envVariables;
  public final StoredPropertyBase<Boolean> passParentEnvParameters;
  public final StoredPropertyBase<Boolean> compileBeforeLaunch;

  public GradleRunConfigurationOptions()
  {
    workingDir = string("");
    workingDir.setName("workingDir");
    vmOptions = string("");
    vmOptions.setName("vmOptions");
    goals = string("quarkusDev");
    goals.setName("goals");
    arguments = string("");
    arguments.setName("arguments");
    envVariables = map();
    envVariables.setName("envVariables");
    passParentEnvParameters = property(true);
    passParentEnvParameters.setName("passParentEnvParameters");
    compileBeforeLaunch = property(true);
    compileBeforeLaunch.setName("compileBeforeLaunch");
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
    return arguments.getValue(this);
  }

  public void setArguments(@Nullable String pArguments)
  {
    arguments.setValue(this, pArguments);
  }

  @Nullable
  @Override
  public String getJreName()
  {
    return null;
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

  @Override
  public boolean getCompileBeforeLaunch()
  {
    return compileBeforeLaunch.getValue(this);
  }

  public void setCompileBeforeLaunch(boolean pCompile)
  {
    compileBeforeLaunch.setValue(this, pCompile);
  }

  @NotNull
  @Override
  public List<String> getGoals()
  {
    String value = goals.getValue(this);
    if(value == null)
      return List.of();
    return ParametersListUtil.parse(value);
  }

  public void setGoals(@Nullable List<String> pProfiles)
  {
    goals.setValue(this, pProfiles == null ? null : ParametersListUtil.join(pProfiles));
  }

  @NotNull
  @Override
  public List<String> getProfiles()
  {
    return List.of();
  }

}
