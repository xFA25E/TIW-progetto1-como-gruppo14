package it.polimi.tiw.bank.dao;

import it.polimi.tiw.bank.beans.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDao {

	private Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    public List<Account> findAllByCustomerId(long customerId) throws SQLException {
    	List<Account> accountList = new ArrayList<Account>();

    	String query =
    			"SELECT a.account_id," +
    			"       a.customer_id, " +
    			"       t.total_amount AS amount, " +
    			"       (SELECT MAX(creation_time) " +
    			"          FROM transfer " +
    			"         WHERE source_account_id = a.account_id " +
    			"            OR destination_account_id = a.account_id) AS last_edit " +
    			"  FROM account AS a " +
    			"         JOIN account_total_amount AS t USING (account_id) " +
    			" WHERE a.customer_id = ?";
    	ResultSet result = null;
    	PreparedStatement pstatement = null;
    	try {
    		pstatement = connection.prepareStatement(query);
    		pstatement.setLong(1, customerId);
    		result = pstatement.executeQuery();
    		while(result.next()) {
    			Account account = new Account();
    			account.setAccountId(result.getLong("account_id"));
    			account.setCustomerId(result.getLong("customer_id"));
    			account.setAmount(result.getLong("amount"));
    			account.setLastEdit(result.getDate("last_edit"));
                accountList.add(account);
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
        return accountList;
    }


    public Account findAccountByAccountId(long accountId) throws SQLException {
    	Account account = null;

    	String query =
    			"SELECT a.account_id," +
    			"       a.customer_id, " +
    			"       t.total_amount AS amount, " +
    			"       (SELECT MAX(creation_time) " +
    			"          FROM transfer " +
    			"         WHERE source_account_id = a.account_id " +
            "            OR destination_account_id = a.account_id) AS last_edit " +
            "  FROM account AS a " +
            "         JOIN account_total_amount AS t USING (account_id) " +
            " WHERE a.account_id = ?";
    	ResultSet result = null;
    	PreparedStatement pstatement = null;
    	try {
    		pstatement = connection.prepareStatement(query);
    		pstatement.setLong(1, accountId);
    		result = pstatement.executeQuery();
    		while(result.next()) {
    			account = new Account();
    			account.setAccountId(result.getLong("account_id"));
    			account.setCustomerId(result.getLong("customer_id"));
    			account.setAmount(result.getLong("amount"));
    			account.setLastEdit(result.getDate("last_edit"));
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

	public void deleteAccountByAccountId(long accountId) throws SQLException {
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

    public void changeAccountAmountByAccountId(long accountId, long amount)
        throws SQLException {
		String query =
				"UPDATE account\n" +
				"   SET deposited_amount = ?\n" +
				" WHERE account_id = ?";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setLong(1, amount);
            pstatement.setLong(2, accountId);
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

    public long createAccount(String userName, long amount) throws SQLException {
        String query =
        		"INSERT INTO account (customer_id, deposited_amount)\n" +
        		"VALUES ((SELECT customer_id\n" +
        		"           FROM customer\n" +
        		"          WHERE email = ?), ?)";

        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstatement.setString(1, userName);
            pstatement.setLong(2, amount);
            int affectedRows = pstatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
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
}
