package com.hadoop.examples.logAnalysiser;

import java.io.File;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class LogAnalysiser extends Configured implements Tool {
    public static class ReduceClass extends MapReduceBase implements Reducer<Text, LongWritable,Text, LongWritable> 
        {
            public void reduce(Text key, Iterator<LongWritable> values,
                    OutputCollector<Text, LongWritable> output, Reporter reporter)throws IOException
            {
                Text newkey = new Text();
                newkey.set(key.toString().substring(key.toString().indexOf("::")+1));
                LongWritable result = new LongWritable();
                long tmp = 0;
                int counter = 0;
                while(values.hasNext())//�ۼ�ͬһ��key��ͳ�ƽ��
                {
                    tmp = tmp + values.next().get();
                    
                    counter = counter +1;//���Ĵ���̫�ã�JobTracker��ʱ��û���յ��������ΪTaskTracker�Ѿ�ʧЧ����˶�ʱ����һ��
                    if (counter == 1000)
                    {
                        counter = 0;
                        reporter.progress();
                    }
                }
                result.set(tmp);
                output.collect(newkey, result);//������Ļ��ܽ��
            }    
        }
   
   
    public static class PartitionerClass implements Partitioner<Text, LongWritable>
        {
            public int getPartition(Text key, LongWritable value, int numPartitions)
            {
                if (numPartitions >= 2)//Reduce �������ж��������Ǵ�����ͳ�Ʒ��䵽��ͬ��Reduce
                    if (key.toString().startsWith("logLevel::"))
                        return 0;
                    else if(key.toString().startsWith("moduleName::"))
                        return 1;
                    else return 0;
                else
                    return 0;
            }
            public void configure(JobConf job){}    
    }
   
   
    public static class MapClass extends MapReduceBase 
            implements Mapper<LongWritable, Text, Text, LongWritable> 
        {
            public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter)
                    throws IOException
            {    
                String line = value.toString();//û������RecordReader������Ĭ�ϲ���line��ʵ�֣�key�����кţ�value����������
                System.out.println("line=="+line);
                if (line == null || line.equals(""))
                    return;
                String[] words = line.split(" ");
                if (words == null || words.length < 3)
                    return;
                String logLevel = words[1];
                String moduleName = words[2];
                LongWritable recbytes = new LongWritable(1);
                Text record = new Text();
                record.set(new StringBuffer("logLevel::").append(logLevel).toString());
                reporter.progress();
                output.collect(record, recbytes);//�����־����ͳ�ƽ����ͨ��logLevel::��Ϊǰ׺����ʾ��
                record.clear();
                record.set(new StringBuffer("moduleName::").append(moduleName).toString());
                System.out.println("output key=="+record.toString());
                output.collect(record, new LongWritable(1));//���ģ������ͳ�ƽ����ͨ��moduleName::��Ϊǰ׺����ʾ
            }    
        }
   
   
    public static void main(String[] args)
            {
                    try
                    {
            int res;
            res = ToolRunner.run(new Configuration(),new LogAnalysiser(), args);
            System.exit(res);
                    } catch (Exception e)
                    {
                            e.printStackTrace();
                    }
            }
            public int run(String[] args) throws Exception
            {
                    if (args == null || args.length <2)
                    {
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
                    SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
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
                    if (!inputdir.exists() || !inputdir.isDirectory())
                    {
                            System.out.println("inputpath not exist or isn't dir!");
                            return 0;
                    }
                    if (!outputdir.exists())
                    {
                            new File(outputpath).mkdirs();
                    }
                    
                    JobConf conf = new JobConf(getConf(),LogAnalysiser.class);//����Config
//                    FileSystem fileSys = FileSystem.get(conf);
//                    System.out.println("localDir=="+inputpath);
//                    System.out.println("dfs dir=="+shortin);
//                    fileSys.copyFromLocalFile(new Path(inputpath), new Path(shortin));//�������ļ�ϵͳ���ļ�������HDFS��

                    conf.setJobName("analysisjob");
                    conf.setOutputKeyClass(Text.class);//�����key���ͣ���OutputFormat����
                    conf.setOutputValueClass(LongWritable.class); //�����value���ͣ���OutputFormat����
//                    conf.setJarByClass(MapClass.class);
//                    conf.setJarByClass(ReduceClass.class);
//                    conf.setJarByClass(PartitionerClass.class);
//                    conf.setJar("hadoopTest.jar");
                    conf.setJarByClass(getClass());
                    conf.setMapperClass(MapClass.class);
                    conf.setReducerClass(ReduceClass.class);
                    conf.setPartitionerClass(PartitionerClass.class);
                    conf.set("mapred.reduce.tasks", "2");//ǿ����Ҫ������Reduce���ֱ��������ʹ�����ͳ��
                    FileInputFormat.setInputPaths(conf, shortin);//hdfs�е�����·��
                    FileOutputFormat.setOutputPath(conf, new Path(shortout));//hdfs�����·��
                    
                    Date startTime = new Date();
                    System.out.println("Job started: " + startTime);
                    JobClient.runJob(conf);
                    
                    Date end_time = new Date();
                    System.out.println("Job ended: " + end_time);
                    System.out.println("The job took " + (end_time.getTime() - startTime.getTime()) /1000 + " seconds.");
                    //ɾ��������������ʱ�ļ�
//                    fileSys.copyToLocalFile(new Path(shortout),new Path(outputpath));
//                    fileSys.delete(new Path(shortin),true);
//                    fileSys.delete(new Path(shortout),true);
                    return 0;
            }

    
}
