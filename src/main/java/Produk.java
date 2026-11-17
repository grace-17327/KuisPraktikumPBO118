
import java.io.Serializable;

public abstract class Produk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idProduk;
    private String nama;
    private int stok;

    public Produk(String idProduk, String nama, int stok) {
        this.idProduk = idProduk;
        this.nama = nama;
        setStok(stok);
    }

    public String getIdProduk() {
        return idProduk;
    }

    public String getNama() {
        return nama;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            throw new IllegalArgumentException("Stok tidak boleh negatif!");
        }
    }

    public abstract void tampilkanInfo();
}
