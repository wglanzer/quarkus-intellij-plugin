package io.conceptive.quarkus.plugin.runconfig.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.*;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

/**
 * Encapsulates a bunch of maven parameters and adds possible quarkus parameters
 *
 * @author w.glanzer, 13.06.2019
 */
@SuppressWarnings("unused")
public class QuarkusSettings
{

  public static final String TAG = "QuarkusSettings";

  private MavenGeneralSettings mavenGeneralSettings;
  private MavenRunnerSettings mavenRunnerSettings;
  private MavenRunnerParameters mavenRunnerParameters;

  public QuarkusSettings()
  {
  }

  public QuarkusSettings(Project project)
  {
    this(null, null, new MavenRunnerParameters());
  }

  private QuarkusSettings(@Nullable MavenGeneralSettings cs, @Nullable MavenRunnerSettings rs, MavenRunnerParameters rp)
  {
    this.mavenGeneralSettings = cs == null ? null : cs.clone();
    this.mavenRunnerSettings = rs == null ? null : rs.clone();
    this.mavenRunnerParameters = rp.clone();
  }

  public MavenGeneralSettings getMavenGeneralSettings()
  {
    return mavenGeneralSettings;
  }

  public void setMavenGeneralSettings(MavenGeneralSettings pMavenGeneralSettings)
  {
    mavenGeneralSettings = pMavenGeneralSettings;
  }

  public MavenRunnerSettings getMavenRunnerSettings()
  {
    return mavenRunnerSettings;
  }

  public void setMavenRunnerSettings(MavenRunnerSettings pMavenRunnerSettings)
  {
    mavenRunnerSettings = pMavenRunnerSettings;
  }

  public MavenRunnerParameters getMavenRunnerParameters()
  {
    return mavenRunnerParameters;
  }

  public void setMavenRunnerParameters(MavenRunnerParameters pMavenRunnerParameters)
  {
    mavenRunnerParameters = pMavenRunnerParameters;
  }

  public QuarkusSettings clone()
  {
    return new QuarkusSettings(mavenGeneralSettings, mavenRunnerSettings, mavenRunnerParameters);
  }

}
