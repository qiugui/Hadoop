 package com.hadoop.examples.hive.UDF;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 /** 
* @ClassName: Strip 
* @Description: 剪除字符串的操作 
* @author qiugui 
* @date 2015年2月12日 上午9:27:30 
*  
*/ 
public class Strip extends UDF{

	private Text result=new Text();
	
	public Text evaluate(Text text){
		if(text==null){
			return null;
		}
		
		result.set(StringUtils.strip(text.toString()));
		
		return result;
	}
	
	public Text evaluate(Text text,String str){
		if(text==null){
			return null;
		}
		result.set(StringUtils.strip(text.toString(), str));
		return result;
	}
}

 