package com.github.difftim.security.signing.grpc;

import com.github.difftim.security.signing.SignatureVerifier;
import com.google.protobuf.MessageLite;
import io.grpc.*;
import io.netty.util.internal.StringUtil;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class GrpcSignatureVerifierInterceptor  implements io.grpc.ServerInterceptor {

    final private SignatureVerifier signatureVerifier;

    public GrpcSignatureVerifierInterceptor(SignatureVerifier.KeyStorage keyStorage) {
        this.signatureVerifier = new SignatureVerifier(keyStorage);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onMessage(ReqT req) {
                Status status = Status.OK;
                //noinspection ConstantConditions
                do {
                    // get parameters relate to signing
                    String appid = headers.get(Metadata.Key.of("appid", ASCII_STRING_MARSHALLER));
                    String timestamp = headers.get(Metadata.Key.of("timestamp", ASCII_STRING_MARSHALLER));
                    String nonce = headers.get(Metadata.Key.of("nonce", ASCII_STRING_MARSHALLER));
                    String signature = headers.get(Metadata.Key.of("signature", ASCII_STRING_MARSHALLER));
                    if (StringUtil.isNullOrEmpty(appid) || StringUtil.isNullOrEmpty(timestamp) || StringUtil.isNullOrEmpty(nonce) || StringUtil.isNullOrEmpty(signature)) {
                        status = Status.UNAUTHENTICATED.withDescription("appid, timestamp, nonce, algorithm or signature parameters are missing");
                        break;
                    }

                    // get source IP
                    String sourceIP = getSourceIP(call, headers);
                    if (null == sourceIP) {
                        status = Status.UNAUTHENTICATED.withDescription("unknown source IP");
                        break;
                    }

                    // verify
                    try {
                        byte[] reqBody;
                        final String version = headers.get(Metadata.Key.of("version", ASCII_STRING_MARSHALLER));
                        if (version != null && version.equals("2")) {
                            reqBody = ((MessageLite) req).toByteArray();
                        } else {
                            reqBody = req.toString().getBytes(StandardCharsets.UTF_8);//((String) req).getBytes(StandardCharsets.UTF_8);
                        }
                        signatureVerifier.verify(appid, Long.parseLong(timestamp), nonce, signature, reqBody, sourceIP);
                    } catch (SignatureVerifier.InvalidAppIDException e) {
                        status = Status.UNAUTHENTICATED.withDescription("invalid appid");
                    } catch (SignatureVerifier.ExpiredException e) {
                        status = Status.UNAUTHENTICATED.withDescription("signature has expired");
                    } catch (SignatureVerifier.SourceIPNotAllowedException e) {
                        status = Status.UNAUTHENTICATED.withDescription("source IP is not allowed");
                    } catch (SignatureVerifier.InvalidSignatureException e) {
                        status = Status.UNAUTHENTICATED.withDescription("invalid signature");
                    } catch (SignatureVerifier.ReplayRequestException e) {
                        status = Status.UNAUTHENTICATED.withDescription("replay request");
                    } catch (SignatureVerifier.ServerErrorException e) {
                        status = Status.INTERNAL.withDescription("unknown server error occurred during verifying signature");
                    }
                } while (false);

                if (status != Status.OK) {
                    call.close(status,new Metadata());
//                    throw new StatusRuntimeException(status);
                }else {
                    super.onMessage(req);
                }
            }
        };
    }

    private <ReqT, RespT> String getSourceIP(ServerCall<ReqT, RespT> call, Metadata headers) {
        String sourceIP = headers.get(Metadata.Key.of("X-Forward-For", ASCII_STRING_MARSHALLER));
        if (null != sourceIP) {
            sourceIP = sourceIP.split(",")[0].trim();
        } else {
            SocketAddress address = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
            if (null != address) {
                sourceIP = address.toString();
                sourceIP = sourceIP.substring(1, sourceIP.indexOf(":"));
            }
        }

        return sourceIP;
    }
}
