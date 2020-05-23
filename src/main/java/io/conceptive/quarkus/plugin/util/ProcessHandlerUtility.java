package io.conceptive.quarkus.plugin.util;

import com.intellij.build.process.BuildProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
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
  public static void detachProcessSilently(@NotNull ProcessHandler pHandler)
  {
    try
    {
      Field state = ProcessHandler.class.getDeclaredField("myState");
      state.setAccessible(true);
      AtomicReference oldStateRef = (AtomicReference) state.get(pHandler);
      Object oldState = oldStateRef.get();

      if (pHandler instanceof BuildProcessHandler)
        //noinspection UnstableApiUsage
        ((BuildProcessHandler) pHandler).forceProcessDetach();
      else
      {
        Method notifyProcessDetached = ProcessHandler.class.getDeclaredMethod("notifyProcessDetached");
        notifyProcessDetached.setAccessible(true);
        notifyProcessDetached.invoke(pHandler);
      }

      // reset state, because it is "not really dead"
      oldStateRef.set(oldState);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to detach process handler", e);
    }
  }

}
