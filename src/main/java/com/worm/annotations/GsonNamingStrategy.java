package com.worm.annotations;

import com.google.gson.FieldNamingStrategy;

import java.lang.reflect.Field;

public class GsonNamingStrategy implements FieldNamingStrategy {
    @Override
    public String translateName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column.name();
    }
}
