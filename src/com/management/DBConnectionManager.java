package com.management;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DBConnectionManager {
  private static Connection con = null;

  public static Connection establishConnection() throws SQLException {
    try (FileReader fr = new FileReader("D:\\java programming\\HospitalManagementSystem\\src\\database.properties")) {
      Properties p = new Properties();
      p.load(fr);
      String driver = p.getProperty("DB_DRIVER_CLASS");
//      System.out.println("Driver work");
      String url = p.getProperty("DB_URL");
//      System.out.println("url work");
      String user = p.getProperty("DB_USERNAME");
//      System.out.println("usewr work");
      String pass = p.getProperty("DB_PASSWORD");
//      System.out.println("pass work");

      Class.forName(driver);
      con=DriverManager.getConnection(url, user, pass);
       con.setAutoCommit(true);
      return con;
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("An unexpected error occurred. Please try again later or contact the system administrator if the issue persists.");
    }

    return con;
  }
}


