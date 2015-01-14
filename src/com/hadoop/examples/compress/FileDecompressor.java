 package com.hadoop.examples.compress;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
 /** 
* @ClassName: FileDecompressor 
* @Description: 通过由文件扩展名推断而来的codec来对文件进行解压缩 
* @author qiugui 
* @date 2015年1月12日 上午9:55:43 
*  
*/ 
public class FileDecompressor {

	public static void main(String[] args) throws Exception {
		String uri = args[0];
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		
		Path inputPath = new Path(uri);
		CompressionCodecFactory factory = new CompressionCodecFactory(conf);
		CompressionCodec codec = factory.getCodec(inputPath);
		
		if (codec == null){
			System.err.println("No codec found for " + uri);
			System.exit(1);
		}
		
		//获得输出路径
		String outputUri = CompressionCodecFactory.removeSuffix(uri, codec.getDefaultExtension());
		
		InputStream in = null;
		OutputStream out = null;
		
		try {
			in = codec.createInputStream(fs.open(inputPath));
			out = fs.create(new Path(outputUri));
			IOUtils.copyBytes(in, out, conf);
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}

	}

}

 