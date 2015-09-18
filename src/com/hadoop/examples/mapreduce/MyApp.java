 package com.hadoop.examples.mapreduce;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
 /** 
  * @ClassName: MyApp 
  * @author qiugui 
  * @date 2015年1月15日 上午9:48:14 
  * @Description: 一个标准的MapReduce程序流程图 
  * 
  *  1.编写一个MapReduce类,继承Configured类，并且实现Tool接口；
  *  2.编写一个MapClass类，继承Mapper类，实现map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)方法；
  *  3.编写一个ReduceClass类，继承Reducer类，实现reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)方法；
  *  4.编写一个PartitionerClass类，继承Partitioner类，实现getPartition(java.lang.Object, java.lang.Object, int)方法；
  *  5.重写Tool接口中的run(String[] args)函数；
  *  6.编写一个主函数main，返回 System.exit(res = ToolRunner.run(new Configuration(), new MyApp(), args))。
  *  
  */

/**********************************************************************************
 * org.apache.hadoop.conf.Configured											  *
 * 		Base class for things that may be configured with a Configuration.        *
 * 		Configured基础类，可由Configuration类进行配置									  *
 * 																				  *
 * org.apache.hadoop.util.Tool													  *
 * 		A tool interface that supports handling of generic command-line options.  *
 * 		Tool接口，它支持通用的命令行选项的处理，需重写run(String[] args)函数					  *
 * 																				  *
 **********************************************************************************/
public class MyApp extends Configured implements Tool{

	/****************************************************************************************************** 
	* @ClassName: MapClass  																			  *
	* @author qiugui 																					  *
	* @date 2015年1月15日 上午10:38:01 																		  *
	* @Description: 																					  *
	* 		Maps input key/value pairs to a set of intermediate key/value pairs.						  *
	* 		将输入的key/value对 映射到 中间的key/value对															  *
	*  																									  *
	*  		The framework 																				  *
	*  			1.first calls setup(org.apache.hadoop.mapreduce.Mapper.Context).Called once at the 		  *
	*  			beginning of the task.;						 											  *
	*  				map任务一旦开始，便调用setup()函数。在任务开始时调用一次											  *
	*  			2.followed by map(Object, Object, Context) for each key/value pair in the InputSplit.	  * 
	*  			Called once for each key/value pair in the input split.;	  							  *
	*  				紧接着，在输入分片中，对每一个key/value对执行map()函数。输入分片中的每一对key/value执行一次				  *
	*  			3.Finally cleanup(Context) is called. Called once at the end of the task.				  *
	*  				最后，调用cleanup()函数。在任务结束时调用一次													  *
	*  																									  *
	*  		All intermediate values associated with a given output key are subsequently grouped by the 	  *
	*  framework, and passed to a Reducer(class) to determine the final output. Users can control the 	  *
	*  sorting and grouping by specifying two key RawComparator(class) classes.							  *
	*  		所有与给定输出key关联的中间value，紧接着被框架分组，并且传递给一个Reducer来确定最终输出。用户可以通过指定两个RawComparator	  *
	*  （原生比较器）类来控制排序和分组。																			  *
	*  		The Mapper outputs are partitioned per Reducer. Users can control which keys (and hence 	  *
	*  records) go to which Reducer by implementing a custom Partitioner(class).						  *
	*  		Mapper的输出值被分割到每个Reducer。用户可以通过实现一个自定义Partitioner类，来控制哪些key（和对应的record）输出给哪些	  *
	*  Reducer。																							  *
	*  		Users can optionally specify a combiner, via Job.setCombinerClass(Class), to perform local 	  *
	*  aggregation of the intermediate outputs, which helps to cut down the amount of data transferred 	  *
	*  from the Mapper to the Reducer.																	  *
	*  		用户可以选择性地，通过Job.setCombinerClass(Class)指定一个合并器combiner，来执行中间输出的本地聚合，这样有助于减少从	  *
	*  Mapper到Reducer的数据传输量。																			  *
	*  		Applications can specify if and how the intermediate outputs are to be compressed and which   *
	*  CompressionCodecs(interface) are to be used via the Configuration.								  *
	*  		应用程序可以指定是否以及如何压缩中间的输出值，以及通过配置使用哪种压缩接口CompressionCodecs(interface)。				  *
	*  																									  *
	*******************************************************************************************************/ 
	public static class MapClass extends Mapper<Object, Text, Text, IntWritable>{
		
		private Text record = new Text();
		private final static IntWritable one = new IntWritable(1);
		
		/************************************************************************************************************   
		 * <p>Title: map</p>																						*   
		 * <p>Description: 																							*
		 * 			Called once for each key/value pair in the input split. Most applications 						*
		 * 		should override this, but the default is the identity function.</p>   								*
		 * @param key - KEYIN																						*
		 * @param value - VALUEIN																					*
		 * @param context - org.apache.hadoop.mapreduce.Mapper.Context context										*
		 * @throws IOException																						*
		 * @throws InterruptedException   																			*
		 * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)  *
		 * 																											* 
		 ************************************************************************************************************/
		 
		public void map(Object key, Text value, Context context) 
				throws IOException, InterruptedException {
			
			//***********分词案例
			/*StringTokenizer itr = new StringTokenizer(value.toString());
		     
			while (itr.hasMoreTokens()) {
		       record.set(itr.nextToken());
		       context.write(record, one);
		     }*/
			
			//***********日志分析案例
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

			// 输出日志级别统计结果，通过 logLevel:: 作为前缀来标示。
			record.clear();
			record.set(new StringBuffer("logLevel::").append(logLevel)
					.toString());
			context.write(record, one);

			// 输出模块名的统计结果，通过 moduleName:: 作为前缀来标示
			record.clear();
			record.set(new StringBuffer("moduleName::").append(moduleName)
					.toString());
			context.write(record, one);
			
		}
	}
	
	
	/********************************************************************************************************* 
	* @ClassName: ReduceClass  																				 *
	* @author qiugui 																						 *
	* @date 2015年1月15日 上午11:18:45 																			 *
	* @Description: 																						 *
	* 		Reduces a set of intermediate values which share a key to a smaller set of values.				 *
	* 		将 共享key的中间value 缩减为一个较小的value集　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　*
	* 																										 *
	* 		Reducer has 3 primary phases:																	 *
	* 		1.Shuffle																						 *
	* 				The Reducer copies the sorted output from each Mapper using HTTP across the network.	 *
	* 				Reducer利用HTTP协议，通过网络复制每个Mapper输出的已排序的值											 *
	* 		2.Sort																							 *
	* 				The framework merge sorts Reducer inputs by keys (since different Mappers may have 		 *
	* 			output the same key).The shuffle and sort phases occur simultaneously i.e. while outputs	 * 
	* 			are being fetched they are merged.															 *
	* 				框架根据key合并排序Reducer的输入值（因为不同的Mapper可能输出相同的key）。混合和排序阶段同时发生，例如：当输出值取回时，	 *
	* 			    它们已经被合并																					 *
	* 																										 *
	* 				SecondarySort																			 *
	* 																										 *
	* 				To achieve a secondary sort on the values returned by the value iterator, the 			 *
	* 			application should extend the key with the secondary key and define a grouping comparator. 	 *
	* 			The keys will be sorted using the entire key, but will be grouped using the grouping 		 *
	* 			comparator to decide which keys and values are sent in the same call to reduce.The grouping  *
	* 			comparator is specified via Job.setGroupingComparatorClass(Class). The sort order is 		 *
	* 			controlled by Job.setSortComparatorClass(Class).											 *
	* 				想在值迭代器返回的值中实现二次排序，应用程序要用二级key来扩展key，并且定义一个分组比较器。keys将使用整个key来排序，但是会使用	 *
	* 			分组比较器分组，来决定哪些key和value在相同的调用中发送给reduce。分组比较器经过Job.setGroupingComparatorClass指定。	 *
	* 			排序顺序由Job.setSortComparatorClass控制。														 *
	* 																										 *
	* 				For example, say that you want to find duplicate web pages and tag them all with the 	 *
	* 			url of the "best" known example. You would set up the job like:								 *
	* 																										 *
	* 				Map Input Key: url																		 *
	* 				Map Input Value: document																 *
	* 				Map Output Key: document checksum, url pagerank											 *
	* 				Map Output Value: url																	 *
	* 				Partitioner: by checksum																 *
	* 				OutputKeyComparator: by checksum and then decreasing pagerank							 *
	* 				OutputValueGroupingComparator: by checksum												 *
	* 																										 *
	* 		3.Reduce																						 *
	* 				In this phase the reduce(Object, Iterable, Context) method is called for each <key, 	 *
	* 			(collection of values)> in the sorted inputs.												 *
	* 				The output of the reduce task is typically written to a RecordWriter via				 * 
	* 			TaskInputOutputContext.write(Object, Object).												 *
	* 				该阶段，在已排序的输入中，为每一个<key,(value集合)>调用reduce()函数。reduce任务的输出通常写到一个经过			 *
	* 			TaskInputOutputContext.write(Object, Object)指定的RecordWriter类。								 *
	* 																										 *
	* 		The output of the Reducer is not re-sorted.														 *
	*  		Reducer的输出不是再排序的																				 *
	*  																										 *
	**********************************************************************************************************/ 
	public static class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable>{
		
		private IntWritable result = new IntWritable();
		
		/****************************************************************************************************************************   
		 * <p>Title: reduce</p>   																									*
		 * <p>Description: 																											*
		 * 			This method is called once for each key. Most applications will define their reduce 							*
		 * 		class by overriding this method. The default implementation is an identity function.</p>   							*
		 * @param key - KEYIN																										*
		 * @param values - VALUEIN																									*
		 * @param context - org.apache.hadoop.mapreduce.Reducer.Context context														*
		 * @throws IOException																										*
		 * @throws InterruptedException   																							*
		 * @see org.apache.hadoop.mapreduce.Reducer#reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)  *
		 *  																														*
		 ****************************************************************************************************************************/
		 
		public void reduce(Text key, Iterable<IntWritable> values, Context context) 
			   throws IOException, InterruptedException {
			//*******分词/日志分析 案例
			int sum = 0;
	    	for (IntWritable val : values) {
	    		sum += val.get();
	    	}
	    	result.set(sum);
	    	context.write(key, result);
		}
	}
	
	
	/************************************************************************************************************* 
	* @ClassName: PartitionerClass 																				 * 
	* @author qiugui 																							 *
	* @date 2015年1月15日 下午2:01:26 																				 *
	* @Description: 																							 *
	* 		Partitions the key space.																			 *
	* 		分割key空间																							 *
	* 																											 *
	* 		Partitioner controls the partitioning of the keys of the intermediate map-outputs. The key (or 		 *
	* 	a subset of the key) is used to derive the partition, typically by a hash function. The	total number	 * 
	* 	of partitions is the same as the number of reduce tasks for the job. Hence this controls which of 		 *
	* 	the m reduce tasks the intermediate key (and hence the record) is sent for reduction.					 *
	* 		分割器控制着map输出的中间值的 key的分割。key（或是key的子集）通常是经过一个哈希函数来导出不同的部分。划分的总数与job的reduce的任务数相	 *
	* 	同。因此，这用来控制m个reduce任务中哪一个中间的key（和该记录）被发送至reduction。											 *
	* 																											 *
	* 		Note: If you require your Partitioner class to obtain the Job's configuration object, implement 	 *
	* 	the Configurable interface.																				 *
	*  		如果你需要你的Partitioner类获得Job的configuration对象，需实现Configurable接口									 *
	*  																											 *
	**************************************************************************************************************/ 
	public static class PartitionerClass extends Partitioner<Text, IntWritable> {
		
		
		/******************************************************************************************************   
		 * <p>Title: getPartition</p>   																	  *
		 * <p>Description: 																					  *
		 * 			Get the partition number for a given key (hence record) given the total number of 		  *
		 * 		partitions i.e. number of reduce-tasks for the job.											  *
		 * 			根据给定的分区总数，也就是job中reduce的任务数，获取给定key（相应的record）的分区号，							  *
		 * 																									  *
		 * 			Typically a hash function on a all or a subset of the key.								  *
		 * 			通常是在全部或者子集的key上，使用哈希函数</p>   														  *
		 * @param key - the key to be partioned.															  *
		 * @param value - the entry value.																	  *
		 * @param numPartitions - the total number of partitions.											  *
		 * @return the partition number for the key.  														  *
		 * @see org.apache.hadoop.mapreduce.Partitioner#getPartition(java.lang.Object, java.lang.Object, int) *
		 *    																								  *
		 ******************************************************************************************************/
		 
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
	
	
	
	
	
	/***************************************************************************   
	 * @Title: main   														   *   
	 * @param args 															   *
	 * @Description: 主函数，用于调用ToolRunner类的run函数							   *
	 *       																   *
	 *        																   *
	 * org.apache.hadoop.util.ToolRunner									   *
	 * 		Be used to run classes implementing Tool interface.It works in 	   *
	 * 		conjunction with GenericOptionsParser to parse the generic hadoop  *
	 * 		command line arguments and modifies the Configuration of the Tool. *
	 * 		用来运行实现Tool接口的类。它配合GenericOptionsParser类来解析通用的hadoop命令行参数，   *
	 * 		并且修改Tool的配置。													   *
	 * 																		   *
	 ***************************************************************************/
	 
	public static void main(String[] args){
		int res;
		try {
			
			/************************************************************************************************
			 * public static int run(Configuration conf,Tool tool,String[] args)throws Exception			*
			 * 																								*
             * Runs the given Tool by Tool.run(String[]), after parsing with the given generic arguments. 	*
             * Uses the given Configuration, or builds one if null. Sets the Tool's configuration with the	*
             * possibly modified version of the conf.														*
             * Parameters:																					*
             *		conf - Configuration for the Tool.														*
			 *		tool - Tool to run.																		*
			 *		args - command-line arguments to the tool.												*
			 * Returns:																						*
			 *		exit code of the Tool.run(String[]) method.												*
			 * Throws:																						*
			 *		Exception																				*
			 * 																								*
			 ************************************************************************************************/
			res = ToolRunner.run(new Configuration(), new MyApp(), args);
			System.exit(res);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	/********************************************************************   
	 * <p>Title: run</p>   												*
	 * <p>Description: Execute the command with the given arguments.</p>* 
	 *		根据指定的参数执行命令  												*
	 * @param args - command specific arguments.						*
	 * @return exit code.												*
	 * @throws Exception.   											*
	 * @see org.apache.hadoop.util.Tool#run(java.lang.String[])   		*
	 * 																	*
	 ********************************************************************/
	@Override
	public int run(String[] args) throws Exception {
		
		if (args == null || args.length < 2) {
			System.out.println("need inputpath and outputpath");
			return 1;
		}
		
		String shortin = args[0];
		String shortout = args[1];
		
		// Configuration processed by ToolRunner
        Configuration conf = getConf();
        
        conf.set("mapred.remote.os", "Linux");
		conf.set("fs.defaultFS", "hdfs://192.168.216.130:9000");
		conf.set("yarn.resourcemanager.address", "192.168.216.130:8032");
		conf.set("mapreduce.framework.name", "yarn");
		//Error:/bin/bash: line 0: fg: no job control
		conf.set("mapreduce.app-submission.cross-platform", "true");
		conf.set("mapred.jar", "D:\\Documents\\workspace-sts-3.6.1.RELEASE\\Hadoop\\WordCount.jar");
        
        Job job = Job.getInstance(conf, "MyApp");
        //或者用下面的两行代码
        //Job job = Job.getInstance(conf);
        //job.setJobName("MyApp");
        
        job.setJarByClass(MyApp.class);
        job.setMapperClass(MapClass.class);
        job.setCombinerClass(ReduceClass.class);
        job.setReducerClass(ReduceClass.class);
        job.setPartitionerClass(PartitionerClass.class);
        job.setNumReduceTasks(2);
        
        FileInputFormat.setInputPaths(job, new Path(shortin));
        FileOutputFormat.setOutputPath(job, new Path(shortout));
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        Date startTime = new Date();
		System.out.println("Job started: " + startTime);
		
		job.waitForCompletion(true);
		
		Date endTime = new Date();
		System.out.println("Job ended: " + endTime);
		
		System.out.println("The job took "
				+ (endTime.getTime() - startTime.getTime()) / 1000
				+ " seconds.");

		//删除输入和输出的临时文件
		FileSystem fs = FileSystem.get(conf);
		fs.copyToLocalFile(new Path(shortout), new Path("/usr/hadoop/data"));
		fs.delete(new Path(shortin), true);
		fs.delete(new Path(shortout), true);
        
		return 0;
		 
	}

}

 
