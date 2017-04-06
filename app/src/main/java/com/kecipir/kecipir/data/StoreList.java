package com.kecipir.kecipir.data;

/**
 * Created by Albani on 10/20/2015.
 */
public class StoreList {

    private String id;
    private String nama;
    private String harga;
    private String tgl;
    private String image;
    private String stock;
    private String satuan;
    private String grade;
    private String petani;
    private boolean sale;

    public StoreList(){

    }

    public StoreList(String id, String nama, String harga, String tgl, String image, String stock, String satuan, String grade, String petani, boolean sale){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.tgl = tgl;
        this.image = image;
        this.stock = stock;
        this.satuan = satuan;
        this.grade = grade;
        this.petani = petani;
        this.sale = sale;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isSale() {
        return sale;
    }

    public void setSale(boolean sale) {
        this.sale = sale;
    }

    public String getPetani() {
        return petani;
    }

    public void setPetani(String petani) {
        this.petani = petani;
    }
}
