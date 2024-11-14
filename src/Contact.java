public class Contact {
    private String nama;
    private String telepon;
    private String kategori;

    public Contact(String nama, String telepon, String kategori) {
        this.nama = nama;
        this.telepon = telepon;
        this.kategori = kategori;
    }

    public String getNama() {
        return nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getKategori() {
        return kategori;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
