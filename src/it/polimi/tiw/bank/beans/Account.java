package it.polimi.tiw.bank.beans;

import java.util.Date;

public class Account {
	
	private long accountId;
	private long customerId;
	private long amount;
	private Date lastEdit;
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public long getAmount() {
		return amount;
	}
	public long getEuros() {
		return amount / 100;
	}
	public String getCents() {
		return String.format("%02d", amount % 100);
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public Date getLastEdit() {
		return lastEdit;
	}
	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}
	
}
