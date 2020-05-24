package io.conceptive.quarkus.plugin.runconfig.executionfacade.gradle;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.externalSystem.model.task.*;
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemProgressNotificationManager;
import io.conceptive.quarkus.plugin.util.QuarkusUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.util.*;

/**
 * Listener that gets notified, if a gradle build produced text
 *
 * @author w.glanzer, 23.05.2020
 */
public class GradleNotificationListener extends ExternalSystemTaskNotificationListenerAdapter
{
  private static final Map<String, Runnable> executeOnFinish = new HashMap<>();

  static
  {
    // Register to JetBrains NotificationManager, because of registering via plugin.xml does not work currently (?)
    ApplicationManager.getApplication().getService(ExternalSystemProgressNotificationManager.class)
        .addNotificationListener(new GradleNotificationListener());
  }

  @Override
  public void onTaskOutput(@NotNull ExternalSystemTaskId id, @NotNull String text, boolean stdOut)
  {
    synchronized (executeOnFinish)
    {
      if (id.getProjectSystemId().equals(GradleConstants.SYSTEM_ID) && QuarkusUtility.containsDebugReadyString(text))
      {
        Runnable onReady = executeOnFinish.get(id.getIdeProjectId());
        if (onReady != null)
          onReady.run();
      }
    }
  }

  /**
   * Adds a new handler to this NotificationListener that gets notified, if a debugger can be connected
   *
   * @param pProjectID ID of the Project
   * @param pRunnable  Runnable to execute
   */
  public static void addHandler(@NotNull String pProjectID, @NotNull Runnable pRunnable)
  {
    synchronized (executeOnFinish)
    {
      executeOnFinish.put(pProjectID, pRunnable);
    }
  }
}
