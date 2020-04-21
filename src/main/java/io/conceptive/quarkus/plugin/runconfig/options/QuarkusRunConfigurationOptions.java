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

  public QuarkusRunConfigurationOptions()
  {
    workingDir = string("");
    workingDir.setName("workingDir");
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

}
