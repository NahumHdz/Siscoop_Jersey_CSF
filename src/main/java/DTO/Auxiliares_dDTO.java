/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

import com.fenoreste.rest.Entidades.AuxiliaresDPK;
import java.math.BigDecimal;

/**
 *
 * @author nahum
 */
public class Auxiliares_dDTO {

    protected AuxiliaresDPK auxiliaresDPK;
    private Short cargoabono;
    private BigDecimal monto;
    private BigDecimal montoio;
    private BigDecimal montoim;
    private BigDecimal montoiva;
    private Integer idorigenc;
    private String periodo;
    private Short idtipo;
    private Integer idpoliza;
    private Short tipomov;
    private BigDecimal saldoec;
    private Integer transaccion;
    private BigDecimal montoivaim;
    private BigDecimal efectivo;
    private int diasvencidos;
    private BigDecimal montovencido;
    private Integer ticket;
    private BigDecimal montoidnc;
    private BigDecimal montoieco;
    private BigDecimal montoidncm;
    private BigDecimal montoiecom;
    private int total_lista;

    public Auxiliares_dDTO() {
    }

    public Auxiliares_dDTO(AuxiliaresDPK auxiliaresDPK, Short cargoabono, BigDecimal monto, BigDecimal montoio, BigDecimal montoim, BigDecimal montoiva, Integer idorigenc, String periodo, Short idtipo, Integer idpoliza, Short tipomov, BigDecimal saldoec, Integer transaccion, BigDecimal montoivaim, BigDecimal efectivo, int diasvencidos, BigDecimal montovencido, Integer ticket, BigDecimal montoidnc, BigDecimal montoieco, BigDecimal montoidncm, BigDecimal montoiecom, int total_lista) {
        this.auxiliaresDPK = auxiliaresDPK;
        this.cargoabono = cargoabono;
        this.monto = monto;
        this.montoio = montoio;
        this.montoim = montoim;
        this.montoiva = montoiva;
        this.idorigenc = idorigenc;
        this.periodo = periodo;
        this.idtipo = idtipo;
        this.idpoliza = idpoliza;
        this.tipomov = tipomov;
        this.saldoec = saldoec;
        this.transaccion = transaccion;
        this.montoivaim = montoivaim;
        this.efectivo = efectivo;
        this.diasvencidos = diasvencidos;
        this.montovencido = montovencido;
        this.ticket = ticket;
        this.montoidnc = montoidnc;
        this.montoieco = montoieco;
        this.montoidncm = montoidncm;
        this.montoiecom = montoiecom;
        this.total_lista = total_lista;
    }

    public AuxiliaresDPK getAuxiliaresDPK() {
        return auxiliaresDPK;
    }

    public void setAuxiliaresDPK(AuxiliaresDPK auxiliaresDPK) {
        this.auxiliaresDPK = auxiliaresDPK;
    }

    public Short getCargoabono() {
        return cargoabono;
    }

    public void setCargoabono(Short cargoabono) {
        this.cargoabono = cargoabono;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getMontoio() {
        return montoio;
    }

    public void setMontoio(BigDecimal montoio) {
        this.montoio = montoio;
    }

    public BigDecimal getMontoim() {
        return montoim;
    }

    public void setMontoim(BigDecimal montoim) {
        this.montoim = montoim;
    }

    public BigDecimal getMontoiva() {
        return montoiva;
    }

    public void setMontoiva(BigDecimal montoiva) {
        this.montoiva = montoiva;
    }

    public Integer getIdorigenc() {
        return idorigenc;
    }

    public void setIdorigenc(Integer idorigenc) {
        this.idorigenc = idorigenc;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Short getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(Short idtipo) {
        this.idtipo = idtipo;
    }

    public Integer getIdpoliza() {
        return idpoliza;
    }

    public void setIdpoliza(Integer idpoliza) {
        this.idpoliza = idpoliza;
    }

    public Short getTipomov() {
        return tipomov;
    }

    public void setTipomov(Short tipomov) {
        this.tipomov = tipomov;
    }

    public BigDecimal getSaldoec() {
        return saldoec;
    }

    public void setSaldoec(BigDecimal saldoec) {
        this.saldoec = saldoec;
    }

    public Integer getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Integer transaccion) {
        this.transaccion = transaccion;
    }

    public BigDecimal getMontoivaim() {
        return montoivaim;
    }

    public void setMontoivaim(BigDecimal montoivaim) {
        this.montoivaim = montoivaim;
    }

    public BigDecimal getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(BigDecimal efectivo) {
        this.efectivo = efectivo;
    }

    public int getDiasvencidos() {
        return diasvencidos;
    }

    public void setDiasvencidos(int diasvencidos) {
        this.diasvencidos = diasvencidos;
    }

    public BigDecimal getMontovencido() {
        return montovencido;
    }

    public void setMontovencido(BigDecimal montovencido) {
        this.montovencido = montovencido;
    }

    public Integer getTicket() {
        return ticket;
    }

    public void setTicket(Integer ticket) {
        this.ticket = ticket;
    }

    public BigDecimal getMontoidnc() {
        return montoidnc;
    }

    public void setMontoidnc(BigDecimal montoidnc) {
        this.montoidnc = montoidnc;
    }

    public BigDecimal getMontoieco() {
        return montoieco;
    }

    public void setMontoieco(BigDecimal montoieco) {
        this.montoieco = montoieco;
    }

    public BigDecimal getMontoidncm() {
        return montoidncm;
    }

    public void setMontoidncm(BigDecimal montoidncm) {
        this.montoidncm = montoidncm;
    }

    public BigDecimal getMontoiecom() {
        return montoiecom;
    }

    public void setMontoiecom(BigDecimal montoiecom) {
        this.montoiecom = montoiecom;
    }

    public int getTotal_lista() {
        return total_lista;
    }

    public void setTotal_lista(int total_lista) {
        this.total_lista = total_lista;
    }

    @Override
    public String toString() {
        return "Auxiliares_dDTO{" + "auxiliaresDPK=" + auxiliaresDPK + ", cargoabono=" + cargoabono + ", monto=" + monto + ", montoio=" + montoio + ", montoim=" + montoim + ", montoiva=" + montoiva + ", idorigenc=" + idorigenc + ", periodo=" + periodo + ", idtipo=" + idtipo + ", idpoliza=" + idpoliza + ", tipomov=" + tipomov + ", saldoec=" + saldoec + ", transaccion=" + transaccion + ", montoivaim=" + montoivaim + ", efectivo=" + efectivo + ", diasvencidos=" + diasvencidos + ", montovencido=" + montovencido + ", ticket=" + ticket + ", montoidnc=" + montoidnc + ", montoieco=" + montoieco + ", montoidncm=" + montoidncm + ", montoiecom=" + montoiecom + ", total_lista=" + total_lista + '}';
    }

}
