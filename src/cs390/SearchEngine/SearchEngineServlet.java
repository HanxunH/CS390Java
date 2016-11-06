package cs390.SearchEngine;

import java.io.IOException;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/**
 * Created by Curtis on 11/3/16.
 */
public class SearchEngineServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        // Actual logic goes here.
        String searchWord = request.getParameter("searchWord");
        SearchEngine se = new SearchEngine(searchWord);
        se.start();

        request.getSession().setAttribute("keyword", searchWord);
        request.setAttribute("result_list", se.searchEngineResult); // add to request

        request.getRequestDispatcher("search.jsp").forward(request,response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request,response);
    }
}
