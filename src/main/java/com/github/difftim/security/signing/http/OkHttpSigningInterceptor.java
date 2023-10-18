package com.github.difftim.security.signing.http;

import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.difftim.security.signing.http.Constants.*;

public class OkHttpSigningInterceptor implements Interceptor {

    private final HttpSigner httpSigner;
    private final List<String> headersToSign;

    public OkHttpSigningInterceptor(String appid, byte[] key, List<String> headersToSign) {
        this.httpSigner = new HttpSigner(appid, key);
        this.headersToSign = headersToSign;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {

        Request request = chain.request();

        // query parameters
        Map<String, List<String>> parameters = new HashMap<>();
        for (String queryName : request.url().queryParameterNames()) {
            parameters.put(queryName, request.url().queryParameterValues(queryName));
        }

        // headers
        Map<String, String> headers = new HashMap<>();
        Headers requestHeaders = request.headers();
        for (String headerName : headersToSign) {
            String value = requestHeaders.get(headerName);
            if (null != value) {
                headers.put(headerName, value);
            }
        }
        String signedHeadersList = String.join(",", headers.keySet());

        // body
        Buffer bodyBuffer = new Buffer();
        if (null != request.body()) {
            request.body().writeTo(bodyBuffer);
        }

        // sign
        HttpSignature httpSignature = httpSigner.sign(request.method(), request.url().encodedPath(), parameters, headers, bodyBuffer.readByteArray());

        // add signature to headers
        Request newRequest = request.newBuilder()
                .addHeader(HEADER_NAME_APPID, httpSigner.getAppid())
                .addHeader(HEADER_NAME_TIMESTAMP, String.valueOf(httpSignature.getTimestamp()))
                .addHeader(HEADER_NAME_NONCE, httpSignature.getNonce())
                .addHeader(HEADER_NAME_ALGORITHM, httpSignature.getAlgorithm())
                .addHeader(HEADER_NAME_SIGNEDHEADERS, signedHeadersList)
                .addHeader(HEADER_NAME_SIGNATURE, httpSignature.getSignature())
                .build();

        Response response = chain.proceed(newRequest);

        return response;
    }
}
