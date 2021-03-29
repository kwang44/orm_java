package com.korm.test;

import com.korm.connection.KormConnectionManager;
import com.korm.connection.KormSession;
import com.korm.manager.KormObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KormDemoTest {
    KormConnectionManager kormConnectionManager = ConSingleton.getInstance().getKormConnectionManager();
    KormObjectMapper kormObjectMapper = ConSingleton.getInstance().getKormObjectMapper();

    @Test
    @Order(1)
    public void demoBegin() throws SQLException {
        kormObjectMapper.createTable();
    }

    @Test
    @Order(2)
    public void demoSave() throws SQLException, IllegalAccessException {
        for (int i = 0; i < 20; i++) {
            TestObject testObject = new TestObject(i);
            testObject.col1 %= 2;
            testObject.col2 %= 3;
            kormObjectMapper.save(testObject);
        }
    }

    @Test
    @Order(3)
    public void demoGet() throws IllegalAccessException, SQLException, InstantiationException {
        Map<String, Object> findMap = new HashMap();
        findMap.put("Column 1", 1);
        findMap.put("Column 2", 1);
        Object[] objects = kormObjectMapper.get(findMap);

        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    @Order(4)
    public void demoUpdate() throws IllegalAccessException, SQLException, InstantiationException {
        Map<String, Object> findMap = new HashMap();
        findMap.put("Column 1", 1);
        findMap.put("Column 2", 1);

        Map<String, Object> setMap = new HashMap();
        setMap.put("Column 6", "Hello World!!!");

        kormObjectMapper.update(findMap, setMap);

        Object[] objects = kormObjectMapper.get(findMap);
        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    @Order(5)
    public void demoDelete() throws SQLException, InstantiationException, IllegalAccessException {
        Map<String, Object> findMap = new HashMap();
        findMap.put("Column 1", 0);

        kormObjectMapper.delete(findMap);
        Object[] objects = kormObjectMapper.get(findMap);
        System.out.println(objects);
    }

    @Test
    @Order(6)
    public void demoGetAll() throws IllegalAccessException, SQLException, InstantiationException {
        Object[] objects = kormObjectMapper.get(null);

        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    @Order(7)
    public void demoSession() throws SQLException {
        KormSession kormSession = kormConnectionManager.getKormSession();
        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormSession);

        Map<String, Object> findMap = new HashMap();
        findMap.put("Column 1", 1);

        kormObjectMapper.delete(findMap);
        kormSession.rollBack();

        Map<String, Object> setMap = new HashMap();
        setMap.put("Column 6", "Session Demo!!!");
        kormObjectMapper.update(null, setMap);

        kormSession.commit();
        kormSession.close();
    }

    @Test
    @Order(8)
    public void demoDeleteAll() throws SQLException {
        kormObjectMapper.deleteAll();
    }

    @Test
    @Order(9)
    void demoLast() throws SQLException {
        kormObjectMapper.dropTable();
    }
}
