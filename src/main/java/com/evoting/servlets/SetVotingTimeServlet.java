package com.evoting.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.evoting.dao.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/setVotingTimeServlet")
public class SetVotingTimeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String startStr = request.getParameter("startTime");  // e.g., "2025-04-15T08:00"
        String endStr = request.getParameter("endTime");      // e.g., "2025-04-15T17:00"

        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Set Voting Time</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("<link rel='stylesheet' href='setvotingtime.css'>");
        out.println("</head><body>");
        out.println("<div class='center'>");

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(startStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endStr, formatter);

            try (Connection con = DBConnection.getConnection()) {
                // Check if row with ID = 1 exists
                PreparedStatement checkStmt = con.prepareStatement("SELECT COUNT(*) FROM VOTING_TIME WHERE ID = 1");
                ResultSet rs = checkStmt.executeQuery();
                boolean exists = rs.next() && rs.getInt(1) > 0;
                rs.close();
                checkStmt.close();

                if (exists) {
                    PreparedStatement updateStmt = con.prepareStatement(
                        "UPDATE VOTING_TIME SET START_TIME = ?, END_TIME = ? WHERE ID = 1");
                    updateStmt.setTimestamp(1, Timestamp.valueOf(startTime));
                    updateStmt.setTimestamp(2, Timestamp.valueOf(endTime));
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    PreparedStatement insertStmt = con.prepareStatement(
                        "INSERT INTO VOTING_TIME (ID, START_TIME, END_TIME) VALUES (1, ?, ?)");
                    insertStmt.setTimestamp(1, Timestamp.valueOf(startTime));
                    insertStmt.setTimestamp(2, Timestamp.valueOf(endTime));
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
            }

            out.println("<div class='message-box'>");
            out.println("<h2>🗓️ Voting time has been successfully updated.</h2>");
            out.println("<a href='dashboard.html' class='button-link'>Return to Dashboard</a>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='message-box'>");
            out.println("<h2 class='error'>❌ Error: " + e.getMessage() + "</h2>");
            out.println("<a href='dashboard.html' class='button-link'>Return</a>");
            out.println("</div>");
        }

        out.println("</div></body></html>");
    }
}
