package org.whispersystems.textsecuregcm.util;

import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountExtend;
import org.whispersystems.textsecuregcm.storage.GroupMembersTable;

import java.util.Map;
import java.util.regex.Pattern;

public class ParameterValidator {
    final static private Pattern patternNumber = Pattern.compile("\\+\\d{5,11}");
    final static private Pattern patternSignature = Pattern.compile(".{0,256}");
    final static private Pattern patternTimeZone = Pattern.compile("^[+-]?\\d{1,2}(\\.\\d{1,2})?$");
    final static private Pattern patternAvatar = Pattern.compile(".{0,1024}");
    final static private Pattern patternAddress = Pattern.compile(".{0,64}");
    final static private Pattern patternPrivateConfigs = Pattern.compile(".{0,10485760}");
    final static private Pattern patternProtectedConfigs = Pattern.compile(".{0,10485760}");
    final static private Pattern patternPublicConfigs = Pattern.compile(".{0,10485760}");
    final static private Pattern patternOauthAppClientId = Pattern.compile("\\w{1,128}");
    final static private Pattern patternOauthScope = Pattern.compile("[\\w,]{1,4096}");

    public static boolean validateNumber(String input) {
        return patternNumber.matcher(input).matches();
    }

    public static boolean validateName(String input) {
        return true;
    }

    public static boolean validateSignature(String input) {
        return patternSignature.matcher(input).matches();
    }

    public static boolean validateTimeZone(String input) {
        return patternTimeZone.matcher(input).matches();
    }

    public static boolean validateAvatar(String input) {
        return patternAvatar.matcher(input).matches();
    }

    public static boolean validateGender(Integer input) {
        return input >= Account.GENDER.NONE.ordinal() && input <= Account.GENDER.OTHER.ordinal();
    }

    public static boolean validateAddress(String input) {
        return patternAddress.matcher(input).matches();
    }
    public static boolean validateGlobalNotification(Object input) {
        if(input instanceof  Integer) {
            return GroupMembersTable.NOTIFICATION.fromOrdinal((Integer) input) != null;
        }
        return false;
    }
    public static boolean validatePrivateConfigs(Map<String, Object> privateConfigs) {
        return patternPrivateConfigs.matcher(privateConfigs.toString()).matches();
//        if(privateConfigs==null||privateConfigs.size()==0){
//            return true;
//        }
//        for(String key:privateConfigs.keySet()){
//            if(!AccountExtend.FieldsInfo.privateConfigFields.containsKey(key)){
//                return false;
//            }
//            Object value=privateConfigs.get(key);
//            Class type=AccountExtend.FieldsInfo.privateConfigFields.get(key).getType();
//            if(type.isInstance(value)){
//                continue;
//            }
//        }
//        return true;
    }
    public static boolean validatePublicConfigs(Map<String, Object> publicConfigs) {
        return patternPublicConfigs.matcher(publicConfigs.toString()).matches();
//        if(publicConfigs==null||publicConfigs.size()==0){
//            return true;
//        }
//        for(String key:publicConfigs.keySet()){
//            if(!AccountExtend.FieldsInfo.publicConfigFields.containsKey(key)){
//                return false;
//            }
//            Object value=publicConfigs.get(key);
//            Class type=AccountExtend.FieldsInfo.publicConfigFields.get(key).getType();
//            if(type.isInstance(value)){
//                continue;
//            }
//        }
//        return true;
    }
    public static boolean validateProtectedConfigs(Map<String, Object> protectedConfigs) {
        return patternProtectedConfigs.matcher(protectedConfigs.toString()).matches();
//        if(protectedConfigs==null||protectedConfigs.size()==0){
//            return true;
//        }
//        for(String key:protectedConfigs.keySet()){
//            if(!AccountExtend.FieldsInfo.protectedConfigFields.containsKey(key)){
//                return false;
//            }
//            Object value=protectedConfigs.get(key);
//            Class type=AccountExtend.FieldsInfo.protectedConfigFields.get(key).getType();
//            if(type.isInstance(value)){
//                continue;
//            }
//        }
//        return true;
    }

    public static String cutdown(String input, int length) {
        if (input == null) {
            return null;
        }
        if (input.length() > length) {
            return input.substring(0, length);
        }
        return input;
    }

    public static boolean validateOauthAppClientId(String appid) {
        return patternOauthAppClientId.matcher(appid).matches();
    }

    public static boolean validateOauthScope(String scope) {
        return patternOauthScope.matcher(scope).matches();
    }
}
