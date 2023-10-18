package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.github.difftim.base.respone.BaseResponse;
import com.google.gson.JsonPrimitive;
import com.okta.jwt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.WhisperServerConfigurationApollo;
import org.whispersystems.textsecuregcm.entities.AuthenticationEmailCheckRequest;
import org.whispersystems.textsecuregcm.entities.AuthenticationOktaRequest;
import org.whispersystems.textsecuregcm.entities.AuthenticationOktaResponse;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/v1/auth")
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AccountsManager accountsManager;
    private final InternalAccountManager internalAccountManager;

    private final AccessTokenVerifier oktaAccessTokenVerifier;
    private final IdTokenVerifier oktaIdTokenVerifier;
    private final WhisperServerConfigurationApollo whisperServerConfigurationApollo;
    public static String emailReg="^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";


    public AuthenticationController(AccountsManager accountsManager, InternalAccountManager internalAccountManager,WhisperServerConfigurationApollo whisperServerConfigurationApollo) {
        this.accountsManager = accountsManager;
        this.internalAccountManager = internalAccountManager;
        this.whisperServerConfigurationApollo=whisperServerConfigurationApollo;
        this.oktaAccessTokenVerifier = JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer("https://test.okta.com/oauth2/default")
                .setAudience("api://default")                   // defaults to 'api://default'
                .setConnectionTimeout(Duration.ofSeconds(5))    // defaults to 1s
                .setRetryMaxAttempts(3)                     // defaults to 2
                .setRetryMaxElapsed(Duration.ofSeconds(10)) // defaults to 10s
                .build();
        this.oktaIdTokenVerifier = JwtVerifiers.idTokenVerifierBuilder()
                .setIssuer("https://test.okta.com/oauth2/default")
                .setClientId("0oaajoq0o17tUUOE4357")
                .setConnectionTimeout(Duration.ofSeconds(5))    // defaults to 1s
                .setRetryMaxAttempts(3)                     // defaults to 2
                .setRetryMaxElapsed(Duration.ofSeconds(10)) // defaults to 10s
                .build();
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public void emailCheck(@Valid AuthenticationEmailCheckRequest emailCheckRequest) {

    }

}
