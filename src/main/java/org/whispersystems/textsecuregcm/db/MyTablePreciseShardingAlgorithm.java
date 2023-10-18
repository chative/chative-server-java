package org.whispersystems.textsecuregcm.db;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class MyTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    private final Logger logger = LoggerFactory.getLogger(MyDBPreciseShardingAlgorithm.class);

    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<String> shardingValue) {

        /**
         * tableNames 对应分片库中所有分片表的集合
         * shardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片健（字段），value 为从 SQL 中解析出的分片健的值
         */
        for (String tableName : tableNames) {
            /**
             * 取模算法，分片健 % 表数量
             */
            String value = Math.floorMod(Math.floorDiv(shardingValue.getValue().hashCode(),tableNames.size()),tableNames.size()) + "";
            if (tableName.endsWith(value)) {
                logger.info("shardingValue:{},tableName:{}",shardingValue.getValue(),tableName);
                return tableName;
            }
        }
        throw new IllegalArgumentException();
    }
}