package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class InteractsTable {
    public static final int LIMIT=3;
    public enum TYPE {
        THUMBS_UP(0);
        private int code;
        TYPE(int code){
            this.code=code;
        }
        public int getCode(){
            return code;
        }

        public static TYPE fromCode(int n) {
            for(TYPE type: TYPE.values()){
                if(type.getCode()==n){
                    return type;
                }
            }
            return null;
        }
    }

    @SqlQuery("INSERT INTO interacts (number,source,last_update_time,type,comment )" +
            " VALUES (:number,:source,:last_update_time,:type,:comment) ON CONFLICT (number,source)  DO UPDATE SET last_update_time=:last_update_time returning xmax")
    public abstract long insert(@InteractBinder Interact interact);


    @Mapper(InteractsMapper.class)
    @SqlQuery("select * from  interacts  where number=:number order by last_update_time desc limit " + LIMIT+"")
    public abstract List<Interact> queryByNumber(@Bind("number") String number);

    @SqlQuery("select count(1) from  interacts  where number=:number ")
    public abstract long queryCountByNumber(@Bind("number") String number);


    @BindingAnnotation(InteractsTable.InteractBinder.AccountBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface InteractBinder {
        public static class AccountBinderFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<InteractsTable.InteractBinder ,Interact>() {
                    @Override
                    public void bind(SQLStatement<?> sql,
                                     InteractsTable.InteractBinder interactBinder,
                                     Interact interact)
                    {
                        sql.bind("number", interact.getNumber());
                        sql.bind("source",interact.getSource());
                        sql.bind("last_update_time",interact.getLastUpdateTime());
                        sql.bind("type", interact.getType());
                        sql.bind("comment", interact.getComment());
                    }
                };
            }
        }
    }

    public static class InteractsMapper implements ResultSetMapper<Interact> {
        @Override
        public Interact map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new Interact(
                    resultSet.getLong("id"),
                    resultSet.getString("number"),
                    resultSet.getString("source"),
                    resultSet.getLong("last_update_time"),
                    resultSet.getInt("type"),
                    resultSet.getString("comment"));
        }
    }
}
