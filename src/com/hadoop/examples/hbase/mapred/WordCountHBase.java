 package com.hadoop.examples.hbase.mapred;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
 public class WordCountHBase {

	 //实现Map类
	 public static class MapClass extends Mapper<LongWritable, Text, Text, IntWritable>{
		 private final static IntWritable one = new IntWritable(1);
		 private Text record = new Text();
		 
		 public void map(LongWritable key,Text value,Context context) 
				 throws IOException, InterruptedException{
			 StringTokenizer itr = new StringTokenizer(value.toString());
			 while(itr.hasMoreTokens()){
				 record.set(itr.nextToken());
				 context.write(record, one);
			 }
		 }
	 }
	 
	 //实现Reduce类
	 public static class ReduceClass extends TableReducer<Text, IntWritable, NullWritable>{
		 
		 public void reduce(Text key,Iterable<IntWritable> values,Context context) 
				 throws IOException, InterruptedException{
			 int sum = 0 ;
			 for(IntWritable val : values){
				 sum +=val.get();
			 }
			 
			 //Put实例化，每个词存一行
			 Put put = new Put(Bytes.toBytes(key.toString()));
			 put.add(Bytes.toBytes("content"), Bytes.toBytes("count"), 
					 Bytes.toBytes(String.valueOf(sum)));
			 
			 context.write(NullWritable.get(), put);
		 }
	 }
	 
	 //创建HBase数据表
	 @SuppressWarnings({ "deprecation", "resource" })
	public static void createHBaseTable(String tableName) 
			 throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		 //创建表描述
		 HTableDescriptor tableDesc = new HTableDescriptor(tableName);
		 //创建列簇描述
		 HColumnDescriptor columnDesc = new HColumnDescriptor("content");
		 
		 tableDesc.addFamily(columnDesc);
		 
		 //配置HBase
		 Configuration conf = HBaseConfiguration.create();
		 conf.set("hbase.master", "192.168.216.130:60000");
		 conf.set("hbase.zookeeper.quorum", "192.168.216.132,192.168.216.133,192.168.216.134");
		 conf.set("hbase.zookeeper.property.clientPort", "2181");
		 
		 HBaseAdmin admin = new HBaseAdmin(conf);
		 
		 if (admin.tableExists(tableName)){
			 System.out.println("该表已经存在，正在重新创建！");
			 admin.disableTable(tableName);
			 admin.deleteTable(tableName);
		 }
		 
		 System.out.println("创建表： " + tableName);
		 admin.createTable(tableDesc);
	 }
	 
	 public static void main(String[] args) 
			 throws MasterNotRunningException, ZooKeeperConnectionException, IOException, ClassNotFoundException, InterruptedException{
		 String tableName = "wordcount";
		 
		 //第一步：创建数据表
		 WordCountHBase.createHBaseTable(tableName);
		 
		 //第二步： 进行MapReduce处理
		 Configuration conf = new Configuration();
		 
		 conf.set("mapred.remote.os", "Linux");
		 conf.set("fs.defaultFS", "hdfs://192.168.216.130:9000");
		 conf.set("yarn.resourcemanager.address", "192.168.216.130:8032");
		 conf.set("mapreduce.framework.name", "yarn");
		 conf.set("mapred.jar", "D:\\Documents\\workspace-sts-3.6.1.RELEASE\\Hadoop\\WordCountHBase.jar");
		 //conf.set("mapred.jar", "D:\\Documents\\workspace-sts-3.6.1.RELEASE\\Hadoop\\WordCount.jar");
		 conf.set("hbase.master", "192.168.216.130:60000");
		 conf.set("hbase.zookeeper.quorum", "192.168.216.132,192.168.216.133,192.168.216.134");
		 conf.set("hbase.zookeeper.property.clientPort", "2181");
		 conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);
		 
		 Job job = Job.getInstance(conf, "mapred wordcount");
		 
		 //设置Map和Reduce处理类
		 job.setMapperClass(MapClass.class);
		 job.setReducerClass(ReduceClass.class);
		 
		 //设置输出类型
		 job.setMapOutputKeyClass(Text.class);
		 job.setMapOutputValueClass(IntWritable.class);
		 
		 //设置输入输出格式
		 job.setInputFormatClass(TextInputFormat.class);
		 job.setOutputFormatClass(TableOutputFormat.class);
		 
		 //设置输入目录
		 FileInputFormat.addInputPath(job, new Path("/input"));
		 
		 System.exit(job.waitForCompletion(true) ? 0 : 1);
	 }
}

 