package com.bank.servlets.accountServlets;
import com.bank.exceptions.AccountException;
import com.bank.exceptions.BeneficiaryException;
import com.bank.exceptions.InputException;
import com.bank.models.*;
import com.bank.models.Account;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/account/beneficiary")
public class BeneficiaryServlet extends HttpServlet {
    static Gson gson = new Gson();

    @Override
    public void init() {
        Database.getConnection();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try {
            printWriter = response.getWriter();
            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            String beneficiaryAccountParam = request.getParameter(Strings.KEY_BENEFICIARY_ACCOUNT_NUMBER);
            if(accountNumberParam.isEmpty() | beneficiaryAccountParam.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            int beneficiaryAccount = Integer.parseInt(beneficiaryAccountParam);
            Account account = Bank.getAccountByAccountNumber(accountNumber);
            Beneficiary beneficiary = account.getBeneficiary(beneficiaryAccount);
            response.setStatus(200);
            responseMap.put(Strings.KEY_BENEFICIARY,beneficiary);
            responseMap.put(Strings.KEY_MESSAGE,Strings.TRANSACTION_SUCCESS);
        } catch (IOException | SQLException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE, Strings.SOMETHING_WENT_WRONG);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (InputException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (BeneficiaryException e) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try {
            printWriter = response.getWriter();
            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            String beneficiaryAccountNumberParam = request.getParameter(Strings.KEY_BENEFICIARY_ACCOUNT_NUMBER);
            String beneficiaryIfsc = request.getParameter(Strings.KEY_BENEFICIARY_IFSC);
            String beneficiaryNickName = request.getParameter(Strings.KEY_BENEFICIARY_NICK_NAME);
            if(accountNumberParam.isEmpty() | beneficiaryAccountNumberParam.isEmpty() | beneficiaryIfsc.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            int beneficiaryAccountNumber = Integer.parseInt(beneficiaryAccountNumberParam);
            Account account = Bank.getAccountByAccountNumber(accountNumber);
            Beneficiary beneficiary = new Beneficiary(beneficiaryAccountNumber,beneficiaryIfsc,beneficiaryNickName);
            account.checkAndAddBeneficiary(beneficiary);
            response.setStatus(200);
            responseMap.put(Strings.KEY_MESSAGE,Strings.ADD_BENEFICIARY_SUCCESS);
        } catch (IOException | SQLException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE, Strings.SOMETHING_WENT_WRONG);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (InputException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (BeneficiaryException e) {
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try {
            printWriter = response.getWriter();
            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            String beneficiaryAccountParam = request.getParameter(Strings.KEY_BENEFICIARY_ACCOUNT_NUMBER);
            if(accountNumberParam.isEmpty() | beneficiaryAccountParam.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            int beneficiaryAccount = Integer.parseInt(beneficiaryAccountParam);
            Account account = Bank.getAccountByAccountNumber(accountNumber);
            account.removeBeneficiary(beneficiaryAccount);
            response.setStatus(200);
            responseMap.put(Strings.KEY_MESSAGE,Strings.REMOVE_BENEFICIARY_SUCCESS);
        } catch (IOException | SQLException e) {
            response.setStatus(500);
            responseMap.put(Strings.KEY_MESSAGE, Strings.SOMETHING_WENT_WRONG);
        } catch (AccountException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (InputException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } catch (BeneficiaryException e) {
            response.setStatus(e.getErrorCode());
            responseMap.put(Strings.KEY_MESSAGE,e.getMessage());
        } finally {
            assert printWriter != null;
            printWriter.println(gson.toJson(responseMap));
            printWriter.flush();
            printWriter.close();
        }
    }
}
