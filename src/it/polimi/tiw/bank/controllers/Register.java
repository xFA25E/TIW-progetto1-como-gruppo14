package it.polimi.tiw.bank.controllers;

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
 * Servlet implementation class Register
 */
@WebServlet("/DoRegister")
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {

        if (request.getSession(false) != null) {
            response.sendRedirect("/Bank/home");
        } else {
            String fullName = request.getParameter("full-name");
            String userName = request.getParameter("user-name");
            String password = request.getParameter("password");

            if (fullName == null || fullName.isEmpty()
                || userName == null || userName.isEmpty()
                || password == null || password.isEmpty()) {
                response.sendRedirect("/Bank/login");
            } else {
                CustomerDao customerDao = new CustomerDao(connection);

                String salt = PasswordManager.generateSalt();
                String hash = PasswordManager.hashPassword(password, salt);

                try {
                    long customerId = customerDao.createCustomer(fullName,
                                                                 userName,
                                                                 hash, salt);
                    HttpSession session = request.getSession(true);
                    session.setAttribute("CUSTOMERID", customerId);
                    response.sendRedirect("/Bank/home");
                } catch (SQLException e) {
                    response.sendRedirect("/Bank/login");
                }
            }
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {

        doPost(request, response);
    }

}
