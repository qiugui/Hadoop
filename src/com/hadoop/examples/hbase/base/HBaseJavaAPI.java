 package com.hadoop.examples.hbase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

 public class HBaseJavaAPI {

	 //声明静态配置
	 private static Configuration conf = null;
	 
	 static{
		 conf = HBaseConfiguration.create();
		 conf.set("hbase.master", "192.168.216.130:60000");
		 conf.set("hbase.zookeeper.quorum", "192.168.216.132,192.168.216.133,192.168.216.134");
		 conf.set("hbase.zookeeper.property.clientPort", "2181");
	 }
	 
	 //创建数据库表
	 @SuppressWarnings("deprecation")
	private static void createTable(String tableName,String[] columnFamilies) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		 //创建一个数据库管理员
		 HBaseAdmin hAdmin = new HBaseAdmin(conf);
		 
		 if (hAdmin.tableExists(tableName)){
			 System.out.println("############WARNING	表已经存在");
			 System.exit(0);
		 } else {
			 //新建一个student表的描述
			 HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			 //在表里添加列簇
			 for (String cloumnFamily : columnFamilies){
				 tableDesc.addFamily(new HColumnDescriptor(cloumnFamily));
			 }
			 //根据配置好的描述建表
			 hAdmin.createTable(tableDesc);
			 System.out.println("############INFO	创建表成功");
		 }
		 hAdmin.close();
	 }
	 
	 //删除数据库表
	 public static void deleteTable(String tableName) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		 //新建一个数据库管理员
		 HBaseAdmin hAdmin = new HBaseAdmin(conf);
		 
		 if(hAdmin.tableExists(tableName)){
			 //关闭一个表
			 hAdmin.disableTable(tableName);
			 //删除一个表
			 hAdmin.deleteTable(tableName);
			 System.out.println("############INFO	删除表成功");
		 } else {
			 System.out.println("############WARNING	删除表失败");
			 System.exit(0);
		 }
		 hAdmin.close();
	 }
	 
	 //添加一条数据
	 public static void addRow(String tableName,String row,String columnFamily,
			 String column,String value) throws IOException{
		 HTable table = new HTable(conf, tableName);
		 Put put = new Put(Bytes.toBytes(row));
		 //参数分别是：列簇、列、值
		 put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
		 table.put(put);
		 table.close();
	 }
	 
	 //删除一条数据
	 public static void delRow(String tableName,String row) throws IOException{
		 HTable table = new HTable(conf, tableName);
		 Delete del = new Delete(Bytes.toBytes(row));
		 table.delete(del);
		 table.close();
	 }
	 
	 //删除多条数据
	 public static void delMultiRows(String tableName,String[] rows) throws IOException{
		 HTable table = new HTable(conf, Bytes.toBytes(tableName));
		 List<Delete> list = new ArrayList<Delete>();
		 
		 for (String row : rows){
			 Delete del = new Delete(Bytes.toBytes(row));
			 list.add(del);
		 }
		 
		 table.delete(list);
		 table.close();
	 }
	 
	 //获取一条数据
	 @SuppressWarnings("deprecation")
	public static void getRow(String tableName,String row) throws IOException{
		 HTable table = new HTable(conf, Bytes.toBytes(tableName));
		 Get get = new Get(Bytes.toBytes(row));
		 Result result = table.get(get);
		 System.out.println("############INFO	行名： " + row);
		 for (KeyValue rowKV : result.raw()){
			 
			 System.out.print("	时间戳： " + rowKV.getTimestamp());
			 System.out.print("	列簇名： " + new String(rowKV.getFamily()));
			 System.out.print("	列名: " +  new String(rowKV.getQualifier()));
			 System.out.print("	值: " +  new String(rowKV.getValue()));
			 System.out.println();
		 }
		 table.close();
	 }
	 
	 //获取所有数据
	 @SuppressWarnings("deprecation")
	public static void getAllRows(String tableName) throws IOException{
		 HTable table = new HTable(conf, Bytes.toBytes(tableName));
		 Scan scan = new Scan();
		 ResultScanner results = table.getScanner(scan);
		 
		 //输出结果
		 for(Result result : results){
			 System.out.println("");
			 for (KeyValue rowKV : result.raw()){
				 System.out.print("############INFO	行名： " + new String(rowKV.getRow()));
				 System.out.print("	时间戳： " + rowKV.getTimestamp());
				 System.out.print("	列簇名： " + new String(rowKV.getFamily()));
				 System.out.print("	列名: " +  new String(rowKV.getQualifier()));
				 System.out.print("	值: " +  new String(rowKV.getValue()));
				 System.out.println();
			 } 
		 }
		 
		 table.close();
	 }
	 
	 public static void main(String[] args){
		 try {
			
			 String tableNameString = "student";
			 
			 //第一步：创建数据库表
			 String[] columnFamilies = {"info", "course"};
			 HBaseJavaAPI.createTable(tableNameString, columnFamilies);
			 
			 //第二步：向数据表中添加数据
			 HBaseJavaAPI.addRow(tableNameString, "qiugui", "info", "age", "20");
			 HBaseJavaAPI.addRow(tableNameString, "qiugui", "info", "sex", "boy");
			 HBaseJavaAPI.addRow(tableNameString, "qiugui", "course", "china", "97");
			 HBaseJavaAPI.addRow(tableNameString, "qiugui", "course", "math", "130");
			 HBaseJavaAPI.addRow(tableNameString, "qiugui", "course", "english", "85");
			 
			 HBaseJavaAPI.addRow(tableNameString, "qiudan", "info", "age", "22");
			 HBaseJavaAPI.addRow(tableNameString, "qiudan", "info", "sex", "boy");
			 HBaseJavaAPI.addRow(tableNameString, "qiudan", "course", "china", "98");
			 HBaseJavaAPI.addRow(tableNameString, "qiudan", "course", "math", "100");
			 HBaseJavaAPI.addRow(tableNameString, "qiudan", "course", "english", "70");
			 
			 HBaseJavaAPI.addRow(tableNameString, "taojing", "info", "age", "24");
			 HBaseJavaAPI.addRow(tableNameString, "taojing", "info", "sex", "boy");
			 HBaseJavaAPI.addRow(tableNameString, "taojing", "course", "china", "99");
			 HBaseJavaAPI.addRow(tableNameString, "taojing", "course", "math", "130");
			 HBaseJavaAPI.addRow(tableNameString, "taojing", "course", "english", "89");
			 
			 //第三步：获取一条数据
			 System.out.println("############INFO	获取一条数据：");
			 HBaseJavaAPI.getRow(tableNameString, "qiugui");
			 
			 //第四步：获取所有数据
			 System.out.println("############INFO	获取所有数据");
			 HBaseJavaAPI.getAllRows(tableNameString);
			 System.out.println("############INFO	获取完成");
			 
			 //第五步：删除一条数据
			 System.out.println("############INFO	删除一条数据");
			 HBaseJavaAPI.delRow(tableNameString, "qiugui");
			 
			 //第六步：删除多条数据
			 System.out.println("############INFO	删除多条数据");
			 String[] rows = {"qiudan","taojing"};
			 HBaseJavaAPI.delMultiRows(tableNameString, rows);
			 
			 System.out.println("############INFO	获取所有数据");
			 HBaseJavaAPI.getAllRows(tableNameString);
			 System.out.println("############INFO	获取完成");
			 
			 //第七步：删除数据表
			 System.out.println("############INFO	删除数据表");
			 HBaseJavaAPI.deleteTable(tableNameString);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	 }
}

 