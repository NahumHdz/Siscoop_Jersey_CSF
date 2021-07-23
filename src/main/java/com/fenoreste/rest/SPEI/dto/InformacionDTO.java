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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class InformacionDTO {

    private SPEISolicitanteResponse Solicitante;
    private SPEIBeneficiario Beneficiario;
    private SPEITransaccion Transaccion;
    private String URLConsultaCEP;

    public InformacionDTO() {
    }

    public InformacionDTO(SPEISolicitanteResponse Solicitante, SPEIBeneficiario Beneficiario, SPEITransaccion Transaccion, String URLConsultaCEP) {
        this.Solicitante = Solicitante;
        this.Beneficiario = Beneficiario;
        this.Transaccion = Transaccion;
        this.URLConsultaCEP = URLConsultaCEP;
    }

    public SPEISolicitanteResponse getSolicitante() {
        return Solicitante;
    }

    public void setSolicitante(SPEISolicitanteResponse Solicitante) {
        this.Solicitante = Solicitante;
    }

    public SPEIBeneficiario getBeneficiario() {
        return Beneficiario;
    }

    public void setBeneficiario(SPEIBeneficiario Beneficiario) {
        this.Beneficiario = Beneficiario;
    }

    public SPEITransaccion getTransaccion() {
        return Transaccion;
    }

    public void setTransaccion(SPEITransaccion Transaccion) {
        this.Transaccion = Transaccion;
    }

    public String getURLConsultaCEP() {
        return URLConsultaCEP;
    }

    public void setURLConsultaCEP(String URLConsultaCEP) {
        this.URLConsultaCEP = URLConsultaCEP;
    }
    
    
}
