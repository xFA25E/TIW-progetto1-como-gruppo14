package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.dao.CustomerDao;
import it.polimi.tiw.bank.beans.Customer;
import it.polimi.tiw.bank.password_manager.PasswordManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
@MultipartConfig
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        long customerId;

        // Extract parameters
        String fullName = request.getParameter("full-name");
        String eMail = request.getParameter("email");
        String password = request.getParameter("password");
        String passwordRepeat = request.getParameter("password-repeat");

        // If parameters are not valid, send error
        if (eMail == null || eMail.isEmpty()
            || password == null || password.isEmpty()
            || passwordRepeat == null || passwordRepeat.isEmpty()
            || fullName == null || fullName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Credentials must not be null");
            return;
        }

        // If passwords are not equal, send error
        if (!password.equals(passwordRepeat)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Passwords should be equal");
            return;
        }

        String salt = PasswordManager.generateSalt();
        String hash = PasswordManager.hashPassword(password, salt);

        if (!eMail.matches("^[^@]+@[^@]+\\.[^@]{2,}$")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid email");
            return;
        }

        // Establish db connection
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

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            customerId = new CustomerDao(connection).createCustomer(fullName, eMail, hash, salt);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        // Success, send session and e-mail
        request.getSession().setAttribute("CUSTOMERID", customerId);
        response.setStatus(HttpServletResponse.SC_OK);
	}
}
