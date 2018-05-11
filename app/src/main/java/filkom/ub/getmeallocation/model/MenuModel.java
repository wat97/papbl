package filkom.ub.getmeallocation.model;

import java.io.Serializable;

public class MenuModel implements Serializable{

    private String namaMenu;
    private String harga;
    private String date;
    private String imageUrl;

    public MenuModel() {
    }

    public MenuModel(String namaMenu, String harga, String date, String imageUrl) {
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
