 package com.hadoop.examples.hive.loginfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 public class DBHelper {

	 /**
	  * 作用：负责建立Hive与MySQL的连接
	  * 
	  * 由于开销很大，所以采用单例模式
	  */
	 
	 private static Connection connToHive = null;
	 private static Connection connToMySQL = null;
	 
	 private DBHelper() {
		 
	 }
	 
	 //获得与Hive的连接，如果连接已经初始化，则直接返回
	 public static Connection getHiveConn() throws SQLException {
		 if (connToHive == null) {
			 try {
				Class.forName("org.apache.hive.jdbc.HiveDriver");
			} catch (ClassNotFoundException e) {
				 e.printStackTrace();
				 System.exit(1);
				 
			}
			 connToHive = DriverManager.getConnection("jdbc:hive2://192.168.216.130:10000/default","hadoop","");
		 }
		 
		 return connToHive;
	 }
	 
	 //获得与MySQL的连接
	 public static Connection getMySQLConn() throws SQLException{
		 if (connToMySQL == null) {
			 try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				
				 e.printStackTrace();
				 System.exit(1);
			}
			 connToMySQL = DriverManager.getConnection("jdbc:mysql://192.168.216.130:3306/mysqlhive?"
			 		+ "useUnicode=true&characterEncoding=latin1","root","");
		 }
		 
		 return connToMySQL;
	 }
	 
	 public static void closeHiveConn() throws SQLException {
		 if (connToHive != null){
			 connToHive.close();
		 }
	 }
	 
	 public static void closeMySQLConn() throws SQLException {
		 if (connToMySQL != null){
			 connToMySQL.close();
		 }
	 }
	 
	 /** 
	* @ClassName: HiveUtil 
	* @Description: 针对Hive的工具类 
	* @author qiugui 
	* @date 2015年1月20日 下午4:24:44 
	*  
	*/ 
	static class HiveUtil {
		 //创建表
		@SuppressWarnings("unused")
		public static void createTable(String sql) throws SQLException {
			Connection conn = DBHelper.getHiveConn();
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(sql);	
		}
		
		//依据条件查询数据
		public static ResultSet queryData(String sql) throws SQLException{
			Connection conn = DBHelper.getHiveConn();
			Statement stmt = conn.createStatement();
			ResultSet res = stmt.executeQuery(sql);
			return res;
		}
		
		//加载数据
		@SuppressWarnings("unused")
		public static void loadData(String sql) throws SQLException {
			Connection conn = DBHelper.getHiveConn();
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(sql);
		}
		
		//把数据存储到MySQL中
		@SuppressWarnings("unused")
		public static void hiveToMySQL(ResultSet res) throws SQLException{
			Connection conn = DBHelper.getMySQLConn();
			Statement stmt = conn.createStatement();
			while(res.next()){
				String rdata = res.getString(1);
				String time = res.getString(2);
				String type = res.getString(3);
				String relateclass = res.getString(4);
				String information = res.getString(5)+res.getString(6)+res.getString(7);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into hadooplog values(0,'");
				sql.append(rdata + "','");
				sql.append(time + "','");
				sql.append(type + "','");
				sql.append(relateclass + "','");
				sql.append(information + "')");
				
				int i = stmt.executeUpdate(sql.toString());
			}
		}
	 }
	
}

 