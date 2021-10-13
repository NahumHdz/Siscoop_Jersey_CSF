/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author nahum
 */
@Entity
@Table(name = "ejecutar_alerts")
public class Ejecutar_Alerts implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private Integer id;
    @Column(name = "alertcode")
    private String alertCode;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "accountid")
    private String accountId;
    @Column(name = "customerid")
    private String customerid;
    @Column(name = "monto")
    private Double monto;
    @Column(name = "fechaejecucion")
    private Date fechaejecucion;

    public Ejecutar_Alerts() {
    }

    public Ejecutar_Alerts(Integer id, String alertCode, boolean enabled, String accountId, String customerid, Double monto, Date fechaejecucion) {
        this.id = id;
        this.alertCode = alertCode;
        this.enabled = enabled;
        this.accountId = accountId;
        this.customerid = customerid;
        this.monto = monto;
        this.fechaejecucion = fechaejecucion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlertCode() {
        return alertCode;
    }

    public void setAlertCode(String alertCode) {
        this.alertCode = alertCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Date getFechaejecucion() {
        return fechaejecucion;
    }

    public void setFechaejecucion(Date fechaejecucion) {
        this.fechaejecucion = fechaejecucion;
    }

    @Override
    public String toString() {
        return "Ejecutar_Alerts{" + "id=" + id + ", alertCode=" + alertCode + ", enabled=" + enabled + ", accountId=" + accountId + ", customerid=" + customerid + ", monto=" + monto + ", fechaejecucion=" + fechaejecucion + '}';
    }

}
