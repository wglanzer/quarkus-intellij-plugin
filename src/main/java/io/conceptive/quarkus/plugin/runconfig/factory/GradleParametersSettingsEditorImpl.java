package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.common.base.Strings;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.externalSystem.*;
import com.intellij.openapi.externalSystem.service.ui.ExternalProjectPathField;
import com.intellij.openapi.externalSystem.util.*;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.*;
import com.intellij.util.ui.UIUtil;
import io.conceptive.quarkus.plugin.runconfig.options.GradleRunConfigurationOptions;
import org.jetbrains.annotations.*;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * General-Tab
 *
 * @author w.glanzer, 21.04.2020
 */
class GradleParametersSettingsEditorImpl extends SettingsEditor<GradleRunConfigImpl> implements PanelWithAnchor
{

  private final LabeledComponent<ExternalProjectPathField> workingDirComponent;
  private final LabeledComponent<RawCommandLineEditor> vmOptions;
  private final LabeledComponent<RawCommandLineEditor> arguments;
  private final EnvironmentVariablesComponent envVariables;
  private final LabeledComponent<JCheckBox> compileBeforeLaunch;
  private JComponent myAnchor;

  public GradleParametersSettingsEditorImpl(@NotNull Project pProject)
  {
    ExternalSystemManager<?, ?, ?, ?, ?> manager = ExternalSystemApiUtil.getManager(GradleConstants.SYSTEM_ID);
    FileChooserDescriptor projectPathChooserDescriptor = null;
    if (manager instanceof ExternalSystemUiAware)
      projectPathChooserDescriptor = ((ExternalSystemUiAware) manager).getExternalProjectConfigDescriptor();
    if (projectPathChooserDescriptor == null)
      projectPathChooserDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();

    workingDirComponent = new LabeledComponent<>();
    workingDirComponent.setLabelLocation(BorderLayout.WEST);
    workingDirComponent.setText("Gradle Project");
    workingDirComponent.setComponent(new ExternalProjectPathField(pProject, GradleConstants.SYSTEM_ID, projectPathChooserDescriptor,
                                                                  ExternalSystemBundle.message("settings.label.select.project", GradleConstants.SYSTEM_ID.getReadableName())));
    vmOptions = new LabeledComponent<>();
    vmOptions.setComponent(new RawCommandLineEditor());
    vmOptions.setLabelLocation(BorderLayout.WEST);
    vmOptions.setText("VM Options");
    arguments = new LabeledComponent<>();
    arguments.setComponent(new RawCommandLineEditor());
    arguments.setLabelLocation(BorderLayout.WEST);
    arguments.setText("Arguments");
    envVariables = new EnvironmentVariablesComponent();
    envVariables.setText("Environment Variables");
    envVariables.setLabelLocation(BorderLayout.WEST);
    compileBeforeLaunch = new LabeledComponent<>();
    compileBeforeLaunch.setComponent(new JCheckBox());
    compileBeforeLaunch.setText("Compile before launch");
    compileBeforeLaunch.setLabelLocation(BorderLayout.WEST);
    myAnchor = UIUtil.mergeComponentsWithAnchor(workingDirComponent, vmOptions, arguments, envVariables, compileBeforeLaunch);
  }

  @Override
  protected void resetEditorFrom(@NotNull GradleRunConfigImpl pImpl)
  {
    GradleRunConfigurationOptions options = pImpl.getOptions();
    workingDirComponent.getComponent().setText(Strings.nullToEmpty(options.getWorkingDir()));
    vmOptions.getComponent().setText(Strings.nullToEmpty(options.getVmOptions()));
    arguments.getComponent().setText(Strings.nullToEmpty(options.getArguments()));
    envVariables.setEnvs(options.getEnvVariables() == null ? new HashMap<>() : options.getEnvVariables());
    envVariables.setPassParentEnvs(options.getPassParentEnvParameters());
    compileBeforeLaunch.getComponent().setSelected(options.getCompileBeforeLaunch());
  }

  @Override
  protected void applyEditorTo(@NotNull GradleRunConfigImpl pImpl)
  {
    GradleRunConfigurationOptions options = pImpl.getOptions();
    options.setWorkingDir(workingDirComponent.getComponent().getText());
    options.setVmOptions(vmOptions.getComponent().getText());
    options.setArguments(arguments.getComponent().getText());
    options.setEnvVariables(envVariables.getEnvs());
    options.setPassParentEnvParameters(envVariables.isPassParentEnvs());
    options.setCompileBeforeLaunch(compileBeforeLaunch.getComponent().isSelected());
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
    panel.add(arguments);
    panel.add(Box.createVerticalStrut(4));
    panel.add(envVariables);
    panel.add(Box.createVerticalStrut(4));
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
    arguments.setAnchor(anchor);
    envVariables.setAnchor(anchor);
    compileBeforeLaunch.setAnchor(anchor);
  }

}
