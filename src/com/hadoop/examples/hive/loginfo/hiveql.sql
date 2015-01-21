#loginfo
create table if not exists loginfo ( rdate string, time array<string>, type string, relateclass string, information1 string, information2 string, information3 string ) row format delimited fields terminated by ' ' collection items terminated by ',' map keys terminated by ':'


#mysqlhive.hadooplog
create table hadooplog (id int(11) NOT NULL auto_increment,rdata varchar(50) default NULL,time varchar(50) default NULL,type varchar(50) default NULL,relateclass TINYTEXT default NULL,information LONGTEXT default NULL,PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;