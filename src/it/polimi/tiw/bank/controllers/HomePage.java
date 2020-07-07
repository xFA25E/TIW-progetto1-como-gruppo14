package it.polimi.tiw.bank.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.dao.AccountDao;
import it.polimi.tiw.bank.beans.Customer;
import it.polimi.tiw.bank.dao.CustomerDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Home
 */
@WebServlet("/HomePage")
public class HomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HomePage() {
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
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("./login");
        }

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

        Customer customer;
        List<Account> accountList = new ArrayList<>(); 
        long customerId = (Long) session.getAttribute("CUSTOMERID");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            customer = new CustomerDao(connection).findCustomerById(customerId);
            if (customer == null) {
            	session.invalidate();
            	response.sendRedirect("./login");
            	return;
            }
        	accountList = new AccountDao(connection).findAllByCustomerId(customerId);
        } catch (SQLException e) {
            session.invalidate();
            response.sendRedirect("./login");
            return;
        }

        String path = "/Templates/Home/Home.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("fullName", customer.getFullName());
        ctx.setVariable("accounts", accountList);

        templateEngine.process(path, ctx, response.getWriter());
	}
}
