package com.microservices.user.core.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MongoDateTime  {
	public static final String DATE_FORMAT_yyyyMM = "yyyyMM";
	public static final String DATE_FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
    public static final String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String DATE_FORMAT_hhmmss = "HHmmss";
    public static final String DATE_TIME_FORMAT_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    
	private Integer date;
	private Integer time;	
	private Long isoDate = new Date().getTime();
	
	private void init(Date d) {
		date = convertDateyyyyMMdd(d);
		time = convertDateHHmmss(d);
		isoDate = d.getTime();
	}
	
	public MongoDateTime() {
		init(new Date());
	}
	
	public MongoDateTime(Date date) {
		init(date);
	}
	
	public Integer getDate() {
		return date;
	}
	
	public void setDate(Integer date) {	
		this.date = date;
	}
	
	public Integer getTime() {
		return time;
	}
	
	public void setTime(Integer time) {
		this.time = time;
	}

	public Long getIsoDate() {
		return isoDate;
	}

	public void setIsoDate(Long isoDate) {
		this.isoDate = isoDate;
	}
	
	@JsonIgnore
	public Date getJavaDate() {
	    return new Date(isoDate);
	}
	
	public static MongoDateTime createDate(Date date){
		return new MongoDateTime(date);
	}
	
	public static Integer convertDateyyyyMM(Date date){
		return Integer.valueOf(
				new SimpleDateFormat(MongoDateTime.DATE_FORMAT_yyyyMM).format(date));
	}
	public static Integer convertDateyyyyMMdd(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(MongoDateTime.DATE_FORMAT_yyyyMMdd);
		return Integer.valueOf(formatter.format(date));
	}
	
	public static Integer convertDateHHmmss(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(MongoDateTime.DATE_FORMAT_hhmmss);
		return Integer.valueOf(formatter.format(date));
	}
	
	public static String convertDateyyyyMMddHHmmss(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(MongoDateTime.DATE_FORMAT_yyyyMMddHHmmss);
		return formatter.format(date);
	}
}
