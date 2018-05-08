package filkom.ub.getmeallocation.model;

import java.io.Serializable;

public class RestoranModel implements Serializable{

    private MenuModel menu;
    private String namaRestoran;
    private String lokasi;

    public RestoranModel() {
    }

    public RestoranModel(String namaRestoran, String lokasi) {
        this.namaRestoran = namaRestoran;
        this.lokasi = lokasi;
        //this.menu = menuModel;
    }

    public MenuModel getMenuModel() {
        return menu;
    }

    public void setMenuModel(MenuModel menuModel) {
        this.menu = menuModel;
    }

    public String getNamaRestoran() {
        return namaRestoran;
    }

    public void setNamaRestoran(String namaRestoran) {
        this.namaRestoran = namaRestoran;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
}
