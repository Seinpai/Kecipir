package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 04/04/2016.
 */
public class PembayaranList {
    private String noNota;
    private String total;
    private String nama;
    private String tglTransaksi;
    private String tglPanen;
    private String payment;
    private String paymentType;

    public PembayaranList(){

    }

    public PembayaranList(String noNota, String total, String nama, String tglTransaksi, String tglPanen,
                          String payment, String paymentType){
        this.noNota = noNota;
        this.total = total;
        this.nama = nama;
        this.tglTransaksi = tglTransaksi;
        this.tglPanen = tglPanen;
        this.payment = payment;
        this.paymentType = paymentType;
    }

    public String getNoNota() {
        return noNota;
    }

    public void setNoNota(String noNota) {
        this.noNota = noNota;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(String tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public String getTglPanen() {
        return tglPanen;
    }

    public void setTglPanen(String tglPanen) {
        this.tglPanen = tglPanen;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
