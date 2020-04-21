package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.GenericProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * DefaultProgramRunner for Quarkus-Instances.
 * Necessary to start quarkus in RUN and DEBUG state
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusRunConfigRunner extends GenericProgramRunner<RunnerSettings>
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
    // only run our quarkus config type
    if (pRunProfile instanceof RunConfiguration)
      return ((RunConfiguration) pRunProfile).getType() instanceof QuarkusRunConfigType;
    return false;
  }

}
