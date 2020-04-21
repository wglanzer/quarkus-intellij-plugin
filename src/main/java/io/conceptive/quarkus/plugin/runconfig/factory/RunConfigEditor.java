package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.inject.Inject;
import com.intellij.openapi.options.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Editor to display RunConfigImpl visually
 *
 * @author w.glanzer, 20.04.2020
 */
class RunConfigEditor extends SettingsEditorGroup<RunConfigImpl>
{

  interface Factory
  {
    /**
     * @return creates a new instance of RunConfigEditor
     */
    @NotNull
    RunConfigEditor create();
  }

  @Inject
  private RunConfigEditor()
  {
    addEditor("General", new _GeneralEditor());
  }

  /**
   * SettingsEditor-Impl for "General" tab
   */
  private static class _GeneralEditor extends SettingsEditor<RunConfigImpl>
  {
    @Override
    protected void resetEditorFrom(@NotNull RunConfigImpl pConfig)
    {
      //todo
    }

    @Override
    protected void applyEditorTo(@NotNull RunConfigImpl pConfig) throws ConfigurationException
    {
      //todo
    }

    @NotNull
    @Override
    protected JComponent createEditor()
    {
      //todo
      return new JLabel("dummy");
    }
  }

}
