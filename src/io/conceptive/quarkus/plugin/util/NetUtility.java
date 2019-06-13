package io.conceptive.quarkus.plugin.util;

import com.intellij.util.net.NetUtils;

import java.io.IOException;

/**
 * @author w.glanzer, 13.06.2019
 */
public class NetUtility
{

  /**
   * @return Returns a free port to be used by sockets
   */
  public static int _findAvailableSocketPortUnchecked()
  {
    try
    {
      return NetUtils.findAvailableSocketPort();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

}
