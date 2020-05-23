package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusMavenRunConfigType;
import io.conceptive.quarkus.plugin.runconfig.options.IQuarkusRunConfigurationOptions;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.*;
import org.jetbrains.idea.maven.server.*;

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
    return QuarkusMavenRunConfigType.ICON;
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
    stateRef = null;
  }

  /**
   * Creates a new JavaParameters instance to start Quarkus with debugging parameters set
   *
   * @return Parameters instance
   */
  @Override
  public JavaParameters createJavaParameters(@Nullable Project project) throws ExecutionException
  {
    MavenRunnerParameters params = new MavenRunnerParameters();
    params.setGoals(_getGoals());

    String workingDir = options.getWorkingDir();
    if (workingDir != null)
      params.setWorkingDirPath(workingDir);

    MavenRunnerSettings rsettings = MavenRunner.getInstance(getProject()).getState().clone();
    rsettings.setVmOptions(options.getVmOptions());
    rsettings.setJreName(options.getJreName());
    Map<String, String> envVariables = options.getEnvVariables();
    if (envVariables != null)
      rsettings.setEnvironmentProperties(envVariables);
    rsettings.setPassParentEnv(options.getPassParentEnvParameters());

    Map<String, String> props = new HashMap<>(rsettings.getMavenProperties());
    if (attachDebugger)
      props.put("debug", String.valueOf(port));
    rsettings.setMavenProperties(props);

    return _removeMavenEventListener(MavenExternalParameters.createJavaParameters(getProject(), params, null, rsettings, this));
  }

  /**
   * @return goals to be executed
   */
  @NotNull
  private List<String> _getGoals()
  {
    List<String> goals = new ArrayList<>();
    if (options.getCompileBeforeLaunch())
    {
      goals.add("clean");
      goals.add("compile");
    }
    goals.add("quarkus:dev");
    return goals;
  }

  /**
   * Removes the maven event listener, so that no "ARTIFACT_RESOLVED" or "ARTIFACT_RESOLVING" logs appear in console
   *
   * @param pParameters JavaParameters to manipulate
   * @return the manipulated parameters
   */
  @NotNull
  private static JavaParameters _removeMavenEventListener(@NotNull JavaParameters pParameters)
  {
    String listenerPath = MavenServerManager.getMavenEventListener().getAbsolutePath();
    String extClassPath = pParameters.getVMParametersList().getPropertyValue(MavenServerEmbedder.MAVEN_EXT_CLASS_PATH);
    if (extClassPath != null && extClassPath.contains(listenerPath))
      pParameters.getVMParametersList().replaceOrAppend("-D" + MavenServerEmbedder.MAVEN_EXT_CLASS_PATH, extClassPath.replace(listenerPath, ""));
    return pParameters;
  }
}
