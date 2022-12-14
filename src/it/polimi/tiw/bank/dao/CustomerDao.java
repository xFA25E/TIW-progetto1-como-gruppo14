package it.polimi.tiw.bank.dao;

import it.polimi.tiw.bank.beans.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDao {
    private Connection connection;

    public CustomerDao(Connection connection) {
        this.connection = connection;
    }

    public Customer findCustomerByEmail(String eMail) throws SQLException {
        Customer customer = null;
        String query = "SELECT * FROM customer WHERE email = ?";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, eMail);
            result = pstatement.executeQuery();
            while (result.next()) {
                customer = new Customer();
                customer.setCustomerId(result.getLong("customer_id"));
                customer.setFullName(result.getString("full_name"));
                customer.setEmail(result.getString("email"));
                customer.setPasswordHash(result.getString("password_hash"));
                customer.setPasswordSalt(result.getString("password_salt"));
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
        return customer;
    }

    public Customer findCustomerById(long customerId) throws SQLException {
        Customer customer = null;
        String query = "SELECT * FROM customer WHERE customer_id = ?";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setLong(1, customerId);
            result = pstatement.executeQuery();
            while (result.next()) {
                customer = new Customer();
                customer.setCustomerId(result.getInt("customer_id"));
                customer.setFullName(result.getString("full_name"));
                customer.setEmail(result.getString("email"));
                customer.setPasswordHash(result.getString("password_hash"));
                customer.setPasswordSalt(result.getString("password_salt"));
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
        return customer;
    }

    public long createCustomer(String fullName,
                               String eMail,
                               String passwordHash,
                               String passwordSalt)
        throws SQLException {
        String query = ("INSERT INTO customer (full_name,"
                        + "                    email,"
                        + "                    password_hash,"
                        + "                    password_salt)"
                        + "VALUES (?, ?, ?, ?)");

        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstatement.setString(1, fullName);
            pstatement.setString(2, eMail);
            pstatement.setString(3, passwordHash);
            pstatement.setString(4, passwordSalt);
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

    public void deleteCustomerByEmail(String eMail) throws SQLException {
        String query = "DELETE FROM customer WHERE email = ?";

        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, eMail);
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
