import com.worm.connection.KormConnectionManager;
import com.worm.manager.KormObjectMapper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestDriver {
    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException {


//        Class clazz = RandomizedStuff.class;
//        System.out.println(clazz.getDeclaredFields().length);
//
//        for (Field field : clazz.getDeclaredFields()) {
//            System.out.println(field.getType());
//        }

        KormConnectionManager wormConnectionManager= new KormConnectionManager("jdbc:postgresql://localhost:5432/", "postgres", "112233");
        KormObjectMapper<RandomizedStuff> randomizedStuffWormMapper = new KormObjectMapper<>(RandomizedStuff.class, wormConnectionManager);

        RandomizedStuff randomizedStuff = new RandomizedStuff();
//        System.out.println(randomizedStuffWormMapper.save(randomizedStuff));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Column #1", 1);
        map.put("Column #2", 3);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("Column #3", 66555);
//        map2.put("Column #2", 2);

        System.out.println(randomizedStuffWormMapper.update(map, map2));



//        Arrays.stream(randomizedStuffWormMapper.get(map)).forEach(Object -> System.out.println(Object));
//        System.out.println(randomizedStuffWormMapper.delete(map));
//        System.out.println(randomizedStuffWormMapper.deleteAll());
//        System.out.println(randomizedStuffWormMapper.createTable());

    }
}
