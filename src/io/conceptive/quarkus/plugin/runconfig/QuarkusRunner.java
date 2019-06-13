package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * DefaultProgramRunner for Quarkus-Instances
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusRunner extends DefaultProgramRunner
{

  private static final String _ID = "QuarkusMavenBridge_Runner";

  @NotNull
  @Override
  public String getRunnerId()
  {
    return _ID;
  }

  @Override
  public boolean canRun(@NotNull String pExecutorId, @NotNull RunProfile pRunProfile)
  {
    return pRunProfile instanceof QuarkusRunConfig;
  }

}
