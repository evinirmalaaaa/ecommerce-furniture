
package menu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
     @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Method untuk menangani tombol simpan
    @FXML
    private void handleRegisterButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Memanggil method untuk menyimpan data ke database
        saveToDatabase(username, password);
    }

    // Method untuk menyimpan data ke database
    private void saveToDatabase(String username, String password) {
        // Gantilah dengan informasi koneksi database Anda
        String jdbcUrl = "jdbc:mysql://localhost:3306/furniture";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String sql = "INSERT INTO users (username, password, level) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, "user");
                
                // Eksekusi pernyataan SQL
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                // Tampilkan pop-up berhasil
                showAlert(AlertType.INFORMATION, "Berhasil", "Data berhasil disimpan ke database.");
                } else {
                    // Tampilkan pop-up gagal
                    showAlert(AlertType.ERROR, "Gagal", "Gagal menyimpan data ke database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
}
