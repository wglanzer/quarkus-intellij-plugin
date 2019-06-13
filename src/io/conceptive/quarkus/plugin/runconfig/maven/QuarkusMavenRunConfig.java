package io.conceptive.quarkus.plugin.runconfig.maven;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Part I: Start Quarkus instance via Maven goal
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusMavenRunConfig extends MavenRunConfiguration
{

  private final int port;
  private final Consumer<ProcessHandler> onRdy;
  private final boolean attachDebugger;

  public QuarkusMavenRunConfig(@NotNull Project project, @Nullable Integer pPort, @Nullable Consumer<ProcessHandler> pOnRdy)
  {
    super(project, MavenRunConfigurationType.getInstance().getConfigurationFactories()[0], "");
    attachDebugger = pOnRdy != null && pPort != null;
    port = attachDebugger ? pPort : -1;
    onRdy = pOnRdy;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor pExecutor, @NotNull ExecutionEnvironment pExecutionEnvironment)
  {
    return new QuarkusMavenState(this, pExecutionEnvironment, attachDebugger, onRdy);
  }

  /**
   * Creates a new JavaParameters instance to start Quarkus with debugging parameters set
   *
   * @return Parameters instance
   */
  @NotNull
  JavaParameters createJavaParameters() throws ExecutionException //todo
  {
    MavenRunnerParameters params = new MavenRunnerParameters();
    params.setWorkingDirPath(getProject().getBasePath());
    params.setGoals(Arrays.asList("compile", "quarkus:dev"));
    MavenRunnerSettings settings = new MavenRunnerSettings();
    Map<String, String> props = new HashMap<>();
    if(attachDebugger)
      props.put("debug", String.valueOf(port));
    settings.setPassParentEnv(true);
    settings.setMavenProperties(props);
    return MavenExternalParameters.createJavaParameters(getProject(), params, null, settings);
  }

}
