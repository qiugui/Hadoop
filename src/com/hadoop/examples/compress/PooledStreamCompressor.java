 package com.hadoop.examples.compress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.util.ReflectionUtils;
 /** 
* @ClassName: PooledStreamCompressor 
* @Description: 使用压缩池对读取自标准输入的数据进行压缩，然后将其写到标准输出 
* @author qiugui 
* @date 2015年1月12日 上午10:19:38 
*  
*/ 
public class PooledStreamCompressor {

	public static void main(String[] args) throws Exception {
		String codecClassname = args[0];
		Class<?> codecClass = Class.forName(codecClassname);
		Configuration conf = new Configuration();
		
		CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, conf);
		Compressor compressor = null;
		
		try {
			compressor = CodecPool.getCompressor(codec); 
			
			//通过codec的重载方法createOutputStream()中，对于指定的CompressionCodec，从池中获取一个Compressor实例
			CompressionOutputStream out = codec.createOutputStream(System.out, compressor);
			IOUtils.copyBytes(System.in, out, 4096, false);
			out.finish();
		} finally {
			//作用：在不同的数据流之间来回复制数据，即使出现IOException异常，也可以确保compressor可以返回池中
			CodecPool.returnCompressor(compressor);
		}
		

	}

}

 