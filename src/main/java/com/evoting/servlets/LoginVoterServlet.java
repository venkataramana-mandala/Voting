package com.evoting.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.evoting.dao.DBConnection;

@WebServlet("/loginVoter")
public class LoginVoterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String voterId = request.getParameter("voter_id");
        String password = request.getParameter("password");

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Voter Login</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("<link rel='stylesheet' href='login.css'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<nav class='navbar'>");
        out.println("<div class='navbar-links'>");
        out.println("<a href='index.html'><i class='fas fa-home'></i> Home</a>");
        out.println("<a href='register.html'><i class='fas fa-user-plus'></i> Register</a>");
        out.println("<a href='login_voter.html'><i class='fas fa-sign-in-alt'></i> Voter Login</a>");
        out.println("<a href='login_admin.html'><i class='fas fa-user-shield'></i> Admin Login</a>");
        out.println("</div>");
        out.println("</nav>");

        out.println("<main class='login-container'>");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM voters WHERE voter_id=? AND password=?");
            ps.setString(1, voterId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hasVoted = rs.getString("has_voted");

                if (hasVoted.equalsIgnoreCase("N")) {
                    HttpSession session = request.getSession();
                    session.setAttribute("voter_id", voterId);
                    response.sendRedirect("vote");
                    return;
                } else {
                    out.println("<div class='icon success'><i class='fas fa-check-circle'></i></div>");
                    out.println("<h2>Already Voted</h2>");
                    out.println("<p class='status'>You have already cast your vote. Thank you for participating.</p>");
                    out.println("<a class='btn-link' href='index.html'><i class='fas fa-home'></i> Back to Home</a>");
                }
            } else {
                out.println("<div class='icon error'><i class='fas fa-times-circle'></i></div>");
                out.println("<h2>Login Failed</h2>");
                out.println("<p class='status'>Invalid Voter ID or Password. Please try again.</p>");
                out.println("<a class='btn-link' href='login_voter.html'><i class='fas fa-arrow-left'></i> Try Again</a>");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='icon warning'><i class='fas fa-exclamation-triangle'></i></div>");
            out.println("<h2>Server Error</h2>");
            out.println("<p class='status'>Something went wrong. Please try again later.</p>");
            out.println("<a class='btn-link' href='login_voter.html'><i class='fas fa-arrow-left'></i> Back to Login</a>");
        }

        out.println("</main>");
        out.println("</body>");
        out.println("</html>");
    }
}
