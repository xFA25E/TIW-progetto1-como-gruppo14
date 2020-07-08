package it.polimi.tiw.bank.dao;

import it.polimi.tiw.bank.beans.Contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ContactsDao {
    private Connection connection;

    public ContactsDao(Connection connection) {
        this.connection = connection;
    }

    public Contacts findContactsByCustomerId(long customerId) throws SQLException {
        Contacts contacts = new Contacts(customerId);
        String query = "SELECT * FROM contact_information WHERE customer_id = ?";

        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setLong(1, customerId);
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Long contactCustomerId = result.getLong("contact_customer_id");
                    Long contactAccountId = result.getLong("contact_account_id");
                    String contactFullName = result.getString("contact_full_name");
                    contacts.addContact(contactCustomerId, contactAccountId, contactFullName);
                }
            }
        }
        return contacts;
    }

    public void addContactByCustomerId(long customerId, long accountId) throws SQLException {
        String query = "INSERT INTO contact (customer_id, account_id) VALUES (?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstatement.setLong(1, customerId);
            pstatement.setLong(2, accountId);
            int affectedRows = pstatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Failed adding contact.");
            }
        }
    }
}
