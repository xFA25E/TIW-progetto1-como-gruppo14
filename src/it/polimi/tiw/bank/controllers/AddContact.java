package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Transfer;
import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.Customer;

import it.polimi.tiw.bank.dao.TransferDao;
import it.polimi.tiw.bank.dao.AccountDao;
import it.polimi.tiw.bank.dao.CustomerDao;
import it.polimi.tiw.bank.dao.ContactsDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class AddContact
 */
@WebServlet("/AddContact")
public class AddContact extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddContact() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Incorrect credentials");
            return;
        }

        String accountIdString = request.getParameter("account-id");
        if (accountIdString == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Account Id cannot be null");
            return;
        }

        if (!accountIdString.matches("^[0-9]+$")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Account Id is not numeric");
            return;
        }

        long accountId = Long.parseLong(accountIdString);
        long customerId = (long) session.getAttribute("CUSTOMERID");

        // Establish db connection
        ServletContext context = getServletContext();
        String driver = context.getInitParameter("dbDriver");
        String url = context.getInitParameter("dbUrl");
        String user = context.getInitParameter("dbUser");
        String password = context.getInitParameter("dbPassword");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            new ContactsDao(connection).addContactByCustomerId(customerId, accountId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
