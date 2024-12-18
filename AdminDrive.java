import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminDrive extends Driver {
    private Admin admin;
    private ListBarang listBarang;
    private List<Transaksi> listTransaksi;
    private Invoice invoice;

    public AdminDrive(Admin admin) {
        this.admin = admin;
        this.listBarang = new ListBarang(); // Inisialisasi ListBarang
        this.listTransaksi = new ArrayList<>();
    }

    @Override
    public Admin login(String username, String password, List<Akun> accounts) {
        // Mengecek apakah username dan password cocok dengan data admin yang ada
        for (Akun akun : accounts) {
            if (akun instanceof Admin && akun.getUsername().equals(username) && akun.getPassword().equals(password)) {
                return (Admin) akun;
            }
        }
        return null; // Jika login gagal
    }

    @Override
    public void start() {
        // Memulai antarmuka utama untuk admin
        Scanner scanner = new Scanner(System.in);
        System.out.println("==========================");
        System.out.println("Anda login sebagaim admin.");
        System.out.println("==========================\n");
        while (true) {
            System.out.println("------------------");
            System.out.println("    Menu Admin");
            System.out.println("------------------");
            System.out.println("1. Tambah Barang");
            System.out.println("2. Edit Barang");
            System.out.println("3. Hapus Barang");
            System.out.println("4. Lihat Barang");
            System.out.println("5. Lihat Transaksi");
            System.out.println("6. Logout");
            System.out.println("------------------");
            System.out.print("Pilih: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer input

            switch (choice) {
                case 1 -> { // Menambahkan barang baru
                    System.out.print("Masukkan ID Barang: ");
                    String id = scanner.nextLine();
                    System.out.print("Masukkan Nama Barang: ");
                    String name = scanner.nextLine();
                    System.out.print("Masukkan Harga Barang: ");
                    double price = scanner.nextDouble();
                    System.out.print("Masukkan Stok Barang: ");
                    int stock = scanner.nextInt();
                    listBarang.tambahBarang(new Barang(id, name, price, stock)); // Menambah barang ke daftar
                    Barang.simpanDataBarang(listBarang.barang); // Menyimpan data barang ke file
                }
                case 2 -> { // Mengedit data barang berdasarkan ID
                    listBarang.tampilkanSemuaBarang();
                    System.out.print("ID Barang yang ingin diedit: ");
                    String idBarang = scanner.nextLine();
                    Barang barang = listBarang.cariBarang(idBarang);
                    if (barang != null) {
                        System.out.print("Nama Baru: ");
                        String namaBaru = scanner.nextLine();
                        System.out.print("Harga Baru: ");
                        double hargaBaru = scanner.nextDouble();
                        System.out.print("Stok Baru: ");
                        int stokBaru = scanner.nextInt();
                        scanner.nextLine(); // Clear buffer
                        barang.setNamaBarang(namaBaru);
                        barang.setHargaBarang(hargaBaru);
                        barang.setStokBarang(stokBaru);
                        System.out.println("Barang berhasil diedit.");
                        Barang.simpanDataBarang(listBarang.barang);
                    }
                }
                case 3 -> { // Menghapus barang dari daftar
                    listBarang.tampilkanSemuaBarang();
                    System.out.print("ID Barang yang ingin dihapus: ");
                    String idBarang = scanner.nextLine();
                    listBarang.hapusBarang(idBarang);
                    Barang.simpanDataBarang(listBarang.barang); // Menyimpan data barang setelah penghapusan
                }
                case 4 -> { // Menampilkan semua barang yang tersedia
                    System.out.println("Daftar Barang:");
                    listBarang.tampilkanSemuaBarang();
                }
                case 5 -> { // Menampilkan riwayat transaksi
                    System.out.println("Riwayat Transaksi:");
                    invoice.bacaDariFile("invoices.txt");
                }
                case 6 -> { // Logout dari sistem
                    System.out.println("Logout berhasil.");
                    return;
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }
}
