 package com.hadoop.examples.test;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
 
 /** 
* @ClassName: StringTextComparisonTest 
* @Description: String和Text的不同 
* @author qiugui 
* @date 2015年1月12日 下午2:39:30 
*  
*/ 
public class StringTextComparisonTest {

	@Test
	public void string(){
		String s = "\u0041\u00DF\u6771\uD801\uDC00";
		Assert.assertEquals(s.length(), 5);
		Assert.assertEquals(s.indexOf("\u0041"), 0);
		Assert.assertEquals(s.charAt(0), '\u0041');
		Assert.assertEquals(s.codePointAt(0), 0x0041);
	}
	
	@Test
	public void text(){
		Text t = new Text("\u0041\u00DF\u6771\uD801\uDC00");
		Text text = new Text("hadoop");
		Assert.assertEquals(text.toString(), "hadoop");
		Assert.assertEquals(t.getLength(), 10);
	}
}

 