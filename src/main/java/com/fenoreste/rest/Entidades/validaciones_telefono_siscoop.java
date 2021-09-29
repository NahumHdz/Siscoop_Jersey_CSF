package com.fenoreste.rest.Entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "validaciones_telefonos_siscoop")
public class validaciones_telefono_siscoop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "customerid")
    private String customerid;

    @Column(name = "validacion")
    private String validacion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "celular")
    private String celular;

    @Column(name = "email")
    private String email;

    public validaciones_telefono_siscoop() {
    }

    public validaciones_telefono_siscoop(String validacion, String customerid, String telefono, String celular, String email) {
        this.validacion = validacion;
        this.customerid = customerid;
        this.telefono = telefono;
        this.celular = celular;
        this.email = email;
    }

    public String getValidacion() {
        return validacion;
    }

    public void setValidacion(String validacion) {
        this.validacion = validacion;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "validaciones_telefono_siscoop{" + "validacion=" + validacion + ", customerid=" + customerid + ", telefono=" + telefono + ", celular=" + celular + ", email=" + email + '}';
    }

}
