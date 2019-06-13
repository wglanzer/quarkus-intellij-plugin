package io.conceptive.quarkus.plugin.util;

import com.intellij.execution.process.*;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to forward all ProcessEvents (excluding "startNotified") to another handler
 *
 * @author w.glanzer, 13.06.2019
 */
public class ForwardProcessListener extends ProcessAdapter
{

  private final ProcessHandler target;
  private final boolean redirectText;

  public ForwardProcessListener(@NotNull ProcessHandler pTarget, boolean pRedirectText)
  {
    target = pTarget;
    redirectText = pRedirectText;
  }

  @Override
  public void processTerminated(@NotNull ProcessEvent event)
  {
    target.destroyProcess();
  }

  @Override
  public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed)
  {
    target.destroyProcess();
  }

  @Override
  public void onTextAvailable(@NotNull ProcessEvent pProcessEvent, @NotNull Key pKey)
  {
    if (redirectText)
      target.notifyTextAvailable(pProcessEvent.getText(), pKey);
  }

}
