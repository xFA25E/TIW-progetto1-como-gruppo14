package it.polimi.tiw.bank.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter()
                .append("<body> andiamo da dio"
                        + "<form action=\"/Bank/authenticate\" method=\"POST\" >"
                        + "User <input name=\"user-name\" type=\"text\" value=\"\">"
                        + "<br>"
                        + "Pass <input name=\"password\" type=\"text\" value=\"\">"
                        + "<input type=\"submit\" value=\"sabbamit\">"
                        + "</form>"
                        + "</body>");
        } else {
            response.sendRedirect("/Bank/home");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
