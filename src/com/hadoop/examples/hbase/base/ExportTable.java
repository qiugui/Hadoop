 package com.hadoop.examples.hbase.base;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
 public class ExportTable {

	 public static void main (String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		 Configuration  conf = HBaseConfiguration.create();
		 
		 conf.set("hbase.master", "192.168.216.130:60000");
		 conf.set("hbase.zookeeper.quorum", "192.168.216.132,192.168.216.133,192.168.216.134");
		 conf.set("hbase.zookeeper.property.clientPort", "2181");
		 
		 HBaseAdmin admin = new HBaseAdmin(conf);
		 HTableDescriptor tableDescriptor = admin.getTableDescriptor(Bytes.toBytes("scores"));
		 
		 byte[] name = tableDescriptor.getName();
		 System.out.println("输出结果如下：");
		 
		 System.out.println("	表名： " + new String(name));
		 
		 HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
		 
		 for (HColumnDescriptor column : columnFamilies){
			 System.out.println("		列簇名： " + column.getNameAsString());
		 }
		 
		 admin.close();
	 }
}

 