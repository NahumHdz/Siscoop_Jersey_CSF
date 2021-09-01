/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.services.SPEI;

import com.fenoreste.rest.SPEI.dto.EnviarOrdenSPEIDTO;
import com.fenoreste.rest.SPEI.dto.InformacionDTO;
import com.fenoreste.rest.SPEI.dto.SPEIBeneficiario;
import com.fenoreste.rest.SPEI.dto.SPEISolicitanteRequest;
import com.fenoreste.rest.SPEI.dto.SPEISolicitanteResponse;
import com.fenoreste.rest.SPEI.dto.SPEITransaccion;
import com.github.cliftonlabs.json_simple.JsonObject;
import javax.json.Json;
import com.github.cliftonlabs.json_simple.JsonArray;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author wilmer
 */
@Path("api/spei")
public class ResourcesSPEI {

    @Path("SrvEnviaOrden")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response SrvEnviaOrden(@HeaderParam("authorization") String authorization,
            @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Accept") String accept,
            @HeaderParam("InstitucionOperante") int institucionOperante,
            @HeaderParam("Empresa") String empresa,
            @HeaderParam("TipoPago") int tipoPago,
            @HeaderParam("TipoCuenta") int tipoCuenta,
            @HeaderParam("Topologia") String topologia,
            @HeaderParam("Usuario") String usuario,
            @HeaderParam("Prioridad") int prioridad,
            @HeaderParam("ClaveRastreo") String claveRastreo,
            @HeaderParam("MedioEntrega") String medioEntrega,
            @HeaderParam("Firma") String firma, String request) {

        EnviarOrdenSPEIDTO ResponseO = new EnviarOrdenSPEIDTO();
        InformacionDTO informacion = new InformacionDTO();
        JSONObject jsonRequest = new JSONObject(request);
        JSONObject solicitante_ = jsonRequest.getJSONObject("Solicitante");
        JSONObject beneficiario_ = jsonRequest.getJSONObject("Beneficiario");
        JsonObject jsonResponse = new JsonObject();

        try {

            SPEISolicitanteRequest solicitante = new SPEISolicitanteRequest();
            solicitante.setCIF(solicitante_.getInt("CIF"));
            solicitante.setClabe(solicitante_.getString("CLABE"));
            solicitante.setCorreoElectronico(solicitante_.getString("CorreoElectronico"));
            solicitante.setNombre(solicitante_.getString("Nombre"));
            solicitante.setRfcCurp(solicitante_.getString("RfcCurp"));

            SPEIBeneficiario beneficiario = new SPEIBeneficiario();
            beneficiario.setNombre(beneficiario_.getString("Nombre"));
            beneficiario.setCorreoElectronico(beneficiario_.getString("CorreoElectronico"));
            beneficiario.setCuentaTarjeta(beneficiario_.getString("CuentaTarjeta"));
            beneficiario.setInstitucionContraparte(beneficiario_.getInt("InstitucionContraparte"));
            beneficiario.setRfcCurp(beneficiario_.getString("RfcCurp"));
            beneficiario.setTipoCuenta(beneficiario_.getInt("TipoCuenta"));

            SPEITransaccion transaccion = new SPEITransaccion();
            transaccion.setIdentificadorUnicoSPEI("01154778");
            transaccion.setClaveRastreo("2021061090650115477856789ABCD");
            transaccion.setConceptoPago("PRUEBA SPEI");
            transaccion.setEstado("008");
            transaccion.setFechaHoraOperacion("2019-10-30T22:44:24.845Z");
            transaccion.setMonto(0.01);
            transaccion.setIVA(0.00);
            transaccion.setNumeroReferencia(1234567);
            transaccion.setFechaHoraCaptura("2021-07-14T12:04:23.000-06:00");
            transaccion.setComision(0.0);
            transaccion.setCausaDevolucion(0);
            transaccion.setClaveRastreoDevolucion("2021061090650115477856789ABDE");
            
            //Respuesta para enviar orden
            SPEISolicitanteResponse solicitanteRes = new SPEISolicitanteResponse();
            solicitanteRes.setCIF(solicitante.getCIF());
            solicitanteRes.setCorreoElectronico(solicitante.getCorreoElectronico());
            solicitanteRes.setCuentaTarjeta(solicitante.getClabe());
            solicitanteRes.setNombre(solicitante.getNombre());
            solicitanteRes.setRfcCurp(solicitante.getRfcCurp());
            solicitanteRes.setTipoCuenta(40);

            informacion.setSolicitante(solicitanteRes);
            informacion.setBeneficiario(beneficiario);
            informacion.setTransaccion(transaccion);
            informacion.setURLConsultaCEP("string");

            ResponseO.setObjetoInformacion(informacion);
            ResponseO.setIdTransaccion("25AEA00451CA23P1204PT89754P4126L8");
            ResponseO.setEstatusProceso(0);
            ResponseO.setMensaje("OK");

            javax.json.JsonObject jsito = null;

            JsonObject jsonSolicitante = new JsonObject();
            jsonSolicitante.put("CIF", String.valueOf(solicitanteRes.getCIF()));
            jsonSolicitante.put("CuentaTarjeta", solicitanteRes.getCuentaTarjeta());
            jsonSolicitante.put("Nombre", solicitante.getNombre());
            jsonSolicitante.put("RfcCurp", solicitante.getRfcCurp());
            jsonSolicitante.put("TipoCuenta", solicitanteRes.getTipoCuenta());
            jsonSolicitante.put("CorreoElectronico", solicitante.getCorreoElectronico());

            JsonObject jsonBeneficiario = new JsonObject();
            jsonBeneficiario.put("CuentaTarjeta", beneficiario.getCuentaTarjeta());
            jsonBeneficiario.put("Nombre", beneficiario.getNombre());
            jsonBeneficiario.put("RfcCurp", beneficiario.getRfcCurp());
            jsonBeneficiario.put("InstitucionContraparte", beneficiario.getInstitucionContraparte());
            jsonBeneficiario.put("TipoCuenta", beneficiario.getTipoCuenta());
            jsonBeneficiario.put("CorreoElectronico", beneficiario.getCorreoElectronico());

            JsonObject DatosTransaccion = new JsonObject();
            DatosTransaccion.put("IdentificadorUnicoSPEI", String.valueOf(transaccion.getNumeroReferencia()));
            DatosTransaccion.put("ClaveRastreo", transaccion.getClaveRastreo());
            DatosTransaccion.put("ConceptoPago", transaccion.getConceptoPago());
            DatosTransaccion.put("Estado", transaccion.getEstado());
            DatosTransaccion.put("FechaHoraOperacion", transaccion.getFechaHoraOperacion());
            DatosTransaccion.put("Monto", transaccion.getMonto());
            DatosTransaccion.put("IVA", transaccion.getIVA());
            DatosTransaccion.put("NumeroReferencia", transaccion.getNumeroReferencia());
            DatosTransaccion.put("FechaHoraCaptura", transaccion.getFechaHoraCaptura());
            DatosTransaccion.put("Comision", transaccion.getComision());
            DatosTransaccion.put("CausaDevolucion", transaccion.getCausaDevolucion());
            DatosTransaccion.put("ClaveRastreoDevolucion", transaccion.getClaveRastreoDevolucion());

            JsonObject DatosInformacion = new JsonObject();
            DatosInformacion.put("Solicitante", jsonSolicitante);
            DatosInformacion.put("Beneficiario", jsonBeneficiario);
            DatosInformacion.put("DatosTransaccion", DatosTransaccion);
            DatosInformacion.put("URLConsultaCEP", informacion.getURLConsultaCEP());

            jsonResponse.put("ObjetoInformacion", DatosInformacion);
            jsonResponse.put("EstatusProceso", 0);
            jsonResponse.put("IdTransaccion", "25AEA00451CA23P1204PT89754P4126L8");
            jsonResponse.put("Mensaje", "OK");

        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());

        }

        return Response.status(Response.Status.OK).entity(jsonResponse).build();
    }

    @POST
    @Path("/SrvConsultaOrden")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response SrvConsultaOrden(@HeaderParam("Authorization") String authorization,
            @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Accept") String accept,
            @HeaderParam("IdCanalASP") int idAspCanal,
            String request) {
        JSONObject requestObject = new JSONObject(request);
        EnviarOrdenSPEIDTO responseDTO = new EnviarOrdenSPEIDTO();
        InformacionDTO informacion = new InformacionDTO();
        JsonObject jsonResponse = new JsonObject();
        try {
            SPEISolicitanteResponse solicitante = new SPEISolicitanteResponse();
            solicitante.setCIF(123456);
            solicitante.setCuentaTarjeta("856040902522300450");
            solicitante.setCorreoElectronico("ANAMARIA@HOTMAIL.COM");
            solicitante.setNombre("ANA MARIA LOPEZ CASTRO");
            solicitante.setRfcCurp("CALA931128MZ9");
            solicitante.setTipoCuenta(40);

            SPEIBeneficiario beneficiario = new SPEIBeneficiario();
            beneficiario.setNombre("Juan Jose Torres");
            beneficiario.setCorreoElectronico("");
            beneficiario.setCuentaTarjeta("8800001235");
            beneficiario.setInstitucionContraparte(510041);
            beneficiario.setRfcCurp("TOJ010203BB1");
            beneficiario.setTipoCuenta(10);

            SPEITransaccion transaccion = new SPEITransaccion();
            transaccion.setIdentificadorUnicoSPEI("01154778");
            transaccion.setClaveRastreo("2021061090650115477856789ABCD");
            transaccion.setConceptoPago("PRUEBA SPEI");
            transaccion.setEstado("008");
            transaccion.setFechaHoraOperacion("2019-10-30T22:44:24.845Z");
            transaccion.setMonto(0.01);
            transaccion.setIVA(0.00);
            transaccion.setNumeroReferencia(1234567);
            transaccion.setFechaHoraCaptura("2021-07-14T12:04:23.000-06:00");
            transaccion.setComision(0.0);
            transaccion.setCausaDevolucion(0);
            transaccion.setClaveRastreoDevolucion("2021061090650115477856789ABDE");

            informacion.setSolicitante(solicitante);
            informacion.setBeneficiario(beneficiario);
            informacion.setTransaccion(transaccion);
            informacion.setURLConsultaCEP("string");

            responseDTO.setObjetoInformacion(informacion);
            responseDTO.setIdTransaccion("25AEA00451CA23P1204PT89754P4126L8");
            responseDTO.setEstatusProceso(0);
            responseDTO.setMensaje("OK");

            javax.json.JsonObject jsito = null;

            JsonObject jsonSolicitante = new JsonObject();
            jsonSolicitante.put("CIF", String.valueOf(123456));
            jsonSolicitante.put("CuentaTarjeta", solicitante.getCuentaTarjeta());
            jsonSolicitante.put("Nombre", solicitante.getNombre());
            jsonSolicitante.put("RfcCurp", solicitante.getRfcCurp());
            jsonSolicitante.put("TipoCuenta", solicitante.getTipoCuenta());
            jsonSolicitante.put("CorreoElectronico", solicitante.getCorreoElectronico());

            JsonObject jsonBeneficiario = new JsonObject();
            jsonBeneficiario.put("CuentaTarjeta", beneficiario.getCuentaTarjeta());
            jsonBeneficiario.put("Nombre", beneficiario.getNombre());
            jsonBeneficiario.put("RfcCurp", beneficiario.getRfcCurp());
            jsonBeneficiario.put("InstitucionContraparte", beneficiario.getInstitucionContraparte());
            jsonBeneficiario.put("TipoCuenta", beneficiario.getTipoCuenta());
            jsonBeneficiario.put("CorreoElectronico", beneficiario.getCorreoElectronico());

            JsonObject DatosTransaccion = new JsonObject();
            DatosTransaccion.put("IdentificadorUnicoSPEI", String.valueOf(transaccion.getNumeroReferencia()));
            DatosTransaccion.put("ClaveRastreo", transaccion.getClaveRastreo());
            DatosTransaccion.put("ConceptoPago", transaccion.getConceptoPago());
            DatosTransaccion.put("Estado", transaccion.getEstado());
            DatosTransaccion.put("FechaHoraOperacion", transaccion.getFechaHoraOperacion());
            DatosTransaccion.put("Monto", transaccion.getMonto());
            DatosTransaccion.put("IVA", transaccion.getIVA());
            DatosTransaccion.put("NumeroReferencia", transaccion.getNumeroReferencia());
            DatosTransaccion.put("FechaHoraTransferencia", "2019-10-30T22:44:24.845Z");
            DatosTransaccion.put("FechaHoraCaptura", transaccion.getFechaHoraCaptura());
            DatosTransaccion.put("FechaHoraAcuse", "2019-10-30T22:44:24.845Z");
            DatosTransaccion.put("FechaHoraDevolucion", "2019-10-30T22:44:24.845Z");
            DatosTransaccion.put("FechaHoraEntrega", "2019-10-30T22:44:24.845Z");
            DatosTransaccion.put("FechaHoraLiquidacion", "2019-10-30T22:44:24.845Z");
            DatosTransaccion.put("Comision", transaccion.getComision());
            DatosTransaccion.put("CausaDevolucion", transaccion.getCausaDevolucion());
            DatosTransaccion.put("ClaveRastreoDevolucion", transaccion.getClaveRastreoDevolucion());

            JsonObject DatosInformacion = new JsonObject();
            DatosInformacion.put("Solicitante", jsonSolicitante);
            DatosInformacion.put("Beneficiario", jsonBeneficiario);
            DatosInformacion.put("DatosTransaccion", DatosTransaccion);
            DatosInformacion.put("URLConsultaCEP", informacion.getURLConsultaCEP());

            jsonResponse.put("ObjetoInformacion", DatosInformacion);
            jsonResponse.put("EstatusProceso", 0);
            jsonResponse.put("IdTransaccion", "25AEA00451CA23P1204PT89754P4126L8");
            jsonResponse.put("Mensaje", "OK");

        } catch (Exception e) {
            System.out.println("Error al formar response:" + e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(jsonResponse).build();
    }

    @POST
    @Path("/SrvConsultarEstatusOrden")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response SrvConsultarEstatusOrden(@HeaderParam("Authorization") String authorization,
            @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Accept") String accept,
            @HeaderParam("IdCanalASP") int idAspCanal,
            String request) {
        JSONObject requestObject = new JSONObject(request);
        JSONArray listaSpeiId = requestObject.getJSONArray("ListaIdentificadorUnicoSPEI");
        JsonObject jsonResponse = new JsonObject();
        try {
            JsonArray lista = new JsonArray();
            String idSPEI = "";
            for (int i = 0; i < listaSpeiId.length(); i++) {
                JsonObject jsonT = new JsonObject();
                idSPEI = listaSpeiId.get(i).toString();
                jsonT.put("IdentificadorUnicoSPEI", idSPEI);
                jsonT.put("Estado", "004");
                lista.add(jsonT);
            }
            jsonResponse.put("ObjetoInformacion", lista);
            jsonResponse.put("EstatusProceso", 0);
            jsonResponse.put("IdTransaccion", "25AEA00451CA23P1204PT89754P4126L8");
            jsonResponse.put("Mensaje", "OK");

        } catch (Exception e) {
            System.out.println("Error al formar response:" + e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(jsonResponse).build();
    }

    @POST
    @Path("/SrvConsultarOrdenes")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response SrvConsultarOrdenes(@HeaderParam("Authorization") String authorization,
            @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Accept") String accept,
            @HeaderParam("IdCanalASP") int idAspCanal,
            String request) {
        JSONObject requestObject = new JSONObject(request);
        EnviarOrdenSPEIDTO responseDTO = new EnviarOrdenSPEIDTO();
        InformacionDTO informacion = new InformacionDTO();
        JsonObject jsonResponse = new JsonObject();
        JsonArray jsonArr = new JsonArray();

        JsonArray informacionJson = new JsonArray();
        JsonObject DatosInformacion = new JsonObject();
        try {
            List<Object> lista = new LinkedList<>();
            for (int i = 0; i < 2; i++) {
                JsonObject jsoni = new JsonObject();
                SPEISolicitanteResponse solicitante = new SPEISolicitanteResponse();
                solicitante.setCIF(123456);
                solicitante.setCuentaTarjeta("856040902522300450");
                solicitante.setCorreoElectronico("ANAMARIA@HOTMAIL.COM");
                solicitante.setNombre("ANA MARIA LOPEZ CASTRO");
                solicitante.setRfcCurp("CALA931128MZ9");
                solicitante.setTipoCuenta(40);

                SPEIBeneficiario beneficiario = new SPEIBeneficiario();
                beneficiario.setNombre("Juan Jose Torres");
                beneficiario.setCorreoElectronico("");
                beneficiario.setCuentaTarjeta("8800001235");
                beneficiario.setInstitucionContraparte(510041);
                beneficiario.setRfcCurp("TOJ010203BB1");
                beneficiario.setTipoCuenta(10);

                SPEITransaccion transaccion = new SPEITransaccion();
                transaccion.setIdentificadorUnicoSPEI("01154778");
                transaccion.setClaveRastreo("2021061090650115477856789ABCD");
                transaccion.setConceptoPago("PRUEBA SPEI");
                transaccion.setEstado("008");
                transaccion.setFechaHoraOperacion("2019-10-30T22:44:24.845Z");
                transaccion.setMonto(0.01);
                transaccion.setIVA(0.00);
                transaccion.setNumeroReferencia(1234567);
                transaccion.setFechaHoraCaptura("2021-07-14T12:04:23.000-06:00");
                transaccion.setComision(0.0);
                transaccion.setCausaDevolucion(0);
                transaccion.setClaveRastreoDevolucion("2021061090650115477856789ABDE");

                informacion.setSolicitante(solicitante);
                informacion.setBeneficiario(beneficiario);
                informacion.setTransaccion(transaccion);
                informacion.setURLConsultaCEP("string");

                responseDTO.setObjetoInformacion(informacion);
                responseDTO.setIdTransaccion("25AEA00451CA23P1204PT89754P4126L8");
                responseDTO.setEstatusProceso(0);
                responseDTO.setMensaje("OK");

                JsonObject jsonSolicitante = new JsonObject();
                jsonSolicitante.put("CIF", String.valueOf(123456));
                jsonSolicitante.put("CuentaTarjeta", solicitante.getCuentaTarjeta());
                jsonSolicitante.put("Nombre", solicitante.getNombre());
                jsonSolicitante.put("RfcCurp", solicitante.getRfcCurp());
                jsonSolicitante.put("TipoCuenta", solicitante.getTipoCuenta());
                jsonSolicitante.put("CorreoElectronico", solicitante.getCorreoElectronico());

                JsonObject jsonBeneficiario = new JsonObject();
                jsonBeneficiario.put("CuentaTarjeta", beneficiario.getCuentaTarjeta());
                jsonBeneficiario.put("Nombre", beneficiario.getNombre());
                jsonBeneficiario.put("RfcCurp", beneficiario.getRfcCurp());
                jsonBeneficiario.put("InstitucionContraparte", beneficiario.getInstitucionContraparte());
                jsonBeneficiario.put("TipoCuenta", beneficiario.getTipoCuenta());
                jsonBeneficiario.put("CorreoElectronico", beneficiario.getCorreoElectronico());

                JsonObject DatosTransaccion = new JsonObject();
                DatosTransaccion.put("IdentificadorUnicoSPEI", String.valueOf(transaccion.getNumeroReferencia()));
                DatosTransaccion.put("ClaveRastreo", transaccion.getClaveRastreo());
                DatosTransaccion.put("ConceptoPago", transaccion.getConceptoPago());
                DatosTransaccion.put("Estado", transaccion.getEstado());
                DatosTransaccion.put("FechaHoraOperacion", transaccion.getFechaHoraOperacion());
                DatosTransaccion.put("Monto", transaccion.getMonto());
                DatosTransaccion.put("IVA", transaccion.getIVA());
                DatosTransaccion.put("NumeroReferencia", transaccion.getNumeroReferencia());
                DatosTransaccion.put("FechaHoraTransferencia", "2019-10-30T22:44:24.845Z");
                DatosTransaccion.put("FechaHoraCaptura", transaccion.getFechaHoraCaptura());
                DatosTransaccion.put("FechaHoraAcuse", "2019-10-30T22:44:24.845Z");
                //DatosTransaccion.put("FechaHoraDevolucion", "2019-10-30T22:44:24.845Z");
                DatosTransaccion.put("FechaHoraEntrega", "2019-10-30T22:44:24.845Z");
                DatosTransaccion.put("FechaHoraLiquidacion", "2019-10-30T22:44:24.845Z");
                DatosTransaccion.put("Comision", transaccion.getComision());
                DatosTransaccion.put("CausaDevolucion", transaccion.getCausaDevolucion());
                DatosTransaccion.put("ClaveRastreoDevolucion", transaccion.getClaveRastreoDevolucion());

                /*informacionJson.add(jsonSolicitante);
            informacionJson.add(jsonBeneficiario);
            informacionJson.add(DatosTransaccion);
            informacionJson.add(informacion.getURLConsultaCEP());
                 */
                DatosInformacion.put("Solicitante", jsonSolicitante);
                DatosInformacion.put("Beneficiario", jsonBeneficiario);
                DatosInformacion.put("DatosTransaccion", DatosTransaccion);
                DatosInformacion.put("URLConsultaCEP", informacion.getURLConsultaCEP());

                informacionJson.add(DatosInformacion);
                /*
            jsonResponse.put("ObjetoInformacion", DatosInformacion);
            jsonResponse.put("EstatusProceso", 0);
            jsonResponse.put("IdTransaccion", "25AEA00451CA23P1204PT89754P4126L8");
            jsonResponse.put("Mensaje", "OK");*/

            }
            jsonResponse.put("ObjetoInformacion", informacionJson);
            //jsonResponse.put("ObjetoInformacion", DatosInformacion);
            jsonResponse.put("EstatusProceso", 0);
            jsonResponse.put("IdTransaccion", "25AEA00451CA23P1204PT89754P4126L8");
            jsonResponse.put("Mensaje", "OK");

        } catch (Exception e) {
            System.out.println("Error al formar response:" + e.getMessage());
        }
        return Response.status(Response.Status.OK).entity(jsonResponse).build();
    }

    private String validaciones(String contentType, String accept, int institucionOperante, String empresa, int tipoPago, int tipoCuenta, String topologia, String usuario, int prioridad, String claveRastreo, String medioEntrega, String firma) {
        return "";

    }
}
