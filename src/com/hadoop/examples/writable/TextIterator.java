 package com.hadoop.examples.writable;

import java.nio.ByteBuffer;

import org.apache.hadoop.io.Text;
 /** 
* @ClassName: TextIterator 
* @Description: 遍历Text对象中的字符 
* @author qiugui 
* @date 2015年1月12日 下午3:34:08 
*  
*/ 
public class TextIterator {

	public static void main (String[] args){
		Text t = new Text("\u0041\u00DF\u6771\uD801\uDC00");
		ByteBuffer buf = ByteBuffer.wrap(t.getBytes(),0,t.getLength());
		
		int cp;
		
		while(buf.hasRemaining() && (cp = Text.bytesToCodePoint(buf))!= -1){
			System.out.println(Integer.toHexString(cp));
		}
	}
	
}

 