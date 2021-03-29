package com.korm.test;

import com.korm.connection.KormConnectionManager;
import com.korm.manager.KormObjectMapper;

public class ConSingleton {
    private static ConSingleton conSingleton= null;
    private static KormConnectionManager kormConnectionManager;
    private static KormObjectMapper kormObjectMapper;

    private ConSingleton() {
        this.kormConnectionManager= new KormConnectionManager("jdbc:postgresql://localhost:5432/", "postgres", "112233");
        this.kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
    }

    public static ConSingleton getInstance() {
        if (conSingleton == null) {
            conSingleton = new ConSingleton();
        }
        return conSingleton;
    }

    public static KormConnectionManager getKormConnectionManager() {
        return kormConnectionManager;
    }

    public static KormObjectMapper getKormObjectMapper() {
        return kormObjectMapper;
    }
}
