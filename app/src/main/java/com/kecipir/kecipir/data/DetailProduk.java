package com.kecipir.kecipir.data;

/**
 * Created by Albani on 10/22/2015.
 */
public class DetailProduk {

    private String idBarangPanen;
    private String tglPanen;
    private String foto;
    private String namaBarang;
    private String namaPetani;
    private String grade;
    private String kategori;
    private String satuan;
    private String stock;
    private String hargaJual;
    private String ket;

    public DetailProduk(){

    }

    public DetailProduk(String idBarangPanen, String tglPanen, String foto, String namaBarang, String namaPetani, String grade, String kategori, String satuan, String stock, String hargaJual, String ket){
        this.idBarangPanen = idBarangPanen;
        this.tglPanen = tglPanen;
        this.foto = foto;
        this.namaBarang = namaBarang;
        this.namaPetani = namaPetani;
        this.grade = grade;
        this.kategori = kategori;
        this.satuan = satuan;
        this.stock  = stock;
        this.hargaJual = hargaJual;
        this.ket = ket;
    }

    public String getIdBarangPanen() {
        return idBarangPanen;
    }

    public void setIdBarangPanen(String idBarangPanen) {
        this.idBarangPanen = idBarangPanen;
    }

    public String getTglPanen() {
        return tglPanen;
    }

    public void setTglPanen(String tglPanen) {
        this.tglPanen = tglPanen;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getNamaPetani() {
        return namaPetani;
    }

    public void setNamaPetani(String namaPetani) {
        this.namaPetani = namaPetani;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(String hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }
}
