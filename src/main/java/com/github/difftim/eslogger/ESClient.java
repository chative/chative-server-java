package com.github.difftim.eslogger;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.*;

import java.util.concurrent.atomic.AtomicInteger;

public class ESClient {
    public void sendLog(IndexRequest indexRequest ){
        mHighClient.indexAsync(indexRequest,COMMON_OPTIONS, mActionListener);
    }

    private final AtomicInteger mSuccessCount = new AtomicInteger(0);
    private final AtomicInteger mFailureCount = new AtomicInteger(0);
    private RestHighLevelClient mHighClient;
    private final ActionListener<IndexResponse> mActionListener = new ActionListener<IndexResponse>() {
        @Override
        public void onResponse(IndexResponse indexResponse) {
            mSuccessCount.addAndGet(1);
        }

        @Override
        public void onFailure(Exception e) {
            System.out.print(e);
            mFailureCount.addAndGet(1);
        }
    };

    private static final ESClient instance = new ESClient();
    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // 默认缓存限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    private ESClient() {
    }

    public static synchronized void initClient(String userName, String password, String endpoint) {
        if (instance.mHighClient != null) return;

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));
        RestClientBuilder builder = RestClient.builder(HttpHost.create(endpoint))
                .setHttpClientConfigCallback(httpClientBuilder ->{
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        return httpClientBuilder;
                });
        instance.mHighClient = new RestHighLevelClient(builder);

    }

    public static ESClient getInstance() {
        return instance;
    }
}  
