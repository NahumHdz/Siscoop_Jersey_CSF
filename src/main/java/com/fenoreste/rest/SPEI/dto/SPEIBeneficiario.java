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
public class SPEIBeneficiario {

    private String Nombre;
    private int TipoCuenta;
    private String CuentaTarjeta;
    private String RfcCurp;
    private int InstitucionContraparte;
    private String CorreoElectronico;

    public SPEIBeneficiario() {
    }

    public SPEIBeneficiario(String Nombre, int TipoCuenta, String CuentaTarjeta, String RfcCurp, int InstitucionContraparte, String CorreoElectronico) {
        this.Nombre = Nombre;
        this.TipoCuenta = TipoCuenta;
        this.CuentaTarjeta = CuentaTarjeta;
        this.RfcCurp = RfcCurp;
        this.InstitucionContraparte = InstitucionContraparte;
        this.CorreoElectronico = CorreoElectronico;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public int getTipoCuenta() {
        return TipoCuenta;
    }

    public void setTipoCuenta(int TipoCuenta) {
        this.TipoCuenta = TipoCuenta;
    }

    public String getCuentaTarjeta() {
        return CuentaTarjeta;
    }

    public void setCuentaTarjeta(String CuentaTarjeta) {
        this.CuentaTarjeta = CuentaTarjeta;
    }

    public String getRfcCurp() {
        return RfcCurp;
    }

    public void setRfcCurp(String RfcCurp) {
        this.RfcCurp = RfcCurp;
    }

    public int getInstitucionContraparte() {
        return InstitucionContraparte;
    }

    public void setInstitucionContraparte(int InstitucionContraparte) {
        this.InstitucionContraparte = InstitucionContraparte;
    }

    public String getCorreoElectronico() {
        return CorreoElectronico;
    }

    public void setCorreoElectronico(String CorreoElectronico) {
        this.CorreoElectronico = CorreoElectronico;
    }

   
}
