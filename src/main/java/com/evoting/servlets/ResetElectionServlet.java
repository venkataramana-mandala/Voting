package com.evoting.servlets;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

import com.evoting.dao.DBConnection;

@WebServlet("/ResetElectionServlet")
public class ResetElectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        PreparedStatement psVotes = null;
        PreparedStatement psVoters = null;
        PreparedStatement psCandidates = null;
        PreparedStatement psTime = null;

        try (Connection con = DBConnection.getConnection()){

            psVotes = con.prepareStatement("DELETE FROM VOTES");
            psVotes.executeUpdate();

            psVoters = con.prepareStatement("DELETE FROM VOTERS");
            psVoters.executeUpdate();

            psCandidates = con.prepareStatement("DELETE FROM CANDIDATES");
            psCandidates.executeUpdate();
            
            psTime = con.prepareStatement("DELETE FROM VOTING_TIME");
            psTime.executeUpdate();
            
            response.sendRedirect("dashboard.html");
            response.getWriter().write("Election reset successfully.");
            psVotes.close();
            psVoters.close();
            psCandidates.close();
            psTime.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("Error resetting election: " + e.getMessage());
        } 
    }
}