package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.dao.TransferDao;

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

/**
 * Servlet implementation class CreateTransfer
 */
@WebServlet("/CreateTransfer")
@MultipartConfig
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTransfer() {
		super();
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

        if (!sourceAccountIdString.matches("[0-9]+")
            || !destinationCustomerIdString.matches("[0-9]+")
            || !destinationAccountIdString.matches("[0-9]+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Accounts should be numeric");
            return;
        }

        if (!amountString.matches("(0|[1-9][0-9]*),[0-9]{2}")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Amount is not correct");
            return;
        }
        
        
        long sourceAccountId = Long.parseLong(sourceAccountIdString);
        long destinationCustomerId = Long.parseLong(destinationCustomerIdString);
        long destinationAccountId = Long.parseLong(destinationAccountIdString);
        long amount = Long.parseLong(amountString.replace(",", ""));

        
        if (sourceAccountId == destinationAccountId) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Can't transfer money to same account");
            return;
        }
        
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
            
           long[] ids = {sourceAccountId, destinationAccountId};
           String json = new Gson().toJson(ids);
           response.setContentType("application/json");
           response.setCharacterEncoding("UTF-8");
           response.getWriter().write(json);
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            return;
        }
    }
}
