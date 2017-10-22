package com.zhangq.paxos.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zhangq.paxos.bean.PaxosUtil;
import com.zhangq.paxos.bean.Proposal;
import com.zhangq.paxos.doer.Acceptor;
import com.zhangq.paxos.doer.Proposer;

/**
 * 主函数 
 * @author linjx
 *
 */
public class Main {
	public static void main(String[] args){
		List<Acceptor> acceptors = new ArrayList<>();
		for	(int i=0;i<5;i++){
			acceptors.add(new Acceptor());
		}
		
		ExecutorService es = Executors.newCachedThreadPool();
		for	(int i=0;i<5;i++){
			Proposal proposal = new Proposal();
			proposal.setId(PaxosUtil.generateId());
			proposal.setName(i+" proposal");
			proposal.setValue(i+"");
			Proposer proposer =  new Proposer(i+"", proposal, acceptors);
			es.submit(proposer);
		}
		es.shutdown();
	}
}
