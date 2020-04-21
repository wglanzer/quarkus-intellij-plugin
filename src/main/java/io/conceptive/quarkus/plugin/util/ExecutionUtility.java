package io.conceptive.quarkus.plugin.util;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.*;
import com.intellij.execution.impl.*;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 20.04.2020
 */
public class ExecutionUtility
{

  /**
   * Executes the given runconfig in a project
   *
   * @param pProject          Project to execute runconfig in
   * @param pRunConfiguration runconfig to execute
   * @param pDebug            true, if it should be started in debug mode
   */
  public static void execute(@NotNull Project pProject, @NotNull RunConfiguration pRunConfiguration, boolean pDebug)
  {
    RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(pProject);
    Executor executor = pDebug ? DefaultDebugExecutor.getDebugExecutorInstance() : DefaultRunExecutor.getRunExecutorInstance();
    ExecutionUtil.runConfiguration(new RunnerAndConfigurationSettingsImpl(runManager, pRunConfiguration), executor);
  }

}
