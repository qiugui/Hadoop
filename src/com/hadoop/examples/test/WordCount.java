 package com.hadoop.examples.test;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 /** 
* @ClassName: WordCount 
* @Description: WordCount实例 
* @author qiugui 
* @date 2015年1月13日 上午9:33:13 
*  
*/ 
public class WordCount {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()){
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}
	
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result = new IntWritable();
		
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int sum = 0;
			for (IntWritable val : values){
				sum +=val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		//conf.addResource("classpath:mapred-site.xml");
	    //conf.set("fs.defaultFS", "hdfs://192.168.216.130:9000");
	    //conf.set("mapreduce.framework.name", "yarn");
	    //conf.set("yarn.resourcemanager.address", "192.168.216.130:8032");
	    //conf.set("mapreduce.jobhistory.address", "192.168.216.130:10020");
	    //conf.set("mapred.remote.os", "Linux");
	    //conf.set("hadoop.job.ugi", "hadoop,hadoop");
		conf.set("fs.defaultFS", "hdfs://192.168.216.130:9000");
		conf.set("yarn.resourcemanager.address", "192.168.216.130:8032");
		conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.application.classpath", "$HADOOP_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*");

		Job job = Job.getInstance(conf, "my word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

 