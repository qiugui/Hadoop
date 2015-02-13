 package com.hadoop.examples.hive.UDF;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.DoubleWritable;
 /** 
* @ClassName: Average 
* @Description: 求一组数平均值的聚合函数
* @author qiugui 
* @date 2015年2月12日 上午11:55:53 
*  
*/ 
@SuppressWarnings("deprecation")
public class Average extends UDAF{
	
	public static class AverageUDAFEvaluate implements UDAFEvaluator{
		public static class PartialResult{
			double sum;
			int count;
		}
		
		private PartialResult partialResult;
		
		public void init(){
			partialResult=null;
		}
		
		public boolean iterate(double value){
//			if(value==null){
//				return true;
//			}
			if(partialResult==null){
				partialResult=new PartialResult();
			}else{
				partialResult.sum+=value;
				partialResult.count++;
			}
			return true;
		}
		
		public PartialResult terminatePartial() {
			return partialResult;
		}
		
		public boolean merge(PartialResult other){
			if(other == null){
				return true;
			}
			
			if(partialResult == null){
				partialResult = new PartialResult();
			}
			partialResult.sum += other.sum;
			partialResult.count += other.count;
			return true;
		}
		
		public double terminate(){
//			if(partialResult == null){
//				return null;
//			}
			return partialResult.sum/partialResult.count;
		}
	}
}

 