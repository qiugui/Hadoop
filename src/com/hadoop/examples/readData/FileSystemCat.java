 package com.hadoop.examples.readData;

import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
 /** 
* @ClassName: FileSystemCat 
* @Description: 通过FileSystem模拟Linux的cat命令：将Hadoop文件系统中的文件内容打印在Terminal 
* @author qiugui 
* @date 2015年1月8日 上午11:31:44 
*  
*/ 
public class FileSystemCat {

	public static void main (String[] args) throws Exception{
		
		//获得hdfs文件系统中文件的地址
		String uri = args[0];
		
		//获得hdfs文件系统的端口等配置
		Configuration conf = new Configuration();
		
		//获得hdfs所使用的文件系统实例
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		
		InputStream in = null;
		
		//FSDataInputStream in = null;
		
		try {
			
			//获得输入流
			in = fs.open(new Path(uri));
			
			IOUtils.copyBytes(in, System.out, 4096, false);
			
			/**
			 * hadoop中的输入流 FSDataInputStream实现了接口seekable，
			 * 其中的seek()方法可以移动到文件中任意位置
			 * 
			 */
//			in.seek(0);
//			IOUtils.copyBytes(in, System.out, 4096, false);
		
		} finally {
			
			IOUtils.closeStream(in);
		
		}
	}
}

 