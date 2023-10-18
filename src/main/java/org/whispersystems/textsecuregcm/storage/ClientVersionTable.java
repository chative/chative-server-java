package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public abstract class ClientVersionTable {

    @SqlUpdate("INSERT INTO client_versions (login,number,device,ua,dft_version,os,last_login ) VALUES (:login,:number,:device,:ua,:dftVersion,:os,now()) ON CONFLICT ON CONSTRAINT pk_client_versions DO UPDATE SET ua = :ua,dft_version=:dftVersion,os=:os,last_login=now();")
    public abstract void update(
            @Bind("login") String login,
            @Bind("number") String number,
            @Bind("device") String device,
            @Bind("ua") String ua,
            @Bind("dftVersion") String dftVersion,
            @Bind("os") String os
    );

}
