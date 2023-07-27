package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.common.base.Strings;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.ui.*;
import com.intellij.ui.*;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.util.ui.UIUtil;
import io.conceptive.quarkus.plugin.runconfig.options.MavenRunConfigurationOptions;
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
class MavenParametersSettingsEditorImpl extends SettingsEditor<MavenRunConfigImpl> implements PanelWithAnchor
{

  private final _WorkingDirectoryComponent workingDirComponent;
  private final LabeledComponent<RawCommandLineEditor> vmOptions;
  private final LabeledComponent<RawCommandLineEditor> goals;
  private final LabeledComponent<SimpleColoredComponent> goalsHint;
  private final LabeledComponent<RawCommandLineEditor> profiles;
  private final LabeledComponent<SimpleColoredComponent> profilesHint;
  private final LabeledComponent<SdkComboBox> jre;
  private final EnvironmentVariablesComponent envVariables;
  private final LabeledComponent<JCheckBox> compileBeforeLaunch;
  private JComponent myAnchor;

  public MavenParametersSettingsEditorImpl(@NotNull Project pProject)
  {
    workingDirComponent = new _WorkingDirectoryComponent(pProject);
    vmOptions = new LabeledComponent<>();
    vmOptions.setComponent(new RawCommandLineEditor());
    vmOptions.setLabelLocation(BorderLayout.WEST);
    vmOptions.setText("VM Options");
    goals = new LabeledComponent<>();
    goals.setComponent(new RawCommandLineEditor());
    goals.setLabelLocation(BorderLayout.WEST);
    goals.setText("Goals");
    goalsHint = new LabeledComponent<>();
    goalsHint.setLabelLocation(BorderLayout.WEST);
    goalsHint.setComponent(new SimpleColoredComponent());
    goalsHint.getComponent().append("Separate maven goals with spaces. Default: \"quarkus:dev\"", SimpleTextAttributes.GRAYED_ATTRIBUTES);
    profiles = new LabeledComponent<>();
    profiles.setComponent(new RawCommandLineEditor());
    profiles.setLabelLocation(BorderLayout.WEST);
    profiles.setText("Profiles");
    profilesHint = new LabeledComponent<>();
    profilesHint.setLabelLocation(BorderLayout.WEST);
    profilesHint.setComponent(new SimpleColoredComponent());
    profilesHint.getComponent().append("Separate maven profiles with spaces", SimpleTextAttributes.GRAYED_ATTRIBUTES);
    jre = new LabeledComponent<>();
    jre.setComponent(new SdkComboBox(SdkComboBoxModel.createProjectJdkComboBoxModel(pProject, this)));
    jre.setLabelLocation(BorderLayout.WEST);
    jre.setText("JRE");
    envVariables = new EnvironmentVariablesComponent();
    envVariables.setText("Environment Variables");
    envVariables.setLabelLocation(BorderLayout.WEST);
    compileBeforeLaunch = new LabeledComponent<>();
    compileBeforeLaunch.setComponent(new JCheckBox());
    compileBeforeLaunch.setText("Compile before launch");
    compileBeforeLaunch.setLabelLocation(BorderLayout.WEST);
    myAnchor = UIUtil.mergeComponentsWithAnchor(workingDirComponent, vmOptions, goals, goalsHint, profiles, profilesHint, jre, envVariables, compileBeforeLaunch);
  }

  @Override
  protected void resetEditorFrom(@NotNull MavenRunConfigImpl pImpl)
  {
    MavenRunConfigurationOptions options = pImpl.getOptions();
    workingDirComponent.setValue(Strings.nullToEmpty(options.getWorkingDir()));
    vmOptions.getComponent().setText(Strings.nullToEmpty(options.getVmOptions()));
    goals.getComponent().setText(ParametersListUtil.join(options.getGoals()));
    profiles.getComponent().setText(ParametersListUtil.join(options.getProfiles()));
    if (options.getJreName() != null)
      jre.getComponent().setSelectedSdk(options.getJreName());
    envVariables.setEnvs(options.getEnvVariables() == null ? new HashMap<>() : options.getEnvVariables());
    envVariables.setPassParentEnvs(options.getPassParentEnvParameters());
    compileBeforeLaunch.getComponent().setSelected(options.getCompileBeforeLaunch());
  }

  @Override
  protected void applyEditorTo(@NotNull MavenRunConfigImpl pImpl)
  {
    MavenRunConfigurationOptions options = pImpl.getOptions();
    options.setWorkingDir(workingDirComponent.getValue());
    options.setVmOptions(vmOptions.getComponent().getText());
    options.setGoals(ParametersListUtil.parse(goals.getComponent().getText()));
    options.setProfiles(ParametersListUtil.parse(profiles.getComponent().getText()));
    options.setJreName(jre.getComponent().getSelectedSdk() != null ? jre.getComponent().getSelectedSdk().getName() : null);
    options.setEnvVariables(envVariables.getEnvs());
    options.setPassParentEnvParameters(envVariables.isPassParentEnvs());
    options.setCompileBeforeLaunch(compileBeforeLaunch.getComponent().isSelected());
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    int gap = 5;
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(workingDirComponent);
    panel.add(Box.createVerticalStrut(gap));
    panel.add(vmOptions);
    panel.add(Box.createVerticalStrut(gap));
    panel.add(goals);
    panel.add(goalsHint);
    panel.add(Box.createVerticalStrut(gap));
    panel.add(profiles);
    panel.add(profilesHint);
    panel.add(Box.createVerticalStrut(gap));
    panel.add(jre);
    panel.add(Box.createVerticalStrut(gap));
    panel.add(envVariables);
    panel.add(Box.createVerticalStrut(gap + 2));
    panel.add(compileBeforeLaunch);
    return panel;
  }

  @Override
  public JComponent getAnchor()
  {
    return myAnchor;
  }

  @Override
  public void setAnchor(@Nullable JComponent anchor)
  {
    myAnchor = anchor;
    workingDirComponent.setAnchor(anchor);
    vmOptions.setAnchor(anchor);
    goals.setAnchor(anchor);
    goalsHint.setAnchor(anchor);
    profiles.setAnchor(anchor);
    profilesHint.setAnchor(anchor);
    jre.setAnchor(anchor);
    envVariables.setAnchor(anchor);
    compileBeforeLaunch.setAnchor(anchor);
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
