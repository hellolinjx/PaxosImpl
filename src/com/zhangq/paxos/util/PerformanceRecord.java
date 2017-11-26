package com.zhangq.paxos.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author linjx
 *	性能记录
 */
public class PerformanceRecord {
	private static final int MAX_ID_VALUE = 200;
	private static PerformanceRecord instance = null;
	private String[] despArray;
	private long[] startArray;
	private int recordCount;
	
	// 实例获取方法
	public static PerformanceRecord getInstance(){
		if	(null==instance){
			synchronized(PerformanceRecord.class){
				if	(null==instance)
					instance = new PerformanceRecord();
			}
		}
		return instance;
	}
	
	private PerformanceRecord(){
		despArray = new String[MAX_ID_VALUE];
		startArray = new long[MAX_ID_VALUE];
		recordCount = 0;
	}
	
	// @brief 开始性能记录。调用此函数后，计时开始
	// @param description 简短的描述信息
	// @param ID 此次性能记录的id，不能重复。 ID >= 0 && ID < 200;
	// @return void
	@SuppressWarnings("unchecked")
	public void start(String description, int ID){
		despArray[ID] = description;
		startArray[ID] = System.currentTimeMillis();
	}
	
	// @brief 结束id为ID的性能记录
	// @param ID 调用start时设置的ID
	// @return void
	@SuppressWarnings("unchecked")
	public void end(int ID){
		long endTime = System.currentTimeMillis();
		String performStr = String.format("%s 耗时为%d ms.", despArray[ID],endTime-startArray[ID]);
		printPerform(performStr);
		// help GC
		despArray[ID] = null;
	}
	
	public void printPerform(String performStr){
		System.out.println(performStr);
	}
}
