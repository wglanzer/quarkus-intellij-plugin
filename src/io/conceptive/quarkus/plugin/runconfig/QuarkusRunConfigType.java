package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * RunConfigType for QuarkusRunConfig
 *
 * @see QuarkusRunConfig
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusRunConfigType extends SimpleConfigurationType
{

  private static final String _ID = "QuarkusMavenBridge";
  private static final String _NAME = "Quarkus";

  protected QuarkusRunConfigType()
  {
    super(_ID, _NAME, null, NotNullLazyValue.createValue(() -> IconLoader.getIcon("io/conceptive/quarkus/plugin/quarkus_logo.png")));
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project pProject)
  {
    return new QuarkusRunConfig(pProject, new _ConfigurationFactory(this));
  }

  /**
   * Factory for QuarkusRunConfig
   */
  private static class _ConfigurationFactory extends ConfigurationFactory
  {
    _ConfigurationFactory(@NotNull ConfigurationType type)
    {
      super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project pProject)
    {
      return new QuarkusRunConfig(pProject, this);
    }
  }

}
