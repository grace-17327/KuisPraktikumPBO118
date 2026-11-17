
public class ProdukMakanan extends Produk {

    public ProdukMakanan(String idProduk, String nama, int stok) {
        super(idProduk, nama, stok);
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("Kategori: Makanan");
        System.out.println("ID Produk: " + getIdProduk());
        System.out.println("Nama Produk: " + getNama());
        System.out.println("Stok: " + getStok());
    }
}
