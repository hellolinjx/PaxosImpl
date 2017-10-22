package com.zhangq.paxos.bean;

import java.util.Random;

/**
 * 工具类
 * @author linjx
 *
 */
public class PaxosUtil {
	
	private static final int CHANCE_TO_CRASH = 4;  // 1/4的概率会因为网络原因丢失响应
	private static int m_id = 0;
	private static Random random = new Random();
	//	提案序列号生成
	public static synchronized int generateId(){
		return m_id++;
	}
	
	//	随机休眠，模拟网络延迟
	public  static int sleepRandom(){
		int timeInMs = random.nextInt(1000) + 10;
		try {
			Thread.currentThread().sleep(timeInMs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeInMs;
	}
	
	public static boolean isCrashed(){
		int x = random.nextInt(CHANCE_TO_CRASH);
		if	(0==x)
			return true;
		return false;
	}
	
	public static void printStr(String string){
		System.out.println(string);
	}
	
}
