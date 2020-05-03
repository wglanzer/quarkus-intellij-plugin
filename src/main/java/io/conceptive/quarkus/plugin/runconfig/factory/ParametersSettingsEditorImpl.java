package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.common.base.Strings;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.*;
import com.intellij.ui.RawCommandLineEditor;
import io.conceptive.quarkus.plugin.runconfig.options.QuarkusRunConfigurationOptions;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.*;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * General-Tab
 *
 * @author w.glanzer, 21.04.2020
 */
class ParametersSettingsEditorImpl extends SettingsEditor<RunConfigImpl>
{

  private final _WorkingDirectoryComponent workingDirComponent;
  private final LabeledComponent<RawCommandLineEditor> vmOptions;
  private final LabeledComponent<JdkComboBox> jre;
  private final ProjectSdksModel projectSdksModel;
  private final EnvironmentVariablesComponent envVariables;

  public ParametersSettingsEditorImpl(@NotNull Project pProject)
  {
    workingDirComponent = new _WorkingDirectoryComponent(pProject);
    vmOptions = new LabeledComponent<>();
    vmOptions.setComponent(new RawCommandLineEditor());
    vmOptions.setLabelLocation(BorderLayout.WEST);
    vmOptions.setText("VM Options");
    projectSdksModel = new ProjectSdksModel();
    projectSdksModel.syncSdks();
    jre = new LabeledComponent<>();
    jre.setComponent(new JdkComboBox(pProject, projectSdksModel, null, null, null, null));
    jre.setLabelLocation(BorderLayout.WEST);
    jre.setText("JRE");
    envVariables = new EnvironmentVariablesComponent();
    envVariables.setLabelLocation(BorderLayout.WEST);
  }

  @Override
  protected void resetEditorFrom(@NotNull RunConfigImpl pImpl)
  {
    QuarkusRunConfigurationOptions options = pImpl.getOptions();
    workingDirComponent.setValue(Strings.nullToEmpty(options.getWorkingDir()));
    vmOptions.getComponent().setText(Strings.nullToEmpty(options.getVmOptions()));
    jre.getComponent().setSelectedItem(projectSdksModel.findSdk(Strings.nullToEmpty(options.getJreName())));
    envVariables.setEnvs(options.getEnvVariables() == null ? new HashMap<>() : options.getEnvVariables());
    envVariables.setPassParentEnvs(options.getPassParentEnvParameters());
  }

  @Override
  protected void applyEditorTo(@NotNull RunConfigImpl pImpl) throws ConfigurationException
  {
    QuarkusRunConfigurationOptions options = pImpl.getOptions();
    options.setWorkingDir(workingDirComponent.getValue());
    options.setVmOptions(vmOptions.getComponent().getText());
    Sdk selectedJDK = jre.getComponent().getSelectedJdk();
    if (selectedJDK != null)
      options.setJreName(selectedJDK.getName());
    options.setEnvVariables(envVariables.getEnvs());
    options.setPassParentEnvParameters(envVariables.isPassParentEnvs());
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(workingDirComponent);
    panel.add(Box.createVerticalStrut(4));
    panel.add(vmOptions);
    panel.add(Box.createVerticalStrut(4));
    panel.add(jre);
    panel.add(Box.createVerticalStrut(4));
    panel.add(envVariables);
    return panel;
  }

  /**
   * Component that displays the workingDirectory and a module/filechooser
   */
  private static class _WorkingDirectoryComponent extends LabeledComponent<JPanel>
  {
    private final TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton();

    public _WorkingDirectoryComponent(@NotNull Project pProject)
    {
      FixedSizeButton projectTreeButton = new FixedSizeButton();
      projectTreeButton.setIcon(AllIcons.Nodes.Module);
      JPanel container = new JPanel(new BorderLayout());
      container.add(textField, BorderLayout.CENTER);
      container.add(projectTreeButton, BorderLayout.EAST);
      setComponent(container);
      setLabelLocation(BorderLayout.WEST);
      setText("Working Directory");
      textField.addBrowseFolderListener("Select Quarkus Module", "", pProject, new MavenPomFileChooserDescriptor(pProject));
      MavenSelectProjectPopup.attachToWorkingDirectoryField(MavenProjectsManager.getInstance(pProject), textField.getTextField(), projectTreeButton, null);
    }

    public String getValue()
    {
      return textField.getText();
    }

    public void setValue(@Nullable String pValue)
    {
      textField.setText(pValue);
    }
  }

}
