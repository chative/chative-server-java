package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.util.SystemMapper;
import java.util.ArrayList;
import java.util.List;

public abstract class PlatformManager {

  private final Logger logger = LoggerFactory.getLogger(PlatformManager.class);
  private MemCache memCache;
  private ObjectMapper        mapper = SystemMapper.getMapper();
  @CreateSqlObject
  abstract PlatformsTable platformsTable();
  @CreateSqlObject
  abstract PlatformAppsTable platformAppsTable();
  public PlatformManager(){}

  public void store(Platform platform) {
    platformsTable().insert(platform);
    memCache.set(getPlatformKey(platform.getPid()),platform);
  }

  public void update(Platform platform) {
    platformsTable().update(platform);
    memCache.remove(getPlatformKey(platform.getPid()));
  }

  public void storePlatformApp(PlatformApp platformApp) {
    platformAppsTable().insert(platformApp);
    memCache.set(getAppKey(platformApp.getAppid()),platformApp);
    memCache.remove(getPlatformAppKey(platformApp.getPid()));
  }

  public void updatePlatformApp(PlatformApp platformApp) {
    platformAppsTable().update(platformApp);
    memCache.remove(getPlatformAppKey(platformApp.getPid()));
    memCache.remove(getAppKey(platformApp.getAppid()));
  }

  @Transaction
  public void remove(String pid) {
    platformsTable().remove(pid);
    memCache.remove(getPlatformKey(pid));
    memCache.remove(getPlatformAppKey(pid));
    List<PlatformApp> platformApps=getPlatformApps(pid);
    if(platformApps!=null){
      for(PlatformApp platformApp:platformApps){
        platformAppsTable().remove(pid,platformApp.getAppid());
        memCache.remove(getAppKey(platformApp.getAppid()));
      }
    }
  }
  @Transaction
  public void removePlatformApp(PlatformApp platformApp) {
    platformAppsTable().remove(platformApp.getPid(),platformApp.getAppid());
    memCache.remove(getPlatformAppKey(platformApp.getPid()));
    memCache.remove(getAppKey(platformApp.getAppid()));
  }

  public Platform get(String pid) {
    Platform platform = (Platform) memCache.get(getPlatformKey(pid),Platform.class);
    if (platform==null) {
      platform=platformsTable().get(pid);
      if (platform!=null) {
        memCache.set(getPlatformKey(pid),Platform.class);
      }
    }
    return platform;
  }

  public PlatformApp getPlatformApp(String appid) {
    PlatformApp platformApp = (PlatformApp) memCache.get(getAppKey(appid), PlatformApp.class);
    if (null == platformApp) {
      platformApp = platformAppsTable().getByAppid(appid);
      if(platformApp!=null){
        memCache.set(getAppKey(appid), platformApp);
      }
    }
    return platformApp;
  }

  public List<PlatformApp> getPlatformApps(String pid) {
    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, PlatformApp.class);
    List<PlatformApp> apps = (List<PlatformApp>)memCache.get(getPlatformAppKey(pid), javaType);
    if (null == apps) {
      apps = platformAppsTable().get(pid);
      if(apps!=null){
        memCache.set(getPlatformAppKey(pid), apps);
      }
    }
    return apps;
  }


  private String getPlatformKey(String pid) {
    return String.join("_", Platform.class.getSimpleName(),"Platform", String.valueOf(Platform.MEMCACHE_VERION), pid);
  }

  private String getPlatformAppKey(String pid) {
    return String.join("_", Platform.class.getSimpleName(), "PlatformApps", String.valueOf(Platform.MEMCACHE_VERION), pid);
  }
  private String getAppKey(String appId) {
    return String.join("_", PlatformApp.class.getSimpleName(), String.valueOf(PlatformsTable.MEMCACHE_VERION), appId);
  }

  public void setMemCache(MemCache memCache) {
    this.memCache = memCache;
  }

}
