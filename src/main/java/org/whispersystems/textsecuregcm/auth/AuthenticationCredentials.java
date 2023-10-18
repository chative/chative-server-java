/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.auth;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AuthenticationCredentials {

  private final Logger logger = LoggerFactory.getLogger(AuthenticationCredentials.class);

  private final String hashedAuthenticationToken;
  private final String salt;

  public AuthenticationCredentials(String hashedAuthenticationToken, String salt) {
    this.hashedAuthenticationToken = hashedAuthenticationToken;
    this.salt                      = salt;
  }

  public AuthenticationCredentials(String authenticationToken) {
    this.salt                      = Math.abs(new SecureRandom().nextInt()) + "";
    this.hashedAuthenticationToken = getHashedValue(salt, authenticationToken);
  }

  public String getHashedAuthenticationToken() {
    return hashedAuthenticationToken;
  }

  public String getSalt() {
    return salt;
  }

  public boolean verify(String authenticationToken) {
    String theirValue = getHashedValue(salt, authenticationToken);

    boolean ok = theirValue.equals(this.hashedAuthenticationToken);
    if (!ok) {
      logger.warn("AuthenticationCredentials verify failed, salt:{},hashedAuthenticationToken:{}, in paramHash:{}  ",
              salt, hashedAuthenticationToken, theirValue);
    }
    return ok;
  }

  private static String getHashedValue(String salt, String token) {
    try {
      return new String(Hex.encodeHex(MessageDigest.getInstance("SHA1").digest((salt + token).getBytes("UTF-8"))));
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      throw new AssertionError(e);
    }
  }

  public static void main(String[] args) {
    AuthenticationCredentials authenticationCredentials=new AuthenticationCredentials("cd567bd1295ee8fd601407328b39ec4807c450f6","900954344");
    System.out.println( authenticationCredentials.verify("a7a6023c52bf054a015a7941bfbec992"));
    String hashedValue = AuthenticationCredentials.getHashedValue("+a4E75xjRZny3OulZg/ZaXgb", "1691528824");
    System.out.println(hashedValue);
    System.out.println("2675ec2bd7d39e846b5f757ad0eed8549263eca4");
  }

}
