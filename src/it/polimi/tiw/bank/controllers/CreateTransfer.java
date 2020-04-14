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
		response.getWriter().append("Served at: ").append(request.getContextPath());
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

			Long sourceAccountId = Long.getLong(request.getParameter("source-account-id"));
			Long destinationCustomerId = Long.getLong(request.getParameter("destination-account-id"));
			Long destinationAccountId = Long.getLong(request.getParameter("destination-account-id"));
			Long amount = Long.getLong(request.getParameter("amount"));
			String cause = request.getParameter("cause");
			long customerId = (long) session.getAttribute("CUSTOMERID");

			if (destinationCustomerId == null || destinationAccountId == null || amount == null || cause == null) {
				response.sendError(505, "Parameters incomplete");
				return;
			}

			try {

				Customer customer = customerDao.findCustomerById(customerId);
				if (customer == null) {
					session.invalidate();
					response.sendRedirect("/Bank/login");
					return;
				}

				transferDao.createTransfer(sourceAccountId, destinationAccountId, destinationCustomerId, amount, cause);
				
				Account account = accountDao.findAccountByAccountId(sourceAccountId);
				long updatedAccountAmountEuro = account.getEuros();
				String updatedAccountAmountCents = account.getCents();

				String path = "/Templates/Transfer/TransferSuccessful.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("sourceAccountId", sourceAccountId);

				ctx.setVariable("destinationAccountId", destinationAccountId);
				ctx.setVariable("destinationCustomerId", destinationCustomerId);

				ctx.setVariable("transferAmountEuros", amount / 100);
				ctx.setVariable("transferAmountCents", String.format("%02d", amount % 100));

				ctx.setVariable("updatedAccountAmountEuro", updatedAccountAmountEuro);
				ctx.setVariable("updatedAccountAmountCents", updatedAccountAmountCents);

//				ctx.setVariable("cause", cause);

				templateEngine.process(path, ctx, response.getWriter());

			} catch (SQLException e) {
				String path = "/Templates/Transfer/TransferFailed.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("sourceAccountId", sourceAccountId);

				ctx.setVariable("destinationAccountId", destinationAccountId);
				ctx.setVariable("destinationCustomerId", destinationCustomerId);

//				ctx.setVariable("cause", cause);

				ctx.setVariable("errorMessage", e.getMessage());

				templateEngine.process(path, ctx, response.getWriter());
			}

		} else {
			response.sendRedirect("/Bank/login");
		}
	}
}
