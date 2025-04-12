package com.evoting.servlets;

import com.evoting.dao.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/castVote")
public class VoteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Read parameters
        String candidateId = request.getParameter("candidate_id");
        HttpSession session = request.getSession(false);
        String voterId = (session != null)
                ? (String) session.getAttribute("voter_id")
                : request.getParameter("voter_id");

        if (voterId == null || candidateId == null) {
            response.sendRedirect("VotePageServlet?error=exception");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // 2) Has this voter already voted?
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT has_voted FROM voters WHERE voter_id = ?")) {
                ps.setString(1, voterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && "Y".equalsIgnoreCase(rs.getString("has_voted"))) {
                        response.sendRedirect("VotePageServlet?error=alreadyVoted");
                        return;
                    }
                }
            }

            // 3) Increment candidate’s vote count
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE candidates SET votes = votes + 1 WHERE candidate_id = ?")) {
                ps.setString(1, candidateId);
                if (ps.executeUpdate() == 0) {
                    response.sendRedirect("VotePageServlet?error=invalidCandidate");
                    return;
                }
            }

            // 4) Mark voter as voted
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE voters SET has_voted = 'Y' WHERE voter_id = ?")) {
                ps.setString(1, voterId);
                ps.executeUpdate();
            }

            // 5) Record the vote in the votes table
            //    vote_id ← votes_seq.NEXTVAL (trigger),
            //    vote_time ← CURRENT_TIMESTAMP (default)
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)")) {
                ps.setString(1, voterId);
                ps.setString(2, candidateId);
                ps.executeUpdate();
            }

            // 6) Success!
            response.sendRedirect("vote_success.html");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("VotePageServlet?error=exception");
        }
    }
}
