 package com.hadoop.examples.readData;

import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
 /** 
* @ClassName: URLCat 
* @Description: 通过URLStreamHandler模拟Linux的cat命令：将Hadoop文件系统中的文件内容打印在Terminal 
* @author qiugui 
* @date 2015年1月8日 上午10:37:54 
*  
*/ 
public class URLCat {

	static{
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}
	
	public static void main(String[] args) throws Exception{
		
		InputStream in=null;
		
		try {
			
			in = new URL(args[0]).openStream();
			
			//复制文件
			IOUtils.copyBytes(in, System.out, 4096, false);
		
		} finally {
			
			IOUtils.closeStream(in);
		
		}
	}
}

 