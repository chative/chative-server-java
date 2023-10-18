package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.LatestVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.whispersystems.textsecuregcm.storage.MemCache;

@Path("/v1/version")
public class VersionController {

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final MemCache memCache;

    public VersionController(MemCache memCache) {
        this.memCache = memCache;
    }

    @Timed
    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public LatestVersion getLatestVersion() {
        String latest;
        String description;
        String apkurl;
        String sha256sum;
        latest = memCache.get("config_update_android_version");
        description = memCache.get("config_update_android_description");
        apkurl = memCache.get("config_update_android_apkurl");
        sha256sum = memCache.get("config_update_android_sha256sum");

        if (null == latest || null == description || null == apkurl || null == sha256sum) {
            logger.error("update info error");
            throw new WebApplicationException(Response.status(500).build());
        }

        return new LatestVersion(latest, description, apkurl, sha256sum);
    }

}
