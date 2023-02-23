package com.bank.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    static Connection connection;
    public static void getConnection(){
        if(connection != null){
            return;
        }
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String MYSQL_USER =  System.getenv("MYSQLUSER");
            String MYSQL_PASSWORD = System.getenv("MYSQLPASS");
             connection  =  DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank",MYSQL_USER,MYSQL_PASSWORD);
        }catch (ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
