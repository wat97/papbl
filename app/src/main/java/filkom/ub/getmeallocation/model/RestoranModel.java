package filkom.ub.getmeallocation.model;

import java.io.Serializable;

public class RestoranModel implements Serializable{

    private MenuModel menu;
    private String namaRestoran;
    private String lat;
    private String lng;

    public RestoranModel() {
    }

    public RestoranModel(String namaRestoran, String lat, String lng) {
        this.namaRestoran = namaRestoran;
        this.lat = lat;
        this.lng = lng;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
