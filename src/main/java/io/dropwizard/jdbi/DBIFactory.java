//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.dropwizard.jdbi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jdbi.InstrumentedTimingCollector;
import com.codahale.metrics.jdbi.strategies.DelegatingStatementNameStrategy;
import com.codahale.metrics.jdbi.strategies.NameStrategies;
import com.codahale.metrics.jdbi.strategies.StatementNameStrategy;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.args.GuavaOptionalArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalInstantArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalJodaTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalLocalDateArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalLocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalOffsetTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalZonedTimeArgumentFactory;
import io.dropwizard.jdbi.args.InstantArgumentFactory;
import io.dropwizard.jdbi.args.InstantMapper;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import io.dropwizard.jdbi.args.LocalDateArgumentFactory;
import io.dropwizard.jdbi.args.LocalDateMapper;
import io.dropwizard.jdbi.args.LocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.LocalDateTimeMapper;
import io.dropwizard.jdbi.args.OffsetDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OffsetDateTimeMapper;
import io.dropwizard.jdbi.args.OptionalArgumentFactory;
import io.dropwizard.jdbi.args.OptionalDoubleArgumentFactory;
import io.dropwizard.jdbi.args.OptionalDoubleMapper;
import io.dropwizard.jdbi.args.OptionalInstantArgumentFactory;
import io.dropwizard.jdbi.args.OptionalIntArgumentFactory;
import io.dropwizard.jdbi.args.OptionalIntMapper;
import io.dropwizard.jdbi.args.OptionalJodaTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLocalDateArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLongArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLongMapper;
import io.dropwizard.jdbi.args.OptionalOffsetDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalZonedDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.ZonedDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.ZonedDateTimeMapper;
import io.dropwizard.jdbi.logging.LogbackLog;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

import java.sql.SQLException;
import java.util.*;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.skife.jdbi.v2.ColonPrefixNamedParamStatementRewriter;
import org.skife.jdbi.v2.DBI;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.db.MyDBPreciseShardingAlgorithm;
import org.whispersystems.textsecuregcm.db.MyTablePreciseShardingAlgorithm;

import javax.sql.DataSource;

public class DBIFactory {
    private static final Logger LOGGER = (Logger)LoggerFactory.getLogger(DBI.class);
    private static final String RAW_SQL = MetricRegistry.name(DBI.class, new String[]{"raw-sql"});
    private static DataSource dataSource;
    public DBIFactory() {
    }

    protected Optional<TimeZone> databaseTimeZone() {
        return Optional.empty();
    }

    public void initDateSource(Environment environment, Map<String,PooledDataSourceFactory> pooledDataSourceFactoryMap) throws SQLException {
        Map<String,DataSource> managedDataSourceMap=new HashMap<>();
        for(String name:pooledDataSourceFactoryMap.keySet()){
            PooledDataSourceFactory configuration=pooledDataSourceFactoryMap.get(name);
            ManagedDataSource dataSource = configuration.build(environment.metrics(), name);
            environment.lifecycle().manage(dataSource);
            managedDataSourceMap.put(name,dataSource);
        }

        // 配置Order表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration("messages_shard","messages_shard_ds${0..2}.messages_shard${0..127}");

        // 配置分库 + 分表策略
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("conversation",new MyDBPreciseShardingAlgorithm()));
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("conversation",new MyTablePreciseShardingAlgorithm()));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
        shardingRuleConfig.setDefaultDataSourceName("messages_shard_default");

        // 获取数据源对象
         dataSource = ShardingDataSourceFactory.createDataSource(managedDataSourceMap, shardingRuleConfig, new Properties());
    }
    public DBI buildByShard(Environment environment, PooledDataSourceFactory configuration,String name) {
        DBI dbi =new DBI(dataSource);
        String validationQuery = configuration.getValidationQuery();
        environment.healthChecks().register(name, new DBIHealthCheck(environment.getHealthCheckExecutorService(), (Duration)configuration.getValidationQueryTimeout().orElseGet(() -> {
            return Duration.seconds(5L);
        }), dbi, validationQuery));
        dbi.setSQLLog(new LogbackLog(LOGGER, Level.TRACE));
        dbi.setTimingCollector(new InstrumentedTimingCollector(environment.metrics(), new DBIFactory.SanerNamingStrategy()));
        if (configuration.isAutoCommentsEnabled()) {
            dbi.setStatementRewriter(new NamePrependingStatementRewriter(new ColonPrefixNamedParamStatementRewriter()));
        }

        this.configure(dbi, configuration);
        return dbi;
    }

    public DBI build(Environment environment, PooledDataSourceFactory configuration, String name) {
        ManagedDataSource dataSource = configuration.build(environment.metrics(), name);
        return this.build(environment, configuration, dataSource, name);
    }

    public DBI build(Environment environment, PooledDataSourceFactory configuration, ManagedDataSource dataSource, String name) {
        DBI dbi = this.newInstance(dataSource);
        environment.lifecycle().manage(dataSource);
        String validationQuery = configuration.getValidationQuery();
        environment.healthChecks().register(name, new DBIHealthCheck(environment.getHealthCheckExecutorService(), (Duration)configuration.getValidationQueryTimeout().orElseGet(() -> {
            return Duration.seconds(5L);
        }), dbi, validationQuery));
        dbi.setSQLLog(new LogbackLog(LOGGER, Level.TRACE));
        dbi.setTimingCollector(new InstrumentedTimingCollector(environment.metrics(), new DBIFactory.SanerNamingStrategy()));
        if (configuration.isAutoCommentsEnabled()) {
            dbi.setStatementRewriter(new NamePrependingStatementRewriter(new ColonPrefixNamedParamStatementRewriter()));
        }

        this.configure(dbi, configuration);
        return dbi;
    }

    protected DBI newInstance(ManagedDataSource dataSource) {
        return new DBI(dataSource);
    }

    protected void configure(DBI dbi, PooledDataSourceFactory configuration) {
        String driverClazz = configuration.getDriverClass();
        dbi.registerArgumentFactory(new GuavaOptionalArgumentFactory(driverClazz));
        dbi.registerArgumentFactory(new OptionalArgumentFactory(driverClazz));
        dbi.registerArgumentFactory(new OptionalDoubleArgumentFactory());
        dbi.registerArgumentFactory(new OptionalIntArgumentFactory());
        dbi.registerArgumentFactory(new OptionalLongArgumentFactory());
        dbi.registerColumnMapper(new OptionalDoubleMapper());
        dbi.registerColumnMapper(new OptionalIntMapper());
        dbi.registerColumnMapper(new OptionalLongMapper());
        dbi.registerContainerFactory(new ImmutableListContainerFactory());
        dbi.registerContainerFactory(new ImmutableSetContainerFactory());
        dbi.registerContainerFactory(new GuavaOptionalContainerFactory());
        dbi.registerContainerFactory(new OptionalContainerFactory());
        Optional<TimeZone> timeZone = this.databaseTimeZone();
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new LocalDateArgumentFactory());
        dbi.registerArgumentFactory(new LocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new InstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OffsetDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new ZonedDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalJodaTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalLocalDateArgumentFactory());
        dbi.registerArgumentFactory(new GuavaOptionalLocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new GuavaOptionalInstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalOffsetTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalZonedTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalJodaTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalLocalDateArgumentFactory());
        dbi.registerArgumentFactory(new OptionalLocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new OptionalInstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalOffsetDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalZonedDateTimeArgumentFactory(timeZone));
        dbi.registerColumnMapper(new JodaDateTimeMapper(timeZone));
        dbi.registerColumnMapper(new LocalDateMapper());
        dbi.registerColumnMapper(new LocalDateTimeMapper());
        dbi.registerColumnMapper(new InstantMapper(timeZone));
        dbi.registerColumnMapper(new OffsetDateTimeMapper(timeZone));
        dbi.registerColumnMapper(new ZonedDateTimeMapper(timeZone));
    }

    private static class SanerNamingStrategy extends DelegatingStatementNameStrategy {
        private SanerNamingStrategy() {
            super(new StatementNameStrategy[]{NameStrategies.CHECK_EMPTY, NameStrategies.CONTEXT_CLASS, NameStrategies.CONTEXT_NAME, NameStrategies.SQL_OBJECT, (statementContext) -> {
                return DBIFactory.RAW_SQL;
            }});
        }
    }
}
