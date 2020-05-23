package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.gradle.GradleRunConfigExecutionFacadeImpl;
import io.conceptive.quarkus.plugin.runconfig.executionfacade.maven.MavenRunConfigExecutionFacadeImpl;

/**
 * @author w.glanzer, 23.05.2020
 */
public class QuarkusExecutionFacadeModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(IRunConfigExecutionFacade.class).annotatedWith(Names.named("maven")).to(MavenRunConfigExecutionFacadeImpl.class);
    bind(IRunConfigExecutionFacade.class).annotatedWith(Names.named("gradle")).to(GradleRunConfigExecutionFacadeImpl.class);
  }

}
