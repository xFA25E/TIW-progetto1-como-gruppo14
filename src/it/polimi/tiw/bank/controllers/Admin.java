package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.dao.AccountDao;
import it.polimi.tiw.bank.dao.CustomerDao;
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
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("<!doctype html>\n" + "<html>\n" + "    <head>\n"
				+ "        <title>Admin Page</title>\n" + "        <style>\n" + "            form {\n"
				+ "                width: 15%;\n" + "                display: inline-block;\n" + "            }\n"
				+ "        </style>\n" + "    </head>\n" + "    <body>\n" + "\n" + "        <form method='POST'>\n"
				+ "            <fieldset>\n" + "                <legend>Create Customer</legend>\n"
				+ "                <input type='hidden' name='action' value='create'>\n"
				+ "                Full Name<br>\n"
				+ "                <input name='full-name' type='text' value=''><br>\n" + "                Email<br>\n"
				+ "                <input name='user-name' type='text' value=''><br>\n" + "                Pass<br>\n"
				+ "                <input name='password' type='text' value=''><br>\n"
				+ "                <input type='submit' value='Create'>\n" + "            </fieldset>\n"
				+ "        </form>\n" + "\n" + "        <form method='POST'>\n" + "            <fieldset>\n"
				+ "                <legend>Delete Customer</legend>\n"
				+ "                <input type='hidden' name='action' value='delete'>\n" + "                Email<br>\n"
				+ "                <input name='user-name' type='text' value=''><br>\n"
				+ "                <input type='submit' value='Delete'>\n" + "            </fieldset>\n"
				+ "        </form>\n" + "\n" + "        <br><br>\n" + "\n" + "        <form method='POST'>\n"
				+ "            <fieldset>\n" + "                <legend>Create Account</legend>\n"
				+ "                <input type='hidden' name='action' value='create-account'>\n"
				+ "                Email<br>\n" + "                <input name='user-name' type='text' value=''><br>\n"
				+ "                Amount<br>\n" + "                <input name='amount' type='text' value=''><br>\n"
				+ "                <input type='submit' value='Create'>\n" + "            </fieldset>\n"
				+ "        </form>\n" + "\n" + "        <form method='POST'>\n" + "            <fieldset>\n"
				+ "                <legend>Delete Account</legend>\n"
				+ "                <input type='hidden' name='action' value='delete-account'>\n"
				+ "                Account<br>\n" + "                <input name='account' type='text' value=''><br>\n"
				+ "                <input type='submit' value='Delete'>\n" + "            </fieldset>\n"
				+ "        </form>\n" + "\n" + "        <br><br>\n" + "\n" + "        <form method='POST'>\n"
				+ "            <fieldset>\n" + "                <legend>Change Deposited Amount</legend>\n"
				+ "                <input type='hidden' name='action' value='change-amount'>\n"
				+ "                Account<br>\n" + "                <input name='account' type='text' value=''><br>\n"
				+ "                Amount<br>\n" + "                <input name='amount' type='text' value=''><br>\n"
				+ "                <input type='submit' value='Change'>\n" + "            </fieldset>\n"
				+ "        </form>\n" + "\n" + "    </body>\n" + "</html>\n" + "");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			String action = request.getParameter("action");

			switch (action) {
			case "create": {
				String fullName = request.getParameter("full-name");
				String userName = request.getParameter("user-name");
				String password = request.getParameter("password");

				CustomerDao customerDao = new CustomerDao(connection);

				String salt = PasswordManager.generateSalt();
				String hash = PasswordManager.hashPassword(password, salt);

				try {
					customerDao.createCustomer(fullName, userName, hash, salt);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

				break;
			case "delete": {
				String userName = request.getParameter("user-name");
				CustomerDao customerDao = new CustomerDao(connection);

				try {
					customerDao.deleteCustomerByEmail(userName);
				} catch (SQLException e) {
				}
			}

				break;
			case "create-account": {
				String userName = request.getParameter("user-name");
				long amount = Long.parseLong(request.getParameter("amount"), 10);

				AccountDao accountDao = new AccountDao(connection);

				try {
					accountDao.createAccount(userName, amount);
				} catch (SQLException e) {
				}

			}

				break;
			case "delete-account": {
				long accountId = Long.parseLong(request.getParameter("account"), 10);

				AccountDao accountDao = new AccountDao(connection);

				try {
					accountDao.deleteAccountByAccountId(accountId);
				} catch (SQLException e) {
				}

			}

				break;
			case "change-amount": {
				long accountId = Long.parseLong(request.getParameter("account"), 10);
				long amount = Long.parseLong(request.getParameter("amount"), 10);

				AccountDao accountDao = new AccountDao(connection);

				try {
					accountDao.changeAccountAmountByAccountId(accountId, amount);
				} catch (SQLException e) {
				}

			}

				break;
			default:
				break;
			}

			doGet(request, response);
		} finally {
			destroy();
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
