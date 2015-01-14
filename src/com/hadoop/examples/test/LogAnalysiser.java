package com.hadoop.examples.test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LogAnalysiser extends Configured implements Tool {

	
	
	public static class MapClass extends
			Mapper<Object, Text, Text, IntWritable> {
		private Text record = new Text();
		private static final IntWritable recbytes = new IntWritable(1);

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			// 没有配置 RecordReader，所以默认采用 line 的实现，
			// key 就是行号，value 就是行内容，
			// 按行 key-value 存放每行 loglevel 和 logmodule 内容
			if (line == null || line.equals(""))
				return;
			String[] words = line.split("> <");
			if (words == null || words.length < 2)
				return;
			String logLevel = words[1];
			String moduleName = words[2];

			record.clear();
			record.set(new StringBuffer("logLevel::").append(logLevel)
					.toString());
			context.write(record, recbytes);
			// 输出日志级别统计结果，通过 logLevel:: 作为前缀来标示。

			record.clear();
			record.set(new StringBuffer("moduleName::").append(moduleName)
					.toString());
			context.write(record, recbytes);
			// 输出模块名的统计结果，通过 moduleName:: 作为前缀来标示
		}
	}

	
	
	public static class PartitionerClass extends Partitioner<Text, IntWritable> {
		public int getPartition(Text key, IntWritable value, int numPartitions) {
			if (numPartitions >= 2)
				// Reduce 个数，判断 loglevel 还是 logmodule
				// 的统计，分配到不同的 Reduce
				if (key.toString().startsWith("logLevel::"))
					return 0;
				else if (key.toString().startsWith("moduleName::"))
					return 1;
				else
					return 0;
			else
				return 0;
		}

	}

	
	
	public static class ReduceClass extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			int tmp = 0;
			for (IntWritable val : values) {
				tmp = tmp + val.get();
			}
			result.set(tmp);
			context.write(key, result);// 输出最后的汇总结果
		}
	}

	
	
	public static void main(String[] args) {
		try {
			int res;
			res = ToolRunner
					.run(new Configuration(), new LogAnalysiser(), args);
			System.exit(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int run(String[] args) throws Exception {
		if (args == null || args.length < 2) {
			System.out.println("need inputpath and outputpath");
			return 1;
		}
		String inputpath = args[0];
		String outputpath = args[1];
		String shortin = args[0];
		String shortout = args[1];
		if (shortin.indexOf(File.separator) >= 0)
			shortin = shortin.substring(shortin.lastIndexOf(File.separator));
		if (shortout.indexOf(File.separator) >= 0)
			shortout = shortout.substring(shortout.lastIndexOf(File.separator));
		SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
		shortout = new StringBuffer(shortout).append("-")
				.append(formater.format(new Date())).toString();

		if (!shortin.startsWith("/"))
			shortin = "/" + shortin;
		if (!shortout.startsWith("/"))
			shortout = "/" + shortout;
		shortin = "/user/oracle/dfs/" + shortin;
		shortout = "/user/oracle/dfs/" + shortout;
		File inputdir = new File(inputpath);
		File outputdir = new File(outputpath);

		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.out.println("inputpath not exist or isn't dir!");
			return 0;
		}
		if (!outputdir.exists()) {
			new File(outputpath).mkdirs();
		}
		// 以下注释的是 hadoop 0.20.X 老版本的 Job 代码，在 hadoop0.23.X 新框架中已经大大简化
		// Configuration conf = getConf();
		// JobConf job = new JobConf(conf, LogAnalysiser.class);
		// JobConf conf = new JobConf(getConf(),LogAnalysiser.class);// 构建
		// Config
		// conf.setJarByClass(MapClass.class);
		// conf.setJarByClass(ReduceClass.class);
		// conf.setJarByClass(PartitionerClass.class);
		// conf.setJar("hadoopTest.jar");
		// job.setJar("hadoopTest.jar");

		// 以下是新的 hadoop 0.23.X Yarn 的 Job 代码

		Job job = Job.getInstance(new Configuration(), "Log Analysiser");
		//job job = new Job(new Configuration());
		job.setJarByClass(LogAnalysiser.class);
		job.setJobName("analysisjob");
		job.setOutputKeyClass(Text.class);// 输出的 key 类型，在 OutputFormat 会检查
		job.setOutputValueClass(IntWritable.class); // 输出的 value 类型，在
													// OutputFormat 会检查
		job.setJarByClass(LogAnalysiser.class);
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(ReduceClass.class);
		job.setReducerClass(ReduceClass.class);
		job.setPartitionerClass(PartitionerClass.class);
		job.setNumReduceTasks(2);// 强制需要有两个 Reduce 来分别处理流量和次数的统计
		FileInputFormat.setInputPaths(job, new Path(shortin));// hdfs 中的输入路径
		FileOutputFormat.setOutputPath(job, new Path(shortout));// hdfs 中输出路径

		Date startTime = new Date();
		System.out.println("Job started: " + startTime);
		job.waitForCompletion(true);
		Date end_time = new Date();
		System.out.println("Job ended: " + end_time);
		System.out.println("The job took "
				+ (end_time.getTime() - startTime.getTime()) / 1000
				+ " seconds.");
		// 删除输入和输出的临时文件
		// fileSys.copyToLocalFile(new Path(shortout),new Path(outputpath));
		// fileSys.delete(new Path(shortin),true);
		// fileSys.delete(new Path(shortout),true);
		return 0;
	}
}
