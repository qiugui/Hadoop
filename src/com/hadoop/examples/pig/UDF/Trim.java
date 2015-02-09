 package com.hadoop.examples.pig.UDF;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
 public class Trim extends EvalFunc<String>{

	@Override
	public String exec(Tuple input) throws IOException {
		if (input==null || input.size()==0){
			return null;
		}
		try {
			Object object=input.get(0);
			if (object==null){
				return null;
			}
			return ((String)object).trim();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
}

 