/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.callback;
import com.fenoreste.rest.Util.AbstractFacade;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import javax.persistence.EntityManagerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author wilmer
 */
public class EjecucionAlerta {

    private static boolean consumoCallBack(String alertCode, String customerId, String accountNumber, String accountType, Double amount) {
        boolean banderaCallBack = false;

        LocalDateTime now = LocalDateTime.now();

        JSONObject json = new JSONObject();

        // 2021-08-23T15:04:50.568
        /*LocalDateTime localDateTime1 = now.plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1);
        LocalDateTime localDateTime2 = localDateTime1.minusYears(1).minusMonths(1).minusWeeks(1).minusDays(1);
        System.out.println(localDateTime2);
        LocalDateTime localDateTime3 = localDateTime2.plusHours(1).plusMinutes(1).plusSeconds(1).plusNanos(100);
        System.out.println(localDateTime3);
        LocalDateTime localDateTime4 = localDateTime3.minusHours(1).minusMinutes(1).minusSeconds(1).minusNanos(100);
        System.out.println(String.valueOf(localDateTime4));
        String v=String.valueOf(localDateTime4);*/
        
        
        //Formamo el request peticion para callcback
        
        
        
        
        try {
            String numbers = accountNumber.substring(accountNumber.length() - 4);
            json.put("alertCode", alertCode);
            json.put("originatorCode", "OMNIA");
            json.put("eventDate", now + "Z");//Fecha en la que se debe ejecutar
            json.put("customerId", customerId.trim());
            json.put("accountNumber", accountNumber.trim());
            json.put("accountType", accountType.trim().toUpperCase());
            JSONObject amounts = new JSONObject();
            amounts.put("amount", amount);
            amounts.put("currencyCode", "MXN");
            json.put("amount", amounts);
            JSONArray arrayD = new JSONArray();
            arrayD.put("SMS");
            arrayD.put("PUSH");
            arrayD.put("SECURE_MESSAGE");
            arrayD.put("EMAIL");
            json.put("deliveryChannels", arrayD);
            JSONObject eventDateTime = new JSONObject();
            eventDateTime.put("value", String.valueOf(now).substring(0, 10).replace("-", "/"));
            eventDateTime.put("valueType", "string");
            json.put("eventdatetime", eventDateTime);
            JSONObject digitalBankingDpt = new JSONObject();
            digitalBankingDpt.put("value", "digitalBankingDpt 1");
            digitalBankingDpt.put("valueType", "string");
            json.put("digitalBankingDpt", digitalBankingDpt);
            JSONObject accountnickname = new JSONObject();
            accountnickname.put("value", "*****" + numbers);
            accountnickname.put("valueType", "string");
            json.put("accountnickname", accountnickname);
            JSONObject currency = new JSONObject();
            currency.put("value", "MXN");
            currency.put("valueType", "string");
            json.put("currency", currency);

            System.out.println("json:" + json.toString());
        } catch (Exception e) {
            System.out.println("Erro en formar request:"+e.getMessage());
        }


        /*
        String request = "{\"alertCode\":\"" + alertCode + "\"," + "\n"
                + "\"originatorCode\":\"OMNIA\"," + "\n"
                + "\"eventDate\":\"" + nows + "\"," + "\n"
                + "\"customerId\":\"" + customerId + "\"," + "\n"
                + "\"accountNumber\":\"" + accountNumber + "\"," + "\n"
                + "\"accountType\":\"" + accountType + "\"," + "\n"
                + "\"amount\":" + "\n"
                + "{\"amount\":" + amount + "," + "\n"
                + "\"currencyCode\":\"MXN\"" + "\n"
                + "}," + "\n"
                + "\"deliveryChannels\":["
                + "\"SMS\"," + "\n"
                + "\"PUSH\"," + "\n"
                + "\"SECURE_MESSAGE\"," + "\n"
                + "\"EMAIL\"" + "\n"
                + "]," + "\n"
                + "\"eventdatetime\":{" + "\n"
                + "\"value\":\"" + "zxcxc" + "\"," + "\n"
                + "\"valueType\":\"string\"" + "\n"
                + "}," + "\n"
                + "\"digitalBankingDpt\":{" + "\n"
                + "\"value\":\"digitalBankingDpt 1\"," + "\n"
                + "\"valueType\":\"string\"" + "\n"
                + "}," + "\n"
                + "\"accountnickname\":{" + "\n"
                + "\"value\":\"********" + numbers + "\"," + "\n"
                + "\"valueType\":\"string\"" + "\n"
                + "}," + "\n"
                + "\"currency\":{" + "\n"
                + "\"value\":\"MXN\"," + "\n"
                + "\"valueType\":\"string\"" + "\n"
                + "}" + "\n"
                + "}";*/
        String uri = "https://cnmuat.siscoop.mx:9943/alertsengine/api/alerts/event/create";
        String output = "";
        try {
            System.out.println("Conectando ws de callBack.....");
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(3000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //String request=" {\"clientBankIdentifiers\":[{\"value\":\"03020710217963\"}],\"productBankIdentifiers\":[{\"value\":\"0302070011027916986\"}]}";
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            int codigoHTTP = conn.getResponseCode();
            System.out.println("CodigoHTTP:" + codigoHTTP);
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            output = conn.getResponseMessage();
            System.out.println("Output from Server .... \n");
            if (codigoHTTP == 200) {
                System.out.println("El codigo fue 200OK");
                banderaCallBack = true;
            } else {
                banderaCallBack = false;
            }
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("Error al conectar a :" + uri);
            return false;
        }
        return banderaCallBack;
    }

}
