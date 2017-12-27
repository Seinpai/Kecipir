package com.kecipir.kecipir.data;

public class ShoppingCart {

    private String id_wishlist;
    private String id_barang;
    private String tgl_panen;
    private String nama_petani;
    private String quantity;
    private String foto;
    private String grade;
    private String satuan;
    private String nama_barang;
    private String harga_jual;
    private String subtotal;
    private String harga_jualrp;
    private String jumlah_harga;
    private String subtotalrp;

    private String kc_harga_asli;
    private String kc_harga_promo;
    private  String kc_jumlah_harga;

    public ShoppingCart(){

    }

    public ShoppingCart(String id_wishlist, String id_barang,String  tgl_panen, String nama_petani,
                        String  quantity,String  foto, String grade, String satuan, String nama_barang,
                        String harga_jual,String subtotal, String harga_jualrp, String jumlah_harga,
                        String subtotalrp, String harga_asli, String harga_promo, String kc_jumlah_harga)
    {
        this.id_barang = id_barang;
        this.tgl_panen = tgl_panen;
        this.nama_petani = nama_petani;
        this.quantity = quantity;
        this.foto = foto;
        this.grade = grade;
        this.satuan = satuan;
        this.nama_barang = nama_barang;
        this.harga_jual = harga_jual;
        this.subtotal = subtotal;
        this.harga_jualrp =harga_jualrp;
        this.jumlah_harga = jumlah_harga;
        this.subtotalrp = subtotalrp;
        this.id_wishlist = id_wishlist;
        this.kc_harga_asli = harga_asli;
        this.kc_harga_promo = harga_promo;
        this.kc_jumlah_harga = kc_jumlah_harga;
    }

    public String getId_wishlist() {
        return id_wishlist;
    }

    public void setId_wishlist(String id_wishlist) {
        this.id_wishlist = id_wishlist;
    }

    public  String getKc_jumlah_harga(){ return  kc_jumlah_harga; };

    public void  setKc_jumlah_harga(String kc_jumlah_harga) { this.kc_jumlah_harga = kc_jumlah_harga; };

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public String getTgl_panen() {
        return tgl_panen;
    }

    public void setTgl_panen(String tgl_panen) {
        this.tgl_panen = tgl_panen;
    }

    public String getNama_petani() {
        return nama_petani;
    }

    public void setNama_petani(String nama_petani) {
        this.nama_petani = nama_petani;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getHarga_jualrp() {
        return harga_jualrp;
    }

    public void setHarga_jualrp(String harga_jualrp) {
        this.harga_jualrp = harga_jualrp;
    }

    public String getSubtotalrp() {
        return subtotalrp;
    }

    public void setSubtotalrp(String subtotalrp) {
        this.subtotalrp = subtotalrp;
    }

    public String getHarga_jual() {
        return harga_jual;
    }

    public void setHarga_jual(String harga_jual) {
        this.harga_jual = harga_jual;
    }


    public String getKc_harga_asli() {
        return kc_harga_asli;
    }

    public void setKc_harga_asli(String kc_harga_asli) {
        this.kc_harga_asli = kc_harga_asli;
    }

    public String getKc_harga_promo() {
        return kc_harga_promo;
    }

    public void setKc_harga_promo(String kc_harga_promo) {
        this.kc_harga_promo = kc_harga_promo;
    }

    public String getJumlah_harga() {
        return jumlah_harga;
    }

    public void setJumlah_harga(String jumlah_harga) {
        this.jumlah_harga = jumlah_harga;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
