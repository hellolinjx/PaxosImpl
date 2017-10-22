package com.zhangq.paxos.bean;
/**
 * 提议者向决策者确认时，决策者的确认回复
 * @author linjx
 *
 */
public class CommitResult {
	//	提交的提案是否被接受
	boolean accepted = false;
	//	决策者的状态
	AcceptorStatus acceptorStatus = AcceptorStatus.NONE;
	//	返回的决策者已确认的提案
	Proposal proposal;
	public boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
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
		return "CommitResult [accepted=" + accepted + ", acceptorStatus=" + acceptorStatus + ", proposal=" + proposal
				+ "]";
	}
}
