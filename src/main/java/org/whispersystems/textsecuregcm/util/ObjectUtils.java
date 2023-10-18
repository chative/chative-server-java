package org.whispersystems.textsecuregcm.util;

import java.util.Objects;

public class ObjectUtils {
    public static boolean objEquals(Object o1,Object o2){
        if(o1!=null &&o1 instanceof String) o1=trimString((String)o1);
        if(o2!=null &&o2 instanceof String) o2=trimString((String)o2);
        return Objects.equals(o1,o2);
    }
    private static String trimString(String str){
        if(StringUtil.isEmpty(str.trim())){
            return null;
        }
        return str.trim();
    }
}
