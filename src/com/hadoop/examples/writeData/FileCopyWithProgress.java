 package com.hadoop.examples.writeData;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
 /** 
* @ClassName: FileCopyWithProgress 
* @Description: 将本地文件复制到Hadoop文件系统
* 				
* 				注：如果打包成jar包，将其放入linux中，其args的两个参数分别为
* 					linux的文件系统(/home/hadoop/*.*) hdfs文件系统
* 				        如果是在Windows中的Eclipse运行该java文件，其args的两个参数分别为
* 					Windows的文件系统(c:/*.*) hdfs文件系统
* 
* @author qiugui 
* @date 2015年1月8日 上午11:53:08 
*  
*/ 
public class FileCopyWithProgress {

	public static void main(String[] args) throws Exception{
		
		//获得本地文件地址
		String localSrc = args[0];
		
		//获得hdfs文件系统的地址
		String dst = args[1];
		
		InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
		
		Configuration conf = new Configuration(); 
		
		FileSystem fs =  FileSystem.get(URI.create(dst), conf);
		
		//获得输出流并显示进度
		OutputStream out =  fs.create(new Path(dst),new Progressable() {
			
			@Override
			public void progress() {
				// TODO Auto-generated method stub
				System.out.println(".");
			}
		});
		
		IOUtils.copyBytes(in, out, 4096, true);
	}
}

 