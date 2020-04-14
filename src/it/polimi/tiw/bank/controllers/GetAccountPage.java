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
import java.util.Calendar;
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
 * Servlet implementation class GetAccountPage
 */
@WebServlet("/GetAccountPage")
public class GetAccountPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetAccountPage() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

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

			HttpSession session = request.getSession(false);

			if (session != null) {
				CustomerDao customerDao = new CustomerDao(connection);
				AccountDao accountDao = new AccountDao(connection);
				TransferDao trasferDao = new TransferDao(connection);
				long customerId = (long) session.getAttribute("CUSTOMERID");
				String accId = request.getParameter("account-id");
				if (accId == null) {
					response.sendRedirect("/home");
					return;
				}
				long accountId = Long.parseLong(accId, 10);

				try {
					Customer customer = customerDao.findCustomerById(customerId);
					Account account = accountDao.findAccountByAccountId(accountId);
					List<Transfer> transfersList = trasferDao.findByAccountId(accountId);
					long earnedAmount = 0;
					long spentAmount = 0;
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 0);

					for (Transfer transfer : transfersList) {
						Calendar transferDate = Calendar.getInstance();
						transferDate.setTime(transfer.getCreationDate());

						if (today.get(Calendar.MONTH) == transferDate.get(Calendar.MONTH)) {
							if (transfer.getSourceAccountId() == accountId) {
								spentAmount += transfer.getAmount();
							} else {
								earnedAmount += transfer.getAmount();
							}
						}
					}

					if (customer == null || account == null || customerId != account.getCustomerId()) {
						session.invalidate();
						response.sendRedirect("/login");
					} else {
						String path = "/Templates/Account/Account.html";
						ServletContext servletContext = getServletContext();
						final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
						ctx.setVariable("accountId", accountId);

						ctx.setVariable("euros", account.getAmount() / 100);
						ctx.setVariable("cents", String.format("%02d", account.getAmount() % 100));

						ctx.setVariable("earnedEuros", earnedAmount / 100);
						ctx.setVariable("earnedCents", String.format("%02d", earnedAmount % 100));

						ctx.setVariable("spentEuros", spentAmount / 100);
						ctx.setVariable("spentCents", String.format("%02d", spentAmount % 100));

						ctx.setVariable("transfers", transfersList);

						templateEngine.process(path, ctx, response.getWriter());
					}
				} catch (SQLException e) {
					session.invalidate();
					response.sendRedirect("/login");
				}
			} else {
				response.sendRedirect("/login");
			}
		} finally {
			destroy();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
