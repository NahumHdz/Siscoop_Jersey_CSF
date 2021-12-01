/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Dao;

import DTO.ogsDTO;
import DTO.opaDTO;
import com.fenoreste.rest.Entidades.e_Alerts;
import com.fenoreste.rest.Entidades.v_Alertas;
import com.fenoreste.rest.Util.AbstractFacade;
import com.fenoreste.rest.Util.Utilidades;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Elliot
 */
public abstract class FacadeAlerts<T> {

    public FacadeAlerts(Class<T> entityClass) {

    }

    Utilidades Util = new Utilidades();

    public String validateAlert(String customerId, String alertCode, boolean enabled, String accountId, Double monto, int tipoalerta) {
        EntityManager em = AbstractFacade.conexion();
        boolean bandera = false;
        System.out.println("CustomerId:" + customerId + ",AlertCode:" + alertCode + "enabled:" + enabled + "accountId:" + accountId + "monto:" + monto);
        try {
            String validationId = "";
            validationId = RandomAlfa().toUpperCase();
            //Verifico que exista el socio y la cuenta
            if (buscarDatosCuenta(customerId, accountId, tipoalerta)) {
                boolean b = false;
                //Verifico que no se halla validado anteriormente los datos 
                String sqlValidado = "SELECT * FROM v_alertas WHERE alertcode='" + alertCode + "' AND customerId='" + customerId + "' AND accountId='" + accountId + "'";
                System.out.println("SQLValidado:" + sqlValidado);
                v_Alertas v = null;
                try {
                    Query queryAlertas = em.createNativeQuery(sqlValidado, v_Alertas.class);
                    v = (v_Alertas) queryAlertas.getSingleResult();
                    b = true;
                } catch (Exception e) {
                    System.out.println("Error al buscar validacion de datos:" + e.getMessage());
                }

                if (b) {
                    if (v != null) {
                        if (v.getAlertCode().equals("BALANCE_ABOVE") || v.getAlertCode().equals("BALANCE_BELOW")) {
                            //Es el mismo estatus
                            if (v.isEnabled() == enabled) {
                                if (Objects.equals(v.getMonto(), monto)) {
                                    validationId = v.getValidationid_generado();
                                    bandera = true;
                                } else {
                                    v.setMonto(monto);
                                    EntityTransaction tr = em.getTransaction();
                                    tr.begin();
                                    em.persist(v);
                                    tr.commit();
                                    validationId = v.getValidationid_generado();
                                    bandera = true;
                                }
                            } //Es diferente el estatus
                            else {
                                if (Objects.equals(v.getMonto(), monto)) {
                                    v.setEnabled(enabled);
                                    EntityTransaction tr = em.getTransaction();
                                    tr.begin();
                                    em.persist(v);
                                    tr.commit();
                                    validationId = v.getValidationid_generado();
                                    bandera = true;
                                } else {
                                    v.setEnabled(enabled);
                                    v.setMonto(monto);
                                    EntityTransaction tr = em.getTransaction();
                                    tr.begin();
                                    em.persist(v);
                                    tr.commit();
                                    validationId = v.getValidationid_generado();
                                    bandera = true;
                                }
                            }
                        } //Preguntamos el estatus si es igual
                        else if (v.isEnabled() == enabled) {
                            //Si ya existe la validacion regresamos el idvaliadacion
                            validationId = v.getValidationid_generado();
                            bandera = true;
                        } //Si el estatus que esta llegando es diferente, es un cambio de estatus
                        else if (v.isEnabled() != enabled) {
                            v.setEnabled(enabled);
                            EntityTransaction tr = em.getTransaction();
                            tr.begin();
                            em.persist(v);
                            tr.commit();
                            validationId = v.getValidationid_generado();
                            bandera = true;
                        }
                    }
                } else {
                    System.out.println("no existe registros se insertara");
                    v_Alertas vl = new v_Alertas();
                    vl.setAlertCode(alertCode);
                    vl.setCustomerid(customerId);
                    vl.setEnabled(enabled);
                    vl.setFechaejecucion(new Date());
                    vl.setAccountId(accountId);
                    vl.setValidationid_generado(validationId);
                    vl.setMonto(monto);
                    EntityTransaction tr = em.getTransaction();
                    tr.begin();
                    em.persist(vl);
                    tr.commit();
                    bandera = true;
                }
            }
            if (bandera) {
                return validationId;
            }
        } catch (Exception e) {
            System.out.println("Error al generar codigo de validacion:" + e.getMessage());
        } finally {
            em.close();
        }
        return null;
    }

    public String executeAlert(String validationId) {
        EntityManager em = AbstractFacade.conexion();
        boolean bandera = false;
        String mensaje = "";
        try {
            String consulta = "SELECT * FROM v_alertas WHERE validationid_generado='" + validationId + "'";
            System.out.println("Consulta:" + consulta);
            v_Alertas alertaValida = null;
            e_Alerts alertaEjecutada = null;
            boolean b = false;
            try {
                Query query1 = em.createNativeQuery(consulta, v_Alertas.class);
                alertaValida = (v_Alertas) query1.getSingleResult();
                //Buscamos si no existe ya un registro para la alerta y con estatus true
                String busquedaAlerta = "SELECT * FROM e_alertas WHERE customerid='" + alertaValida.getCustomerid() + "' AND alertcode='" + alertaValida.getAlertCode() + "' AND accountid='" + alertaValida.getAccountId() + "'";
                Query queryb = em.createNativeQuery(busquedaAlerta, e_Alerts.class);
                alertaEjecutada = (e_Alerts) queryb.getSingleResult();
                b = true;
            } catch (Exception e) {

                System.out.println("Error al buscar validacion");
            }

            if (b) {
                if (alertaEjecutada.getAlertCode().equals("BALANCE_ABOVE") || alertaEjecutada.getAlertCode().equals("BALANCE_BELOW")) {
                    if (alertaEjecutada.isEnabled() == alertaValida.isEnabled()) {
                        int exe_monto = alertaEjecutada.getMonto().intValue();
                        int val_monto = alertaValida.getMonto().intValue();
                        System.out.println("EXECUTE MONTO: " + exe_monto + " | " + "VALIDATE MONTO: " + val_monto);
                        if (exe_monto == val_monto) {
                            System.out.println("EL MONTO ES IGUAL (IF)");
                            //mensaje = "La alerta ya esta registrada con estatus completado";
                            System.out.println("La alerta ya esta registrada con estatus completado");
                            bandera = true;
                        } else {
                            System.out.println("EL MONTO ES DIFERENTE (IF)");
                            em.getTransaction().begin();
                            alertaEjecutada.setMonto(alertaValida.getMonto());
                            em.persist(alertaEjecutada);
                            em.getTransaction().commit();
                            bandera = true;
                        }
                    } else {
                        int exe_monto = alertaEjecutada.getMonto().intValue();
                        int val_monto = alertaValida.getMonto().intValue();
                        System.out.println("EXECUTE MONTO: " + exe_monto + " | " + "VALIDATE MONTO: " + val_monto);
                        if (exe_monto == val_monto) {
                            System.out.println("EL MONTO ES IGUAL (ELSE)");
                            em.getTransaction().begin();
                            alertaEjecutada.setEnabled(alertaValida.isEnabled());
                            em.persist(alertaEjecutada);
                            em.getTransaction().commit();
                            bandera = true;
                        } else {
                            System.out.println("EL MONTO ES DIFERENTE (ELSE)");
                            em.getTransaction().begin();
                            alertaEjecutada.setEnabled(alertaValida.isEnabled());
                            alertaEjecutada.setMonto(alertaValida.getMonto());
                            em.persist(alertaEjecutada);
                            em.getTransaction().commit();
                            bandera = true;
                        }
                    }
                } else if (alertaEjecutada.isEnabled() == alertaValida.isEnabled()) {
                    //mensaje = "La alerta ya esta registrada con estatus completado";
                    System.out.println("La alerta ya esta registrada con estatus completado");
                    bandera = true;
                } else {
                    em.getTransaction().begin();
                    alertaEjecutada.setEnabled(alertaValida.isEnabled());
                    em.persist(alertaEjecutada);
                    em.getTransaction().commit();
                    bandera = true;
                }
            } else {
                e_Alerts executeAlerts = new e_Alerts();
                executeAlerts.setAccountId(alertaValida.getAccountId());
                executeAlerts.setAlertCode(alertaValida.getAlertCode());
                executeAlerts.setCustomerid(alertaValida.getCustomerid());
                executeAlerts.setEnabled(alertaValida.isEnabled());
                executeAlerts.setFechaejecucion(new Date());
                executeAlerts.setMonto(alertaValida.getMonto());
                executeAlerts.setOperator(alertaValida.getOperator());
                executeAlerts.setProperty(alertaValida.getProperty());
                executeAlerts.setRuleType(alertaValida.getRuleType());
                executeAlerts.setAlert_enviado(false);
                //tipos_cuenta_siscoop tps = em.find(tipos_cuenta_siscoop.class, Integer.parseInt(alertaValida.getAccountId().substring(6, 11)));
                //consumoCallBack(alertaValida.getAlertCode(), alertaValida.getCustomerid(), alertaValida.getAccountId(), tps.getProducttypename(), alertaValida.getMonto());
                em.getTransaction().begin();
                em.persist(executeAlerts);
                // em.remove(alertaValida);
                em.getTransaction().commit();
                bandera = true;
            }

            if (bandera) {
                return "completed";
            } else {
                return mensaje;
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar:" + e.getMessage());
            em.close();
            return e.getMessage();
        } finally {
            em.close();
        }
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

    public boolean buscarDatosCuenta(String customerId, String accountId, int tipoalerta) {
        EntityManager em = AbstractFacade.conexion();
        boolean bandera = false;
        opaDTO opa = null;
        ogsDTO ogs = Util.ogs(customerId);
        String consulta = "";
        if (tipoalerta != 1) {
            opa = Util.opa(accountId);
            consulta = "SELECT count(*) FROM auxiliares a WHERE "
                    + " a.idorigenp = " + opa.getIdorigenp() + " AND a.idproducto = " + opa.getIdproducto() + " AND a.idauxiliar = " + opa.getIdauxiliar()
                    + " AND a.idorigen = " + ogs.getIdorigen() + " AND a.idgrupo = " + ogs.getIdgrupo() + " AND a.idsocio = " + ogs.getIdsocio() + " AND a.estatus = 2";
        } else {
            consulta = "SELECT count(*) FROM auxiliares a WHERE "
                    + " a.idorigen = " + ogs.getIdorigen() + " AND a.idgrupo = " + ogs.getIdgrupo() + " AND a.idsocio = " + ogs.getIdsocio() + " AND a.estatus = 2";
        }
        try {
            Query query = em.createNativeQuery(consulta);
            int count = Integer.parseInt(String.valueOf(query.getSingleResult()));
            System.out.println("count:" + count);
            if (count > 0) {
                bandera = true;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar persona:" + e.getMessage());
        } finally {
            em.close();
        }
        return bandera;
    }

    /*public void cerrar() {
        emf.close();
    }*/
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
        if (hora.toUpperCase().replace(" ", "").equals("22:00PM")) {

        }
    }

    /*public boolean actividad_horario() {
        EntityManager em = AbstractFacade.conexion();
        boolean bandera_ = false;
        try {
            if (Util.actividad(em)) {
                bandera_ = true;
            }
        } catch (Exception e) {
            System.out.println("ERROR AL VERIFICAR EL HORARIO DE ACTIVIDAD");
        } finally {
            em.close();
        }

        return bandera_;
    }*/
}
