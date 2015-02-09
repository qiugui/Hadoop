 package com.hadoop.examples.pig.UDF;

import java.io.IOException;

import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;
 public class IsGoodQuality extends FilterFunc{

	@Override
	public Boolean exec(Tuple tuple) throws IOException {
		if (tuple==null || tuple.size()==0){
			return false;
		}
		try {
			Object object = tuple.get(0);
			if (object==null){
				return false;
			}
			int i=(int)object;
			return i==0 || i==1 || i==4 || i==5 || i==9;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	 
}

 