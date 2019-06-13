package io.conceptive.quarkus.plugin.runconfig.settings.general;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import io.conceptive.quarkus.plugin.runconfig.QuarkusRunConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Editor for "Configuration" Tab
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusGeneralSettingsEditor extends SettingsEditor<QuarkusRunConfig>
{
  private final QuarkusGeneralPanel myPanel;

  public QuarkusGeneralSettingsEditor(@NotNull Project pProject)
  {
    myPanel = new QuarkusGeneralPanel(pProject);
  }

  @Override
  protected void resetEditorFrom(@NotNull QuarkusRunConfig pQuarkusRunConfig)
  {
    myPanel.getData(pQuarkusRunConfig.getMySettings());
  }

  @Override
  protected void applyEditorTo(@NotNull QuarkusRunConfig pQuarkusRunConfig)
  {
    myPanel.setData(pQuarkusRunConfig.getMySettings());
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    return myPanel.createComponent();
  }

}
