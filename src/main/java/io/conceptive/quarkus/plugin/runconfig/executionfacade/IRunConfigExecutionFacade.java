package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.NotNull;

/**
 * the quarkus run configuration only delegates to maven and debug (if debug pressed)
 *
 * @author w.glanzer, 21.04.2020
 */
public interface IRunConfigExecutionFacade
{

  /**
   * Executes the nested maven run config
   *
   * @param pSource  Source-Config
   * @param pOptions Execution Options
   */
  void executeNestedRunConfigs(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions);

  /**
   * Executes the nested maven run config and attaches debugger when possible
   *
   * @param pSource    Source-Config
   * @param pOptions   Execution Options
   * @param pDebugPort Port for Debugger
   */
  void executeNestedRunConfigs(@NotNull RunnerAndConfigurationSettings pSettings, @NotNull RunConfiguration pSource, @NotNull IQuarkusRunConfigurationOptions pOptions, @NotNull Integer pDebugPort);

}
