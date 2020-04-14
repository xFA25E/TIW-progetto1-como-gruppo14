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
	Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTransfer() {
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

		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (session != null) {
			CustomerDao customerDao = new CustomerDao(connection);
			AccountDao accountDao = new AccountDao(connection);
			TransferDao transferDao = new TransferDao(connection);

			Long sourceAccountId = Long.parseLong(request.getParameter("source-account"));
			Long destinationCustomerId = Long.parseLong(request.getParameter("destination-customer"));
			Long destinationAccountId = Long.parseLong(request.getParameter("destination-account"));
			Long amount = Long.parseLong(request.getParameter("amount").replace(",", ""));
			String cause = request.getParameter("cause");
			long customerId = (long) session.getAttribute("CUSTOMERID");

			if (destinationCustomerId == null || destinationAccountId == null || amount == null || cause == null) {
				response.sendError(505, "Parameters incomplete");
				return;
			}

			session.setAttribute("sourceAccountId", sourceAccountId);
			session.setAttribute("destinationAccountId", destinationAccountId);
			session.setAttribute("destinationCustomerId", destinationCustomerId);
			session.setAttribute("amount", amount);

			try {

				Customer customer = customerDao.findCustomerById(customerId);
				if (customer == null) {
					session.invalidate();
					response.sendRedirect("/Bank/login");
					return;
				}

				transferDao.createTransfer(sourceAccountId, destinationAccountId, destinationCustomerId, amount, cause);
				response.sendRedirect("/Bank/transfer-successful");

			} catch (SQLException e) {
				response.sendRedirect("/Bank/account?account-id=" + sourceAccountId);
			} catch (Exception e) {
				session.setAttribute("error", e);
				response.sendRedirect("/Bank/transfer-failed");
			}

		} else {
			response.sendRedirect("/Bank/login");
		}
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
