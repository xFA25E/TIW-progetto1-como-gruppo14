package it.polimi.tiw.bank.dao;

import it.polimi.tiw.bank.beans.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDao {
	
	private Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }
	
    public Account findAllByCustomer(long customerId) throws SQLException {
    	Account account = null;
    	String query = "SELECT * FROM account WHERE customer_id = ?";
    	ResultSet result = null;
    	PreparedStatement pstatement = null;
    	try {
    		pstatement = connection.prepareStatement(query);
    		pstatement.setLong(2, customerId);
    		result = pstatement.executeQuery();
    		while(result.next()) {
    			account = new Account();
    			account.setAccountId(result.getLong("account_id"));
    			account.setCustomerId(result.getLong("customer_id"));
                account.setDepositedMoney(result.getLong("deposited_amount"));
    		}
    	}catch (SQLException e) {
            throw new SQLException(e);

        } finally {
            try {
                result.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close result");
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return account;
    }
    
    public Account findAccountById(long accountId) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM account WHERE account_id = ?";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setLong(1, accountId);
            result = pstatement.executeQuery();
            while (result.next()) {
                account = new Account();
                account.setAccountId(result.getLong("account_id"));
                account.setCustomerId(result.getLong("customer_id"));
                account.setDepositedMoney(result.getLong("deposited_amount"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);

        } finally {
            try {
                result.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close result");
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return account;
    }
    
    public long createAccount(long customerId, long depositedMoney) 
    	throws SQLException {
		String query = ("INSERT INTO account (customer_id, deposited_amount)"
		     + "VALUES (?,?)");
		
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setLong(2, customerId);
			pstatement.setLong(3, depositedMoney);
			int affectedRows = pstatement.executeUpdate();
			
			if (affectedRows == 0) {
				throw new SQLException("Creating account failed, no rows affected.");
			}
			
			try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				} else {
					throw new SQLException("Creating account failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
		throw new SQLException(e);
		} finally {
			try {
			pstatement.close();
			} catch (Exception e1) {
			
			}
		}
	}
	
	public void deleteAccountById(long accountId) throws SQLException {
		String query = "DELETE FROM account WHERE account_id = ?";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setLong(1, accountId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {
			
			}
		}
	}
    
}
