package io.conceptive.quarkus.plugin.runconfig.maven;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.settings.QuarkusSettings;
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
  private final QuarkusSettings settings;
  private final Consumer<ProcessHandler> onRdy;
  private final boolean attachDebugger;

  public QuarkusMavenRunConfig(@NotNull Project project, @NotNull QuarkusSettings pSettings,
                               @Nullable Integer pPort, @Nullable Consumer<ProcessHandler> pOnRdy)
  {
    super(project, MavenRunConfigurationType.getInstance().getConfigurationFactories()[0], "");
    settings = pSettings;
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
  JavaParameters createJavaParameters() throws ExecutionException
  {
    MavenRunnerParameters params = settings.getMavenRunnerParameters().clone();
    params.setGoals(Arrays.asList("compile", "quarkus:dev"));

    MavenRunnerSettings rsettings = settings.getMavenRunnerSettings().clone();
    if(rsettings == null)
      rsettings = MavenRunner.getInstance(getProject()).getState().clone();

    Map<String, String> props = new HashMap<>(rsettings.getMavenProperties());
    if(attachDebugger)
      props.put("debug", String.valueOf(port));
    rsettings.setMavenProperties(props);

    return MavenExternalParameters.createJavaParameters(getProject(), params, null, rsettings);
  }

}
