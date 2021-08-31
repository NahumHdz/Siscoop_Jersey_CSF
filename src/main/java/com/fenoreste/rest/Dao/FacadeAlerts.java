/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Dao;

import com.fenoreste.rest.Entidades.e_Alerts;
import com.fenoreste.rest.Entidades.tipos_cuenta_siscoop;
import com.fenoreste.rest.Entidades.v_Alertas;
import com.fenoreste.rest.Util.AbstractFacade;
import com.fenoreste.rest.Util.UtilDate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ofPattern;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Elliot
 */
public abstract class FacadeAlerts<T> {

    private static EntityManagerFactory emf;

    public FacadeAlerts(Class<T> entityClass) {
        emf = AbstractFacade.conexion();
    }

    public String validateAlert(String customerId, String alertCode, boolean enabled, String accountId, String property, Double monto, String operator, String ruleType) {
        EntityManager em = emf.createEntityManager();
        boolean bandera = false;
        try {
            String validationId = "";
            validationId = RandomAlfa().toUpperCase();
            if (buscarDatosCuenta(customerId, accountId)) {
                v_Alertas vl = new v_Alertas();
                vl.setAlertCode(alertCode);
                vl.setCustomerid(customerId);
                vl.setEnabled(true);
                vl.setFechaejecucion(new Date());
                vl.setAccountId(accountId);
                vl.setValidationid_generado(validationId);
                vl.setMonto(monto);
                vl.setProperty(property);
                vl.setOperator(operator);
                vl.setRuleType(ruleType);
                EntityTransaction tr = em.getTransaction();
                tr.begin();
                em.persist(vl);
                tr.commit();
                bandera = true;
            }
            if (bandera) {
                return validationId;
            }
        } catch (Exception e) {
            System.out.println("Error al generar codigo de validacion:" + e.getMessage());
        }
        return null;
    }

    public String executeAlert(String validationId) {
        EntityManager em = emf.createEntityManager();
        boolean bandera = false;
        try {
            String consulta = "SELECT * FROM v_alertas WHERE validationid_generado='" + validationId + "'";
            System.out.println("Consulta:" + consulta);
            Query query1 = em.createNativeQuery(consulta, v_Alertas.class);
            v_Alertas alertaValida = (v_Alertas) query1.getSingleResult();
            e_Alerts executeAlerts = new e_Alerts();
            executeAlerts.setAccountId(alertaValida.getAccountId());
            executeAlerts.setAlertCode(alertaValida.getAlertCode());
            executeAlerts.setCustomerid(alertaValida.getCustomerid());
            executeAlerts.setEnabled(true);
            executeAlerts.setFechaejecucion(new Date());
            executeAlerts.setMonto(alertaValida.getMonto());
            executeAlerts.setOperator(alertaValida.getOperator());
            executeAlerts.setProperty(alertaValida.getProperty());
            executeAlerts.setRuleType(alertaValida.getRuleType());
            tipos_cuenta_siscoop tps = em.find(tipos_cuenta_siscoop.class, Integer.parseInt(alertaValida.getAccountId().substring(6, 11)));
            //consumoCallBack(alertaValida.getAlertCode(), alertaValida.getCustomerid(), alertaValida.getAccountId(), tps.getProducttypename(), alertaValida.getMonto())) {
            em.getTransaction().begin();
            em.persist(executeAlerts);
            em.remove(alertaValida);
            em.getTransaction().commit();
            bandera = true;
            if (bandera) {
                return "completed";
            } else {
                return "rejected";
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar:" + e.getMessage());
        }
        return "Fail";
    }

    public Date stringToDate(String cadena) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
        Date fechaDate = null;

        try {
            fechaDate = formato.parse(cadena);
        } catch (Exception ex) {
            System.out.println("Error fecha:" + ex.getMessage());
        }
        System.out.println("fechaDate:" + fechaDate);
        return fechaDate;
    }

    public String RandomAlfa() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        String cadena = "";
        for (int i = 0; i < 15; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            cadena = cadena + rndChar;
        }
        System.out.println("Cadena:" + cadena);
        return cadena;
    }

    public boolean buscarDatosCuenta(String customerId, String accountId) {
        EntityManager em = emf.createEntityManager();
        boolean bandera = false;
        try {
            String consulta = "SELECT count(*) FROM auxiliares a WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','')='" + accountId + "'"
                    + " AND replace(to_char(a.idorigen,'099999')||to_char(a.idgrupo,'09')||to_char(a.idsocio,'099999'),' ','')='" + customerId + "' AND a.estatus=2";
            Query query = em.createNativeQuery(consulta);
            int count = Integer.parseInt(String.valueOf(query.getSingleResult()));
            System.out.println("count:" + count);
            if (count > 0) {
                bandera = true;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar persona:" + e.getMessage());
        }
        return bandera;
    }

    public void cerrar() {
        emf.close();
    }

    private static boolean consumoCallBack(String alertCode, String customerId, String accountNumber, String accountType, Double amount) {
        boolean banderaCallBack = false;
        pruebas();

        LocalDateTime now = LocalDateTime.now();

        // 2021-08-23T15:04:50.568
        /*LocalDateTime localDateTime1 = now.plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1);
        LocalDateTime localDateTime2 = localDateTime1.minusYears(1).minusMonths(1).minusWeeks(1).minusDays(1);
        System.out.println(localDateTime2);
        LocalDateTime localDateTime3 = localDateTime2.plusHours(1).plusMinutes(1).plusSeconds(1).plusNanos(100);
        System.out.println(localDateTime3);
        LocalDateTime localDateTime4 = localDateTime3.minusHours(1).minusMinutes(1).minusSeconds(1).minusNanos(100);
        System.out.println(String.valueOf(localDateTime4));
        String v=String.valueOf(localDateTime4);*/
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

    public static void pruebas() {
        try {
            LocalDateTime now = LocalDateTime.now();
            System.out.println("now:" + now);
        } catch (Exception e) {
            System.out.println("Errrpprprpr:" + e.getMessage());
        }
    }

    public void ejecutarAlerta() {
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("HH:mm a");
        String hora = dateFormatLocal.format(new Date());
        if(hora.toUpperCase().replace(" ","").equals("22:00PM")){
            
        }
    }
}
