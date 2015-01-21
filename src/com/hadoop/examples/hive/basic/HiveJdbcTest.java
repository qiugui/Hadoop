 package com.hadoop.examples.hive.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 public class HiveJdbcTest {
	 private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	 
	 public static void main (String[] args) throws SQLException{
		 try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {

			 e.printStackTrace();
			 System.exit(1);
			 
		}
		 Connection ct =DriverManager.getConnection("jdbc:hive2://192.168.216.130:10000/default","hadoop","");
		 Statement st = ct.createStatement();
		 String tablename = "xp";
		 String sql = "show tables '" + tablename +"'";
		 System.out.println("Running: " + sql);
		 ResultSet rs = st.executeQuery(sql);
		 if (rs.next()){
			 System.out.println("*******************"+rs.getString(1));
		 }
		 
		 sql = "describe " + tablename;
		 System.out.println("Running: " + sql);
		 rs = st.executeQuery(sql);
		 while (rs.next()){
			 System.out.println("*******************"+rs.getString(1));
		 }
		 
		 sql = "select * from " + tablename;
		 System.out.println("Running: " + sql);
		 rs = st.executeQuery(sql);
		 while (rs.next()){
			 System.out.println("*******************"+String.valueOf(rs.getInt(1)) + "\t" + rs.getString(2));
		 }
		 
		 sql = "select count(*) from " + tablename;
		 System.out.println("Running: " + sql);
		 rs = st.executeQuery(sql);
		 while (rs.next()){
			 System.out.println("*******************"+String.valueOf(rs.getInt(1)));
		 }
	 }
}
