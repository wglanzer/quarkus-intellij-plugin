package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
                .implement(RunConfiguration.class, RunConfigImpl.class)
                .build(IRunConfigFactory.class));
    install(new FactoryModuleBuilder()
                .build(RunConfigEditor.Factory.class));
  }

}
