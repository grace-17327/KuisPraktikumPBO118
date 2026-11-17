

import java.io.*;
import java.util.*;

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
        daftarPelanggan.put(id, new Pelanggan(id, nama));
    }

    public void tambahProduk(String id, String nama, int stok) {
        switch (id) {
            case "OBJ-A":
                daftarProduk.put(id, new ProdukElektronik(id, nama, stok));
                break;
            case "OBJ-B":
                daftarProduk.put(id, new ProdukPakaian(id, nama, stok));
                break;
            case "OBJ-C":
                daftarProduk.put(id, new ProdukMakanan(id, nama, stok));
                break;
            default:
                System.out.println("Jenis produk tidak dikenal: " + id);
        }
    }

    public void jalankanTransaksiDariFile(String namaFile) throws Exception {

        try (BufferedReader reader = new BufferedReader(new FileReader(namaFile))) {

            String isiFile = reader.readLine();

            if (isiFile == null || isiFile.trim().isEmpty()) {
                throw new IOException("File transaksi kosong!");
            }

            String[] daftarData = isiFile.split("\\|");

            for (String data : daftarData) {
                try {
                    String[] bagian = data.split("/");

                    String idPelanggan = bagian[0].substring(3);
                    String idProduk = bagian[1].substring(3);
                    int jumlah = Integer.parseInt(bagian[2].substring(4));

                    prosesTransaksi(idPelanggan, idProduk, jumlah);

                } catch (TransaksiException e) {
                    System.out.println("[TRANSAKSI GAGAL] " + e.getMessage());

                } catch (Exception e) {
                    System.out.println("[DATA TIDAK VALID] " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new IOException("Gagal membaca file: " + e.getMessage());
        }
    }

    public void prosesTransaksi(String idPelanggan, String idProduk, int jumlah)
            throws TransaksiException {

        Pelanggan pelanggan = daftarPelanggan.get(idPelanggan);
        Produk produk = daftarProduk.get(idProduk);

        if (pelanggan == null) {
            throw new TransaksiException("Pelanggan " + idPelanggan + " tidak ditemukan!");
        }

        if (produk == null) {
            throw new TransaksiException("Produk " + idProduk + " tidak ditemukan!");
        }

        if (jumlah <= 0) {
            throw new TransaksiException("Jumlah pembelian harus lebih dari 0!");
        }

        if (produk.getStok() < jumlah) {
            throw new TransaksiException("Stok " + produk.getNama() + " tidak mencukupi!");
        }

        produk.setStok(produk.getStok() - jumlah);

        Transaksi transaksi = new Transaksi(pelanggan, produk, jumlah);
        daftarTransaksi.add(transaksi);

        System.out.println("[TRANSAKSI BERHASIL]");
        transaksi.tampilkanInfo();
    }

    public void simpanLaporanAkhir() {

        // SERIALISASI SNAPSHOT.OBJ
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("snapshot.obj"))) {

            output.writeObject(daftarPelanggan);
            output.writeObject(daftarProduk);
            output.writeObject(daftarTransaksi);

            System.out.println("Snapshot berhasil disimpan.");

        } catch (IOException e) {
            System.out.println("Gagal menyimpan snapshot: " + e.getMessage());
        }

        // LAPORAN KODE ID MENGGUNAKAN KEYSET
        try (FileWriter writer = new FileWriter("laporan.txt")) {

            TreeSet<String> semuaID = new TreeSet<>();

            semuaID.addAll(daftarPelanggan.keySet());
            semuaID.addAll(daftarProduk.keySet());

            for (String id : semuaID) {
                writer.write(id + "\n");
            }

            System.out.println("Laporan berhasil disimpan.");

        } catch (IOException e) {
            System.out.println("Gagal menyimpan laporan: " + e.getMessage());
        }
    }
}
