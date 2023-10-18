package org.whispersystems.textsecuregcm.internal;

import com.google.common.io.BaseEncoding;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.whispersystems.dropwizard.simpleauth.Authenticator;
import org.whispersystems.textsecuregcm.storage.Account;

public class UserAuthServerInsterceptor<C, P> implements ServerInterceptor {
    final private Logger logger = LoggerFactory.getLogger(UserAuthServerInsterceptor.class);
    private String realm = "realm";
    private String prefix = "Basic";
    protected Authenticator<C, P> authenticator;
    private static final String AUTH_CONTEXT = "authContext";
    private static final Context.Key<Object> AUTH_CONTEXT_KEY = Context.key(AUTH_CONTEXT);

    public UserAuthServerInsterceptor(Authenticator<C, P> authenticator) {
        this.authenticator=authenticator;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        if(!call.getMethodDescriptor().getBareMethodName().equals("createToken")){
            return next.startCall(call, headers);
        }
        //获取客户端参数
        Metadata.Key<String> authorization = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        String header = headers.get(authorization);
        try {
            if (header != null) {
                int space = header.indexOf(32);
                if (space > 0) {
                    String method = header.substring(0, space);
                    if (this.prefix.equalsIgnoreCase(method)) {
                        String decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
                        int i = decoded.indexOf(58);
                        if (i > 0) {
                            String username = decoded.substring(0, i);
                            String password = decoded.substring(i + 1);
                            BasicCredentials credentials = new BasicCredentials(username, password);

                            try {
                                Optional<P> principal = this.authenticator.authenticate((C) credentials);
                                if (principal.isPresent()) {
                                    Context context = Context
                                            .current()
                                            .withValue(AUTH_CONTEXT_KEY, principal.get());
                                    return Contexts.interceptCall(context, call, headers, next);
                                }
                            } catch (AuthenticationException var11) {
                                logger.warn("Error authenticating credentials", var11);
                                call.close(Status.UNAUTHENTICATED.withDescription("auth failed"), headers);
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException var12) {
            logger.warn("Error decoding credentials", var12);
            call.close(Status.UNAUTHENTICATED.withDescription("auth failed"), headers);
        }
        call.close(Status.UNAUTHENTICATED.withDescription("auth failed"), headers);
        return null;
        // throw new WebApplicationException(this.unauthorizedHandler.buildResponse(this.prefix, this.realm));
    }
    /**
     * To be used from services
     * @return
     */
    public static Account getCurrentContextAccount() {
        return AUTH_CONTEXT_KEY.get()==null?null:(Account) AUTH_CONTEXT_KEY.get();
    }
}
