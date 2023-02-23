package com.bank.servlets.accountServlets;

import com.bank.exceptions.AccountException;
import com.bank.exceptions.BeneficiaryException;
import com.bank.exceptions.InputException;
import com.bank.models.Account;
import com.bank.models.Bank;
import com.bank.models.Beneficiary;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/account/getAllBeneficiaries")
public class GetAllBeneficiaries extends HttpServlet {
    static Gson gson = new Gson();

    @Override
    public void init() {
        Database.getConnection();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType(Strings.CONTENT_TYPE);
        response.setCharacterEncoding(Strings.CHARACTER_ENCODING);
        PrintWriter printWriter = null;
        Map<String,Object> responseMap = new HashMap<>();
        try {
            printWriter = response.getWriter();
            String accountNumberParam = request.getParameter(Strings.KEY_ACCOUNT_NUMBER);
            if(accountNumberParam.isEmpty()){
                throw new InputException();
            }
            int accountNumber = Integer.parseInt(accountNumberParam);
            Account account = Bank.getAccountByAccountNumber(accountNumber);
            List<Beneficiary> beneficiaries = account.viewBeneficiaries();
            response.setStatus(200);
            responseMap.put(Strings.KEY_BENEFICIARIES,beneficiaries);
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
}
