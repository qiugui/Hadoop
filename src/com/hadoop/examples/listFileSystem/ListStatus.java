 package com.hadoop.examples.listFileSystem;

import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

 /** 
* @ClassName: ListStatus 
* @Description: 查询文件系统
* @author qiugui 
* @date 2015年1月8日 下午2:26:43 
*  
*/ 
public class ListStatus {

	static FileSystem fs=null;
	static String uri = null;
	static Configuration conf = null;
	
	public static void main(String[] args) throws Exception{
		ListStatus listStatus = new ListStatus();
		
		uri = args[0];
		conf = new Configuration();
		fs = FileSystem.get(URI.create(uri), conf);
		
		Path[] paths = new Path[args.length];
		
		//循环出所有的路径
		for (int i=0;i<paths.length;i++ ){
			paths[i]=new Path(args[i]);
		}
		
		//将每一个文件路径传入并调用listStatus()方法，并将FileStatus对象存入数组中
		FileStatus[] status = fs.listStatus(paths);
		
		System.out.println("**********所有文件系统***********");
		
		for (FileStatus f : status){
			listStatus.listFile(f);
		}
		
		System.out.println("**********Path***********");
		
		//将FileStatus对象数组转成Path对象
		Path[] listedPaths = FileUtil.stat2Paths(status);
		
		//打印出文件系统的信息
		for (Path p : listedPaths){
			System.out.println(p.getName());
		}	
		
	}
	
	
	/**   
	 * @Title: listFile   
	 * @Description: 利用递归将文件夹里的文件列出   
	 * @param status
	 * @throws Exception        
	 */
	 
	public void listFile(FileStatus status) throws Exception{
		if(status.isFile()){
			
			System.out.println("包含文件"+status.getPath().toString()+" "+status.getGroup()+" "+status.getOwner()+" "+status.getPermission().toString());
//			System.out.println("文件内容");
//			this.catFile(status.getPath());

		} else {
			System.out.println("");
			System.out.println("文件夹  "+status.getPath().toString()+" "+status.getGroup()+" "+status.getOwner()+" "+status.getPermission().toString());
			
			//删除对应的文件delete(Path path, boolean recurisive)，其中涉及到权限问题，可修改权限hadoop fs -chmod 755 /
//			if("hdfs://Master.hadoop:9000/system".equals(status.getPath().toString())){
//				System.out.println("删除该文件夹");
//				fs.delete(status.getPath(), true);
//			}
			Path path = new Path(status.getPath().toString());
			FileStatus[] statu = fs.listStatus(path);
			for (FileStatus f : statu){
				this.listFile(f);
			}
		}
	}
	
	/**   
	 * @Title: catFile   
	 * @Description: 列出文件内容   
	 * @param path
	 * @throws Exception        
	 */
	public void catFile(Path path) throws Exception{
		InputStream in = null ;
		
		in = fs.open(path);
		
		IOUtils.copyBytes(in, System.out, 4096, true);
		
		IOUtils.closeStream(System.out);
	}
}

 