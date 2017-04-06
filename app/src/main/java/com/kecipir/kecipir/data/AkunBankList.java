package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 27/05/2016.
 */
public class AkunBankList {

    private String namaRekening;
    private String noRekening;
    private String namaBank;

    public AkunBankList(){

    }

    public AkunBankList(String namaRekening, String noRekening, String namaBank){
        this.namaRekening = namaRekening;
        this.namaBank = namaBank;
        this.noRekening = noRekening;
    }

    public String getNamaRekening() {
        return namaRekening;
    }

    public void setNamaRekening(String namaRekening) {
        this.namaRekening = namaRekening;
    }

    public String getNoRekening() {
        return noRekening;
    }

    public void setNoRekening(String noRekening) {
        this.noRekening = noRekening;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
    }
}
