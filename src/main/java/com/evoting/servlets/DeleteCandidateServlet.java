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

@WebServlet("/deleteCandidate")
public class DeleteCandidateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");

        try (Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM candidates WHERE CANDIDATE_ID=?");
            ps.setString(1, id);
            ps.executeUpdate();
            con.close();
            ps.close();
            response.sendRedirect("dashboard.html?status=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.html?error=exception");
        }
    }
}
