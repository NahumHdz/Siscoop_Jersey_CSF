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
public class EnviarOrdenSPEIDTO {

    private InformacionDTO ObjetoInformacion;
    private Integer EstatusProceso;
    private String IdTransaccion;
    private String Mensaje;

    public EnviarOrdenSPEIDTO() {
    }

    public EnviarOrdenSPEIDTO(InformacionDTO ObjetoInformacion, Integer EstatusProceso, String IdTransaccion, String Mensaje) {
        this.ObjetoInformacion = ObjetoInformacion;
        this.EstatusProceso = EstatusProceso;
        this.IdTransaccion = IdTransaccion;
        this.Mensaje = Mensaje;
    }

    public InformacionDTO getObjetoInformacion() {
        return ObjetoInformacion;
    }

    public void setObjetoInformacion(InformacionDTO ObjetoInformacion) {
        this.ObjetoInformacion = ObjetoInformacion;
    }

    public Integer getEstatusProceso() {
        return EstatusProceso;
    }

    public void setEstatusProceso(Integer EstatusProceso) {
        this.EstatusProceso = EstatusProceso;
    }

    public String getIdTransaccion() {
        return IdTransaccion;
    }

    public void setIdTransaccion(String IdTransaccion) {
        this.IdTransaccion = IdTransaccion;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String Mensaje) {
        this.Mensaje = Mensaje;
    }
   
    
    

  

}
