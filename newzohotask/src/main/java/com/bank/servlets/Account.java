package com.bank.servlets;

import com.bank.exceptions.AccountException;
import com.bank.models.Bank;
import com.bank.models.Database;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "Account", urlPatterns = "/viewAccounts")
public class Account extends HttpServlet {

    @Override
    public void init() {
            Database.getConnection();
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            List<com.bank.models.Account> accounts = Bank.viewAllAccounts();
            System.out.println(accounts);
            printWriter.println(gson.toJson(accounts));
        } catch (AccountException e) {
            response.setStatus(404);
            printWriter.println(e.getMessage());
        } catch (SQLException | IOException e) {
            printWriter.println(e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.flush();
            printWriter.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        handleRequest(request,response);
    }

}
