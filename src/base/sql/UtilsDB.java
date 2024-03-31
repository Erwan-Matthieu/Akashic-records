package base.sql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import base.annotations.ColumnAnnotation;
import base.annotations.KeyAnnotation;
import base.annotations.TableAnnotation;
import reflect.NullOrEmptyException;
import reflect.Reflect;

import static connection.Connection.getConnection;

public class UtilsDB extends Reflect {

    private String TABLE_NAME = getTableName();

    private String SPACE = " ";
    private String[] PARENTHESIS = {"(",")"};
    private String COMMA = ",";
    private String QUESTION_MARK = "?";
    private String EQUAL = " = ";

    protected List<Object> fetchAll() throws Exception{
        List<Object> results = new ArrayList<>();
        List<Object[]> columnsAttributesList = fetchColumnsAttributes();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + TABLE_NAME;

        try {

            conn = getConnection();
            ps = conn.prepareStatement(query);

            rs = ps.executeQuery();

            while (rs.next()) {
                Object instance = this.instance();

                for (Object[] objects : columnsAttributesList) {
                    Method method = instance.getClass().getMethod(((Method) objects[2]).getName(), ((Field) objects[0]).getType());
                    method.invoke(instance, rs.getObject((String) objects[1]));
                }

                results.add(instance);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (conn != null) {
                conn.close();
            }
        }

        return results;
    }

    protected boolean insert() throws Exception {
        boolean check = false;

        Connection conn = null;
        PreparedStatement ps = null;

        List<Object[]> insertableFields = fetchInsertableFields();

        String query = createInsertQuery(insertableFields);

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);

            int pI = 1;

            for (Object[] objects : insertableFields) {
                ps.setObject(pI, objects[2]);
                pI++;
            }

            if (ps.executeUpdate() == 1) {
                check = true;
            }

            conn.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return check;
    }

    protected boolean update(Object[]... conditions) throws Exception {
        boolean check = false;

        Connection conn = null;
        PreparedStatement ps = null;

        List<Object[]> insertableFields = fetchInsertableFields();

        String query = createUpdateQuery(insertableFields, conditions);

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);

            int pI = 1;

            for (Object[] objects : insertableFields) {
                Class<?> clazz = ((Field) objects[0]).getType();
                Method method = ps.getClass().getMethod("set" + firstCharacterToUpperCase(clazz.getSimpleName()), int.class, clazz);
                method.setAccessible(true);
                method.invoke(ps, pI, objects[2]);
                pI++;
            }

            for (Object[] condition : conditions) {
                ps.setObject(pI, condition[1]);
                pI++;
            }

            if (ps.executeUpdate() > 0) {
                check = true;
            }

            conn.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return check;
    }

    protected boolean delete() throws Exception {
        boolean check = false;

        Connection conn = null;
        PreparedStatement ps = null;

        List<Object[]> condition = fetchPrimaryKey();

        String query = createDeleteQuery(condition);

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);

            int pI = 1;

            for (Object[] objects : condition) {
                ps.setObject(pI, objects[1]);
                pI++;
            }

            if (ps.executeUpdate() > 0) {
                check = true;
            }

            conn.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return check;
    }

    protected boolean deleteWithConditions(List<Object[]> conditions) throws Exception {
        boolean check = false;

        Connection conn = null;
        PreparedStatement ps = null;

        String query = createDeleteQuery(conditions);

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);

            int pI = 1;

            for (Object[] objects : conditions) {
                ps.setObject(pI, objects[1]);
                pI++;
            }

            if (ps.executeUpdate() > 0) {
                check = true;
            }

            conn.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return check;
    }

    private String createDeleteQuery(List<Object[]> conditions) {

        StringBuilder builder = new StringBuilder("DELETE FROM " + TABLE_NAME);

        builder.append(SPACE).append("WHERE").append(SPACE);

        int size = conditions.size();
        int n = 0;

        for (Object[] objects : conditions) {
            builder.append(objects[0]).append(SPACE).append(EQUAL).append(SPACE).append(QUESTION_MARK);

            if (n < size - 1) {
                builder.append(COMMA);
            }
        }
        
        return builder.toString();
    }

    private String createInsertQuery(List<Object[]> insertableFields) {
    
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        
        builder.append(TABLE_NAME).append(PARENTHESIS[0]);

        int size = insertableFields.size();
        int n = 0;

        for (Object[] objects : insertableFields) {
            builder.append(objects[1]);
            n++;
            
            if (n < size) {
                builder.append(COMMA);
            }
        }

        builder.append(PARENTHESIS[1]).append(SPACE).append("VALUES").append(SPACE).append(PARENTHESIS[0]);

        for (int i = 0; i < size; i++) {
            builder.append(QUESTION_MARK);

            if (i < size-1) {
                builder.append(COMMA);
            }
        }

        builder.append(PARENTHESIS[1]);

        return builder.toString();

    }

    private String createUpdateQuery(List<Object[]> insertableFields, Object[]... conditions) {
        
        StringBuilder builder = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");

        int size = insertableFields.size();
        int n = 0;

        for (Object[] objects : insertableFields) {
            builder.append(objects[1]).append(EQUAL).append(QUESTION_MARK);

            if (n < size - 1) {
                builder.append(COMMA);
            }
            builder.append(SPACE);
            n++;
        }

        builder.append("WHERE").append(SPACE);

        size = conditions.length;
        n = 0;

        for (Object[] condition : conditions) {
            builder.append(condition[0]).append(EQUAL).append(QUESTION_MARK);

            if (n < size - 1) {
                builder.append(COMMA).append(SPACE);
            }
            n++;
        }
        
        return builder.toString();
    }

    private List<Object[]> fetchPrimaryKey() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullOrEmptyException {

        List<Object[]> primaryKeys = new ArrayList<>();

        for (Field field : this.fetchFields()) {
            if (field.isAnnotationPresent(KeyAnnotation.class)) {
                KeyAnnotation annotation = field.getAnnotation(KeyAnnotation.class);

                if (annotation.isPrimaryKey()) {
                    Method method = getMethod("get" + firstCharacterToUpperCase(field.getName()), field.getType());
                    Object[] primaryKey = {field,method.invoke(this)};
                    primaryKeys.add(primaryKey);
                }
            }
        }

        return primaryKeys;

    }

    private List<Object[]> fetchColumnsAttributes() throws NoSuchMethodException, SecurityException, NullOrEmptyException {
        List<Object[]> columnsAttributesList = new ArrayList<>();
        
        for (Field field : this.fetchFields()) {
            if (field.isAnnotationPresent(ColumnAnnotation.class)) {
                ColumnAnnotation annotation = field.getAnnotation(ColumnAnnotation.class);
                Method method = getMethod("set" + firstCharacterToUpperCase(field.getName()),field.getType());

                Object[] columnAttribute = {field,annotation.columnName(),method};

                columnsAttributesList.add(columnAttribute);
            }
        }

        return columnsAttributesList;
    }

    private List<Object[]> fetchInsertableFields() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullOrEmptyException {
        List<Object[]> insertableFields = new ArrayList<>();
        
        for (Field field : this.fetchFields()) {
            if (field.isAnnotationPresent(ColumnAnnotation.class)) {
                ColumnAnnotation annotation = field.getAnnotation(ColumnAnnotation.class);

                if (annotation.enableInsertable()) {
                    Method method = getMethod("get" + firstCharacterToUpperCase(field.getName()));
    
                    Object[] insertableField = {field,annotation.columnName(),method.invoke(this)};
    
                    insertableFields.add(insertableField);
                }

            }
        }

        return insertableFields;
    }

    private String getTableName() {
        TableAnnotation annotation = this.getClass().getAnnotation(TableAnnotation.class);

        return annotation.tableName();
    }

    public static Object[] createConditionArray(String columnName, Object value) {
        Object[] conditionArray = {columnName,value};
        return conditionArray;
    }
}
