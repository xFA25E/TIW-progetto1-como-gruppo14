package it.polimi.tiw.bank.beans;

import java.util.Date;

public class Transfer {
    private long transferId;
    private long sourceAccountId;
    private long destinationAccountId;
    private Date creationDate;
    private long amount;
    private String cause;

    public long getTransferId() {
        return transferId;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    public Date getCreationDate() {
    	return creationDate;
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
	
    public String getCause() {
        return cause;
    }
    
    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public void setSourceAccountId(long sourceAccountId) {
    	this.sourceAccountId = sourceAccountId;
    }

    public void setDestinationAccountId(long destinationAccountId) {
    	this.destinationAccountId = destinationAccountId;
    }

    public void setCreationDate(Date creationDate) {
    	this.creationDate = creationDate;
    }
    
    public void setAmount(long amount) {
    	this.amount = amount;
    }
    
    public void setCause(String cause) {
    	this.cause = cause;
    }
}

