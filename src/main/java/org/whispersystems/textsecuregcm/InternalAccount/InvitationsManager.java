package org.whispersystems.textsecuregcm.InternalAccount;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.InternalAccountsInvitationRow;
import org.whispersystems.textsecuregcm.storage.InternalAccountsInvitationTable;
import org.whispersystems.textsecuregcm.util.RandomString;

import java.util.List;
import java.util.Optional;

public class InvitationsManager {
  private final Logger logger = LoggerFactory.getLogger(InvitationsManager.class);
  private final RandomString mRandomString = new RandomString(32);

  public final AccountsManager accountsManager;
  public final InternalAccountsInvitationTable internalAccountsInvitationTable;

  public InvitationsManager(AccountsManager accountsManager, InternalAccountsInvitationTable internalAccountsInvitationTable) {
    this.accountsManager = accountsManager;
    this.internalAccountsInvitationTable = internalAccountsInvitationTable;
  }

  //public String generateInvitationCode(Optional<String> number, Optional<String> name, Optional<String> email, Optional<List<String>> teams, Optional<String> invitor, Optional<String> okta_id, Optional<String> okta_org) {
  //
  //  while (true) {
  //    String code = mRandomString.nextString();
  //    List<InternalAccountsInvitationRow> il = internalAccountsInvitationTable.get(code);
  //    if (0 != il.size()) {
  //      continue;
  //    }
  //
  //    internalAccountsInvitationTable.insert(
  //            code,
  //            invitor.isPresent() ? invitor.get() : "",
  //            System.currentTimeMillis(),
  //            0,
  //            number.isPresent() ? number.get() : "",
  //            name.isPresent() ? name.get() : "",
  //            teams.isPresent() ? String.join(",", teams.get()) : "",
  //            email.isPresent() ? email.get() : "",
  //            "",
  //            okta_id.isPresent() ? okta_id.get() : "",
  //            okta_org.isPresent() ? okta_org.get() : ""
  //    );
  //    return code;
  //  }
  //}

  public List<InternalAccountsInvitationRow> getInvitationList(int offset, int limit, String email, String name, String account, String code, String inviter) {
    return internalAccountsInvitationTable.getInvitationList(offset, limit, email, name, account, code, inviter);
  }

  public long getInvitationTotal(String email, String name, String account, String code, String inviter){
    return internalAccountsInvitationTable.getInvitationTotal(email, name, account, code, inviter);
  }

  public void update(String code, String orgs, String inviter){
    internalAccountsInvitationTable.update(code, orgs, inviter);
  }

  public void updateByEmail(long timestatmp, String email, String code){
    internalAccountsInvitationTable.updateByEmail(timestatmp, email, code);
  }

  public List<InternalAccountsInvitationRow> get(String code){
    return internalAccountsInvitationTable.get(code);
  }
}
