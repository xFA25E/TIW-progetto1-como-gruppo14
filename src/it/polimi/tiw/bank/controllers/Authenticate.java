package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.dao.CustomerDao;
import it.polimi.tiw.bank.beans.Customer;
import it.polimi.tiw.bank.password_manager.PasswordManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Authenticate
 */
@WebServlet("/Authenticate")
@MultipartConfig
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Authenticate() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        Customer customer;

        // Extract parameters
        String eMail = request.getParameter("email");
        String password = request.getParameter("password");

        // If parameters are not valid, send error
        if (eMail == null || eMail.isEmpty() || password == null || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Credentials must not be null");
            return;
        }

        ServletContext context = getServletContext();
        String driver = context.getInitParameter("dbDriver");
        String url = context.getInitParameter("dbUrl");
        String user = context.getInitParameter("dbUser");
        String pass = context.getInitParameter("dbPassword");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        }

        // Establish db connection and
        // query database for customer user
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            customer = new CustomerDao(connection).findCustomerByEmail(eMail);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        if (customer == null) { // If user was not found
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Incorrect credentials");
        } else {
            String hash = customer.getPasswordHash();
            String salt = customer.getPasswordSalt();

            if (PasswordManager.verifyPassword(password, hash, salt)) {
                request.getSession().setAttribute("CUSTOMERID", customer.getCustomerId());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Incorrect credentials");
            }
        }
    }
}
