package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusRunConfigType;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.*;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * Part I: Start Quarkus instance via Maven goal
 *
 * @author w.glanzer, 13.06.2019
 */
class QuarkusMavenRunConfig extends MavenRunConfiguration
{

  private int port;
  private Consumer<ProcessHandler> onRdy;
  private boolean attachDebugger;
  private IQuarkusRunConfigurationOptions options;
  private WeakReference<RunProfileState> stateRef = null;
  private Runnable onRestart;

  public QuarkusMavenRunConfig(@NotNull Project project)
  {
    super(project, MavenRunConfigurationType.getInstance().getConfigurationFactories()[0], "");
  }

  @Nullable
  @Override
  public Icon getIcon()
  {
    return QuarkusRunConfigType.ICON;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    // Rerun is not supported - it has to be initialized again
    if (stateRef != null && stateRef.get() != null)
    {
      if (onRestart != null)
        onRestart.run();
      return null;
    }

    QuarkusMavenState state = new QuarkusMavenState(this, pExecutionEnvironment, attachDebugger, onRdy);
    stateRef = new WeakReference<>(state);
    return state;
  }

  /**
   * Reinitializes this RunConfig with new settings
   *
   * @param pPort  Debug-Port
   * @param pOnRdy Consumer which handles ready-Events
   */
  public void reinit(@Nullable Integer pPort, @NotNull IQuarkusRunConfigurationOptions pOptions, @Nullable Consumer<ProcessHandler> pOnRdy, @Nullable Runnable pOnRestart)
  {
    attachDebugger = pOnRdy != null && pPort != null;
    port = attachDebugger ? pPort : -1;
    options = pOptions;
    onRdy = pOnRdy;
    onRestart = pOnRestart;
  }

  /**
   * Creates a new JavaParameters instance to start Quarkus with debugging parameters set
   *
   * @return Parameters instance
   */
  @NotNull
  JavaParameters createJavaParameters() throws ExecutionException
  {
    MavenRunnerParameters params = new MavenRunnerParameters();
    params.setGoals(Arrays.asList("clean", "compile", "quarkus:dev"));

    String workingDir = options.getWorkingDir();
    if (workingDir != null)
      params.setWorkingDirPath(workingDir);

    MavenRunnerSettings rsettings = MavenRunner.getInstance(getProject()).getState().clone();

    Map<String, String> props = new HashMap<>(rsettings.getMavenProperties());
    if (attachDebugger)
      props.put("debug", String.valueOf(port));
    rsettings.setMavenProperties(props);

    return MavenExternalParameters.createJavaParameters(getProject(), params, null, rsettings, this);
  }

}
