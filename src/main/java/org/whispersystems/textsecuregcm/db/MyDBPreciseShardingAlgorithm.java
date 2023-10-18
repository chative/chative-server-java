package org.whispersystems.textsecuregcm.db;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class MyDBPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    private final Logger logger = LoggerFactory.getLogger(MyDBPreciseShardingAlgorithm.class);

    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<String> shardingValue) {

        /**
         * databaseNames 所有分片库的集合
         * shardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片健（字段），value 为从 SQL 中解析出的分片健的值
         */
        for (String databaseName : databaseNames) {
            String value = Math.floorMod(shardingValue.getValue().hashCode(),databaseNames.size())+"";
            if (databaseName.endsWith(value)) {
                logger.info("shardingValue:{},databaseName:{}",shardingValue.getValue(),databaseName);
                return databaseName;
            }
        }
        throw new IllegalArgumentException();
    }
}