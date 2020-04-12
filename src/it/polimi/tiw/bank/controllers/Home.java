package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Customer;
import it.polimi.tiw.bank.dao.CustomerDao;

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
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
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
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            CustomerDao customerDao = new CustomerDao(connection);
            long customerId = (Long) session.getAttribute("CUSTOMERID");

            try {
                Customer customer = customerDao.findCustomerById(customerId);

                if (customer == null) {
                    session.invalidate();
                    response.sendRedirect("/Bank/login");
                } else {
                    response.getWriter()
                        .append("<body>Hello "
                                + customer.getFullName()
                                + "<br>you can <form method=\"POST\" action=\"/Bank/forget\">"
                                + "<input type=\"submit\" value=\"Log Out\">"
                                + "</form>"
                                + "<br>or you can <form method=\"POST\" action=\"/Bank/delete\">"
                                + "<input type=\"submit\" value=\"Delete Customer\">"
                                + "</form>");
                }
            } catch (SQLException e) {
                session.invalidate();
                response.sendRedirect("/Bank/login");
            }
        } else {
            response.sendRedirect("/Bank/login");
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
