package it.polimi.tiw.bank.beans;

public class Account {
	
	private long accountId;
	private long customerId;
	private long depositedMoney;
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long costumerId) {
		this.customerId = customerId;
	}
	public long getDepositedMoney() {
		return depositedMoney;
	}
	public void setDepositedMoney(long depositedMoney) {
		this.depositedMoney = depositedMoney;
	}
	
}
