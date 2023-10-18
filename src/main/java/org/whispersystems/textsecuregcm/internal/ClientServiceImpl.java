package org.whispersystems.textsecuregcm.internal;


import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.internal.clients.*;
import org.whispersystems.textsecuregcm.internal.common.Empty;
import org.whispersystems.textsecuregcm.storage.ClientInfoRow;
import org.whispersystems.textsecuregcm.storage.ClientVersion;
import org.whispersystems.textsecuregcm.storage.ClientVersionRow;

import java.util.List;

public class ClientServiceImpl extends ClientsServiceGrpc.ClientsServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    public static void setClientVersion(ClientVersion clientVersion) {
        ClientServiceImpl.clientVersion = clientVersion;
    }

    private static ClientVersion clientVersion;

    @Override
    public void getVersionInfo(Empty request, StreamObserver<ClientVersionResponse> responseObserver) {
        ClientVersionResponse.Builder resBuilder = ClientVersionResponse.newBuilder();

        //List<ClientVersionRow> macOSList = clientVersion.aggVersionCnt("macOS");
        //for (ClientVersionRow row : macOSList) {
        //    if (row.getDft_version() == null ) continue;
        //    ClientVersionResponse.ClientVersionCnt.Builder builder = ClientVersionResponse.ClientVersionCnt.newBuilder();
        //    resBuilder.addMacOSVersions(builder.setVersion(row.getDft_version()).setCnt(row.getCount()).build());
        //}
        //
        //List<ClientVersionRow> iOSList = clientVersion.aggVersionCnt("iOS");
        //for (ClientVersionRow row : iOSList) {
        //    if (row.getDft_version() == null ) continue;
        //    ClientVersionResponse.ClientVersionCnt.Builder builder = ClientVersionResponse.ClientVersionCnt.newBuilder();
        //    resBuilder.addIOSVersions(builder.setVersion(row.getDft_version()).setCnt(row.getCount()).build());
        //}
        //

        responseObserver.onNext(resBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getVersionInfoByVer(ClientVersionRequest request, StreamObserver<ClientVersionResponse> responseObserver) {
        ClientVersionResponse.Builder resBuilder = ClientVersionResponse.newBuilder();
        final List<ClientVersionRequest.OSVersion> osVersionsList = request.getOsVersionsList();
        for ( ClientVersionRequest.OSVersion osVersion : osVersionsList ) {
            List<ClientVersionRow> versionRows = clientVersion.aggVersionCnt(osVersion.getOs(),osVersion.getMinVersion());
            final ClientVersionResponse.ClientVersionCntWithOS.Builder versionCntBuilder =
                    ClientVersionResponse.ClientVersionCntWithOS.newBuilder();
            versionCntBuilder.setOs(osVersion.getOs());
            logger.info("getVersionInfoByVer os:{}, versionRows size:{}" , osVersion.getOs(),versionRows.size());
            for (ClientVersionRow row : versionRows) {
                if (row.getDft_version() == null) {
                    logger.warn("getVersionInfoByVer os:{}, versionRows size:{}, row.getDft_version() == null" , osVersion.getOs(),versionRows.size());
                    continue;
                }
                ClientVersionResponse.ClientVersionCnt.Builder builder = ClientVersionResponse.ClientVersionCnt.newBuilder();
                versionCntBuilder.addVersions(builder.setVersion(row.getDft_version()).setCnt(row.getCount()).build());
            }
            resBuilder.addOsVersions(versionCntBuilder.build());
        }

        responseObserver.onNext(resBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getVersionInfoByUid(ClientQueryRequest request, StreamObserver<ClientInfoResponse> responseObserver) {
        final ClientInfoRow clientInfo = clientVersion.getClientInfo(request.getUid(), request.getDeviceId());
        if (clientInfo == null) {
            responseObserver.onNext(ClientInfoResponse.newBuilder().
                    setOs("").setVersion("").build());
            return;
        } else {
            responseObserver.onNext(ClientInfoResponse.newBuilder().
                    setVersion(clientInfo.getDftVersion()).setOs(clientInfo.getOs()).build());
        }

        responseObserver.onCompleted();
    }


}
