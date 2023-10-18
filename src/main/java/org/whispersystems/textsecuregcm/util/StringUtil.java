package org.whispersystems.textsecuregcm.util;

public class StringUtil {
    public static boolean isEmpty(String str){
        return str==null||str.trim().length()==0;
    }

    public static boolean isEmpty(Integer integer) {
        return null == integer || 0 == integer;
    }

    public static String nullIfEmpty(String str) {
        return isEmpty(str) ? null : str;
    }

    public static Integer nullIfZero(Integer integer) {
        return isEmpty(integer) ? null : integer;
    }
}
