package com.bank.servlets;

import com.bank.exceptions.AccountException;
import com.bank.exceptions.TransactionException;
import com.bank.models.Account;
import com.bank.models.Bank;
import com.bank.models.Database;
import com.bank.models.Strings;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "Account", urlPatterns = "/account")
public class AccountServlet extends HttpServlet {
    static Gson gson = new Gson();

    @Override
    public void init() {
            Database.getConnection();
    }


    protected void doPut(HttpServletRequest request,HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();

        try {
            printWriter = response.getWriter();
            Set<String> keySet = request.getParameterMap().keySet();
            if(keySet.isEmpty() |(!keySet.contains(Strings.KEY_ACCOUNT_NUMBER))| (keySet.contains(Strings.KEY_PASSWORD) && keySet.contains(Strings.KEY_AMOUNT))){
                response.setStatus(400);
                responseMap.put(Strings.KEY_MESSAGE, Strings.INVALID_REQUEST);
                printWriter.println(gson.toJson(responseMap));
                return;
            }
            if(keySet.contains(Strings.KEY_ACCOUNT_NUMBER) && keySet.contains(Strings.KEY_PASSWORD)){
                int accountNumber = Integer.parseInt(request.getParameter(Strings.KEY_ACCOUNT_NUMBER));
                Account account = Bank.getAccountByAccountNumber(accountNumber);
                account.changePassword((request.getParameter(Strings.KEY_PASSWORD)));
                response.setStatus(200);
                responseMap.put(Strings.KEY_MESSAGE,Strings.CHANGE_PASSWORD_SUCCESS);
            }
            if(keySet.contains(Strings.KEY_ACCOUNT_NUMBER) && keySet.contains(Strings.KEY_AMOUNT)){
                int accountNumber = Integer.parseInt(request.getParameter(Strings.KEY_ACCOUNT_NUMBER));
                int amount = Integer.parseInt(request.getParameter(Strings.KEY_AMOUNT));
                Bank.deposit(accountNumber,amount);
                response.setStatus(200);
                responseMap.put(Strings.KEY_MESSAGE,Strings.UPDATE_BALANCE_SUCCESS);
            }
        } catch (IOException | SQLException e) {
           response.setStatus(500);
           responseMap.put(Strings.KEY_MESSAGE, Strings.SOMETHING_WENT_WRONG);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (TransactionException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try {
            printWriter = response.getWriter();
            List<Account> accounts = Bank.viewAllAccounts();
            responseMap.put(Strings.KEY_ACCOUNTS,accounts);
            responseMap.put(Strings.KEY_MESSAGE,Strings.TRANSACTION_SUCCESS);
        } catch (AccountException e) {
            response.setStatus(404);
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (SQLException | IOException e) {
            response.setStatus(500);
            assert printWriter != null;
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String, Object> responseMap = new HashMap<>();
        try{
            printWriter = response.getWriter();
            String ckycNumber = request.getParameter(Strings.KEY_CKYC);
            String password = request.getParameter(Strings.KEY_PASSWORD);
            Account account = new Account(ckycNumber,password);
            Bank.createAccount(account);
            response.setStatus(201);
            responseMap.put(Strings.KEY_ACCOUNT,account);
            responseMap.put(Strings.KEY_MESSAGE, Strings.ACCOUNT_CREATION_SUCCESS);

        } catch (AccountException  e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (SQLException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }

}
