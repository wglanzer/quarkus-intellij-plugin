package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.*;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.*;

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
      return ((RunConfiguration) pRunProfile).getType() instanceof IQuarkusRunConfigType;
    return false;
  }

  @Nullable
  @Override
  protected RunContentDescriptor doExecute(@NotNull RunProfileState pState, @NotNull ExecutionEnvironment pEnv) throws ExecutionException
  {
    ExecutionResult result = pState.execute(pEnv.getExecutor(), this);
    if (result == null)
      return null;
    return new RunContentBuilder(result, pEnv).showRunContent(pEnv.getContentToReuse());
  }
}
