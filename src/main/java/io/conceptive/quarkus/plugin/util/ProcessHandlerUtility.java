package io.conceptive.quarkus.plugin.util;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author w.glanzer, 23.05.2020
 */
public class ProcessHandlerUtility
{

  /**
   * Detaches a ProcessHandler silently, so that only the GUI will be informed that it "has detached".
   * This is usefull, if you want a ProcessHandler to be running in background - with no handler on GUI.
   *
   * @param pHandler Handler to detach
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void detachProcessSilently(@NotNull Project pProject, @NotNull ProcessHandler pHandler)
  {
    try
    {
      // Safe state, because we re set it afterwards
      AtomicReference oldStateRef = _getStateRef(pHandler);
      Object oldState = oldStateRef.get();

      // Just notify listeners and execution manager
      _notifyProcessTerminated(pHandler);
      _disposeRunContentDescriptor(pProject, pHandler);

      // reset state and exit code, because it is "not really dead"
      oldStateRef.set(oldState);
      _setExitCode(pHandler, null);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to detach process handler", e);
    }
  }

  /**
   * Returns the state holder of the given process handler
   *
   * @param pHandler Handler to retrieve state holder from
   * @return the atomicReference that holds the current state
   */
  @NotNull
  private static AtomicReference<?> _getStateRef(@NotNull ProcessHandler pHandler) throws Exception
  {
    Field state = ProcessHandler.class.getDeclaredField("myState");
    state.setAccessible(true);
    //noinspection rawtypes
    return (AtomicReference) state.get(pHandler);
  }

  /**
   * Notifies all listeners of pHandler, that the process has terminated
   *
   * @param pHandler Handler to retrieve listeners from
   */
  private static void _notifyProcessTerminated(@NotNull ProcessHandler pHandler) throws Exception
  {
    Method notifyProcessTerminated = ProcessHandler.class.getDeclaredMethod("notifyProcessTerminated", int.class);
    notifyProcessTerminated.setAccessible(true);
    notifyProcessTerminated.invoke(pHandler, 0);
  }

  /**
   * The execution manager has to be handled specially, because of disposing run content descriptors
   *
   * @param pProject Project
   * @param pHandler Handler
   */
  private static void _disposeRunContentDescriptor(@NotNull Project pProject, @NotNull ProcessHandler pHandler) throws Exception
  {
    Field runningConfigurations = ExecutionManagerImpl.class.getDeclaredField("runningConfigurations");
    runningConfigurations.setAccessible(true);

    //noinspection unchecked
    List<Object> runConfigs = (List<Object>) runningConfigurations.get(ExecutionManager.getInstance(pProject));
    List<RunContentDescriptor> descriptorsToDispose = new ArrayList<>();
    for (Object runConfig : runConfigs)
    {
      Field descriptor = runConfig.getClass().getDeclaredField("descriptor");
      descriptor.setAccessible(true);
      RunContentDescriptor desc = (RunContentDescriptor) descriptor.get(runConfig);
      if (Objects.equals(desc.getProcessHandler(), pHandler))
        descriptorsToDispose.add(desc);
    }

    ApplicationManager.getApplication().executeOnPooledThread(() -> descriptorsToDispose.forEach(pDesc -> {
      Content content = pDesc.getAttachedContent();
      pDesc.dispose();
      if (content != null)
        pDesc.setAttachedContent(content);
    }));
  }

  /**
   * Sets the exitCode of a given process handler
   *
   * @param pHandler  ProcessHandler to set the exit code
   * @param pExitCode Code to set
   */
  @SuppressWarnings("SameParameterValue")
  private static void _setExitCode(@NotNull ProcessHandler pHandler, @Nullable Integer pExitCode) throws Exception
  {
    Field exitCode = ProcessHandler.class.getDeclaredField("myExitCode");
    exitCode.setAccessible(true);
    exitCode.set(pHandler, pExitCode);
  }

}
