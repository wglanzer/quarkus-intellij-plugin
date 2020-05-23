package io.conceptive.quarkus.plugin.runconfig;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import io.conceptive.quarkus.plugin.runconfig.factory.IRunConfigFactory;
import io.conceptive.quarkus.plugin.runconfig.options.MavenRunConfigurationOptions;
import org.jetbrains.annotations.*;

/**
 * RunConfigType for QuarkusRunConfig running with maven as build tool
 *
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusMavenRunConfigType extends SimpleConfigurationType implements IQuarkusRunConfigType
{

  private static final String _ID = "QuarkusMavenBridge";
  private static final String _NAME = "Quarkus (Maven)";
  private static final Injector _INJECTOR = Guice.createInjector(new QuarkusRunConfigModule());

  protected QuarkusMavenRunConfigType()
  {
    super(_ID, _NAME, null, NotNullLazyValue.createValue(() -> ICON));
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project pProject)
  {
    return _INJECTOR.getInstance(Key.get(IRunConfigFactory.class, Names.named("maven"))).createQuarkusRunConfiguration(pProject, this);
  }

  @Nullable
  @Override
  public Class<? extends BaseState> getOptionsClass()
  {
    return MavenRunConfigurationOptions.class;
  }

  @NotNull
  @Override
  public RunConfigurationSingletonPolicy getSingletonPolicy()
  {
    return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
  }

}
