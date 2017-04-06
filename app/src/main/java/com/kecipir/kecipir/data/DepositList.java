package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 14/04/2016.
 */
public class DepositList {

    private String noDeposit;
    private String amount;
    private String tgl;
    private String status;
    private String ket;
    private String payment;
    private String paymentType;

    public DepositList(){

    }

    public DepositList(String noDeposit, String amount, String tgl, String status, String ket,
                       String payment, String paymentType){
        this.noDeposit = noDeposit;
        this.amount = amount;
        this.tgl = tgl;
        this.status = status;
        this.ket = ket;
        this.payment = payment;
        this.paymentType = paymentType;
    }

    public String getNoDeposit() {
        return noDeposit;
    }

    public String getAmount() {
        return amount;
    }

    public String getTgl() {
        return tgl;
    }

    public String getStatus() {
        return status;
    }

    public String getKet() {
        return ket;
    }

    public String getPayment() {
        return payment;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
