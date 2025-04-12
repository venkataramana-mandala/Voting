package com.evoting.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.evoting.dao.DBConnection;

import java.io.*;
import java.sql.*;

@WebServlet("/vote")
public class VotePageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("voter_id") == null) {
            response.sendRedirect("login_voter.html");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Cast Your Vote</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("<link rel='stylesheet' href='vote.css'>"); // custom vote page CSS
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'>");
        out.println("</head><body class='vote-page-body'>");

        out.println("<nav class='vote-navbar'>");
        out.println("<div class='vote-links'>");
        out.println("<a href='index.html'><i class='fas fa-home'></i> Home</a>");
        out.println("<a href='register.html'><i class='fas fa-user-plus'></i> Register</a>");
        out.println("<a href='login_voter.html'><i class='fas fa-sign-in-alt'></i> Voter Login</a>");
        out.println("<a href='index.html'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div>");
        out.println("</nav>");

        out.println("<div class='vote-container'>");

        try (Connection con = DBConnection.getConnection()) {
            Timestamp now = new Timestamp(System.currentTimeMillis());

            PreparedStatement psTime = con.prepareStatement("SELECT START_TIME, END_TIME FROM VOTING_TIME WHERE ID = 1");
            ResultSet rsTime = psTime.executeQuery();

            if (rsTime.next()) {
                Timestamp start = rsTime.getTimestamp("START_TIME");
                Timestamp end = rsTime.getTimestamp("END_TIME");

                out.println("<div class='vote-time-info'>");
                out.println("<p><i class='fas fa-clock'></i> Current Time: " + now + "</p>");
                out.println("<p><i class='fas fa-play'></i> Voting Starts: " + start + "</p>");
                out.println("<p><i class='fas fa-stop'></i> Voting Ends: " + end + "</p>");
                out.println("</div>");

                if (start == null || end == null) {
                    out.println("<div class='vote-status-box error'><i class='fas fa-exclamation-circle'></i> Voting schedule is unavailable. Please contact admin.</div>");
                } else if (now.before(start)) {
                    out.println("<div class='vote-status-box warning'><i class='fas fa-hourglass-start'></i> Voting hasn't started yet. Please come back later.</div>");
                } else if (now.after(end)) {
                    out.println("<div class='vote-status-box warning'><i class='fas fa-hourglass-end'></i> Voting has ended. Thank you for participating.</div>");
                } else {
                    out.println("<h2 class='vote-title'><i class='fas fa-vote-yea'></i> Cast Your Vote</h2>");
                    out.println("<form action='castVote' method='post' class='vote-form'>");
                    out.println("<label for='candidate'>Choose Candidate</label>");
                    out.println("<select name='candidate_id' id='candidate' required>");
                    out.println("<option value=''>-- Select Candidate --</option>");

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT CANDIDATE_ID, NAME, PARTY FROM CANDIDATES");

                    while (rs.next()) {
                        int id = rs.getInt("CANDIDATE_ID");
                        String name = rs.getString("NAME");
                        String party = rs.getString("PARTY");
                        out.println("<option value='" + id + "'>" + name + " (" + party + ")</option>");
                    }

                    rs.close();
                    stmt.close();

                    out.println("</select>");
                    out.println("<button type='submit' class='vote-button'><i class='fas fa-check'></i> Submit Vote</button>");
                    out.println("</form>");
                }

            } else {
                out.println("<div class='vote-status-box error'><i class='fas fa-exclamation-triangle'></i> Voting schedule not found. Contact system administrator.</div>");
            }

            rsTime.close();
            psTime.close();

        } catch (Exception e) {
            out.println("<div class='vote-status-box error'><i class='fas fa-bug'></i> Unexpected error: " + e.getMessage() + "</div>");
            e.printStackTrace(out);
        }

        out.println("</div></body></html>");
    }
}
