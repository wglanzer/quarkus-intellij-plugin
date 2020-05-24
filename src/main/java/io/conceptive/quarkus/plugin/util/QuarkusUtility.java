package io.conceptive.quarkus.plugin.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.*;

/**
 * @author w.glanzer, 24.05.2020
 */
public class QuarkusUtility
{

  private static final String _DEBUGGER_READY_STRING = "Listening for transport dt_socket at address";
  private static final Pattern _DEBUGGER_READY_PATTERN = Pattern.compile(_DEBUGGER_READY_STRING + ".*$");

  /**
   * Checks, if pTest contains the string which signalizes, that the debugger is ready to attach
   *
   * @param pTest String to test
   * @return true, if it contains this piece of text
   */
  public static boolean containsDebugReadyString(@NotNull String pTest)
  {
    return pTest.contains(_DEBUGGER_READY_STRING);
  }

  /**
   * Returns the text that appears after the "debugger ready" string
   *
   * @param pTest String to split
   * @return the resulting text, not null
   */
  @NotNull
  public static String getTextAfterDebugReadyString(@NotNull String pTest)
  {
    Matcher matcher = _DEBUGGER_READY_PATTERN.matcher(pTest);
    if (matcher.find())
    {
      int endPos = matcher.end();
      if (endPos < pTest.length())
        return pTest.substring(endPos).trim();
      else if (endPos == pTest.length())
        return "";
    }
    return pTest;
  }

}
