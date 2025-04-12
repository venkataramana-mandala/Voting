package com.evoting.servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.evoting.dao.DBConnection;

@WebServlet("/registerVoter")
public class RegisterVoterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String voterId = request.getParameter("voter_id");
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Voter Registration</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("<link rel='stylesheet' href='register.css'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'>");
        out.println("</head>");
        out.println("<body>");

        // Navigation Bar
        out.println("<nav class='navbar'>");
        out.println("<div class='navbar-links'>");
        out.println("<a href='index.html'><i class='fas fa-home'></i> Home</a>");
        out.println("<a href='register.html'><i class='fas fa-user-plus'></i> Register</a>");
        out.println("<a href='login_voter.html'><i class='fas fa-sign-in-alt'></i> Voter Login</a>");
        out.println("<a href='login_admin.html'><i class='fas fa-user-shield'></i> Admin Login</a>");
        out.println("</div>");
        out.println("</nav>");

        out.println("<main class='message-box'>");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement check = con.prepareStatement("SELECT * FROM voters WHERE voter_id = ?");
            check.setString(1, voterId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                out.println("<i class='fas fa-user-check error'></i>");
                out.println("<h2>You are already registered!</h2>");
                out.println("<a class='btn-link' href='login_voter.html'><i class='fas fa-sign-in-alt'></i> Go to Login</a>");
            } else {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO voters (voter_id, name, password, has_voted) VALUES (?, ?, ?, 'N')");
                ps.setString(1, voterId);
                ps.setString(2, name);
                ps.setString(3, password);
                ps.executeUpdate();
                ps.close();

                out.println("<i class='fas fa-check-circle success'></i>");
                out.println("<h2>Registration Successful!</h2>");
                out.println("<p>Welcome, <strong>" + name + "</strong>. You may now log in to vote.</p>");
                out.println("<a class='btn-link' href='login_voter.html'><i class='fas fa-arrow-right'></i> Go to Login</a>");
            }

            rs.close();
            check.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<i class='fas fa-exclamation-circle warning'></i>");
            out.println("<h2>Registration Failed</h2>");
            out.println("<p>There was an error processing your request. Please try again later.</p>");
            out.println("<a class='btn-link' href='register.html'><i class='fas fa-arrow-left'></i> Back to Register</a>");
        }

        out.println("</main>");
        out.println("</body>");
        out.println("</html>");
    }
}
