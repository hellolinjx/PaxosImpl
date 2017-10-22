package com.zhangq.paxos.bean;

/**
 * 决策者记录已处理提案的状态
 * @author linjx
 *
 */
public enum AcceptorStatus {
	ACCEPTED, //	接受
	PROMISED, //	承诺 
	NONE	//	未处理过任何提案
}
