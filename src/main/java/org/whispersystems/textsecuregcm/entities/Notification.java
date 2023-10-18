package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.GroupMembersTable;

import java.util.List;

public class Notification implements Cloneable{
    @JsonProperty
    private int type=-1;
    @JsonProperty
    private Args args;

    @JsonProperty
    private String payload;

    public static class NotificationTermTemplate{
        public static String PERSONAL_NORMAL="%s : [new message]";
        public static String PERSONAL_FILE="%s : [file]";
        public static String PERSONAL_REPLY="%s : [new message]";
        public static String PERSONAL_CALL="%s is calling you";
        public static String PERSONAL_CALL_CANCEL="%s cancelled the call" ;
        public static String PERSONAL_CALL_TIMEOUT="%s  called you , you missed the call" ;


        public static String GROUP_NORMAL="%s : [new message]";
        public static String GROUP_FILE="%s : [file]";
        public static String GROUP_MENTIONS_DESTINATION="%s : @ you [new message] ";
        public static String GROUP_MENTIONS_OTHER="%s : @ %s [new message]";
        public static String GROUP_MENTIONS_ALL="%s : @ all [new message]";
        public static String GROUP_REPLY_DESTINATION="%s : @ you [new message] ";
        public static String GROUP_REPLY_OTHER="%s : @ %s [new message]";
        public static String GROUP_CALL="%s invite you to a meeting ,Click to join";
        public static String GROUP_CALL_COLSE=" %s close the meeting";
        public static String GROUP_CALL_OVER=" The meeting has ended";
        public static String GROUP_ADD_ANNOUNCEMENT   ="%s : @all [announcement]";
        public static String GROUP_UPDATE_ANNOUNCEMENT   ="%s : @all [announcement]";

        public static String RECALL_MSG   ="\"%s\" recalled a message";
        public static String TASK_MSG   ="%s : [task]";
    }

    public enum Type {
        PERSONAL_NORMAL(0,NotificationTermTemplate.PERSONAL_CALL),
        PERSONAL_FILE(1,NotificationTermTemplate.PERSONAL_FILE),
        PERSONAL_REPLY(2,NotificationTermTemplate.PERSONAL_REPLY),
        PERSONAL_CALL(3,NotificationTermTemplate.PERSONAL_CALL),
        PERSONAL_CALL_CANCEL(4,NotificationTermTemplate.PERSONAL_CALL_CANCEL),
        PERSONAL_CALL_TIMEOUT(5,NotificationTermTemplate.PERSONAL_CALL_TIMEOUT),
        GROUP_NORMAL(6,NotificationTermTemplate.GROUP_NORMAL),
        GROUP_FILE(7,NotificationTermTemplate.GROUP_FILE),
        GROUP_MENTIONS_DESTINATION(8,NotificationTermTemplate.GROUP_MENTIONS_DESTINATION),
        GROUP_MENTIONS_OTHER(9,NotificationTermTemplate.GROUP_MENTIONS_OTHER),
        GROUP_MENTIONS_ALL(10,NotificationTermTemplate.GROUP_MENTIONS_ALL),
        GROUP_REPLY_DESTINATION(11,NotificationTermTemplate.GROUP_REPLY_DESTINATION),
        GROUP_REPLY_OTHER(12,NotificationTermTemplate.GROUP_REPLY_OTHER),
        GROUP_CALL(13,NotificationTermTemplate.GROUP_CALL),
        GROUP_CALL_COLSE(14,NotificationTermTemplate.GROUP_CALL_COLSE),
        GROUP_CALL_OVER(15,NotificationTermTemplate.GROUP_CALL_OVER),
        GROUP_ADD_ANNOUNCEMENT(16,NotificationTermTemplate.GROUP_ADD_ANNOUNCEMENT),
        GROUP_UPDATE_ANNOUNCEMENT(17,NotificationTermTemplate.GROUP_UPDATE_ANNOUNCEMENT),
        RECALL_MSG(18,NotificationTermTemplate.RECALL_MSG),
        RECALL_MENTIONS_MSG(19, NotificationTermTemplate.RECALL_MSG),
        TASK_MSG(20,NotificationTermTemplate.TASK_MSG),
        GROUP_ADD_PIN(21,"%s has pinned a message");

        private int  code=0;
        private String template;
        Type(int code,String template)
        {
            this.code=code;
            this.template=template;
        }
        public int getCode()
        {
            return code;
        }
        public String getTemplate()
        {
            return template;
        }


        public static Type fromCode(int n) {
            for(Type type:Type.values()){
                if(type.getCode()==n){
                    return type;
                }
            }
            return null;
        }

    }
    public static class Args implements Cloneable{
        @JsonProperty
        private String gid;
        @JsonProperty
        private String gname;
        @JsonProperty
        private String passthrough;
        @JsonProperty
        private List<String> mentionedPersons;

        private String collapseId;

        private String mentionedStr;

        private Account source;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getPassthrough() {
            return passthrough;
        }

        public void setPassthrough(String passthrough) {
            this.passthrough = passthrough;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public List<String> getMentionedPersons() {
            return mentionedPersons;
        }

        public void setMentionedPersons(List<String> mentionedPersons) {
            this.mentionedPersons = mentionedPersons;
        }

        public String getCollapseId() {
            return collapseId;
        }

        public void setCollapseId(String collapseId) {
            this.collapseId = collapseId;
        }

        public String getMentionedStr() {
            return mentionedStr;
        }

        public void setMentionedStr(String mentionedStr) {
            this.mentionedStr = mentionedStr;
        }

        public Account getSource() {
            return source;
        }

        public void setSource(Account source) {
            this.source = source;
        }

        @Override
        public int hashCode(){
            return super.hashCode();
        }

        @Override
        public Object clone()  {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        System.out.println(Notification.Type.PERSONAL_CALL.name());
    }
}

// generate Notification protobuf structure
// protoc --java_out=src/main/java src/main/resources/Notification.proto




