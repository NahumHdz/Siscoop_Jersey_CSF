/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Util;

import com.fenoreste.rest.Entidades.e_Alerts;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author wilmer
 */
public class TimerBeepClock implements Runnable {

    public void run() {
        Toolkit.getDefaultToolkit().beep();
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("HH:mm:ss a");
        String hora = dateFormatLocal.format(new Date());
        //eliminamos todos los PDF a las 4:00AM
        if (hora.replace(" ", "").equals("04:00:00AM")) {
            ejecutarAlerta();
        }
    }

    public void ejecutarAlerta() {
        EntityManagerFactory emf = AbstractFacade.conexion();
        EntityManager em = emf.createEntityManager();
        String uri = "https://cnmuat.siscoop.mx:9943/alertsengine/api/alerts/event/create";
        String output = "";
        try {
            String listaAlertas = "SELECT * FROM e_alertas WHERE enabled=true";
            Query query = em.createNativeQuery(listaAlertas, e_Alerts.class);
            List<e_Alerts> ListaAlertas = query.getResultList();
            String accountNumber = "";
            String alertCode = "";
            String customerId = "";
            String accountType = "";
            Double amount = 0.0;
            for (int i = 0; i < ListaAlertas.size(); i++) {
                LocalDateTime now = LocalDateTime.now();
                String numbers = accountNumber.substring(accountNumber.length() - 4);
                JSONObject json = new JSONObject();
                json.put("alertCode", alertCode);
                json.put("originatorCode", "OMNIA");
                json.put("eventDate", now + "Z");
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
                }
                conn.disconnect();
            }
        } catch (Exception e) {
            em.close();
            emf.close();
            System.out.println("Error al conectar a :" + e.getMessage());
        }finally{
            em.close();
            emf.close();
            
        }

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
 /*public static void main(String[] args) {
        ScheduledExecutorService scheduler
                = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new TimerBeepClock();
        int initialDelay = 1;
        int periodicDelay = 1;
        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay,TimeUnit.SECONDS);
    }*/
}