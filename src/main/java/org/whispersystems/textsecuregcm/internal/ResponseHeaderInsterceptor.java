package org.whispersystems.textsecuregcm.internal;


import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.ResponseHeader;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;

import java.util.Locale;

public class ResponseHeaderInsterceptor implements ServerInterceptor {
    final private Logger logger = LoggerFactory.getLogger(ResponseHeaderInsterceptor.class);


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        CustomServerCall<ReqT, RespT> customServerCall = new CustomServerCall<>(call);
        ServerCall.Listener<ReqT> listener = next.startCall(customServerCall, headers);
        return listener;
    }

    class CustomServerCall<ReqT, RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {
        private Metadata headers=null;
        protected CustomServerCall(ServerCall<ReqT, RespT> delegate) {
            super(delegate);
        }
        public void sendMessage(RespT message) {
            if(message instanceof  BaseResponse) {
                BaseResponse baseResponse = message != null ? (BaseResponse) message : null;
                if (baseResponse != null) {
                    headers.put(Metadata.Key.of(ResponseHeader.STATUS.name().toLowerCase(Locale.ROOT), Metadata.ASCII_STRING_MARSHALLER), baseResponse.getStatus() + "");
                    headers.put(Metadata.Key.of(ResponseHeader.REASON.name().toLowerCase(Locale.ROOT), Metadata.ASCII_STRING_MARSHALLER), baseResponse.getReason() + "");

                }
            }
            super.sendHeaders(headers);
            this.delegate().sendMessage(message);
        }
        // ...
        @Override
        public void sendHeaders(Metadata headers) {
            //因ServerCallStreamObserverImpl.onNext方法中，先调用sendHeaders，此时RespT message 还是空，无法从其中获取信息，
            // 故此时只是将headers存下来，在sendMessage时再sendHeaders
            //headers.put(Metadata.Key.of("adfa",Metadata.ASCII_STRING_MARSHALLER),"bbb");
            this.headers=headers;
        }
    }

}
