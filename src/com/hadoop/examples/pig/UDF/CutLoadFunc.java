 package com.hadoop.examples.pig.UDF;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.pig.LoadFunc;
import org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigSplit;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

import com.google.common.collect.Range;


 public class CutLoadFunc extends LoadFunc{

	 private static final Log log = LogFactory.getLog(CutLoadFunc.class);
	 private final List<Range> ranges;
	 private final TupleFactory tupleFactory = TupleFactory.getInstance();
	 private final RecordReader reader;
	 
	 
	 public CutLoadFunc(String cutPattern) {
		ranges = Range.pa

	}
	 
	@Override
	public InputFormat getInputFormat() throws IOException {
		// TODO Auto-generated method stub
		 return null;
		 
	}

	@Override
	public Tuple getNext() throws IOException {
		// TODO Auto-generated method stub
		 return null;
		 
	}

	@Override
	public void prepareToRead(RecordReader reader, PigSplit split)
			throws IOException {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void setLocation(String location, Job job) throws IOException {
		// TODO Auto-generated method stub
		 
	}

}

 