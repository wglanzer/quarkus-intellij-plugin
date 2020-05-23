package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessHandler;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.*;

import java.util.function.Consumer;

/**
 * @author w.glanzer, 23.05.2020
 */
public interface IInternalRunConfigs
{

  interface IDebugRunConfig extends RunConfiguration
  {
    /**
     * Reinitializes this RunConfig with new settings
     *
     * @param pBuildProcessHandler ProcessHandler for the build Process
     * @param pPort                Debug-Port
     * @param pOnRestart           Runnable that gets called, if this runconfig gets restartet
     */
    void reinit(@Nullable ProcessHandler pBuildProcessHandler, int pPort, @Nullable Runnable pOnRestart);
  }

  interface IBuildRunConfig extends RunConfiguration
  {
    /**
     * Reinitializes this RunConfig with new settings
     *
     * @param pPort  Debug-Port
     * @param pOnRdy Consumer which handles ready-Events
     */
    void reinit(@Nullable Integer pPort, @NotNull IQuarkusRunConfigurationOptions pOptions, @Nullable Consumer<ProcessHandler> pOnRdy, @Nullable Runnable pOnRestart);
  }

}
