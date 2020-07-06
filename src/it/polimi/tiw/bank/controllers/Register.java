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
	private Connection connection = null;

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
		try {
            // Extract parameters
            String fullName = request.getParameter("full-name");
            String eMail = request.getParameter("email");
            String password = request.getParameter("password");
            System.out.printf("%s, %s, %s\n", fullName, eMail, password);
            
            for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            	System.out.println(e.getKey() + " => " + e.getValue());
            }
            
            // If parameters are not valid, send error
            if (eMail == null || eMail.isEmpty()
                || password == null || password.isEmpty()
                || fullName == null || fullName.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Credentials must not be null");
                return;
            }

            String salt = PasswordManager.generateSalt();
            String hash = PasswordManager.hashPassword(password, salt);

            // Establish db connection
			try {
				ServletContext context = getServletContext();
				String driver = context.getInitParameter("dbDriver");
				String url = context.getInitParameter("dbUrl");
				String user = context.getInitParameter("dbUser");
				String pass = context.getInitParameter("dbPassword");
				Class.forName(driver);
				connection = DriverManager.getConnection(url, user, pass);
			} catch (ClassNotFoundException e) {
				throw new UnavailableException("Can't load database driver");
			} catch (SQLException e) {
				throw new UnavailableException("Couldn't get db connection");
			}

            // Query database for customer user
			long customerId;
            try {
                customerId = new CustomerDao(connection).createCustomer(fullName, eMail, hash, salt);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal server error, retry later");
                return;
            }

            // Success, send session and e-mail
            request.getSession().setAttribute("CUSTOMERID", customerId);
            response.setStatus(HttpServletResponse.SC_OK);
        } finally {
			destroy();
		}
	}

	public void destroy() {
		// Close the connection
		if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {}
        }
	}
}
