package org.whispersystems.textsecuregcm.websocket;

import io.dropwizard.auth.basic.BasicCredentials;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.auth.AuthorizationHeader;
import org.whispersystems.textsecuregcm.auth.InvalidAuthorizationHeaderException;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.filter.UserAgentFilter;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.storage.MemCache;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebSocketAccountAuthenticator implements WebSocketAuthenticator<Account> {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketAccountAuthenticator.class);

  private MemCache memCache;
  private final AccountAuthenticator accountAuthenticator;

  public WebSocketAccountAuthenticator(AccountAuthenticator accountAuthenticator,MemCache memCache) {
    this.accountAuthenticator = accountAuthenticator;
    this.memCache=memCache;
  }

  @Override
  public Optional<Account> authenticate(UpgradeRequest request) throws AuthenticationException {
    try {
      Map<String, List<String>> parameters = request.getParameterMap();
      List<String> usernames = parameters.get("login");
      List<String> passwords = parameters.get("password");

      Optional<Account> accountOptional;
      BasicCredentials credentials = null;
      if (usernames == null || usernames.size() == 0 ||
              passwords == null || passwords.size() == 0) {
        final String authorization = request.getHeader("Authorization");
        accountOptional = accountAuthenticator.authenticate(authorization);
        if (!accountOptional.isPresent() && authorization != null) {
          final AuthorizationHeader authorizationHeader = AuthorizationHeader.fromFullHeader(authorization);
          credentials = new BasicCredentials(authorizationHeader.getNumber(),
                  "");
        }
      } else {
        credentials = new BasicCredentials(usernames.get(0).replace(" ", "+"),
                passwords.get(0).replace(" ", "+"));
        accountOptional = accountAuthenticator.authenticate(credentials);
      }

      if (interceptConn(accountOptional, request, credentials)) {
        String number = accountOptional.get().getNumber();
        Device device = accountOptional.get().getMasterDevice().get();
        String userAgent = request.getHeader("User-Agent");
        Device authDevice = accountOptional.get().getAuthenticatedDevice().get();
        UserAgentFilter.sendApn(memCache, device, number, device.equals(authDevice));
        logger.info(String.format("reject websocket connection，number:%s , device:%d , User-Agent:%s", number, authDevice.getId(), userAgent));
//        return null;
        throw new AuthenticationException("Your App is outdated and no longer supported. Please update it first.");
      }
      return accountOptional;
    } catch (io.dropwizard.auth.AuthenticationException e) {
      throw new AuthenticationException(e);
    } catch (InvalidAuthorizationHeaderException e) {
      logger.warn("Invalid authorization header", e);
      return Optional.empty();
    }
  }

  public boolean interceptConn( Optional<Account> accountOptional,UpgradeRequest request,BasicCredentials credentials)
          throws InvalidAuthorizationHeaderException {
    if (!accountOptional.isPresent() ){ // 无有效的账号,检查是否是删除过的账号
      if (credentials == null){
        return false;
      }
      AuthorizationHeader authorizationHeader = AuthorizationHeader.fromUserAndPassword(
              credentials.getUsername(), credentials.getPassword());
      if (accountAuthenticator.isDeleted(authorizationHeader.getNumber())) {
        logger.warn("Operation denied. This account is already unregistered. uid:{}", authorizationHeader.getNumber());
        throw new WebApplicationException(Response.status(403).entity(
                new BaseResponse(1, 10110,
                        "Operation denied. This account is already unregistered.", null)).build());
      }
      return false;
    }
    else if ( UserAgentFilter.upgradeSwitch) {
      String userAgent = request.getHeader("User-Agent");
      String number = accountOptional.get().getNumber();
      if (StringUtil.isEmpty(userAgent)) {
        memCache.sadd(UserAgentFilter.undefineVersionUsersKey, number);
        return true;
      }
      if (Pattern.matches(UserAgentFilter.uaPatternStrForIos, userAgent)) {
        return handlerUserAgent(number,userAgent,UserAgentFilter.iOSVersion,UserAgentFilter.uaPatternStrForIos,UserAgentFilter.userVersionsIOSKey,UserAgentFilter.oldVersionUsersIOSKey);
      } else if (Pattern.matches(UserAgentFilter.uaPatternStrForAndroid, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
        return handlerUserAgent(number,userAgent,UserAgentFilter.androidVersion,UserAgentFilter.uaPatternStrForAndroid,UserAgentFilter.userVersionsAndroidKey,UserAgentFilter.oldVersionUsersAndroidKey);
      } else if (Pattern.matches(UserAgentFilter.uaPatternStrForMac, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
        return handlerUserAgent(number,userAgent,UserAgentFilter.macVersion,UserAgentFilter.uaPatternStrForMac,UserAgentFilter.userVersionsMacKey,UserAgentFilter.oldVersionUsersMacKey);
      } else if (Pattern.matches(UserAgentFilter.uaPatternStrForLinux, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
        return handlerUserAgent(number,userAgent,UserAgentFilter.linuxVersion,UserAgentFilter.uaPatternStrForLinux,UserAgentFilter.userVersionsLinuxKey,UserAgentFilter.oldVersionUsersLinuxKey);
      } else {
        memCache.sadd(UserAgentFilter.undefineVersionUsersKey, number);
        return true;
      }
    }
    return false;
  }

  public boolean handlerUserAgent(String number,String userAgent,String versionLimit,String uaPatternStr,String userVersionsKey,String oldVersionUsersKey){
    if (StringUtil.isEmpty(versionLimit)) {
      return false;
    }
    Pattern pattern = Pattern.compile(uaPatternStr);
    Matcher matcher = pattern.matcher(userAgent);
    if (matcher.find()) {
      String difftVersion = matcher.group(1);
      memCache.hset(userVersionsKey, number, difftVersion);
      memCache.srem(UserAgentFilter.undefineVersionUsersKey,number);
      if (difftVersion.compareTo(versionLimit) < 0) {
        memCache.sadd(oldVersionUsersKey, number);
        return true;
      } else {
        memCache.srem(oldVersionUsersKey, number);
        return false;
      }
    }else{
      return true;
    }
  }
}
