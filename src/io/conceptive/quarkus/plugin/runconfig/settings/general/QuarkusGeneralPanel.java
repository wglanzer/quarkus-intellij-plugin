package io.conceptive.quarkus.plugin.runconfig.settings.general;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.externalSystem.service.execution.cmd.ParametersListLexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.TextFieldCompletionProvider;
import com.intellij.util.execution.ParametersListUtil;
import io.conceptive.quarkus.plugin.runconfig.settings.QuarkusSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.*;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.*;

/**
 * @author w.glanzer, 13.06.2019
 */
public class QuarkusGeneralPanel implements PanelWithAnchor
{
  private JPanel panel;
  private EnvironmentVariablesComponent environmentVariablesComponent;
  protected LabeledComponent<TextFieldWithBrowseButton> workingDirComponent;
  private LabeledComponent<EditorTextField> profilesComponent;
  private JBLabel myFakeLabel;
  private FixedSizeButton showProjectTreeButton;
  private JComponent anchor;

  QuarkusGeneralPanel(@NotNull final Project project)
  {
    workingDirComponent.getComponent().addBrowseFolderListener("Working Directory", "", project, new MavenPomFileChooserDescriptor(project));

    if (!project.isDefault())
    {
      TextFieldCompletionProvider profilesCompletionProvider = new TextFieldCompletionProvider(true)
      {
        @Override
        protected final void addCompletionVariants(@NotNull String text, int offset, @NotNull String prefix, @NotNull CompletionResultSet result)
        {
          MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
          for (String profile : manager.getAvailableProfiles())
          {
            result.addElement(LookupElementBuilder.create(ParametersListUtil.join(profile)));
          }
        }

        @NotNull
        @Override
        protected String getPrefix(@NotNull String currentTextPrefix)
        {
          ParametersListLexer lexer = new ParametersListLexer(currentTextPrefix);
          while (lexer.nextToken())
          {
            if (lexer.getTokenEnd() == currentTextPrefix.length())
            {
              String prefix = lexer.getCurrentToken();
              if (prefix.startsWith("-") || prefix.startsWith("!"))
              {
                prefix = prefix.substring(1);
              }
              return prefix;
            }
          }

          return "";
        }
      };

      profilesComponent.setComponent(profilesCompletionProvider.createEditor(project));
    }

    showProjectTreeButton.setIcon(AllIcons.Actions.Module);
    MavenSelectProjectPopup.attachToWorkingDirectoryField(MavenProjectsManager.getInstance(project),
                                                          workingDirComponent.getComponent().getTextField(),
                                                          showProjectTreeButton,
                                                          workingDirComponent);

    environmentVariablesComponent.setPassParentEnvs(true);

    setAnchor(profilesComponent.getLabel());
  }

  JComponent createComponent()
  {
    return panel;
  }

  void setData(final QuarkusSettings pSettings)
  {
    MavenRunnerParameters runnerParams = pSettings.getMavenRunnerParameters();
    MavenRunnerSettings runnerSettings = pSettings.getMavenRunnerSettings();
    if(runnerParams == null)
      pSettings.setMavenRunnerParameters((runnerParams = new MavenRunnerParameters()));
    if(runnerSettings == null)
      pSettings.setMavenRunnerSettings((runnerSettings = new MavenRunnerSettings()));

    runnerParams.setWorkingDirPath(workingDirComponent.getComponent().getText());

    Map<String, Boolean> profilesMap = new LinkedHashMap<>();
    for (String profile : ParametersListUtil.parse(profilesComponent.getComponent().getText()))
    {
      boolean isEnabled = true;
      if (profile.startsWith("-") || profile.startsWith("!"))
      {
        profile = profile.substring(1);
        if (profile.isEmpty()) continue;

        isEnabled = false;
      }

      profilesMap.put(profile, isEnabled);
    }
    runnerParams.setProfilesMap(profilesMap);

    runnerSettings.setEnvironmentProperties(environmentVariablesComponent.getEnvs());
    runnerSettings.setPassParentEnv(environmentVariablesComponent.isPassParentEnvs());
  }

  void getData(final QuarkusSettings pSettings)
  {
    MavenRunnerParameters runnerParams = pSettings.getMavenRunnerParameters();
    MavenRunnerSettings runnerSettings = pSettings.getMavenRunnerSettings();

    if(runnerParams != null)
    {
      workingDirComponent.getComponent().setText(runnerParams.getWorkingDirPath());

      ParametersList parametersList = new ParametersList();

      for (Map.Entry<String, Boolean> entry : runnerParams.getProfilesMap().entrySet())
      {
        String profileName = entry.getKey();
        if (!entry.getValue())
          profileName = '-' + profileName;
        parametersList.add(profileName);
      }

      profilesComponent.getComponent().setText(parametersList.getParametersString());
    }

    if(runnerSettings != null)
    {
      environmentVariablesComponent.setEnvs(runnerSettings.getEnvironmentProperties());
      environmentVariablesComponent.setPassParentEnvs(runnerSettings.isPassParentEnv());
    }
  }

  @Override
  public JComponent getAnchor()
  {
    return anchor;
  }

  @Override
  public void setAnchor(JComponent anchor)
  {
    this.anchor = anchor;
    environmentVariablesComponent.setAnchor(anchor);
    workingDirComponent.setAnchor(anchor);
    profilesComponent.setAnchor(anchor);
    myFakeLabel.setAnchor(anchor);
  }
}