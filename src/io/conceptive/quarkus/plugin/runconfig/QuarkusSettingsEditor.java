package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.openapi.options.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Editor for the QuarkusRunConfig
 *
 * @author w.glanzer, 12.06.2019
 */
class QuarkusSettingsEditor extends SettingsEditor<QuarkusRunConfig>
{

  @Override
  protected void resetEditorFrom(@NotNull QuarkusRunConfig pRunConfiguration)
  {
    //todo
  }

  @Override
  protected void applyEditorTo(@NotNull QuarkusRunConfig pRunConfiguration) throws ConfigurationException
  {
    //todo
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    //todo
    return new JPanel();
  }

}
