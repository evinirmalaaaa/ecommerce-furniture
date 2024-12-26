
package menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.*;
import java.time.LocalDate;

public class RiwayatPembelianController {


    @FXML
    private TableView<Transaction> tableView;
    
    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;
     
    @FXML
    private TableColumn<Transaction, Double> totalAmountColumn;

    @FXML
    private TableColumn<Transaction, String> foodNameColumn;

    @FXML
    private TableColumn<Transaction, Double> hargaColumn;

    @FXML
    private TableColumn<Transaction, String> detailColumn;

    @FXML
    private TableColumn<Transaction, Integer> jumlahColumn;

    @FXML
    private Button backToBerandaButton;

    @FXML
    private Label label;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/furniture";  // Ganti dengan URL database Anda
    private static final String DB_USERNAME = "root";  // Ganti dengan username database Anda
    private static final String DB_PASSWORD = "";  // Ganti dengan password database Anda

    private Connection conn;
    private int loggedInUserId;
    // Method untuk memuat data transaksi
    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
private void loadTransactionHistory() {
   ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    // Query untuk mengambil data transaksi
    String query = "SELECT t.id AS transaction_id, t.total_amount " +
                   "FROM transactions t " +
                   "WHERE t.user_id = 6";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, loggedInUserId);  // Gantilah loggedInUserId dengan nilai yang sesuai
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int transactionId = rs.getInt("transaction_id");
            double totalAmount = rs.getDouble("total_amount");

            // Tambahkan data ke dalam ObservableList
            transactions.add(new Transaction(transactionId, totalAmount));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Set data ke table view
    tableView.setItems(transactions);
}

    // Method yang dipanggil ketika halaman dibuka
    public void initialize() {
        // Set column bindings
    transactionIdColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty().asObject());
    totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());

    // Ambil user ID yang sedang login dari LoginController
    loggedInUserId = LoginController.loggedInUserId; // Menggunakan ID yang login

    // Pastikan untuk menghubungkan ke database
    connectToDatabase();

    // Memuat transaksi
    loadTransactionHistory();

       
    }






    // Method untuk tombol kembali ke beranda
    @FXML
    private void backToBeranda() {
        // Implementasikan logika untuk kembali ke halaman beranda
        System.out.println("Kembali ke beranda");
    }
}
