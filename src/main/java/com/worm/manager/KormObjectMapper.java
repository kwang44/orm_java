package com.worm.manager;

import com.worm.annotations.Column;
import com.worm.connection.KormConnectionManager;
import com.worm.exception.IncorrectKormAnnotationException;
import com.worm.annotations.Table;
import com.worm.exception.MapperException;
import org.javatuples.Triplet;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class KormObjectMapper<T> {

    //Class we're working on
    Class<T> clazz;
    //name of the table
    String tableName;
    //Contain all the columns of a table with it's name, field, and class respectively.
    Triplet<String, Field, Class>[] columns;
    //Contain only the primary key
    Triplet<String, Field, Class>[] primaryKeys;

    KormConnectionManager wormConnectionManager;

    /**
     * This parse the class with the proper annotations
     * @param clazz Class to be worked on
     * @param wormConnectionManager
     */

    public KormObjectMapper(Class<T> clazz, KormConnectionManager wormConnectionManager) {
        this.clazz = clazz;
        this.wormConnectionManager = wormConnectionManager;

        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table == null)
            throw new IncorrectKormAnnotationException("No table annotation found");

        this.tableName = table.name();

        ArrayList<Triplet<String, Field, Class>> columnsArrayList = new ArrayList<Triplet<String, Field, Class>>();
        ArrayList<Triplet<String, Field, Class>> primaryKeysArrayList = new ArrayList<Triplet<String, Field, Class>>();

        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;

            Class dataType = field.getType();

            if (dataType.equals(Integer.TYPE) || dataType.equals(Integer.class)) {
                dataType = Integer.class;
            }
            else if (dataType.equals(String.class)) {
                dataType = String.class;
            }
            else if (dataType.equals(Double.TYPE) || dataType.equals(Double.class)) {
                dataType = Double.class;
            }
            else {
                throw new IncorrectKormAnnotationException("Datatype not supported");
            }
            field.setAccessible(true);

            Triplet<String, Field, Class> triplet = new Triplet<String, Field, Class>(column.name(), field, dataType);
            columnsArrayList.add(triplet);

            if (column.isPrimaryKey())
                primaryKeysArrayList.add(triplet);


        }

        if (columnsArrayList.isEmpty())
            throw new IncorrectKormAnnotationException("No column annotation found");

        this.columns = columnsArrayList.toArray(new Triplet[0]);
        this.primaryKeys = primaryKeysArrayList.toArray(new Triplet[0]);
    }

    /**
     * Create a table with respective table name and column names with type
     * @return
     * @throws SQLException
     */
    public int createTable() throws SQLException {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("create table \"");
        sqlBuilder.append(this.tableName);
        sqlBuilder.append("\" (");

//        ArrayList<String> columnsArrayList = new ArrayList<String>();
//
//        for(Triplet<String, Field, Class> triplet : columns) {
//            Class dataType = triplet.getValue2();
//
//            if (dataType.equals(Integer.TYPE) || dataType.equals(Integer.class)) {
//                columnsArrayList.add("\"" + triplet.getValue0() + "\" int");
//            }
//            else if (dataType.equals(String.class)) {
//                columnsArrayList.add("\"" + triplet.getValue0() + "\" text");
//            }
//            else if (dataType.equals(Double.TYPE) || dataType.equals(Double.class)) {
//                columnsArrayList.add("\"" + triplet.getValue0() + "\" double precision");
//            }
//        }

        String[] columnProperties = Arrays.stream(columns).map(triplet -> {
            Class dataType = triplet.getValue2();
            if (dataType.equals(Integer.TYPE) || dataType.equals(Integer.class)) {
                return "\"" + triplet.getValue0() + "\" int";
            }
            else if (dataType.equals(String.class)) {
                return "\"" + triplet.getValue0() + "\" text";
            }
            else if (dataType.equals(Double.TYPE) || dataType.equals(Double.class)) {
                return "\"" + triplet.getValue0() + "\" double precision";
            }
            return null;
        }).toArray(String[]::new);

        sqlBuilder.append(String.join(", ", columnProperties));

//        columnsArrayList.clear();

//        if (primaryKeys.length > 0) {
//            for (Triplet<String, Field, Class> triplet : primaryKeys) {
//                columnsArrayList.add("\"" + triplet.getValue0() + "\"");
//            }
//            sqlBuilder.append(", primary key(" + String.join(", ", columnsArrayList) + ")");
//        }

        if (primaryKeys.length > 0) {
            columnProperties = Arrays.stream(primaryKeys).map(triplet -> "\"" + triplet.getValue0() + "\"").toArray(String[]::new);
            sqlBuilder.append(", primary key(" + String.join(", ", columnProperties) + ")");
        }

        sqlBuilder.append(");");

        Connection conn = wormConnectionManager.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeUpdate(sqlBuilder.toString());
    }

    /**
     *
     * @param obj Object to be stored
     * @return rows of change
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public int save(Object obj) throws SQLException, IllegalAccessException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into \"" + this.tableName + "\" (");
        String[] temp = Arrays.stream(columns).map(triplet -> "\"" + triplet.getValue0() + "\"").toArray(String[]::new);
        sqlBuilder.append(String.join(", ", temp));
        Arrays.fill(temp, "?");
        sqlBuilder.append(") values (" + String.join(", ", temp) + ");");

        System.out.println(sqlBuilder.toString());

        Connection conn = wormConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());

        for (int i = 1; i <= columns.length; i++) {
            Triplet<String, Field, Class> triplet = columns[i - 1];
            Class dataType = triplet.getValue2();
            pstmt.setObject(i, triplet.getValue1().get(obj));
        }
        System.out.println(pstmt.toString());
        return pstmt.executeUpdate();
    }

    /**
     *
     * @param map Map<String, Object> where string is the column names and object is the object to be stored, use null as a get all method
     * @return Array of objects or null if none found
     */
    public Object[] get(Map<String, Object> map) throws SQLException, IllegalAccessException, InstantiationException {
        StringBuilder sqlBuilder = new StringBuilder("select ");
        String[] columnNames = Arrays.stream(columns).map(triplet -> "\"" + triplet.getValue0() + "\"").toArray(String[]::new);

        sqlBuilder.append(String.join(", ", columnNames) + " from \"" + this.tableName + "\"");

        Connection conn = wormConnectionManager.getConnection();
        PreparedStatement pstmt;

        if (map == null || map.isEmpty()) {
            pstmt = conn.prepareStatement(sqlBuilder.toString());
        }
        else {
            String[] temp = map.keySet().toArray(new String[0]);
            sqlBuilder.append(" where ");
            String[] temp2 = Arrays.stream(temp).map(s -> "(\"" + s + "\" = ?)").toArray(String[]::new);
            sqlBuilder.append(String.join(" and ", temp2));

            pstmt = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < temp.length; i ++) {
                pstmt.setObject(i+1, map.get(temp[i]));
            }
        }
        ResultSet rs = pstmt.executeQuery();

        ArrayList objectArrayList = new ArrayList();

        while (rs.next()) {
            Object obj = clazz.newInstance();
            for (Triplet<String, Field, Class> triplet : columns) {
                triplet.getValue1().set(obj, rs.getObject(triplet.getValue0(), triplet.getValue2()));
            }
            objectArrayList.add(obj);
        }
        if (objectArrayList.isEmpty())
            return null;
        return objectArrayList.toArray();
    }

    /**
     * This method delete row base on map condition
     * @param map search condition where string is column name
     * @return number of rows deleted
     * @throws SQLException
     */
    public int delete(Map<String, Object> map) throws SQLException {

        if (map == null || map.isEmpty()) {
            throw new MapperException("Map is either null or empty, use deleteAll() if you want to delete everything");
        }
        StringBuilder sqlBuilder = new StringBuilder("delete from \"" + this.tableName + "\" where ");

        String[] keys = map.keySet().toArray(new String[0]);

        String[] temp = Arrays.stream(keys).map(s -> "(\"" + s + "\" = ?)").toArray(String[]::new);
        sqlBuilder.append(String.join(" and " , temp) + ";");

        Connection conn = wormConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());

        for (int i = 0; i < keys.length; i++) {
            pstmt.setObject(i+1, map.get(keys[i]));
        }

        return pstmt.executeUpdate();
    }

    /**
     * This method delete all rows in the table
     * @return number of rows deleted
     * @throws SQLException
     */
    public int deleteAll() throws SQLException {
        Connection conn = wormConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("delete from \"" + this.tableName + "\";");
        return pstmt.executeUpdate();
    }

    /**
     * This values base on findMap based on condition in the updateMap
     * @param findMap search condition of the query, if null or empty, will update all rows
     * @param updateMap contain the updated value
     * @return row affected
     */
    public int update(Map<String, Object> findMap, Map<String, Object> updateMap) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("update \"" + this.tableName + "\" set ");

        if (updateMap == null || updateMap.isEmpty())
            throw new MapperException("No value to update to");

        String[] setColumns = updateMap.keySet().toArray(new String[0]);
        sqlBuilder.append(String.join(", ", Arrays.stream(setColumns).map(s -> "\"" + s + "\" = ?").toArray(String[]::new)));

        if (findMap != null && !findMap.isEmpty()) {
            String[] findColumns = findMap.keySet().toArray(new String[0]);
            sqlBuilder.append(" where ");
            sqlBuilder.append(String.join(" and ", Arrays.stream(findColumns).map(s -> "(\"" + s + "\" = ?)").toArray(String[]::new)));
        }


        Connection conn = wormConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());

        int i = 1;

        for (String value : setColumns) {
            pstmt.setObject(i++, updateMap.get(value));
        }

        if (findMap != null && !findMap.isEmpty()) {
            String[] findColumns = findMap.keySet().toArray(new String[0]);
            for (String value : findColumns) {
                pstmt.setObject(i++, findMap.get(value));
            }
        }
        return pstmt.executeUpdate();
    }
}
