package org.whispersystems.testcode;

public class PhoneFix {
    public static void main(String[] args) {
        String phone = "+85855267066955";
        if (phone.length()>=15){
            int l = 3;
            String prefix1 = phone.substring(1, 1 + l);
            String prefix2 = phone.substring(1 + l, 1 + l + l);
            if (prefix1.equals(prefix2)) {
                phone = "+" + phone.substring(1 + l );
                return;
            }
            l = 2;
             prefix1 = phone.substring(1, 1 + l);
             prefix2 = phone.substring(1 + l, 1 + l + l);
            if (prefix1.equals(prefix2)) {
                phone = "+" + phone.substring(1 + l );
                return;
            }
        }
    }
}
