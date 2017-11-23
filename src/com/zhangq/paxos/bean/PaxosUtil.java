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
	
	/*
	 * @brief 提案序列号生成：保证唯一且递增。参考chubby中提议生成算法
	 * @param myID 提议者的ID
	 * @param numCycle 生成提议的轮次
	 * @param 提议者个数
	 * @return 生成的提案id
	 * 
	 */
	public static int generateId(int myID, int numCycle, int proposerCount){
		int id = numCycle * proposerCount + myID;
		return id;
	}
	
	//	随机休眠，模拟网络延迟
	public  static int sleepRandom(){
		int timeInMs = random.nextInt(100) + 10;
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
