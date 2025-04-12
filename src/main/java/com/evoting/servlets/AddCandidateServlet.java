package com.evoting.servlets;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.evoting.dao.DBConnection;

@WebServlet("/addCandidate")
public class AddCandidateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String party = request.getParameter("party");

        try (Connection con = DBConnection.getConnection()){


            PreparedStatement ps = con.prepareStatement("INSERT INTO candidates (name, party, votes) VALUES (?, ?, 0)");
        
            ps.setString(1, name);
            ps.setString(2, party);
            ps.executeUpdate();

            con.close();
            ps.close();
            response.sendRedirect("dashboard.html?status=added");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.html?error=exception");
        }
    }
}