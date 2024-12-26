package menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.scene.layout.HBox;

public class CekPesananController {

    @FXML
    private TableView<Pesanan> tablePesanan;

    @FXML
    private TableColumn<Pesanan, Integer> colIdTransaksi;

    @FXML
    private TableColumn<Pesanan, String> colUserId;

    @FXML
    private TableColumn<Pesanan, String> colTanggal;

    @FXML
    private TableColumn<Pesanan, Double> colTotalAmount;

    @FXML
    private TableColumn<Pesanan, String> colStatus;

    private ObservableList<Pesanan> pesananList;

    // Konfigurasi database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/furniture";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

@FXML
public void initialize() {
    pesananList = FXCollections.observableArrayList();
    loadPesananData();

    colIdTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
    colUserId.setCellValueFactory(new PropertyValueFactory<>("userName"));
    colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
    colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

    // Menampilkan Button di kolom Status
    colStatus.setCellFactory(column -> new TableCell<Pesanan, String>() {
        private final Button btnStatus = new Button("Tolak");
        private final Button btnTerima = new Button("Terima");

        {
            // Mengatur warna tombol
            btnStatus.setStyle("-fx-background-color: red; -fx-text-fill: white;");  // Tombol Tolak menjadi merah
            btnTerima.setStyle("-fx-background-color: green; -fx-text-fill: white;"); // Tombol Terima menjadi hijau

            // Action untuk tombol Update Status
            btnStatus.setOnAction(event -> {
                Pesanan pesanan = getTableView().getItems().get(getIndex());
                String currentStatus = pesanan.getStatus();
                String newStatus = "Menunggu";  // Ganti status sesuai pilihan Anda
                
                if ("Menunggu".equals(currentStatus)) {
                    newStatus = "Sukses"; // Contoh mengubah status dari Menunggu ke Sukses
                } else {
                    newStatus = "Tolak"; // Mengubah ke status "Tolak" jika sudah sukses
                }
                
                pesanan.setStatus(newStatus);
                updateStatusInDatabase(pesanan.getIdTransaksi(), newStatus);

                // Menghapus pesanan yang statusnya sudah diubah dari ObservableList
                pesananList.remove(pesanan); 
                
                // Refresh tabel
                tablePesanan.refresh();

                System.out.println("Status updated: " + newStatus);
            });

            // Action untuk tombol Terima
            btnTerima.setOnAction(event -> {
                Pesanan pesanan = getTableView().getItems().get(getIndex());
                pesanan.setStatus("Sukses");
                updateStatusInDatabase(pesanan.getIdTransaksi(), "Sukses");

                // Menghapus pesanan yang statusnya sudah diubah dari ObservableList
                pesananList.remove(pesanan);
                
                // Refresh tabel
                tablePesanan.refresh();

                System.out.println("Pesanan diterima (Sukses): " + pesanan.getIdTransaksi());
            });
        }

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);

            if (empty || status == null) {
                setGraphic(null);
            } else {
                // Menambahkan kedua tombol (Update Status dan Terima) ke dalam HBox
                HBox hbox = new HBox(5);  // Menambahkan jarak antara tombol
                hbox.getChildren().addAll(btnStatus, btnTerima);  // Menambahkan tombol
                setGraphic(hbox);  // Menampilkan HBox dalam TableCell
            }
        }
    });

    tablePesanan.setItems(pesananList);
}


    // Mengupdate status pesanan ke database
    private void updateStatusInDatabase(int idTransaksi, String status) {
        String query = "UPDATE transactions SET status = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, idTransaksi);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Memuat data pesanan dari database
    private void loadPesananData() {
        String query = "SELECT t.id, u.username, t.transaction_date, t.total_amount, t.status\n" +
                "FROM transactions t\n" +
                "JOIN users u ON t.user_id = u.id";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                pesananList.add(new Pesanan(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("transaction_date"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
