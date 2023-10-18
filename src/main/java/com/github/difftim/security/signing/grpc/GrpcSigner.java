package com.github.difftim.security.signing.grpc;

import com.github.difftim.security.signing.Signature;
import com.github.difftim.security.signing.Signer;
import com.google.protobuf.MessageLite;
import io.grpc.Metadata;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;


import java.nio.charset.StandardCharsets;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class GrpcSigner {
    final public String appid;
    final public String algorithm;
    final public byte[] key;

    final private Signer signer;

    public GrpcSigner(String appid, String algorithm, byte[] key) {
        this.appid = appid;
        this.algorithm = algorithm;
        this.key = key;

        this.signer = new Signer(appid, key);
    }

    public <T extends AbstractStub<T>> T sign2(T stub, MessageLite request) {
        Signature signature = signer.sign(request.toByteArray());

        Metadata meta=new Metadata();
        meta.put(Metadata.Key.of("appid", ASCII_STRING_MARSHALLER), appid);
        meta.put(Metadata.Key.of("timestamp", ASCII_STRING_MARSHALLER), String.valueOf(signature.getTimestamp()));
        meta.put(Metadata.Key.of("nonce", ASCII_STRING_MARSHALLER), signature.getNonce());
        meta.put(Metadata.Key.of("signature", ASCII_STRING_MARSHALLER), signature.getSignature());
        meta.put(Metadata.Key.of("version", ASCII_STRING_MARSHALLER), "2");
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(meta));
    }

    public <T extends AbstractStub<T>> T sign(T stub, Object request) {
        Signature signature = signer.sign(request.toString().getBytes(StandardCharsets.UTF_8));

        Metadata meta=new Metadata();
        meta.put(Metadata.Key.of("appid", ASCII_STRING_MARSHALLER), appid);
        meta.put(Metadata.Key.of("timestamp", ASCII_STRING_MARSHALLER), String.valueOf(signature.getTimestamp()));
        meta.put(Metadata.Key.of("nonce", ASCII_STRING_MARSHALLER), signature.getNonce());
        meta.put(Metadata.Key.of("signature", ASCII_STRING_MARSHALLER), signature.getSignature());
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(meta));
    }
}
