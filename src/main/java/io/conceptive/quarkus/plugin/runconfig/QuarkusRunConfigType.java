package io.conceptive.quarkus.plugin.runconfig;

import com.google.inject.*;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import io.conceptive.quarkus.plugin.runconfig.factory.IRunConfigFactory;
import io.conceptive.quarkus.plugin.runconfig.options.QuarkusRunConfigurationOptions;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * RunConfigType for QuarkusRunConfig
 *
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusRunConfigType extends SimpleConfigurationType
{

  public static final Icon ICON = IconLoader.getIcon("/io/conceptive/quarkus/plugin/quarkus_logo.svg");
  private static final String _ID = "QuarkusMavenBridge";
  private static final String _NAME = "Quarkus";
  private static final Injector _INJECTOR = Guice.createInjector(new QuarkusRunConfigModule());

  protected QuarkusRunConfigType()
  {
    super(_ID, _NAME, null, NotNullLazyValue.createValue(() -> ICON));
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project pProject)
  {
    return _INJECTOR.getInstance(IRunConfigFactory.class).createQuarkusRunConfiguration(pProject, this);
  }

  @Nullable
  @Override
  public Class<? extends BaseState> getOptionsClass()
  {
    return QuarkusRunConfigurationOptions.class;
  }

  @NotNull
  @Override
  public RunConfigurationSingletonPolicy getSingletonPolicy()
  {
    return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
  }

}
