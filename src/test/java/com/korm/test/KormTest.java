package com.korm.test;

import com.korm.connection.KormConnectionManager;
import com.korm.connection.KormSession;
import com.korm.exception.MapperException;
import com.korm.manager.KormObjectMapper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Ignore
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KormTest {

    KormConnectionManager kormConnectionManager= new KormConnectionManager("jdbc:postgresql://localhost:5432/", "postgres", "112233");
    KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);

    @Test
    @Order(1)
    void kormBasicCreateAndDestroyTable() throws SQLException {
        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();
        kormObjectMapper.dropTable();
    }

    @Test
    @Order(2)
    void kormSimpleSaveQueryTest() throws SQLException, IllegalAccessException, InstantiationException {

        TestObject testObject1 = new TestObject(1);
        TestObject testObject2 = new TestObject(1);
        Assertions.assertTrue(testObject1.equals(testObject2));

        Assertions.assertThrows(MapperException.class, () -> {
            new KormObjectMapper(null, kormConnectionManager);
        });
        Assertions.assertThrows(MapperException.class, () -> {
            new KormObjectMapper(TestObject.class, null);
        });

        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();
        kormObjectMapper.save(testObject1);

        Map<String, Object> map= new HashMap();
        map.put("Column 1", 1);
        TestObject newTestObject = (TestObject) kormObjectMapper.get(map)[0];
        Assertions.assertTrue(testObject1.equals(newTestObject));
        kormObjectMapper.dropTable();
    }

    @Test
    @Order(3)
    void KormSaveQueryTest() throws SQLException, IllegalAccessException, InstantiationException {

        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();

        for (int i = 0; i < 10; i++) {
            kormObjectMapper.save(new TestObject(i));
        }

        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap();
            map.put("Column 1", i);
            map.put("Column 2", i);
            map.put("Column 3", Integer.toString(i));
            TestObject temp = (TestObject) kormObjectMapper.get(map)[0];

            Assertions.assertTrue(new TestObject(i).equals(temp));
        }
        kormObjectMapper.dropTable();
    }

    @Test
    @Order(4)
    void kormUpdateTest() throws SQLException, IllegalAccessException, InstantiationException {

        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();

        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject(i);
            testObject.col1 %= 2;
            kormObjectMapper.save(testObject);
        }

        Map<String, Object> findMap = new HashMap();
        findMap.put("Column 1", 0);
        Map<String, Object> setMap = new HashMap();
        setMap.put("Column 6", "Hello World");
        kormObjectMapper.update(findMap, setMap);

        setMap.clear();
        setMap.put("Column 4", 3);
        kormObjectMapper.update(null, setMap);


        for (int i = 0; i < 10; i++) {
            findMap.clear();
            findMap.put("Column 1", i % 2);
            findMap.put("Column 2", i);
            findMap.put("Column 3", Integer.toString(i));
            TestObject temp = (TestObject) kormObjectMapper.get(findMap)[0];

            Assertions.assertEquals(temp.col4, 3);
        }

        kormObjectMapper.dropTable();
    }

    @Test
    @Order(5)
    public void kormDeleteTest() throws SQLException, IllegalAccessException, InstantiationException {
        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();

        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject(i);
            testObject.col1 %= 2;
            kormObjectMapper.save(testObject);
        }

        Map<String, Object> deleteMap = new HashMap();
        deleteMap.put("Column 1", 0);
        kormObjectMapper.delete(deleteMap);

        Object[] objArray = kormObjectMapper.get(null);

        for (Object temp : objArray) {
            TestObject testObject = (TestObject) temp;
            Assertions.assertEquals(testObject.col1, 1);
        }
        kormObjectMapper.deleteAll();

        Assertions.assertTrue(kormObjectMapper.get(null) == null);

        kormObjectMapper.dropTable();
    }

    @Test
    @Order(6)
    public void kormSession() throws SQLException, IllegalAccessException, InstantiationException {
        KormObjectMapper kormObjectMapper = new KormObjectMapper(TestObject.class, kormConnectionManager);
        kormObjectMapper.createTable();

        for (int i = 0; i < 10; i++) {
            kormObjectMapper.save(new TestObject(i));
        }

        KormSession kormSession = kormConnectionManager.getKormSession();
        KormObjectMapper kormObjectMapper2 = new KormObjectMapper(TestObject.class, kormSession);
        kormObjectMapper2.dropTable();
        kormSession.rollBack();
        kormSession.close();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap();
            map.put("Column 1", i);
            map.put("Column 2", i);
            map.put("Column 3", Integer.toString(i));
            TestObject temp = (TestObject) kormObjectMapper.get(map)[0];

            Assertions.assertTrue(new TestObject(i).equals(temp));
        }
        kormObjectMapper.dropTable();
    }
}
