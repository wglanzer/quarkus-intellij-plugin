package io.conceptive.quarkus.plugin.runconfig;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author w.glanzer, 23.05.2020
 */
public interface IQuarkusRunConfigType extends ConfigurationType
{
  Icon ICON = IconLoader.getIcon("/io/conceptive/quarkus/plugin/quarkus_logo.svg");
}
