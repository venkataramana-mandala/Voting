package com.evoting.servlets;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.evoting.dao.DBConnection;

@WebServlet("/getVotes")
public class GetVotesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()){

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM candidates");

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json
                    .append("{\"candidate_id\":").append(rs.getInt("candidate_id"))
                    .append(",\"name\":\"").append(rs.getString("name"))
                    .append("\",\"party\":\"").append(rs.getString("party"))
                    .append("\",\"votes\":").append(rs.getInt("votes"))
                    .append("},");
            }

            if (json.charAt(json.length() - 1) == ',') json.setLength(json.length() - 1);
            json.append("]");

            out.print(json.toString());
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
    }
}
