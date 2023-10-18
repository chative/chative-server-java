package org.whispersystems.textsecuregcm.internal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;

public class InternalServicePermissionService {
    private PlatformManager platformManager;
    private TeamsManagerCasbin teamsManager;

    public InternalServicePermissionService(PlatformManager platformManager,TeamsManagerCasbin teamsManager){
        this.platformManager=platformManager;
        this.teamsManager=teamsManager;
    }
    public boolean isExists(String pid,String appid){
        if(StringUtil.isEmpty(pid)||StringUtil.isEmpty(appid)) return false;
        PlatformApp platformApp=platformManager.getPlatformApp(appid);
        if(platformApp!=null&&pid.equals(platformApp.getPid())) return true;
        return false;
    }
    public boolean isHasObjectPermissionForApp(String appid,Object object){
        if (object == null || StringUtil.isEmpty(appid)) {
            return false;
        }
        JsonObject jsonObject=new Gson().toJsonTree(object).getAsJsonObject();
        if(jsonObject==null||!jsonObject.has("pid")||StringUtil.isEmpty(jsonObject.get("pid").getAsString())) return false;
        PlatformApp platformApp=platformManager.getPlatformApp(appid);
        if(platformApp==null|| StringUtil.isEmpty(platformApp.getPid())) return false;
        return platformApp.getPid().equals(jsonObject.get("pid").getAsString());
    }

    public boolean isHasAccountPermissionForTeam(Account account, String team){
        if (account == null || StringUtil.isEmpty(team)) {
            return false;
        }
        return teamsManager.isInTeam(account,team);
    }
    //public boolean isHasSameTeam(Account account, String operator){
    //    if (account == null || StringUtil.isEmpty(operator)) {
    //        return false;
    //    }
    //    return teamsManager.isFriend(account.getNumber(),operator);
    //}
}
