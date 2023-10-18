package org.whispersystems.textsecuregcm.util;

import java.util.List;
import java.util.Locale;

public class LangMatcher {
    public static String Parse(String lang){
        if (lang == null || lang.isEmpty()) return "en";
        lang = lang.replace('_','-');
        try {
            final List<Locale.LanguageRange> tags = Locale.LanguageRange.parse(lang);
            for (Locale.LanguageRange range : tags) {
                final String target = range.getRange().toLowerCase();
                if (target.contains("en")) return "en";
                if (target.contains("zh") || target.contains("-cn")) return "zh";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "en";
    }
}
