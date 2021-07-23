/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.SPEI.dto;

/**
 *
 * @author wilmer
 */
public class SPEISolicitanteResponse {

    private int CIF;
    private String CuentaTarjeta;
    private String Nombre;
    private String RfcCurp;
    private int TipoCuenta;
    private String CorreoElectronico;

    public SPEISolicitanteResponse() {
    }

    public SPEISolicitanteResponse(int CIF, String CuentaTarjeta, String Nombre, String RfcCurp, int TipoCuenta, String CorreoElectronico) {
        this.CIF = CIF;
        this.CuentaTarjeta = CuentaTarjeta;
        this.Nombre = Nombre;
        this.RfcCurp = RfcCurp;
        this.TipoCuenta = TipoCuenta;
        this.CorreoElectronico = CorreoElectronico;
    }

    public int getCIF() {
        return CIF;
    }

    public void setCIF(int CIF) {
        this.CIF = CIF;
    }

    public String getCuentaTarjeta() {
        return CuentaTarjeta;
    }

    public void setCuentaTarjeta(String CuentaTarjeta) {
        this.CuentaTarjeta = CuentaTarjeta;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getRfcCurp() {
        return RfcCurp;
    }

    public void setRfcCurp(String RfcCurp) {
        this.RfcCurp = RfcCurp;
    }

    public int getTipoCuenta() {
        return TipoCuenta;
    }

    public void setTipoCuenta(int TipoCuenta) {
        this.TipoCuenta = TipoCuenta;
    }

    public String getCorreoElectronico() {
        return CorreoElectronico;
    }

    public void setCorreoElectronico(String CorreoElectronico) {
        this.CorreoElectronico = CorreoElectronico;
    }

    

    
}