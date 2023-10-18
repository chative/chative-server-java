package org.whispersystems.textsecuregcm.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonPropertyAnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(JsonProperty.class) == null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
