package com.batch.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//Import statements...

public class RegisterServlet extends HttpServlet {

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
     String name = req.getParameter("name");
     String phno = req.getParameter("phno");
     resp.setContentType("text/html");
     PrintWriter out = resp.getWriter();

     try (Connection con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          PreparedStatement statement = con.prepareStatement("INSERT INTO users (user_id, pswd, name, phno) VALUES (?, ?, ?, ?)")) {

         statement.setString(1, user_id);
         statement.setString(2, password);
         statement.setString(3, name);
         statement.setString(4, phno);

         int rowsAffected = statement.executeUpdate();

         out.println("<!DOCTYPE html>");
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Registration Result</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<div style=\"padding: 20px; background-color: #f0f0f0; border-radius: 5px;\">");
         if (rowsAffected > 0) {
             out.println("<h2 style=\"color: green;\">User registered successfully</h2>");
         } else {
             out.println("<h2 style=\"color: red;\">Failed to register user</h2>");
         }
         out.println("</div>");
         out.println("</body>");
         out.println("</html>");
         //con.commit(); // Ensure the transaction is committed
     } catch (SQLException e) {
         e.printStackTrace();
         resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
     }
 }
}
