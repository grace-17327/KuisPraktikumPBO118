
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class SistemToko {

    private String namaToko;
    private HashMap<String, Pelanggan> daftarPelanggan;
    private HashMap<String, Produk> daftarProduk;
    private ArrayList<Transaksi> daftarTransaksi;

    public SistemToko(String namaToko) {
        this.namaToko = namaToko;
        daftarPelanggan = new HashMap<>();
        daftarProduk = new HashMap<>();
        daftarTransaksi = new ArrayList<>();
    }

    public void tambahPelanggan(String id, String nama) {
        Pelanggan pelanggan = new Pelanggan(id, nama);
        daftarPelanggan.put(id, pelanggan);
    }

    public void tambahProduk(String id, String nama, int stok) {
        Produk produk;

        switch (id) {
            case "OBJ-A":
                produk = new ProdukElektronik(id, nama, stok);
                break;
            case "OBJ-B":
                produk = new ProdukPakaian(id, nama, stok);
                break;
            case "OBJ-C":
                produk = new ProdukMakanan(id, nama, stok);
                break;
            default:
                produk = new ProdukMakanan(id, nama, stok);
                break;
        }

        daftarProduk.put(id, produk);
    }

    public void jalankanTransaksiDariFile(String namaFile)
            throws IOException {

        try (BufferedReader reader =
                new BufferedReader(new FileReader(namaFile))) {

            String baris;

            while ((baris = reader.readLine()) != null) {

                String[] dataTransaksi = baris.split("\\|");

                for (String data : dataTransaksi) {
                    if (data.trim().isEmpty()) {
                        continue;
                    }

                    try {
                        String[] bagian = data.split("/");

                        String idPelanggan =
                                bagian[0].substring(3);

                        String idProduk =
                                bagian[1].substring(3);

                        int jumlah = Integer.parseInt(
                                bagian[2].substring(4)
                        );

                        prosesTransaksi(
                                idPelanggan,
                                idProduk,
                                jumlah
                        );

                    } catch (TransaksiException e) {
                        System.out.println(
                                "[TRANSAKSI GAGAL] " + e.getMessage()
                        );
                    } catch (Exception e) {
                        System.out.println(
                                "[FORMAT SALAH] " + data
                        );
                    }
                }
            }
        }
    }

    private void prosesTransaksi(
            String idPelanggan,
            String idProduk,
            int jumlah
    ) throws TransaksiException {

        Pelanggan pelanggan =
                daftarPelanggan.get(idPelanggan);

        Produk produk =
                daftarProduk.get(idProduk);

        if (pelanggan == null) {
            throw new TransaksiException(
                    "Pelanggan " + idPelanggan + " tidak ditemukan."
            );
        }

        if (produk == null) {
            throw new TransaksiException(
                    "Produk " + idProduk + " tidak ditemukan."
            );
        }

        if (jumlah <= 0) {
            throw new TransaksiException(
                    "Jumlah pembelian harus lebih dari nol."
            );
        }

        if (produk.getStok() < jumlah) {
            throw new TransaksiException(
                    "Stok produk " + idProduk + " tidak mencukupi."
            );
        }

        produk.setStok(produk.getStok() - jumlah);

        Transaksi transaksi =
                new Transaksi(pelanggan, produk, jumlah);

        daftarTransaksi.add(transaksi);

        System.out.println("\nTransaksi berhasil!");
        transaksi.tampilkanInfo();
    }

    public void simpanLaporanAkhir() {

        try (ObjectOutputStream output =
                new ObjectOutputStream(
                        new FileOutputStream("snapshot.obj"))) {

            output.writeObject(daftarPelanggan);
            output.writeObject(daftarProduk);
            output.writeObject(daftarTransaksi);

            System.out.println(
                    "\nSnapshot berhasil disimpan."
            );

        } catch (IOException e) {
            System.out.println(
                    "Gagal menyimpan snapshot: " + e.getMessage()
            );
        }

        try (FileWriter writer =
                new FileWriter("laporan.txt")) {

            writer.write("=== LAPORAN " + namaToko + " ===\n");

            writer.write("\nDAFTAR ID PELANGGAN:\n");

            for (String id : new TreeSet<>(
                    daftarPelanggan.keySet())) {
                writer.write(id + "\n");
            }

            writer.write("\nDAFTAR ID PRODUK:\n");

            for (String id : new TreeSet<>(
                    daftarProduk.keySet())) {
                writer.write(id + "\n");
            }

            writer.write("\nTOTAL TRANSAKSI: "
                    + daftarTransaksi.size() + "\n");

            writer.write("\nRIWAYAT TRANSAKSI:\n");

            for (Transaksi transaksi : daftarTransaksi) {
                writer.write(
                        transaksi.getPelanggan().getIdPelanggan()
                        + " - "
                        + transaksi.getProduk().getIdProduk()
                        + " - "
                        + transaksi.getJumlah()
                        + "\n"
                );
            }

            System.out.println(
                    "Laporan berhasil disimpan."
            );

        } catch (IOException e) {
            System.out.println(
                    "Gagal menyimpan laporan: " + e.getMessage()
            );
        }
    }
}
