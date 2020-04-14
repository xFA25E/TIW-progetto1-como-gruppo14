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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Authenticate
 */
@WebServlet("/Authenticate")
public class Authenticate extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticate() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {

        if (request.getSession(false) != null) {
            response.sendRedirect("/Bank/home");
        } else {
            String userName = request.getParameter("user-name");
            String password = request.getParameter("password");

            if (userName == null || userName.isEmpty()
                || password == null || password.isEmpty()) {
                response.sendRedirect("/Bank/login");
            } else {
                CustomerDao customerDao = new CustomerDao(connection);
                try {
                    Customer customer = customerDao.findCustomerByUserName(userName);

                    if (customer == null) {
                        response.sendRedirect("/Bank/login");
                    } else {
                        String hash = customer.getPasswordHash();
                        String salt = customer.getPasswordSalt();

                        if (PasswordManager.verifyPassword(password, hash, salt)) {
                            HttpSession session = request.getSession(true);
                            session.setAttribute("CUSTOMERID", customer.getCustomerId());
                            response.sendRedirect("/Bank/home");
                        } else {
                            response.sendRedirect("/Bank/login");
                        }
                    }
                } catch (SQLException e) {
                    response.sendRedirect("/Bank/login");
                }
            }
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    public void destroy() {
	    // Close the connection
	    if (connection != null)
	    	try { 
	    		connection.close(); 
	    	} catch (SQLException ignore) { 	  
	    	}
	  }
}
