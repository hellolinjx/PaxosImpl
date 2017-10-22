package com.zhangq.paxos.bean;

/**
 * 决策者针对提议者的准备提案的回复
 * @author linjx
 *
 */
public class PrepareResult {
	//	是否承诺
	private boolean isPromised;
	//	决策者的状态
	private AcceptorStatus acceptorStatus = AcceptorStatus.NONE;
	//	决策者返回的提案。
	private Proposal proposal;
	public boolean isPromised() {
		return isPromised;
	}
	public void setPromised(boolean isPromised) {
		this.isPromised = isPromised;
	}
	public AcceptorStatus getAcceptorStatus() {
		return acceptorStatus;
	}
	public void setAcceptorStatus(AcceptorStatus acceptorStatus) {
		this.acceptorStatus = acceptorStatus;
	}
	public Proposal getProposal() {
		return proposal;
	}
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}
	@Override
	public String toString() {
		return "PrepareResult [isPromised=" + isPromised + ", acceptorStatus=" + acceptorStatus + ", proposal="
				+ proposal + "]";
	}
	
	
}
