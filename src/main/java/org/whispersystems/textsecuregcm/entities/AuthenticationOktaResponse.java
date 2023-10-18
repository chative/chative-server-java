package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationOktaResponse {

  // 0: OK
  // 1: invalid tokens
  // 2: the account is disabled
  @JsonProperty
  private int status;

  public AuthenticationOktaResponse(int status, int transferable, TransferTokens tokens, Boolean requirePin) {
    this.status = status;
    this.transferable = transferable;
    this.tokens = tokens;
    this.requirePin = requirePin;
  }

  public int getStatus() {
    return status;
  }

  @JsonProperty
  private int transferable; // 是否可迁移数据
  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private TransferTokens tokens; // 如果可transfer data，就放对应的token

  // 0: *invitationCode* is available, to register a new account
  // 1: *account* and *verificationCode* are available, to sign in an existing account.
  @JsonProperty
  private int nextStep;

  @JsonProperty
  private String invitationCode;

  public int getTransferable() {
    return transferable;
  }

  public TransferTokens getTokens() {
    return tokens;
  }

  public int getNextStep() {
    return nextStep;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public String getAccount() {
    return account;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  @JsonProperty
  private String account;

  @JsonProperty
  private String verificationCode;

  public Boolean getRequirePin() {
    return requirePin;
  }

  @JsonProperty
  private Boolean requirePin;

  public AuthenticationOktaResponse(int status, int nextStep, String invitationCode,
                                    String account, String verificationCode,Boolean requirePin) {
    this.status = status;
    this.nextStep = nextStep;
    this.invitationCode = invitationCode;
    this.account = account;
    this.verificationCode = verificationCode;
    this.requirePin = requirePin;
  }

  public static class TransferTokens {
    public String getTdtoken() {
      return tdtoken;
    }

    public String getLogintoken() {
      return logintoken;
    }

    @JsonProperty
    private String tdtoken;
    @JsonProperty
    private String logintoken;

    public TransferTokens(String tdtoken, String logintoken) {
      this.tdtoken = tdtoken;
      this.logintoken = logintoken;
    }
  }
}
