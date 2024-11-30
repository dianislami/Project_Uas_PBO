import java.io.*;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Akun> accounts = new ArrayList<>();
    private static List<Barang> barangList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static Akun akun; // Menyimpan akun yang sedang login
    private static DriverAkun driverAkun = new DriverAkun();
    private static String file = "barang_data.txt";

    public static void main(String[] args) {
        
        loadData(); // Memuat data dari file teks saat program dimulai

        while (true) {
            System.out.println("\nSelamat datang di Sistem Belanja Online!");
            System.out.println("1. Login");
            System.out.println("2. Buat Akun");
            System.out.println("3. Keluar");
            System.out.print("Pilih: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Bersihkan buffer

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    saveData(); // Menyimpan data sebelum keluar
                    System.out.println("Terima kasih telah menggunakan sistem ini.");
                    System.exit(0);
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void login() {
        System.out.print("\nMasukkan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();

        // Menggunakan Driver untuk memverifikasi login
        akun = driverAkun.login(username, password, accounts);

        if (akun != null) {
            System.out.println("Login berhasil!");
            if (akun instanceof Admin) {
                Admin admin = (Admin) akun; // Casting eksplisit
                new AdminDrive(admin).start(); // Menu Admin
            } else if (akun instanceof Customer) {
                Customer customer = (Customer) akun; 
                createCustomerFolder(customer);
                new CustomerDrive(customer, new ListBarang()).start();
            }
        } else {
            System.out.println("Login gagal! Periksa username dan password.");
        }
    }

    private static void createCustomerFolder(Customer customer) {
        // Membuat folder "customer" jika belum ada
        File customerFolder = new File("customer");
        if (!customerFolder.exists()) {
            customerFolder.mkdir();
        }

        // Membuat folder untuk customer yang login berdasarkan username atau ID
        File customerDir = new File(customerFolder, customer.getUsername());
        if (!customerDir.exists()) {
            customerDir.mkdir();  // Membuat folder khusus untuk customer ini
            System.out.println("Folder customer " + customer.getUsername() + " dibuat.");

            // Membuat file keranjang.txt dan transaksi.txt di dalam folder customer
            createCustomerFiles(customerDir);
        }
    }

    private static void createCustomerFiles(File customerDir) {
        try {
            // Membuat file keranjang.txt
            File keranjangFile = new File(customerDir, "keranjang.txt");
            if (!keranjangFile.exists()) {
                keranjangFile.createNewFile(); // Membuat file baru jika belum ada
                System.out.println("File keranjang.txt untuk customer berhasil dibuat.");
                System.out.println("Path file: " + keranjangFile.getAbsolutePath());
            }

            // Membuat file transaksi.txt
            File transaksiFile = new File(customerDir, "transaksi.txt");
            if (!transaksiFile.exists()) {
                transaksiFile.createNewFile(); // Membuat file baru jika belum ada
                System.out.println("File transaksi.txt untuk customer berhasil dibuat.");
            }

        } catch (IOException e) {
            System.out.println("Gagal membuat file untuk customer: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.print("\nMasukkan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();
        System.out.print("Pilih tipe akun (1. Admin, 2. Customer): ");
        int type = scanner.nextInt();
        scanner.nextLine(); // Bersihkan buffer
    
        Akun newAccount;
        if (type == 1) {
            newAccount = new Admin(String.valueOf(accounts.size() + 1), username, password);
        } else if (type == 2) {
            newAccount = new Customer(String.valueOf(accounts.size() + 1), username, password);
        } else {
            System.out.println("Pilihan tidak valid. Akun tidak dibuat.");
            return;
        }
        accounts.add(newAccount);
        saveData(); // Panggil saveData() setelah akun dibuat
        System.out.println("Akun berhasil dibuat! Silakan login.");
    }
    

    private static Barang findBarangById(String id) {
        for (Barang barang : barangList) {
            if (barang.getIdBarang().equals(id)) {
                return barang;
            }
        }
        return null;
    }

    // Memastikan bahwa barangList sudah terisi sebelum memanggil simpanKeFile
    private static void saveData() {
        try {
            // Simpan data Admin, Customer, dan Barang ke file terpisah
            try (BufferedWriter adminWriter = new BufferedWriter(new FileWriter("admin_data.txt"));
                 BufferedWriter customerWriter = new BufferedWriter(new FileWriter("customer_data.txt"))) {

                for (Akun account : accounts) {
                    // Simpan Admin ke file admin_data.txt
                    if (account instanceof Admin) {
                        Admin admin = (Admin) account;
                        adminWriter.write(admin.getId() + "," + admin.getUsername() + "," + admin.getPassword() + "\n");
                    } else if (account instanceof Customer) {
                        Customer customer = (Customer) account;
                        customerWriter.write(customer.getId() + "," + customer.getUsername() + "," + customer.getPassword() + "\n");
                    }
                }

            
            }

        } catch (IOException e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
        }
    }
    

    // Membaca data akun dan barang dari file teks terpisah
    private static void loadData() {
        try {
            // Membaca data Admin, Customer, dan Barang dari file terpisah
            BufferedReader adminReader = new BufferedReader(new FileReader("admin_data.txt"));
            BufferedReader customerReader = new BufferedReader(new FileReader("customer_data.txt"));
            String line;

            // Membaca data Admin
            while ((line = adminReader.readLine()) != null) {
                String[] data = line.split(",");
                accounts.add(new Admin(data[0], data[1], data[2]));
            }

            // Membaca data Customer
            while ((line = customerReader.readLine()) != null) {
                String[] data = line.split(",");
                accounts.add(new Customer(data[0], data[1], data[2]));
            }

        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan. Sistem akan membuat file baru saat menyimpan.");
        } catch (IOException e) {
            System.out.println("Gagal memuat data: " + e.getMessage());
        }
    }
}
