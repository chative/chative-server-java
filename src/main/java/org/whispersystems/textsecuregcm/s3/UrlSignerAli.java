package org.whispersystems.textsecuregcm.s3;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.whispersystems.textsecuregcm.configuration.AttachmentsConfiguration;

import java.net.URL;
import java.util.Date;

public class UrlSignerAli {

    private final String accessKeyId;
    private final String accessKeySecret;

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    private String bucket;

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    private String endpoint;

    public UrlSignerAli(UrlSignerAli signer) {
        this.endpoint = signer.endpoint;
        this.accessKeyId = signer.accessKeyId;
        this.accessKeySecret = signer.accessKeySecret;
        this.bucket = signer.bucket;
    }

    public UrlSignerAli(AttachmentsConfiguration config) {
        this.endpoint = config.getEndpoint();
        this.accessKeyId = config.getAccessKey();
        this.accessKeySecret = config.getAccessSecret();
        this.bucket = config.getBucket();
    }

    public URL getPreSignedUrl(long attachmentId, HttpMethod method) {
        OSSClient client = null;
        try {
            client = new OSSClient(
                    endpoint,
                    accessKeyId,
                    accessKeySecret
            );
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(
                    bucket,
                    "" + attachmentId,
                    method);
            req.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60 * 24));
            URL url = client.generatePresignedUrl(req);
            return url;
        } finally {
            if (null != client) {
                client.shutdown();
            }
        }
    }

    public OSSObject download(String attachmentId){
        OSSClient client = null;
        try {
            client = new OSSClient(
                    endpoint,
                    accessKeyId,
                    accessKeySecret
            );

            OSSObject object = client.getObject(bucket, attachmentId);
            return object;
        } catch (Exception e){
            return null;
        }
    }
}
