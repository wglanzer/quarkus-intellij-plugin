package io.conceptive.quarkus.plugin.runconfig.executionfacade.gradle;

import com.intellij.execution.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.*;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId;
import com.intellij.openapi.externalSystem.service.execution.*;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.util.ProcessHandlerUtility;
import org.jetbrains.annotations.*;

import java.util.function.Consumer;

/**
 * @author w.glanzer, 23.05.2020
 */
class QuarkusGradleState extends ExternalSystemRunnableState
{
  private final Project project;
  private final boolean attachDebugger;
  private final Consumer<ProcessHandler> onReady;

  public QuarkusGradleState(@NotNull ExternalSystemTaskExecutionSettings pSettings, @NotNull Project pProject,
                            @NotNull ExternalSystemRunConfiguration pConfiguration, @NotNull ExecutionEnvironment pExecutionEnvironment,
                            boolean pAttachDebugger, @Nullable Consumer<ProcessHandler> pOnReady)
  {
    super(pSettings, pProject, false, pConfiguration, pExecutionEnvironment);
    project = pProject;
    attachDebugger = pAttachDebugger;
    onReady = pOnReady;
  }

  @Nullable
  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException
  {
    ExecutionResult result = super.execute(executor, runner);
    if (result != null)
      GradleNotificationListener.addHandler(ExternalSystemTaskId.getProjectId(project), () -> {
        if (attachDebugger)
          ProcessHandlerUtility.detachProcessSilently(project, result.getProcessHandler());

        // Delegate Handler
        if (onReady != null)
          onReady.accept(result.getProcessHandler());
      });
    return result;
  }

}
