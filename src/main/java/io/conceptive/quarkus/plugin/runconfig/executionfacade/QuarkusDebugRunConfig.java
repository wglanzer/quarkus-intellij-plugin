package io.conceptive.quarkus.plugin.runconfig.executionfacade;

import com.google.common.base.Strings;
import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.*;
import com.intellij.execution.remote.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.conceptive.quarkus.plugin.runconfig.IQuarkusRunConfigType;
import io.conceptive.quarkus.plugin.util.QuarkusUtility;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * Part II: Connect debugger to started Quarkus instance
 *
 * @author w.glanzer, 13.06.2019
 */
class QuarkusDebugRunConfig extends RemoteConfiguration implements IInternalRunConfigs.IDebugRunConfig
{

  private ProcessHandler buildProcessHandler;
  private Runnable onRestart;
  private WeakReference<RunProfileState> stateRef = null;
  private CachingProcessListener messageCache = null;

  public QuarkusDebugRunConfig(@NotNull Project project)
  {
    super(project, new RemoteConfigurationType());
    HOST = "localhost";
    USE_SOCKET_TRANSPORT = true;
    SERVER_MODE = false;
  }

  @Nullable
  @Override
  public Icon getIcon()
  {
    return IQuarkusRunConfigType.ICON;
  }

  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
  {
    // Rerun is not supported - it has to be initialized again
    if (stateRef != null && stateRef.get() != null)
    {
      if (onRestart != null)
        onRestart.run();
      return null;
    }

    // prepare environment
    _preventSettingsFromBeingSavedToDisk(env);

    // those settings have to be set, so that the runner is able to run correctly
    GenericDebuggerRunnerSettings debuggerSettings = (GenericDebuggerRunnerSettings) env.getRunnerSettings();
    if (debuggerSettings != null)
    {
      debuggerSettings.LOCAL = false;
      debuggerSettings.setDebugPort(USE_SOCKET_TRANSPORT ? PORT : SHMEM_ADDRESS);
      debuggerSettings.setTransport(USE_SOCKET_TRANSPORT ? 0 : 1);
    }

    QuarkusDebugState state = new QuarkusDebugState(getProject(), createRemoteConnection(), AUTO_RESTART, buildProcessHandler, messageCache != null ? messageCache : ArrayList::new);
    stateRef = new WeakReference<>(state);
    return state;
  }

  @Override
  public void reinit(@Nullable ProcessHandler pBuildProcessHandler, int pPort, @Nullable Runnable pOnRestart)
  {
    buildProcessHandler = pBuildProcessHandler;
    onRestart = pOnRestart;
    PORT = String.valueOf(pPort);
    stateRef = null;
  }

  @Override
  public void enableMessageCache(@NotNull ProcessHandler pBuildProcessHandler)
  {
    if (messageCache != null)
      messageCache.invalidate();
    messageCache = new CachingProcessListener(pBuildProcessHandler);
    pBuildProcessHandler.addProcessListener(messageCache);
  }

  /**
   * If something gets set in the execution environments GenericDebuggerRunnerSettings, it will persisted to disk.
   * This is normally okay, because the user did those changes - here it is definitely not okay, because the user has changed nothing!
   * Especially the behaviour for "Random Ports" is ... bad, if it gets changed every run
   *
   * @param pEnvironment Environment to be prevented
   */
  private void _preventSettingsFromBeingSavedToDisk(@NotNull ExecutionEnvironment pEnvironment)
  {
    try
    {
      Field field = ExecutionEnvironment.class.getDeclaredField("myRunnerSettings");
      field.setAccessible(true);
      field.set(pEnvironment, new GenericDebuggerRunnerSettings()
      {
        @Override
        public void readExternal(Element element)
        {
          // nothing, do not change here
        }

        @Override
        public void writeExternal(Element element)
        {
          // nothing, to prevent writing
        }
      });
    }
    catch (Exception e)
    {
      throw new RuntimeException("_preventSettingsFromBeingSavedToDisk does not work anymore", e);
    }
  }

  /**
   * Listener to cache all messages that arrive between hiding build config and showing debugging config
   */
  protected static class CachingProcessListener extends ProcessAdapter implements Supplier<List<Map.Entry<String, Key<?>>>>
  {
    private final List<Map.Entry<String, Key<?>>> cache = new ArrayList<>();
    private ProcessHandler handler;

    public CachingProcessListener(@NotNull ProcessHandler pHandler)
    {
      handler = pHandler;
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType)
    {
      synchronized (cache)
      {
        if (handler != null)
        {
          String text = event.getText();
          if (text != null && cache.isEmpty())
          {
            // remove only first line, because the user already knows the port
            text = QuarkusUtility.getTextAfterDebugReadyString(text);

            // remove "first" linebreak
            if (StringUtils.isBlank(text))
              text = null;
          }

          if (!Strings.isNullOrEmpty(text))
            //noinspection unchecked,rawtypes
            cache.add(new AbstractMap.SimpleImmutableEntry<>(text, outputType));
        }
      }
    }

    @NotNull
    public List<Map.Entry<String, Key<?>>> get()
    {
      synchronized (cache)
      {
        List<Map.Entry<String, Key<?>>> result = new ArrayList<>(cache);
        invalidate();
        return result;
      }
    }

    public void invalidate()
    {
      synchronized (cache)
      {
        if (handler != null)
        {
          handler.removeProcessListener(this);
          handler = null;
        }
        cache.clear();
      }
    }
  }

}
