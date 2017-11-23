package com.zhangq.paxos.doer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zhangq.paxos.bean.AcceptorStatus;
import com.zhangq.paxos.bean.CommitResult;
import com.zhangq.paxos.bean.PaxosUtil;
import com.zhangq.paxos.bean.PrepareResult;
import com.zhangq.paxos.bean.Proposal;

/**
 * 提议者
 * @author linjx
 *
 */
public class Proposer implements Runnable{
	
	// 序列号
	int myID = 0;
	// 提议者的名字
	private String name;
	//	提议者的提案
	private Proposal proposal;
	
	//	是否已经有提案获得大多数决策者确认
	private boolean voted;
	//	大多数决策者的最小个数
	private int halfCount;
	
	private int proposerCount;
	
	private int numCycle = 0;
	//	决策者们
	private List<Acceptor> acceptors;
	
	public Proposer(int myID, String name, int proposerCount, List<Acceptor> acceptors){
		this.myID = myID;
		this.name = name;
		this.acceptors = acceptors;
		this.proposerCount = proposerCount;
		numCycle = 0;
		voted = false;
		halfCount = acceptors.size() / 2 + 1;
		this.proposal = new Proposal(PaxosUtil.generateId(myID, numCycle, proposerCount), 
										myID+ "#Proposal", myID + "最帅!");
		numCycle++;
	}
	
	//	准备提案过程，获得大多数决策者支持后进入确认提案阶段。
	public synchronized boolean prepare(){
		PrepareResult prepareResult = null;
		
		boolean isContinue = true;
		//	已获得承诺个数
		int promisedCount = 0;
		
		do{
			List<Proposal> promisedProposals = new ArrayList<Proposal>(); 
			List<Proposal> acceptedProposals = new ArrayList<Proposal>();
			promisedCount = 0;
			for	(Acceptor acceptor : acceptors){
				
				prepareResult = acceptor.onPrepare(proposal);
				//	随机休眠一段时间，模拟网络延迟。
				PaxosUtil.sleepRandom();
				
				//	模拟网络异常
				if	(null==prepareResult){
					continue;
				}
				
				//	获得承诺
				if	(prepareResult.isPromised()){
					promisedCount ++;
				}else{
					//	决策者已经给了更高id题案的承诺
					if	(prepareResult.getAcceptorStatus()==AcceptorStatus.PROMISED){
						promisedProposals.add(prepareResult.getProposal());
					}
					//	决策者已经通过了一个题案
					if	(prepareResult.getAcceptorStatus()==AcceptorStatus.ACCEPTED){
						acceptedProposals.add(prepareResult.getProposal());
					}
				}
			}// end of for
			
			//	获得多数决策者的承诺
			//	可以进行第二阶段：题案提交
			if	(promisedCount >= halfCount){
				break;
			}
			Proposal votedProposal = votedEnd(acceptedProposals);
			//	决策者已经半数通过题案
			if	(votedProposal !=null){
				System.out.println(myID + " : 决策已经投票结束:" + votedProposal);
				return true;
			}
			
			
			Proposal maxIdAcceptedProposal = getMaxIdProposal(acceptedProposals);
			//	在已经被决策者通过题案中选择序列号最大的决策,作为自己的决策。
			if	(maxIdAcceptedProposal != null){
				proposal.setId(PaxosUtil.generateId(myID, numCycle, proposerCount));
				proposal.setValue(maxIdAcceptedProposal.getValue());
			}else{
				proposal.setId(PaxosUtil.generateId(myID, numCycle, proposerCount));
			}
			
			numCycle ++;
		}while(isContinue);
		return false;
	}
	
	//	获得大多数决策者承诺后，开始进行提案确认
	public synchronized boolean commit(){ 
		boolean isContinue = true;
		
		//	已获得接受该提案的决策者个数
		int acceptedCount = 0;
		do{
			List<Proposal> acceptedProposals = new ArrayList<Proposal>();
			acceptedCount = 0;
			for	(Acceptor acceptor : acceptors){
	
				CommitResult commitResult = acceptor.onCommit(proposal);
				//	模拟网络延迟
				PaxosUtil.sleepRandom();
				
				//	模拟网络异常
				if	(null==commitResult){
					continue;
				}
				
				//	题案被决策者接受。
				if	(commitResult.isAccepted()){
					acceptedCount ++;
				}else{
					acceptedProposals.add(commitResult.getProposal());
				}
			}
			
			//	题案被半数以上决策者接受，说明题案已经被选出来。
			if	(acceptedCount >= halfCount){
				System.out.println(myID + " : 题案已经投票选出:" + proposal);
				return true;
			}else{
				Proposal maxIdAcceptedProposal = getMaxIdProposal(acceptedProposals);
				//	在已经被决策者通过题案中选择序列号最大的决策,重新生成递增id，改变自己的value为序列号最大的value。
				//	这是一种预测，预测此maxIdAccecptedProposal最有可能被超过半数的决策者接受。
				if	(maxIdAcceptedProposal != null){
					proposal.setId(PaxosUtil.generateId(myID, numCycle, proposerCount));
					proposal.setValue(maxIdAcceptedProposal.getValue());
				}else{
					proposal.setId(PaxosUtil.generateId(myID, numCycle, proposerCount));
				}
				
				numCycle++;
				//	回退到决策准备阶段
				if	(prepare())
					return true;
			}
			
		}while(isContinue);
		
		return true;
	}
	
	//	获得序列号最大的提案
	private Proposal getMaxIdProposal(List<Proposal> acceptedProposals){
		
		if	(acceptedProposals.size()>0){
			Proposal retProposal = acceptedProposals.get(0);
			for	(Proposal proposal : acceptedProposals){
				if	(proposal.getId()>retProposal.getId())
					retProposal = proposal;
			}
			
			return retProposal;
		}
		return null;
	}
	
	//	是否已经有某个提案，被大多数决策者接受
	private Proposal votedEnd(List<Proposal> acceptedProposals){
		Map<Proposal, Integer> proposalCount = countAcceptedProposalCount(acceptedProposals);
		for	(Entry<Proposal, Integer> entry : proposalCount.entrySet()){
			if	(entry.getValue()>=halfCount){
				voted = true;
				return entry.getKey();
			}
		}
		return null;
	}
	
	//	计算决策者回复的每个已经被接受的提案计数
	private Map<Proposal, Integer> countAcceptedProposalCount(
			List<Proposal> acceptedProposals){
		Map<Proposal, Integer> proposalCount = new HashMap<>();
		for	(Proposal proposal : acceptedProposals){
			//	决策者没有回复，或者网络异常
			if	(null==proposal) continue;
			int count = 1;
			if	(proposalCount.containsKey(proposal)){
				count = proposalCount.get(proposal) + 1;
			}
			proposalCount.put(proposal, count);
		}
		
		return proposalCount;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		prepare();
		commit();
	}
}
