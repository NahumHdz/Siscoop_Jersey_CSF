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
public class SPEITransaccion {

    private String IdentificadorUnicoSPEI;
    private String ClaveRastreo;
    private String ConceptoPago;
    private String Estado;
    private String FechaHoraOperacion;
    private Double Monto;
    private Double IVA;
    private Integer NumeroReferencia;
    private String FechaHoraTransferencia;
    private String FechaHoraCaptura;
    private String FechaHoraAcuse;
    private String FechaHoraDevolucion;
    private String FechaHoraEntrega;
    private String FechaHoraLiquidacion;
    private Double Comision;
    private int CausaDevolucion;
    private String ClaveRastreoDevolucion;

    public SPEITransaccion() {
    }

    public SPEITransaccion(String IdentificadorUnicoSPEI, String ClaveRastreo, String ConceptoPago, String Estado, String FechaHoraOperacion, Double Monto, Double IVA, Integer NumeroReferencia, String FechaHoraTransferencia, String FechaHoraCaptura, String FechaHoraAcuse, String FechaHoraDevolucion, String FechaHoraEntrega, String FechaHoraLiquidacion, Double Comision, int CausaDevolucion, String ClaveRastreoDevolucion) {
        this.IdentificadorUnicoSPEI = IdentificadorUnicoSPEI;
        this.ClaveRastreo = ClaveRastreo;
        this.ConceptoPago = ConceptoPago;
        this.Estado = Estado;
        this.FechaHoraOperacion = FechaHoraOperacion;
        this.Monto = Monto;
        this.IVA = IVA;
        this.NumeroReferencia = NumeroReferencia;
        this.FechaHoraTransferencia = FechaHoraTransferencia;
        this.FechaHoraCaptura = FechaHoraCaptura;
        this.FechaHoraAcuse = FechaHoraAcuse;
        this.FechaHoraDevolucion = FechaHoraDevolucion;
        this.FechaHoraEntrega = FechaHoraEntrega;
        this.FechaHoraLiquidacion = FechaHoraLiquidacion;
        this.Comision = Comision;
        this.CausaDevolucion = CausaDevolucion;
        this.ClaveRastreoDevolucion = ClaveRastreoDevolucion;
    }

    public String getIdentificadorUnicoSPEI() {
        return IdentificadorUnicoSPEI;
    }

    public void setIdentificadorUnicoSPEI(String IdentificadorUnicoSPEI) {
        this.IdentificadorUnicoSPEI = IdentificadorUnicoSPEI;
    }

    public String getClaveRastreo() {
        return ClaveRastreo;
    }

    public void setClaveRastreo(String ClaveRastreo) {
        this.ClaveRastreo = ClaveRastreo;
    }

    public String getConceptoPago() {
        return ConceptoPago;
    }

    public void setConceptoPago(String ConceptoPago) {
        this.ConceptoPago = ConceptoPago;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String Estado) {
        this.Estado = Estado;
    }

    public String getFechaHoraOperacion() {
        return FechaHoraOperacion;
    }

    public void setFechaHoraOperacion(String FechaHoraOperacion) {
        this.FechaHoraOperacion = FechaHoraOperacion;
    }

    public Double getMonto() {
        return Monto;
    }

    public void setMonto(Double Monto) {
        this.Monto = Monto;
    }

    public Double getIVA() {
        return IVA;
    }

    public void setIVA(Double IVA) {
        this.IVA = IVA;
    }

    public Integer getNumeroReferencia() {
        return NumeroReferencia;
    }

    public void setNumeroReferencia(Integer NumeroReferencia) {
        this.NumeroReferencia = NumeroReferencia;
    }

    public String getFechaHoraTransferencia() {
        return FechaHoraTransferencia;
    }

    public void setFechaHoraTransferencia(String FechaHoraTransferencia) {
        this.FechaHoraTransferencia = FechaHoraTransferencia;
    }

    public String getFechaHoraCaptura() {
        return FechaHoraCaptura;
    }

    public void setFechaHoraCaptura(String FechaHoraCaptura) {
        this.FechaHoraCaptura = FechaHoraCaptura;
    }

    public String getFechaHoraAcuse() {
        return FechaHoraAcuse;
    }

    public void setFechaHoraAcuse(String FechaHoraAcuse) {
        this.FechaHoraAcuse = FechaHoraAcuse;
    }

    public String getFechaHoraDevolucion() {
        return FechaHoraDevolucion;
    }

    public void setFechaHoraDevolucion(String FechaHoraDevolucion) {
        this.FechaHoraDevolucion = FechaHoraDevolucion;
    }

    public String getFechaHoraEntrega() {
        return FechaHoraEntrega;
    }

    public void setFechaHoraEntrega(String FechaHoraEntrega) {
        this.FechaHoraEntrega = FechaHoraEntrega;
    }

    public String getFechaHoraLiquidacion() {
        return FechaHoraLiquidacion;
    }

    public void setFechaHoraLiquidacion(String FechaHoraLiquidacion) {
        this.FechaHoraLiquidacion = FechaHoraLiquidacion;
    }

    public Double getComision() {
        return Comision;
    }

    public void setComision(Double Comision) {
        this.Comision = Comision;
    }

    public int getCausaDevolucion() {
        return CausaDevolucion;
    }

    public void setCausaDevolucion(int CausaDevolucion) {
        this.CausaDevolucion = CausaDevolucion;
    }

    public String getClaveRastreoDevolucion() {
        return ClaveRastreoDevolucion;
    }

    public void setClaveRastreoDevolucion(String ClaveRastreoDevolucion) {
        this.ClaveRastreoDevolucion = ClaveRastreoDevolucion;
    }
    
    
}
