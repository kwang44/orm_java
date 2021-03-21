package com.worm.annotations;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.lang.reflect.Type;

public class GsonExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {

        if (fieldAttributes.getAnnotation(Column.class) == null) {
            return true;
        }
        Class clazz = fieldAttributes.getDeclaredClass();
        if (clazz.equals(Integer.TYPE) || clazz.equals(String.class) || clazz.equals(Double.TYPE) || clazz.equals(Integer.class) || clazz.equals(Double.class)) {
            return false;
        }


        return true;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
