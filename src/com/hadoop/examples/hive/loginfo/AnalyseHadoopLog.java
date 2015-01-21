package com.hadoop.examples.hive.loginfo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hadoop.examples.hive.loginfo.DBHelper.HiveUtil;

public class AnalyseHadoopLog{
		public static void main (String[] args) throws SQLException{
			StringBuffer sql = new StringBuffer();
			
			//第一步：在Hive中建表
			sql.append("create table if not exists loginfo ( rdate string, "
					+ "time array<string>, type string, relateclass string, "
					+ "information1 string, information2 string, information3 string )"
					+ " row format delimited fields terminated by ' '"
					+ " collection items terminated by ','"
					+ " map keys terminated by ':'");
			System.out.println(sql);
			HiveUtil.createTable(sql.toString());
			
			//第二步：加载Hadoop日志文件
			sql.delete(0, sql.length());
			sql.append("load data local inpath '/home/hadoop/hadoop.log' overwrite into table loginfo");
			System.out.println(sql);
			HiveUtil.loadData(sql.toString());
			
			//第三步：查询有用信息
			sql.delete(0, sql.length());
			sql.append("select rdate,time[0],type,relateclass,"
					+ "information1,information2,information3 "
					+ "from loginfo where type='ERROR'");
			System.out.println(sql);
			ResultSet res = HiveUtil.queryData(sql.toString());
			
			//第四步：将查出的信息进过变换后保存到MySQL中
			HiveUtil.hiveToMySQL(res);
			
			//第五步：关闭Hive连接
			DBHelper.closeHiveConn();
			
			//第六步：关闭MySQL连接
			DBHelper.closeMySQLConn();
		}
	}

 