package io.conceptive.quarkus.plugin.runconfig.options;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredPropertyBase;
import org.jetbrains.annotations.Nullable;

/**
 * Serializable options for quarkus run configuration
 *
 * @author w.glanzer, 20.04.2020
 */
public class QuarkusRunConfigurationOptions extends RunConfigurationOptions implements IQuarkusRunConfigurationOptions
{

  public final StoredPropertyBase<String> workingDir;
  public final StoredPropertyBase<String> vmOptions;
  public final StoredPropertyBase<String> jre;

  public QuarkusRunConfigurationOptions()
  {
    workingDir = string("");
    workingDir.setName("workingDir");
    vmOptions = string("");
    vmOptions.setName("vmOptions");
    jre = string("");
    jre.setName("jre");
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
  public String getJRE()
  {
    return jre.getValue(this);
  }

  public void setJRE(@Nullable String pJRE)
  {
    jre.setValue(this, pJRE);
  }
}
