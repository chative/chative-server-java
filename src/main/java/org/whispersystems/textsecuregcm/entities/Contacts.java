package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountExtend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contacts {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Contact extends AccountExtend {

        protected String number;
        protected String name;
        protected String joinedAt;
        protected String email;
        protected String remark;
        protected String signature;
        protected String timeZone;
        protected String department;
        protected String superior;
        protected String avatar;
        protected Integer gender;
        protected String address;
        protected Integer flag;
        protected GiveInteractResponse thumbsUp;
        protected Long extId;

        public Contact(String number, String name, String joinedAt,String remark,  String signature, String timeZone, String department, String superior, String avatar, Integer gender, String address, Integer flag, Map<String,Object> privateConfigs,Map<String,Object> protectedConfigs,Map<String,Object> publicConfigs,GiveInteractResponse thumbsUp,Long extId) {

            this.number = number;
            this.joinedAt = joinedAt;
            this.name = name;
            this.remark = remark;
            //this.email = email;
            this.signature = signature;
            this.timeZone = timeZone;
            this.department = department;
            this.superior = superior;
            this.avatar = avatar;
            this.gender = gender;
            this.address = address;
            this.flag = flag;
            super.setPrivateConfigs(privateConfigs);
            super.setProtectedConfigs(protectedConfigs);
            super.setPublicConfigs(publicConfigs);
            this.thumbsUp=thumbsUp;
            this.extId=extId;
        }

        public static class Builder {
            protected String number;
            protected String name;
            protected String joinedAt;
            protected String remark;
            //protected String email;
            protected String signature;
            protected String timeZone;
            protected String department;
            protected String superior;
            protected String avatar;
            protected Integer gender;
            protected String address;
            protected Integer flag;
            protected Map<String,Object> privateConfigs=new HashMap<>();
            protected Map<String,Object>  protectedConfigs=new HashMap<>();;
            protected Map<String,Object>  publicConfigs=new HashMap<>();
            protected GiveInteractResponse thumbsUp;
            protected Long extId = Account.ExtType.DEFAULT.getId();

            public Builder setNumber(String number) {
                this.number = number;
                return this;
            }

            public Builder setName(String name) {
                this.name = name;
                return this;
            }
            public Builder setRemark(String remark) {
                this.remark = remark;
                return this;
            }

            public Builder setJoinedAt(String joinedAt) {
                this.joinedAt = joinedAt;
                return this;
            }
            //public Builder setEmail(String email) {
            //    this.email = email;
            //    return this;
            //}

            public Builder setSignature(String signature) {
                this.signature = signature;
                return this;
            }

            public Builder setTimeZone(String timeZone) {
                this.timeZone = timeZone;
                return this;
            }

            public Builder setDepartment(String department) {
                this.department = department;
                return this;
            }

            public Builder setSuperior(String superior) {
                this.superior = superior;
                return this;
            }

            public Builder setAvatar(String avatar) {
                this.avatar = avatar;
                return this;
            }

            public Builder setGender(Integer gender) {
                this.gender = gender;
                return this;
            }

            public Builder setAddress(String address) {
                this.address = address;
                return this;
            }

            public Builder setFlag(Integer flag) {
                this.flag = flag;
                return this;
            }

            public Builder setPrivateConfigs(Map<String, Object> privateConfigs) {
                this.privateConfigs = privateConfigs;
                return this;
            }

            public Builder setProtectedConfigs(Map<String, Object> protectedConfigs) {
                this.protectedConfigs = protectedConfigs;
                return this;
            }

            public Builder setPublicConfigs(Map<String, Object> publicConfigs) {
                this.publicConfigs = publicConfigs;
                if (this.publicConfigs != null && this.publicConfigs.get(FieldName.MeetingVersion) == null) {// 没有会议版本号，设置默认值
                    this.publicConfigs.put(FieldName.MeetingVersion, 1);
                }
                if (this.publicConfigs != null && this.publicConfigs.get(FieldName.MsgEncVersion) != null) {// 有会议版本号，设置默认值
                    this.publicConfigs.put(FieldName.MsgEncVersion, 1);
                }
                return this;
            }

            public Builder setThumbsUp(GiveInteractResponse thumbsUp) {
                this.thumbsUp = thumbsUp;
                return this;
            }
            //public Builder setExtId(Long extId) {
            //    this.extId = extId;
            //    return this;
            //}

            public Contact build() {
                return new Contact(number, name, joinedAt ,remark,  signature, timeZone, department, superior, avatar, gender, address, flag,privateConfigs,protectedConfigs,publicConfigs,thumbsUp,extId);
            }
        }

    }

    private Long directoryVersion;
    private List<Contact> contacts;

    public Contacts(Long directoryVersion, List<Contact> contacts) {
        this.directoryVersion = directoryVersion;
        this.contacts = contacts;
    }
}
