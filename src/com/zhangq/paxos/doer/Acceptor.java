package com.zhangq.paxos.doer;

import com.zhangq.paxos.bean.AcceptorStatus;
import com.zhangq.paxos.bean.CommitResult;
import com.zhangq.paxos.bean.PrepareResult;
import com.zhangq.paxos.bean.Proposal;
import com.zhangq.paxos.util.PaxosUtil;

/**
 * 决策者
 * @author linjx
 *
 */
public class Acceptor {
	//	记录已处理提案的状态
	private AcceptorStatus status = AcceptorStatus.NONE; 
	//	记录最新承诺的提案
	private Proposal promisedProposal = new Proposal();
	//	记录最新批准的提案
	private Proposal acceptedProposal = new Proposal();
	
	//	加锁此准备函数，不允许同时访问。模拟单个决策者串行处理一个请求。
	public synchronized PrepareResult onPrepare(Proposal szProposal){
		PrepareResult prepareResult = new PrepareResult();
		
		//	模拟网络不正常，发生丢包、超时现象
		if	(PaxosUtil.isCrashed()){
			//PaxosUtil.printStr("Network not normal: " + this.toString());
			return null;
		}
		
		switch (status){
			//	NONE表示之前没有承诺过任何提议者
			//	此时，接受提案
			case NONE:
				prepareResult.setAcceptorStatus(AcceptorStatus.NONE);
				prepareResult.setPromised(true);
				prepareResult.setProposal(null);
				//	转换自身的状态，已经承诺了提议者，并记录承诺的提案。
				status = AcceptorStatus.PROMISED;
				promisedProposal.copyFromInstance(szProposal);
				return prepareResult;
			//	已经承诺过任意提议者
			case PROMISED:
				//	判断提案的先后顺序，只承诺相对较新的提案
				if	(promisedProposal.getId() > szProposal.getId()){
					prepareResult.setAcceptorStatus(status);
					prepareResult.setPromised(false);
					prepareResult.setProposal(promisedProposal);
					return prepareResult;
				}else{
					promisedProposal.copyFromInstance(szProposal);
					prepareResult.setAcceptorStatus(status);
					prepareResult.setPromised(true);
					prepareResult.setProposal(promisedProposal);
					return prepareResult;
				}
			//	已经批准过提案
			case ACCEPTED:
				//	如果是同一个提案，只是序列号增大
				//	批准提案，更新序列号。
				if	(promisedProposal.getId()<szProposal.getId()
						&& promisedProposal.getValue().equals(szProposal.getValue())){
					promisedProposal.setId(szProposal.getId());
					prepareResult.setAcceptorStatus(status);
					prepareResult.setPromised(true);
					prepareResult.setProposal(promisedProposal);
					return prepareResult;
				}else{ 	//	否则，不予批准
					prepareResult.setAcceptorStatus(status);
					prepareResult.setPromised(false);
					prepareResult.setProposal(acceptedProposal);
					return prepareResult;
				}
			default:
				//return null;
		}
		
		return null;
	}
	
	//	加锁此提交函数，不允许同时访问，模拟单个决策者串行决策
	public synchronized CommitResult onCommit(Proposal szProposal){
		CommitResult commitResult = new CommitResult();
		if	(PaxosUtil.isCrashed()){
			return null;
		}
		switch (status){
			//	不可能存在此状态
			case NONE:	return null;
			//	已经承诺过提案
			case PROMISED:
				//	判断commit提案和承诺提案的序列号大小
				//	大于，接受提案。
				if	(szProposal.getId() >= promisedProposal.getId()){
					promisedProposal.copyFromInstance(szProposal);
					acceptedProposal.copyFromInstance(szProposal);
					status = AcceptorStatus.ACCEPTED;
					commitResult.setAccepted(true);
					commitResult.setAcceptorStatus(status);
					commitResult.setProposal(promisedProposal);
					return commitResult;
					
				}else{	//	小于，回绝提案
					commitResult.setAccepted(false);
					commitResult.setAcceptorStatus(status);
					commitResult.setProposal(promisedProposal);
					return commitResult;
				}
			//	已接受过提案
			case ACCEPTED:
				//	同一提案，序列号较大，接受
				if	(szProposal.getId() > acceptedProposal.getId()
						&& szProposal.getValue().equals(acceptedProposal.getValue())){
					acceptedProposal.setId(szProposal.getId());
					commitResult.setAccepted(true);
					commitResult.setAcceptorStatus(status);
					commitResult.setProposal(acceptedProposal);
					return commitResult;
				}else{	//	否则，回绝提案
					commitResult.setAccepted(false);
					commitResult.setAcceptorStatus(status);
					commitResult.setProposal(acceptedProposal);
					return commitResult;
				}
		}
		
		
		return null;
	}

	@Override
	public String toString() {
		return "Acceptor [status=" + status + ", promisedProposal=" + promisedProposal + ", acceptedProposal="
				+ acceptedProposal + "]";
	}
}
