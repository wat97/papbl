package filkom.ub.getmeallocation.model;

public class MenuModel {

    private String namaMenu;
    private String harga;
    private String date;

    public MenuModel() {
    }

    public MenuModel(String namaMenu, String harga, String date) {
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.date = date;
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
