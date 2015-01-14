 package com.hadoop.examples.compress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;
 /** 
* @ClassName: StreamCompressor 
* @Description: 压缩从标准输入读取的数据，然后将其写到标准输出 
* @author qiugui 
* @date 2015年1月12日 上午9:33:53 
*  
*/ 
public class StreamCompressor {

	public static void main(String[] args) throws Exception {
		String codecClassname = args[0];
		Class<?> codecClass = Class.forName(codecClassname);
		Configuration conf = new Configuration();
		
		//实例化压缩格式类
		CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, conf);
		CompressionOutputStream out = codec.createOutputStream(System.out);
		IOUtils.copyBytes(System.in, out, 4096, false);
		out.finish();
	}
}

 