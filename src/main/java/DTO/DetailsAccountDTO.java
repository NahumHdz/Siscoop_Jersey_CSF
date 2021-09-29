/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

/**
 *
 * @author Elliot
 */
public class DetailsAccountDTO {

    private String accountId;
    private String accountNumber;
    private String displayAccountNumber;
    private String accountType;
    private String currencyCode;
    private String productCode;
    private String status;
    private String sucursal;
    private String openedDate;
    private double tasa;
    private double proximoMontoInteres;
    private String proximaFechaPago;
    private String fechaVencimiento;
    private double montoDesembolso;

    public DetailsAccountDTO() {
    }

    public DetailsAccountDTO(String accountId, String accountNumber, String displayAccountNumber, String accountType, String currencyCode, String productCode, String status, String sucursal, String openedDate, double tasa, double proximoMontoInteres, String proximaFechaPago, String fechaVencimiento, double montoDesembolso) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.displayAccountNumber = displayAccountNumber;
        this.accountType = accountType;
        this.currencyCode = currencyCode;
        this.productCode = productCode;
        this.status = status;
        this.sucursal = sucursal;
        this.openedDate = openedDate;
        this.tasa = tasa;
        this.proximoMontoInteres = proximoMontoInteres;
        this.proximaFechaPago = proximaFechaPago;
        this.fechaVencimiento = fechaVencimiento;
        this.montoDesembolso = montoDesembolso;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDisplayAccountNumber() {
        return displayAccountNumber;
    }

    public void setDisplayAccountNumber(String displayAccountNumber) {
        this.displayAccountNumber = displayAccountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(String openedDate) {
        this.openedDate = openedDate;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public double getProximoMontoInteres() {
        return proximoMontoInteres;
    }

    public void setProximoMontoInteres(double proximoMontoInteres) {
        this.proximoMontoInteres = proximoMontoInteres;
    }

    public String getProximaFechaPago() {
        return proximaFechaPago;
    }

    public void setProximaFechaPago(String proximaFechaPago) {
        this.proximaFechaPago = proximaFechaPago;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public double getMontoDesembolso() {
        return montoDesembolso;
    }

    public void setMontoDesembolso(double montoDesembolso) {
        this.montoDesembolso = montoDesembolso;
    }

}
