package io.conceptive.quarkus.plugin.runconfig.settings.maven;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import io.conceptive.quarkus.plugin.runconfig.QuarkusRunConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.*;

import javax.swing.*;
import java.lang.reflect.Method;

/**
 * Editor for "Maven" Tab
 *
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusMavenGeneralSettingsEditor extends SettingsEditor<QuarkusRunConfig>
{

  private final MavenGeneralPanel myPanel;

  private JCheckBox myUseProjectSettings;

  private final Project myProject;

  public QuarkusMavenGeneralSettingsEditor(@NotNull Project project)
  {
    myProject = project;
    myPanel = new MavenGeneralPanel();
  }

  @Override
  protected void resetEditorFrom(@NotNull QuarkusRunConfig s)
  {
    myUseProjectSettings.setSelected(s.getMySettings().getMavenGeneralSettings() == null);

    if (s.getMySettings().getMavenGeneralSettings() == null)
    {
      MavenGeneralSettings settings = MavenProjectsManager.getInstance(myProject).getGeneralSettings();
      _getData(settings);
    }
    else
      _getData(s.getMySettings().getMavenGeneralSettings());
  }

  @Override
  protected void applyEditorTo(@NotNull QuarkusRunConfig s)
  {
    if (myUseProjectSettings.isSelected())
      s.getMySettings().setMavenGeneralSettings(null);
    else
    {
      MavenGeneralSettings state = s.getMySettings().getMavenGeneralSettings();
      if (state != null)
        _setData(state);
      else
      {
        MavenGeneralSettings settings = MavenProjectsManager.getInstance(myProject).getGeneralSettings().clone();
        _setData(settings);
        s.getMySettings().setMavenGeneralSettings(settings);
      }
    }
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    Pair<JPanel, JCheckBox> pair = MavenDisablePanelCheckbox.createPanel(myPanel.createComponent(), "Use project settings");
    myUseProjectSettings = pair.second;
    return pair.first;
  }

  private void _getData(MavenGeneralSettings pSettings)
  {
    try
    {
      Method getData = MavenGeneralPanel.class.getDeclaredMethod("getData", MavenGeneralSettings.class);
      getData.setAccessible(true);
      getData.invoke(myPanel, pSettings);
    }
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void _setData(MavenGeneralSettings pSettings)
  {
    try
    {
      Method setData = MavenGeneralPanel.class.getDeclaredMethod("setData", MavenGeneralSettings.class);
      setData.setAccessible(true);
      setData.invoke(myPanel, pSettings);
    }
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
