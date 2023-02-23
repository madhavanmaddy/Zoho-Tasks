package com.bank.servlets.accountServlets;

import com.bank.exceptions.AccountException;
import com.bank.exceptions.InputException;
import com.bank.models.Bank;
import com.bank.models.Database;
import com.bank.models.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "Account", urlPatterns = "/account")
public class Account extends HttpServlet {
    static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public void init() {
            Database.getConnection();
    }

    private void getAccountByAccountNumber(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        Map<String,Object> responseMap = new HashMap<>();
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();

            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            if(request.getParameterMap().isEmpty()){
                throw new InputException();
            }
            if(accountNumberParam.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            System.out.println(accountNumber);
            com.bank.models.Account account = Bank.getAccountByAccountNumber(accountNumber);
            response.setStatus(200);
            responseMap.put(Strings.KEY_ACCOUNT,account);
            responseMap.put(Strings.KEY_MESSAGE,Strings.TRANSACTION_SUCCESS);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (SQLException | IOException e) {
            response.setStatus(500);
            assert printWriter != null;
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (InputException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }

    private void createAccount(HttpServletRequest request,HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String, Object> responseMap = new HashMap<>();
        try{
            printWriter = response.getWriter();
            String ckycNumber = request.getParameter(Strings.KEY_CKYC);
            String password = request.getParameter(Strings.KEY_PASSWORD);
            com.bank.models.Account account = new com.bank.models.Account(ckycNumber,password);
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
        } catch (InputException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (IOException e) {
            response.setStatus(500);
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }

    private void deleteAccount(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try{
            printWriter = response.getWriter();
            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            if(accountNumberParam.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            Bank.closeAccount(accountNumber);
        }catch (IOException | SQLException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE, Strings.SOMETHING_WENT_WRONG);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (InputException e) {
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
        getAccountByAccountNumber(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        createAccount(request,response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        deleteAccount(request,response);
    }
}
