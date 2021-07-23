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
public class SPEISolicitanteRequest {

    private int CIF;
    private String Nombre;
    private String RfcCurp;
    private String Clabe;
    private String CorreoElectronico;

    public SPEISolicitanteRequest() {
    }

    public SPEISolicitanteRequest(int CIF, String Nombre, String RfcCurp, String Clabe, String CorreoElectronico) {
        this.CIF = CIF;
        this.Nombre = Nombre;
        this.RfcCurp = RfcCurp;
        this.Clabe = Clabe;
        this.CorreoElectronico = CorreoElectronico;
    }

    public int getCIF() {
        return CIF;
    }

    public void setCIF(int CIF) {
        this.CIF = CIF;
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

    public String getClabe() {
        return Clabe;
    }

    public void setClabe(String Clabe) {
        this.Clabe = Clabe;
    }

    public String getCorreoElectronico() {
        return CorreoElectronico;
    }

    public void setCorreoElectronico(String CorreoElectronico) {
        this.CorreoElectronico = CorreoElectronico;
    }
   
    

}