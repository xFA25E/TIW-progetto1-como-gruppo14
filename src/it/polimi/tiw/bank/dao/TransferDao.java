package it.polimi.tiw.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polimi.tiw.bank.beans.Transfer;

public class TransferDao {
	private Connection connection;

	public TransferDao(Connection connection) {
		this.connection = connection;
	}

	public List<Transfer> findByAccountId(long accountId) throws SQLException {
		List<Transfer> transfers = new ArrayList<Transfer>();
		String query = "SELECT * FROM transfer WHERE source_account_id = ? OR destination_account_id = ? ORDER BY creation_time DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setLong(1, accountId);
			pstatement.setLong(2, accountId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Transfer transfer = new Transfer();
				transfer.setTransferId(result.getLong("transfer_id"));
				transfer.setSourceAccountId(result.getLong("source_account_id"));
				transfer.setDestinationAccountId(result.getLong("destination_account_id"));
				transfer.setCreationDate(result.getDate("creation_time"));
				transfer.setAmount(result.getLong("amount"));
				transfer.setCause(result.getString("cause"));
				transfers.add(transfer);
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return transfers;
	}

	public long createTransfer(long sourceAccountId, long destinationAccountId, long destinationCustomerId, long amount,
			String cause) throws SQLException {
		String query = "SELECT customer_id FROM account WHERE account_id = ? AND customer_id = ?";

		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {

			pstatement = connection.prepareStatement(query);
			pstatement.setLong(1, destinationAccountId);
			pstatement.setLong(2, destinationCustomerId);
			result = pstatement.executeQuery();
			if (!result.next()) {
				throw new SQLException("L'account immesso non appartiene all'utente inserito");
			}

		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}

		query = "INSERT INTO transfer (source_account_id, destination_account_id, amount, cause)\n"
				+ "SELECT ?, ?, ?, ?\n" + "  FROM account_total_amount \n"
				+ " WHERE account_id = ? AND total_amount >= ?\n";

		pstatement = null;
		try {
			pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setLong(1, sourceAccountId);
			pstatement.setLong(2, destinationAccountId);
			pstatement.setLong(3, amount);
			pstatement.setString(4, cause);
			pstatement.setLong(5, sourceAccountId);
			pstatement.setLong(6, amount);
			int affectedRows = pstatement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Non si dispone dei fondi necessari per effettuare il bonifico");
			}

			try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				} else {
					throw new SQLException("Creating transfer failed, no ID obtained.");
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
