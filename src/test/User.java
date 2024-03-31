package test;

import base.annotations.ColumnAnnotation;
import base.annotations.KeyAnnotation;
import base.annotations.TableAnnotation;
import base.sql.UtilsDB;

import static connection.Connection.getConnection;

import java.sql.*;

import java.util.*;

@TableAnnotation(tableName = "users_test")
public class User extends UtilsDB {

    @ColumnAnnotation(columnName = "id", enableInsertable = true)
    @KeyAnnotation(isPrimaryKey = true)
    public int id;
    @ColumnAnnotation(columnName = "first_name", enableInsertable = true)
    public String firstName;
    @ColumnAnnotation(columnName = "last_name", enableInsertable = true)
    public String lastName;
    @ColumnAnnotation(columnName = "email", enableInsertable = true)
    public String email;
    @ColumnAnnotation(columnName = "gender", enableInsertable = true)
    public String gender;
    @ColumnAnnotation(columnName = "ip_address", enableInsertable = true)
    public String ip_address;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getIp_address() {
        return ip_address;
    }
    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }


    public static void main(String[] args) {
        User user = new User();

        try {
            // System.out.println(user.delete(createConditionArray("id", 11)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
