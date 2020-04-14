package it.polimi.tiw.bank.controllers;

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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.dao.AccountDao;

/**
 * Servlet implementation class TransferSuccessful
 */
@WebServlet("/TransferSuccessful")
public class TransferSuccessful extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection = null;
	private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransferSuccessful() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			init();
		HttpSession session = request.getSession();

        if (session != null) {
        	AccountDao accountDao = new AccountDao(connection);
        	
            Long sourceAccountId = (Long) session.getAttribute("sourceAccountId");
            Long destinationAccountId = (Long) session.getAttribute("destinationAccountId");
            Long destinationCustomerId = (Long) session.getAttribute("destinationCustomerId");
            Long amount = (Long) session.getAttribute("amount");

            try {
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

                //		ctx.setVariable("cause", cause);

                templateEngine.process(path, ctx, response.getWriter());
            } catch (SQLException e) {
            	throw new ServletException("Can't find account");
            }
        } else {
            response.sendRedirect("/Bank/login");
        }
		} finally {
			destroy();
		}
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
