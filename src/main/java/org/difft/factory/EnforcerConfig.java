package org.difft.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnforcerConfig {

    @Value("${casbin.redisHost}")
    private String redisHost;

    @Value("${casbin.redisPort}")
    private int redisPort;

    @Value("${casbin.redisSsl}")
    private boolean redisSsl;

    @Value("${casbin.redisPassword}")
    private String redisPassword;

    @Value("${casbin.redisDatabase}")
    private int redisDatabase;

    @Value("${casbin.redisTopic}")
    private String redisTopic;

    @Value("${casbin.redisTimeout}")
    private int redisTimeout;

    @Value("${casbin.dbUrl}")
    private String dbUrl;

    @Value("${casbin.dbDriverClassName}")
    private String dbDriverClassName;

    @Value("${casbin.dbUsername}")
    private String dbUsername;

    @Value("${casbin.dbPassword}")
    private String dbPassword;

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public boolean isRedisSsl() {
        return redisSsl;
    }

    public void setRedisSsl(boolean redisSsl) {
        this.redisSsl = redisSsl;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public String getRedisTopic() {
        return redisTopic;
    }

    public void setRedisTopic(String redisTopic) {
        this.redisTopic = redisTopic;
    }

    public int getRedisTimeout() {
        return redisTimeout;
    }

    public void setRedisTimeout(int redisTimeout) {
        this.redisTimeout = redisTimeout;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbDriverClassName() {
        return dbDriverClassName;
    }

    public void setDbDriverClassName(String dbDriverClassName) {
        this.dbDriverClassName = dbDriverClassName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public int getRedisDatabase() {
        return redisDatabase;
    }

    public void setRedisDatabase(int redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
}
