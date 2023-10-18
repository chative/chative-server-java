package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntity;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityForKafka;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityForRemind;
import org.whispersystems.textsecuregcm.util.Pair;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class MessagesTest {

  public static final int RESULT_SET_CHUNK_SIZE = 100;

  private static final String ID                 = "id";
  private static final String TYPE               = "type";
  private static final String RELAY              = "relay";
  private static final String TIMESTAMP          = "timestamp";
  private static final String SOURCE             = "source";
  private static final String SOURCE_DEVICE      = "source_device";
  private static final String DESTINATION        = "destination";
  private static final String DESTINATION_DEVICE = "destination_device";
  private static final String MESSAGE            = "message";
  private static final String CONTENT            = "content";
  private static final String NOTIFY       = "notify";
  private static final String RECEIVE_TYPE       = "receive_type";


  @SqlUpdate("INSERT INTO messages_test (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY + ") " +
           "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify)")
  @GetGeneratedKeys(columnName="id")
  public abstract long store(@MessageBinder Envelope message,
                     @Bind("destination") String destination,
                     @Bind("destination_device") long destinationDevice,
                     @Bind("notify") boolean notify);

  @SqlUpdate("INSERT INTO messages_test (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY +", " + RECEIVE_TYPE + ") " +
          "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify, :receive_type) ")
  abstract void store(@MessageBinder Envelope message,
                     @Bind("destination") String destination,
                     @Bind("destination_device") long destinationDevice,
                     @Bind("notify") boolean notify, @Bind("receive_type") int receiveType);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_test WHERE " + RECEIVE_TYPE + " = 1   ORDER BY " + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntity> loadForKafKa();

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_test WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device  ORDER BY " + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  public abstract List<OutgoingMessageEntity> load(@Bind("destination")        String destination,
                                            @Bind("destination_device") long destinationDevice);

  @Mapper(MessageMapperForRemind.class)
  @SqlQuery("SELECT SOURCE,\n" +
          "\tDESTINATION\n" +
          "FROM\n" +
          "\t(SELECT *,\n" +
          "\t\t\tROW_NUMBER() OVER (PARTITION BY DESTINATION) AS ROW_ID\n" +
          "\t\tFROM\n" +
          "\t\t\t(SELECT SOURCE,\n" +
          "\t\t\t\t\tDESTINATION\n" +
          "\t\t\t\tFROM messages_test\n" +
          "\t\t\t\tWHERE TYPE != 6\n" +
          "\t\t\t\t\tAND TIMESTAMP >= :begin \n" +
          "\t\t\t\t\tAND TIMESTAMP <= :end \n" +
          "\t\t\t\t\tAND SOURCE != DESTINATION\n" +
          "\t\t\t\t\tAND notify = true\n" +
          "\t\t\t\t\tand length(SOURCE)=12\n" +
          "\t\t\t\tGROUP BY SOURCE,\n" +
          "\t\t\t\t\tDESTINATION) TT) T\n" +
          "WHERE T.ROW_ID <= 5\n" +
          "LIMIT "+ RESULT_SET_CHUNK_SIZE +" offset :offset " )
  abstract List<OutgoingMessageEntityForRemind> loadByTimeRange( @Bind("begin") long begin, @Bind("end") long end, @Bind("offset") long offset);

  @Mapper(MessageMapper.class)
  @SqlQuery("DELETE FROM messages_test WHERE " + ID + " IN (SELECT " + ID + " FROM messages_test WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device AND " + SOURCE + " = :source AND " + TIMESTAMP + " = :timestamp ORDER BY " + ID + " LIMIT 1) RETURNING *")
  abstract OutgoingMessageEntity remove(@Bind("destination")        String destination,
                                        @Bind("destination_device") long destinationDevice,
                                        @Bind("source")             String source,
                                        @Bind("timestamp")          long timestamp);

  @Mapper(DestinationMapper.class)
  @SqlQuery("SELECT DISTINCT ON (destination, destination_device) destination, destination_device FROM messages_test WHERE timestamp > :timestamp ORDER BY destination, destination_device OFFSET :offset LIMIT :limit")
  public abstract List<Pair<String, Integer>> getPendingDestinations(@Bind("timestamp") long sinceTimestamp, @Bind("offset") int offset, @Bind("limit") int limit);

  @Mapper(MessageMapper.class)
  @SqlUpdate("DELETE FROM messages_test WHERE " + ID + " = :id AND " + DESTINATION + " = :destination")
  abstract void remove(@Bind("destination") String destination, @Bind("id") long id);

  @SqlUpdate("DELETE FROM messages_test WHERE " + DESTINATION + " = :destination")
  abstract void clear(@Bind("destination") String destination);

  @SqlUpdate("DELETE FROM messages_test WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device")
  abstract void clear(@Bind("destination") String destination, @Bind("destination_device") long destinationDevice);

  @SqlUpdate("DELETE FROM messages_test WHERE " + TIMESTAMP + " < :timestamp")
  public abstract void removeOld(@Bind("timestamp") long timestamp);

  @SqlUpdate("VACUUM messages_test")
  public abstract void vacuum();

  public static class DestinationMapper implements ResultSetMapper<Pair<String, Integer>> {

    @Override
    public Pair<String, Integer> map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new Pair<>(resultSet.getString(DESTINATION), resultSet.getInt(DESTINATION_DEVICE));
    }
  }

  public static class MessageMapper implements ResultSetMapper<OutgoingMessageEntity> {
    @Override
    public OutgoingMessageEntity map(int i, ResultSet resultSet, StatementContext statementContext)
        throws SQLException
    {

      int    type          = resultSet.getInt(TYPE);
      byte[] legacyMessage = resultSet.getBytes(MESSAGE);
      long   destination_device       = resultSet.getLong(DESTINATION_DEVICE);
      long   receiveType       = resultSet.getLong(RECEIVE_TYPE);

      if (type == Envelope.Type.RECEIPT_VALUE && legacyMessage == null) {
        /// XXX - REMOVE AFTER 10/01/15
        legacyMessage = new byte[0];
      }
      if(receiveType==1){
        return new OutgoingMessageEntityForKafka(resultSet.getLong(ID),
                false,
                type,
                resultSet.getString(RELAY),
                resultSet.getLong(TIMESTAMP),
                resultSet.getString(SOURCE),
                resultSet.getInt(SOURCE_DEVICE),
                legacyMessage,
                resultSet.getBytes(CONTENT),
                resultSet.getBoolean(NOTIFY),
                resultSet.getString(DESTINATION),
                destination_device,-1L,-1L,-1L,0,""
                );
      }
      return new OutgoingMessageEntity(resultSet.getLong(ID),
                                       false,
                                       type,
                                       resultSet.getString(RELAY),
                                       resultSet.getLong(TIMESTAMP),
                                       resultSet.getString(SOURCE),
                                       resultSet.getInt(SOURCE_DEVICE),
                                       legacyMessage,
                                       resultSet.getBytes(CONTENT),
                                       resultSet.getBoolean(NOTIFY),-1L,-1L,-1L,0,"",null,null);
    }
  }

  @BindingAnnotation(MessageBinder.AccountBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface MessageBinder {
    public static class AccountBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<MessageBinder, Envelope>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           MessageBinder accountBinder,
                           Envelope message)
          {
            sql.bind(TYPE, message.getType().getNumber());
            sql.bind(RELAY, message.getRelay());
            sql.bind(TIMESTAMP, message.getTimestamp());
            sql.bind(SOURCE, message.getSource());
            sql.bind(SOURCE_DEVICE, message.getSourceDevice());
            sql.bind(MESSAGE, message.hasLegacyMessage() ? message.getLegacyMessage().toByteArray() : null);
            sql.bind(CONTENT, message.hasContent() ? message.getContent().toByteArray() : null);
          }
        };
      }
    }
  }

  public static class MessageMapperForRemind implements ResultSetMapper<OutgoingMessageEntityForRemind> {
    @Override
    public OutgoingMessageEntityForRemind map(int i, ResultSet resultSet, StatementContext statementContext)
            throws SQLException
    {
      return new OutgoingMessageEntityForRemind(
              resultSet.getString(SOURCE),
              resultSet.getString(DESTINATION));
    }
  }

}
