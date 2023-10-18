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

public abstract class ReadReceiptsTable {
  private static final String ID     = "id";
  private static final String SOURCE        = "source";
  private static final String SOURCE_DEVICE = "source_device";
  private static final String CONVERSATION       = "conversation";
  private static final String MAX_SERVER_TIMESTAMP       = "max_server_timestamp";
  private static final String READ_AT       = "read_at";
  private static final String MAX_NOTIFY_SEQUENCE_ID = "max_notify_sequence_id";
  private static final String UPLOAD_TIME = "upload_time";


  @SqlUpdate("INSERT INTO read_receipts (" +  CONVERSATION + ", " + SOURCE + ", "+ SOURCE_DEVICE + ", "+ MAX_SERVER_TIMESTAMP+ ", "+ READ_AT+ ", "+ MAX_NOTIFY_SEQUENCE_ID+ ", "+ UPLOAD_TIME+
          ") VALUES ( :conversation, :source, :source_device, :max_server_timestamp, :read_at, :max_notify_sequence_id, :upload_time)")
  public abstract void insert(@ReadReceiptBinder ReadReceipt readReceipt);

  @SqlBatch("INSERT INTO read_receipts (" +  CONVERSATION + ", " + SOURCE + ", "+ SOURCE_DEVICE + ", "+ MAX_SERVER_TIMESTAMP+ ", "+ READ_AT+ ", "+ MAX_NOTIFY_SEQUENCE_ID+ ", "+ UPLOAD_TIME+
          ") VALUES ( :conversation, :source, :source_device, :max_server_timestamp, :read_at, :max_notify_sequence_id, :upload_time)")
  public abstract void insertBatch(@ReadReceiptBinder List<ReadReceipt> readReceipts);

  @Mapper(ReadReceiptMapper.class)
  @SqlQuery("SELECT * FROM read_receipts WHERE source=:source and source_device=:source_device")
  public abstract List<ReadReceipt> getBySource(@Bind(SOURCE) String source,@Bind(SOURCE_DEVICE) long source_device);

  @Mapper(ReadReceiptMapper.class)
  @SqlQuery("SELECT * FROM read_receipts WHERE source=:source and source_device=:source_device and conversation=:conversation")
  public abstract List<ReadReceipt> getBySourceAndConversation(@Bind(SOURCE) String source,@Bind(SOURCE_DEVICE) long source_device,@Bind(CONVERSATION) String conversation);

  @Mapper(ReadReceiptMapper.class)
  @SqlQuery("SELECT * FROM read_receipts WHERE source=:source and conversation=:conversation order by max_server_timestamp,max_notify_sequence_id desc limit 1")
  public abstract ReadReceipt getMaxBySourceAndConversation(@Bind(SOURCE) String source,@Bind(CONVERSATION) String conversation);

  @Mapper(ReadReceiptMapper.class)
  @SqlQuery("SELECT * FROM read_receipts WHERE id=:id")
  public abstract List<ReadReceipt> getById(@Bind(ID) String id);

  @SqlUpdate("DELETE FROM read_receipts where id=:id")
  public abstract void delete( @Bind(ID) String id);

  public static class ReadReceiptMapper implements ResultSetMapper<ReadReceipt> {
    @Override
    public ReadReceipt map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new ReadReceipt(
              resultSet.getString(CONVERSATION),
              resultSet.getString(SOURCE),
              resultSet.getLong(SOURCE_DEVICE),
              resultSet.getLong(MAX_SERVER_TIMESTAMP),
              resultSet.getLong(READ_AT),
              resultSet.getLong(MAX_NOTIFY_SEQUENCE_ID),
              resultSet.getLong(UPLOAD_TIME)
      );
    }
  }

  @BindingAnnotation(ReadReceiptBinder.ReadReceiptFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface ReadReceiptBinder {
    public static class ReadReceiptFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<ReadReceiptBinder, ReadReceipt>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           ReadReceiptBinder readReceiptBinder,
                           ReadReceipt readReceipt)
          {
            sql.bind(ID, readReceipt.getId());
            sql.bind(CONVERSATION, readReceipt.getConversation());
            sql.bind(SOURCE, readReceipt.getSource());
            sql.bind(SOURCE_DEVICE,readReceipt.getSourceDevice());
            sql.bind(MAX_SERVER_TIMESTAMP,readReceipt.getMaxServerTimestamp());
            sql.bind(READ_AT,readReceipt.getReadAt());
            sql.bind(MAX_NOTIFY_SEQUENCE_ID,readReceipt.getMaxNotifySequenceId());
            sql.bind(UPLOAD_TIME,readReceipt.getUploadTime());
          }
        };
      }
    }
  }
}
