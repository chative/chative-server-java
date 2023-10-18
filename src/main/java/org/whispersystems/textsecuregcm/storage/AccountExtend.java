package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.whispersystems.textsecuregcm.util.SystemMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AccountExtend {
    public interface FieldName{
        String GLOBAL_NOTIFICATION="globalNotification";
        String PUBLIC_NAME="publicName";
        String MeetingVersion="meetingVersion";
        String MsgEncVersion="msgEncVersion";
    }

    //仅通知自己，自己可见
    @JsonProperty
    private Map<String,Object> privateConfigs=new HashMap<>();
    //仅通知自己和内部用户，自己和内部用户可见
    @JsonProperty
    private Map<String,Object>  protectedConfigs=new HashMap<>();
    //通知所有用户，所有用户可见
    @JsonProperty
    private Map<String,Object>  publicConfigs=new HashMap<>();

    public Object getGlobalNotification(){
        if(privateConfigs==null){
            return null;
        }
        if(privateConfigs.keySet().contains(FieldName.GLOBAL_NOTIFICATION)){
            return privateConfigs.get(FieldName.GLOBAL_NOTIFICATION);
        }
        return null;
    }

    public Object getPrivateConfig(String fileName){
        if(privateConfigs.keySet().contains(fileName)){
           return privateConfigs.get(fileName);
        }
        return null;
    }
    public void setPrivateConfig(String fileName,Object object){
        privateConfigs.put(fileName,object);
    }

    public Object getProtectedConfig(String fileName){
        if(protectedConfigs.keySet().contains(fileName)){
            return protectedConfigs.get(fileName);
        }
        return null;
    }
    public void setProtectedConfig(String fileName,Object object){
        protectedConfigs.put(fileName,object);
    }
    public Object getPublicConfig(String fileName){
        if(publicConfigs.keySet().contains(fileName)){
            return publicConfigs.get(fileName);
        }
        return null;
    }
    public Integer getMeetingVersion(){
        if(publicConfigs.keySet().contains(FieldName.MeetingVersion)){
            return (Integer)publicConfigs.get(FieldName.MeetingVersion);
        }
        return 1;
    }

    //public static void setMaxMsgEncVersion(Integer maxMsgEncVersion) {
    //    AccountExtend.maxMsgEncVersion = maxMsgEncVersion;
    //}
    //
    //static Integer maxMsgEncVersion = 255;

    public Integer getMsgEncVersion(){
        if(publicConfigs.keySet().contains(FieldName.MsgEncVersion)){
            Integer maxMsgEncVersion = AccountsManager.getMaxMsgEncVersion();
            final Integer version = (Integer) publicConfigs.get(FieldName.MsgEncVersion);
            if (version > maxMsgEncVersion) {
                return maxMsgEncVersion;
            }
            return version;
        }
        return 1;
    }

    public Integer getRealMsgEncVersion(){
        if(publicConfigs.keySet().contains(FieldName.MsgEncVersion)){
            return  (Integer) publicConfigs.get(FieldName.MsgEncVersion);
        }
        return 1;
    }


    public void setPublicConfig(String fileName,Object object){
        publicConfigs.put(fileName,object);
    }

    public Map<String, Object> getPrivateConfigs() {
        return privateConfigs;
    }

    public void setPrivateConfigs(Map<String, Object> privateConfigs) {
        if(privateConfigs!=null) {
            this.privateConfigs.putAll(privateConfigs);
        }
    }

    public Map<String, Object> getProtectedConfigs() {
        return protectedConfigs;
    }

    public void setProtectedConfigs(Map<String, Object> protectedConfigs) {
        if(protectedConfigs!=null) {
            this.protectedConfigs.putAll(protectedConfigs);
        }
    }

    public Map<String, Object> getPublicConfigs() {
        return publicConfigs;
    }

    public void setPublicConfigs(Map<String, Object> publicConfigs) {
        if(publicConfigs!=null) {
            this.publicConfigs.putAll(publicConfigs);
        }

    }

    public static class FieldsInfo {
        public static Map<String, Field> privateConfigFields = new HashMap<>();
        public static Map<String, Field> protectedConfigFields = new HashMap<>();
        public static Map<String, Field> publicConfigFields = new HashMap<>();
        static{
            privateConfigFields.put(FieldName.GLOBAL_NOTIFICATION,new Field(FieldName.GLOBAL_NOTIFICATION,Integer.class));
            publicConfigFields.put(FieldName.PUBLIC_NAME,new Field(FieldName.PUBLIC_NAME,String.class));
        }
        public static class Field{
            String name;
            Class type;

            public Field(String name,Class type){
                this.name=name;
                this.type=type;
            }
            public String getName() {
                return name;
            }

            public Class getType() {
                return type;
            }
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        Account account=new Account();
        account.setPrivateConfig("test","aaa");
        account.setPrivateConfig("boolean",true);
        Account account1=new Account();
        account1.setPrivateConfig("test","aaa");
        account1.setPrivateConfig("boolean",true);
        account1.setProtectedConfigs(null);

        ObjectMapper mapper= SystemMapper.getMapper();
        String sss=mapper.writeValueAsString(account);
        System.out.println(mapper.writeValueAsString(account));
        System.out.println(mapper.readValue(sss,Account.class).getPrivateConfig("boolean"));
        System.out.println(mapper.readValue(sss,Account.class).getProtectedConfigs().size());
        System.out.println(Objects.equals(account.getPrivateConfigs(),account1.getPrivateConfigs()));
        String a="";
        int b=1;
        Integer c=3;
        System.out.println(FieldsInfo.privateConfigFields.get(FieldName.GLOBAL_NOTIFICATION).getType().isInstance(a));
        System.out.println(FieldsInfo.privateConfigFields.get(FieldName.GLOBAL_NOTIFICATION).getType().isInstance(b));
        System.out.println(FieldsInfo.privateConfigFields.get(FieldName.GLOBAL_NOTIFICATION).getType().isInstance(c));
    }
}
