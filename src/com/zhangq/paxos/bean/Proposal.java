package com.zhangq.paxos.bean;

/**
 * 提案
 * @author linjx
 *
 */
public class Proposal {
	//	提案名称
	String name;
	//	提案的序列号
	int id;
	//	提案的值
	String value;
	
	public Proposal(){
		
	}
	public Proposal(int id, String name, String value){
		this.id = id;
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void copyFromInstance(Proposal proposal){
		this.id = proposal.id;
		this.name = proposal.name;
		this.value = proposal.value;
	}
	
	@Override
	public String toString() {
		return "Proposal [name=" + name + ", id=" + id + ", value=" + value + "]";
	}
	
}
