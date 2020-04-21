package io.conceptive.quarkus.plugin.runconfig;

import com.google.inject.AbstractModule;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.*;
import io.conceptive.quarkus.plugin.runconfig.factory.RunConfigFactoryModule;

/**
 * @author w.glanzer, 21.04.2020
 */
public class QuarkusRunConfigModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(IRunConfigExecutionFacade.class).to(RunConfigExecutionFacadeImpl.class);
    install(new RunConfigFactoryModule());
  }

}
