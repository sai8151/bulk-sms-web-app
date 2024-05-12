package com.batch.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/proj1?useUnicode=true&characterEncoding=utf-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading MySQL JDBC driver", e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user_id = req.getParameter("user_id");
        String password = req.getParameter("password");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        try (Connection con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            con.setAutoCommit(false);

            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE user_id = ? AND pswd = ?")) {
                statement.setString(1, user_id);
                statement.setString(2, password);

                try (ResultSet rs = statement.executeQuery()) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Login Result</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<div style=\"padding: 20px; background-color: #f0f0f0; border-radius: 5px;\">");
                    if (rs.next()) {
                        out.println("<h2 style=\"color: green;\">Login successful</h2>");
                        RequestDispatcher dispatcher = req.getRequestDispatcher("home.html");
                		
                		dispatcher.forward(req, resp);
                		

                    } else {
                        out.println("<h2 style=\"color: red;\">Login failed: Invalid username or password</h2>");
                        out.println("<h2 style=\"color: red;\">Login failed: Invalid username or password</h2>");
                        RequestDispatcher dispatcher = req.getRequestDispatcher("login.html");
                        dispatcher.forward(req, resp);
                    }
                    out.println("</div>");
                    out.println("</body>");
                    out.println("</html>");
                }
            }

            // No need to call commit because autocommit is enabled by default
            // con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
