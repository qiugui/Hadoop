 package com.hadoop.examples.hbase.mapred;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.MultiTableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

 /** 
* @ClassName: IndexBuilder 
* @Description: 用MapReduce生成HBase索引 
* @author qiugui 
* @date 2015年1月28日 下午3:57:25 
*  
*/ 
public class IndexBuilder {
	
	//索引表唯一的一列为INDEX_ROW，其中INDEX为列簇
	private static final byte[] INDEX_COLUMN = Bytes.toBytes("INDEX");
	private static final byte[] INDEX_QUALIFIER = Bytes.toBytes("ROW");
	
	//实现Map类
	public static class MapClass extends 
	Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Writable>{
		//存储了“列名”到“表名-列名”的映射
		//前者用于获取某列的值，并作为索引表的键值；后者用户作为索引表的表名
		private HashMap<byte[], ImmutableBytesWritable> indexes;
		private byte[] family;
		
		//实现map函数
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void map(ImmutableBytesWritable key,Result value,org.apache.hadoop.mapreduce.Reducer.Context context) throws IOException, InterruptedException{
			
			for (Entry<byte[], ImmutableBytesWritable> index : indexes.entrySet()){
				//获取列名
				byte[] qualifier = index.getKey();
				
				//索引表的表名
				ImmutableBytesWritable tableName = index.getValue();
				
				//根据“列簇： 列名”获得元素值
				byte[] newValue = value.getValue(family, qualifier);
				
				if (newValue != null){
					//以列值作为行键，在列“INDEX:ROW”中插入行键
					Put put = new Put(newValue);
					put.add(INDEX_COLUMN, INDEX_QUALIFIER, key.get());
					
					//在tableName表上执行put，操作使用MultipleOutputFormat时，
					//第二个参数必须是Put和Delete类型
					context.write(tableName, put);
				}
			}
		}
		
		//setup为Mapper中的方法，该方法只在任务初始化时执行一次
		protected void setup(Context context){
			Configuration conf = context.getConfiguration();
			
			//通过set方法传递参数
			String tableName = conf.get("index.tablename");
			String[] fields = conf.getStrings("index.fields");
			
			//fields内为需要做索引的列名
			String familyName = conf.get("index.familyname");
			family = Bytes.toBytes(familyName);
			
			//初始化indexs方法
			indexes = new HashMap<byte[], ImmutableBytesWritable>();
			
			for (String field : fields){
				//如果给name做索引，则索引表的名称为“heroes-name”
				indexes.put(Bytes.toBytes(field), 
						new ImmutableBytesWritable(Bytes.toBytes(tableName + "-" + field)));				
			}
		}
	}
	
	//初始化实例数据表
	@SuppressWarnings({ "deprecation", "resource" })
	public static void initHBaseTable(Configuration conf , String tableName) throws IOException{
		HTableDescriptor tableDesc = new HTableDescriptor(tableName);
		
		HColumnDescriptor columnDesc = new HColumnDescriptor("info");
		
		tableDesc.addFamily(columnDesc);
		
		HBaseAdmin hAdmin = new HBaseAdmin(conf);
		
		if (hAdmin.tableExists(tableName)){
			System.out.println("该数据表已经存在，正在重新创建。");
			hAdmin.disableTable(tableName);
			hAdmin.deleteTable(tableName);
		}
		
		System.out.println("创建表：" + tableName);
		
		//创建表
		hAdmin.createTable(tableDesc);
		HTable table = new HTable(conf, tableName);
		//添加数据
		addRow(table, "1", "info", "name", "peter");
		addRow(table, "1", "info", "email", "peter@heroes.com");
		addRow(table, "1", "info", "power", "absorb abilities");
		
		addRow(table, "2", "info", "name", "hiro");
		addRow(table, "2", "info", "email", "hiro@heroes.com");
		addRow(table, "2", "info", "power", "bend time and space");
		
		addRow(table, "3", "info", "name", "sylar");
		addRow(table, "3", "info", "email", "sylar@heroes.com");
		addRow(table, "3", "info", "power", "hnow how things work");
		
		addRow(table, "4", "info", "name", "claire");
		addRow(table, "4", "info", "email", "claire@heroes.com");
		addRow(table, "4", "info", "power", "heal");
		
		addRow(table, "5", "info", "name", "noah");
		addRow(table, "5", "info", "email", "noah@heroes.com");
		addRow(table, "5", "info", "power", "cath the people with ablities");
	}
	
	//创建一条数据
	public static void addRow(HTable table,String row,
			String columnFamily,String column,String value) throws RetriesExhaustedWithDetailsException, InterruptedIOException{
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
		table.put(put);
	}
	
	//创建索引表
	@SuppressWarnings({ "resource", "deprecation" })
	public static void createIndexTable(Configuration conf,String tableName) throws IOException{
		//新建一个数据库管理员
		HBaseAdmin hAdmin = new HBaseAdmin(conf);
		
		if (hAdmin.tableExists(tableName)){
			System.out.println("该数据表已经存在，正在重新创建。");
			hAdmin.disableTable(tableName);
			hAdmin.deleteTable(tableName);
		}
		
		//新建一个表描述
		HTableDescriptor tableDesc = new HTableDescriptor(tableName);
		//在描述里添加列簇
		tableDesc.addFamily(new HColumnDescriptor(INDEX_COLUMN));
		
		//根据配置好的描述创建表
		hAdmin.createTable(tableDesc);
		System.out.println("创建" + tableName + "表成功");
	}
	
	public static Job configureJob(Configuration conf,String jobName) throws IOException{
		Job job = Job.getInstance(conf);
		job.setJobName(jobName);
		
		//设置Map类
		job.setMapperClass(MapClass.class);
		
		//设置reduce个数
		job.setNumReduceTasks(0);
		
		//设置输入输出格式
		job.setInputFormatClass(TableInputFormat.class);
		job.setOutputFormatClass(MultiTableOutputFormat.class);
		
		return job;
	}

	public static String converScanToString(Scan scan) throws IOException{
		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		//DataOutputStream dos = new DataOutputStream(out);
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		//scan.write(dos);
		//return Base64.encodeBytes(out.toByteArray());
		return Base64.encodeBytes(proto.toByteArray());
	}
	
	public static void main (String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.master", "192.168.216.130:60000");
		conf.set("hbase.zookeeper.quorum", "192.168.216.132,192.168.216.133,192.168.216.134");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
		String tableName = "heroes";
		String columnFamily = "info";
		String[] fields = {"name","power"};
		
		//初始化数据表
		IndexBuilder.initHBaseTable(conf, tableName);
		
		//创建索引表
		for (String field : fields){
			IndexBuilder.createIndexTable(conf, tableName+"-"+field);
		}
		
		//进行MapReduce处理
		
		//设定HBase的输入表
		conf.set(TableInputFormat.INPUT_TABLE, tableName);
		//设定对HBase输入表的Scan方式
		conf.set(TableInputFormat.SCAN, converScanToString(new Scan()));
		
		conf.set("index.tableName", tableName);
		conf.set("index.familyname", columnFamily);
		conf.setStrings("index.fields", fields);
		conf.set("mapred.remote.os", "Linux");
		conf.set("fs.defaultFS", "hdfs://192.168.216.130:9000");
		conf.set("yarn.resourcemanager.address", "192.168.216.130:8032");
		conf.set("mapreduce.framework.name", "yarn");
		conf.set("mapred.jar", "D:\\Documents\\workspace-sts-3.6.1.RELEASE\\Hadoop\\IndexBuilder.jar");
		
		Job job = IndexBuilder.configureJob(conf, "Index Builder");
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

 