package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DirectoryNotify extends Notify{

  public final static String DIRECTORY_CHANGE_LOCK_KEY="DirectoryChange_";
  public final static String DIRECTORY_CHANGE_NOTIFY_LOCK_KEY="DirectoryChangeNotify_";
  public final static String DIRECTORY_CHANGE_STATUS_LOCK_KEY="DirectoryChange_DisableOrEnable";
  public final static String PERSONAL_DIRECTORY_VERSION_KEY="PersonalDirectoryVersion_";
  public final static String DIRECTORY_NOTIFY_LOCK_KEY="DirectoryNotifyLockKey";
  public final static String DIRECTORY_NOTIFY_CHANGE_SET_KEY="DirectoryNotifyChangeSet";
  public final static String DIRECTORY_NOTIFY_CHANGE_MAP_KEY="DirectoryNotifyChangeMap";
  public final static String DIRECTORY_BASIC_INFO_CHANGE_LOCK_KEY="DirectoryBasicInfoChangeLockKey";


  public enum ActionType {
    ADD,
    UPDATE,
    DELETE,
    PERMANENT_DELETE, // 永久删除
  }


  public interface ChangeType {
    String REGISTER="register";
    String UPDATE="update";
    String JOIN_TEAM="joinTeam";
    String LEAVE_TEAM="leaveTeam";
    String DISABLE="disable";
    String ENABLE="enable";
    String DELETE ="unregister";
    String NOT_ACTIVE="notActive";
    String REACTIVATE="reactivate";
  }

  public enum BasicInfoChangeType {
    PRIVATE,
    PROTECTED,
    PUBLIC
  }


  public static class DirectoryMember extends Contacts.Contact {
    @JsonProperty
    private Integer action;

    public DirectoryMember(String number, String name, String remark, String signature, String timeZone, String department, String superior, String avatar, Integer gender, String address, Integer flag, Integer action, Map<String,Object> privateConfigs, Map<String,Object> protectedConfigs, Map<String,Object> publicConfigs,Long extId) {
      super(number,name,null,remark,signature,timeZone,department,superior,avatar,gender,address,flag,privateConfigs,protectedConfigs,publicConfigs,null,extId);
      this.action=action;
    }

    public static class Builder extends Contacts.Contact.Builder {
      private Integer action;
      public Contacts.Contact.Builder setAction(Integer action) {
        this.action = action;
        return this;
      }

      public DirectoryMember build() {
        return new DirectoryMember(number, name, remark, signature, timeZone, department, superior, avatar, gender, address, flag,action,privateConfigs,protectedConfigs,publicConfigs,extId);
      }
    }

    public int getAction() {
      return action;
    }


    @Override
    public boolean equals(Object object){
      if(!(object instanceof DirectoryMember)){
        return false;
      }
      if(object==this){
        return true;
      }
      return number.equals(((DirectoryMember)object).number);
    }
    @Override
    public int hashCode(){
      return number.hashCode();
    }
  }


  public static class NodifyData extends Notify.NodifyData{

    @JsonProperty
    private int ver=1;
    @JsonProperty
    private long directoryVersion;
    @JsonProperty
    private List<DirectoryMember> members;

    public NodifyData(int directoryVersion,List<DirectoryMember> members) {
      this.directoryVersion=directoryVersion;
      this.members = members;
    }

    public long getDirectoryVersion() {
      return directoryVersion;
    }

    public void setDirectoryVersion(long directoryVersion) {
      this.directoryVersion = directoryVersion;
    }

    public List<DirectoryMember> getMembers() {
      return members;
    }

    public void setMembers(List<DirectoryMember> members) {
      this.members = members;
    }
  }

  public DirectoryNotify(long notifyTime, NodifyData data) {
   super(NotifyType.DIRECTORY.ordinal(),notifyTime,null,data, Display.NO.ordinal());
  }

}
