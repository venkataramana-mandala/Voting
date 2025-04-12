package com.evoting.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.evoting.dao.DBConnection;

@WebServlet("/adminLogin")
public class AdminLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Vote</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='center'>");
        out.println("<section>");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection con = DBConnection.getConnection()){

        	PreparedStatement ps = con.prepareStatement("select username, password from admins where username = ? and password = ?");
        	ps.setString(1, username);
        	ps.setString(2, password);

        	ResultSet rs  = ps.executeQuery();
        	if(rs.next()) {
        		response.sendRedirect("dashboard.html");
        	}
        	else {
        		out.println("<h2><i class='fas fa-user-shield'></i> Admin Login</h2>");
                out.println("<div class='message error'><i class='fas fa-exclamation-triangle'></i> Invalid credentials. Please try again.</div>");
                out.println("<a class='retry-link' href='login_admin.html'><i class='fas fa-arrow-left'></i> Back to Login</a>");
            }
        	con.close();
        	rs.close();
        	ps.close();
        }
        catch (Exception e) {
        	e.printStackTrace();
            response.sendRedirect("admin_login.html?error=exception");
        }
        out.println("</section>");
        out.println("</div>");
        out.println("</body></html>");
        
    }
}