package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.intellij.execution.configurations.RunConfiguration;

/**
 * @author w.glanzer, 21.04.2020
 */
public class RunConfigFactoryModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    install(new FactoryModuleBuilder()
                .implement(RunConfiguration.class, MavenRunConfigImpl.class)
                .build(Key.get(IRunConfigFactory.class, Names.named("maven"))));
    install(new FactoryModuleBuilder()
                .implement(RunConfiguration.class, GradleRunConfigImpl.class)
                .build(Key.get(IRunConfigFactory.class, Names.named("gradle"))));
  }

}
