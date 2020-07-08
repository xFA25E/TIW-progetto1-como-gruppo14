package it.polimi.tiw.bank.controllers;

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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.dao.AccountDao;

/**
 * Servlet implementation class GetAccount
 */
@WebServlet("/GetAccount")
@MultipartConfig
public class GetAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAccount() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
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

            long accountId = Long.parseLong(accountIdString);

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
                Account account = new AccountDao(connection).findAccountByAccountId(accountId);
                if (account == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().println("Unauthorized");
                    return;
                }
                String json = new Gson().toJson(account);
    		    response.setContentType("application/json");
    		    response.setCharacterEncoding("UTF-8");
    		    response.getWriter().write(json);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal server error, retry later");
                return;
            }

            
        }

}
