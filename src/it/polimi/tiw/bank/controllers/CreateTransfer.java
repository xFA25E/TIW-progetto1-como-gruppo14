package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Transfer;
import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.Customer;

import it.polimi.tiw.bank.dao.TransferDao;
import it.polimi.tiw.bank.dao.AccountDao;
import it.polimi.tiw.bank.dao.CustomerDao;

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
 * Servlet implementation class CreateTransfer
 */
@WebServlet("/CreateTransfer")
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTransfer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        // Check session
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Incorrect credentials");
            return;
        }

        // Parameters validation
        String sourceAccountIdString = request.getParameter("source-account");
        String destinationCustomerIdString = request.getParameter("destination-customer");
        String destinationAccountIdString = request.getParameter("destination-account");
        String amountString = request.getParameter("amount");
        String cause = request.getParameter("cause");

        if (sourceAccountIdString == null || sourceAccountIdString.isEmpty()
            || destinationCustomerIdString == null || destinationCustomerIdString.isEmpty()
            || destinationAccountIdString == null || destinationAccountIdString.isEmpty()
            || amountString == null || amountString.isEmpty()
            || cause == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Fields cannot be empty");
            return;
        }

        if (sourceAccountIdString.matches("^[0-9]+$")
            || destinationCustomerIdString.matches("^[0-9]+$")
            || destinationAccountIdString.matches("^[0-9]+$")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Accounts should be numeric");
            return;
        }

        if (amountString.matches("(0|[1-9][0-9]*),[0-9]{2}")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Amount is not correct");
            return;
        }

        long sourceAccountId = Long.parseLong(sourceAccountId);
        long destinationCustomerId = Long.parseLong(destinationCustomerId);
        long destinationAccountId = Long.parseLong(destinationAccountId);
        long amount = Long.parseLong(amountString.replace(",", ""));
        long customerId = (long) session.getAttribute("CUSTOMERID");

        // Db connection
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
            new TransferDao(connection).createTransfer(sourceAccountId, destinationAccountId,
                                                       destinationCustomerId, amount, cause);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
        }
    }
}
