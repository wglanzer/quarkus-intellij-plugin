package io.conceptive.quarkus.plugin.runconfig.settings;

import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusRunConfig;
import io.conceptive.quarkus.plugin.runconfig.settings.general.QuarkusGeneralSettingsEditor;
import io.conceptive.quarkus.plugin.runconfig.settings.maven.QuarkusMavenGeneralSettingsEditor;
import org.jetbrains.annotations.NotNull;

/**
 * Editor for the QuarkusRunConfig
 *
 * @author w.glanzer, 12.06.2019
 */
public class QuarkusSettingsEditor extends SettingsEditorGroup<QuarkusRunConfig>
{

  public QuarkusSettingsEditor(@NotNull Project pProject)
  {
    addEditor("General", new QuarkusGeneralSettingsEditor(pProject));
    addEditor("Maven", new QuarkusMavenGeneralSettingsEditor(pProject));
  }

}
