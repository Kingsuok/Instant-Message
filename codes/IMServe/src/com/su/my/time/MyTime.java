package com.su.my.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTime {
	public static String getTimeNoS(){
		Date date=new Date();   
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm");   
		String time=df.format(date);
		return time;
	}
	public static String getTime(){
		Date date=new Date();   
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm:ss");   
		String time=df.format(date);
		return time;
	}
}
