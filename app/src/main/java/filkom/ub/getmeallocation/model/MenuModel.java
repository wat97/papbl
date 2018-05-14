package filkom.ub.getmeallocation.model;

import java.io.Serializable;

public class MenuModel implements Serializable{

    private String namaMenu;
    private String harga;
    private String date;
    private String imageUrl;
    private String uidUser;
    private String namaRestoran;

    public MenuModel() {
    }

    public MenuModel(String uidUser, String namaMenu, String harga, String date, String imageUrl, String namaRestoran) {
        this.uidUser = uidUser;
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.date = date;
        this.imageUrl = imageUrl;
        this.namaRestoran = namaRestoran;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
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

    public String getNamaRestoran() {
        return namaRestoran;
    }

    public void setNamaRestoran(String namaRestoran) {
        this.namaRestoran = namaRestoran;
    }
}
