package com.bank.servlets.bankServlets;
import com.bank.exceptions.TransactionException;
import com.bank.models.*;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/getAllTransactions")
public class GetAllTransactions extends HttpServlet {
    static Gson gson = new Gson();

    @Override
    public void init() {
        Database.getConnection();
    }

    private void getAllTransactions(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try{
            printWriter = response.getWriter();
            List<Transaction> accounts = Bank.viewAllTransactions();
            response.setStatus(200);
            responseMap.put(Strings.KEY_ACCOUNTS,accounts);
            responseMap.put(Strings.KEY_MESSAGE,Strings.TRANSACTION_SUCCESS);
        } catch (TransactionException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (SQLException | IOException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        getAllTransactions(request,response);
    }
}
