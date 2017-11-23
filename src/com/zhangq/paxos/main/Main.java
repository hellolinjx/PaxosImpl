package com.zhangq.paxos.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.zhangq.paxos.doer.Acceptor;
import com.zhangq.paxos.doer.Proposer;

/**
 * 主函数 
 * @author linjx
 *
 */
public class Main {
	private static final int NUM_OF_PROPOSER = 5;
	public static void main(String[] args){
		List<Acceptor> acceptors = new ArrayList<>();
		for	(int i=0;i<5;i++){
			acceptors.add(new Acceptor());
		}
		
		ExecutorService es = Executors.newCachedThreadPool();
		for	(int i=0;i<NUM_OF_PROPOSER;i++){
			Proposer proposer =  new Proposer(i, i + "#Proposer", NUM_OF_PROPOSER, acceptors);
			es.submit(proposer);
		}
		es.shutdown();
	}
}
