package com.worm.annotations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;

public class TestDriver {
    public static void main(String[] args) {
        Tester aTester = new Tester();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new GsonExclusionStrategy());
        gsonBuilder.setFieldNamingStrategy(new GsonNamingStrategy());
        Gson gson = gsonBuilder.create();

        String str = gson.toJson(aTester);

        Field[] fields = Tester.class.getDeclaredFields();

        for (Field field : fields) {
            System.out.println(field.getType().equals(Integer.TYPE));
        }

        System.out.println(str);

    }
}
