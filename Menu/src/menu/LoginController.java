
package menu;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author ThinkPad
 */
public class LoginController implements Initializable {
    @FXML
    private TableView<FoodItem> tableView;
    
 @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code can go here
    }
    
     private ProdukController produkController;
     

    @FXML
    private void loginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            System.out.println("Login successful!");
            navigateToDashboard(username); // Mengirim username untuk menentukan level
        } else {
            System.out.println("Login failed!");
            // Tambahkan logika untuk menangani login gagal, misalnya, menampilkan pesan kesalahan
        }
    }
    
     public LoginController() {
        produkController = new ProdukController();
    }
     
     public void handleLogin() {
        // After successful login, retrieve the logged-in user's ID
        int loggedInUserId = getUserIdFromLogin(); // Replace this with your actual method to get user ID
        
        // Set the logged-in user ID in the ProdukController
        produkController.setLoggedInUserId(loggedInUserId);
    }
    
    private int getUserIdFromLogin() {
        // Retrieve user ID from login process (this could be from a database or session)
        return 1; // For example purposes, returning a static user ID
    }

    private boolean validateLogin(String username, String password) {
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true; // Login berhasil
                } else {
                    System.out.println("Login failed! Username or password incorrect.");
                    return false; // Login gagal
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Login failed! An error occurred: " + e.getMessage());
            return false;
        }
    }

    private void navigateToDashboard(String username) {
       try {
        // Baca level pengguna dan user_id dari database berdasarkan username
        String level = getUserLevel(username);
        int userId = getUserId(username);  // Ambil user_id berdasarkan username

        // Simpan user_id ke variabel global atau session
        setLoggedInUserId(userId);

        // Tentukan dashboard yang sesuai berdasarkan level pengguna
        String dashboardPath = (level.equalsIgnoreCase("admin")) ? "DashboardAdmin.fxml" : "DashboardUser.fxml";
        showAlert(Alert.AlertType.INFORMATION, "Berhasil Login", "Berhasil Login");

        // Buka dashboard dalam jendela baru
        Stage dashboardStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        dashboardStage.setTitle("Dashboard");
        dashboardStage.setScene(scene);
        dashboardStage.show();

        // Tutup jendela login setelah login sukses
        Stage loginStage = (Stage) usernameField.getScene().getWindow();
        loginStage.close();
    } catch (java.io.IOException e) {
        e.printStackTrace();
    }
    }
    
    private int getUserId(String username) {
    try (Connection connection = Koneksi.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
        preparedStatement.setString(1, username);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("id");  // Mengambil ID pengguna
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;  // Kembalikan -1 jika ID pengguna tidak ditemukan
}

// Simpan user_id ke dalam variabel global atau session
public static int loggedInUserId;

public static void setLoggedInUserId(int userId) {
    loggedInUserId = userId;
}

public static int getLoggedInUserId() {
    return loggedInUserId;
}

    private String getUserLevel(String username) {
        // Mendapatkan level pengguna dari database berdasarkan username
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT level FROM users WHERE username = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
        @FXML
    private void register() throws java.io.IOException {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
    Parent berandaRoot = loader.load();
    Scene berandaScene = new Scene(berandaRoot);

    // You can remove the following line since it's not using tableView anymore
    // Stage currentStage = (Stage) tableView.getScene().getWindow();

    // Create a new stage for the register scene
    Stage registerStage = new Stage();
    registerStage.setScene(berandaScene);

    // Show the new stage
    registerStage.show();
}
    
     private void showAlert(Alert.AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
 

}