
package menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ProdukController {
     @FXML
    private TableView<FoodItem> tableView;
    @FXML
    private TableColumn<FoodItem, String> foodNameColumn;
    @FXML
    private TableColumn<FoodItem, String> hargaColumn;
    @FXML
    private TableColumn<FoodItem, String> detailColumn;
     @FXML
    private TableColumn<FoodItem, String> stokColumn;
    @FXML
    private TableColumn<FoodItem, Void> actionColumn;
    
    private int loggedInUserId; // Store the logged-in user's ID
 
     public ProdukController() {
        // Dapatkan ID pengguna yang sedang login
        this.loggedInUserId = LoginController.getLoggedInUserId();
    }
    
     public void initialize() {
        
        displayFoodMenu();
        
         actionColumn.setCellFactory((TableColumn<FoodItem, Void> param) -> new TableCell<FoodItem, Void>() {
           
        private final Button beliButton = new Button("Beli");
        private final Button cartButton = new Button("Cart");

        {
            beliButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            cartButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
            
            beliButton.setOnAction(event -> {
                
                FoodItem foodItem = getTableView().getItems().get(getIndex());
                // Memanggil metode untuk menangani pembelian
                handleBeliButton(foodItem);
            });
            
             cartButton.setOnAction(event -> {
            FoodItem foodItem = getTableView().getItems().get(getIndex());
            addToCart(foodItem);
        });
        }
        
        @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(beliButton, cartButton);
                    buttons.setSpacing(10); // Jarak antar tombol
                    setGraphic(buttons);
                }
            }
    });
         
        
     }
     
     
     
     
     
     
     public void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null); // Tidak ada header
    alert.setContentText(message);
    alert.showAndWait(); // Tampilkan dan tunggu hingga pengguna menutup dialog
}
     
     String jdbcUrl = "jdbc:mysql://localhost:3306/furniture";
        String dbUsername = "root";
        String dbPassword = "";
     
    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId; // Set the logged-in user ID
    }
    
        
    public void addToCart(FoodItem foodItem) {
        if (loggedInUserId == 0) {
            showAlert(Alert.AlertType.ERROR, "User Not Logged In", "You must be logged in to add items to the cart.");
            return;
        }
        
        try {
            // SQL query to add item to cart
            String sql = "INSERT INTO cart (user_id, product_id, quantity) " +
                         "VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE " +
                         "quantity = quantity + VALUES(quantity)";

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                
                // Set parameters for the query
                statement.setInt(1, loggedInUserId);   // Use logged-in user ID
                statement.setInt(2, foodItem.getProductId()); // Use product ID from the FoodItem
                statement.setInt(3, foodItem.getQuantity());  // Use quantity from the FoodItem

                // Execute the query
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item added to cart successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Failed", "Failed to add item to cart.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while interacting with the database: " + e.getMessage());
        }
    
}
    
    
    
    private void handleBeliButton(FoodItem foodItem) {
    Alert dialog = new Alert(Alert.AlertType.NONE);
    dialog.setTitle("Beli Barang");
    dialog.setHeaderText(null);
    dialog.setContentText("Masukkan jumlah yang akan Anda beli:");

    // Create UI components for input
    TextField quantityField = new TextField();
    Label itemNameLabel = new Label("Barang: " + foodItem.getFoodName());
    Label hargaLabel = new Label("Harga per item: " + foodItem.getHarga());

    // Create a grid for layout
    GridPane grid = new GridPane();
    grid.add(itemNameLabel, 0, 0);
    grid.add(hargaLabel, 0, 1);
    grid.add(new Label("Jumlah:"), 0, 2);
    grid.add(quantityField, 1, 2);

    // Set the dialog content to the grid
    dialog.getDialogPane().setContent(grid);

    // Add buttons to the dialog
    dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // Show the dialog and wait for user input
    dialog.showAndWait().ifPresent(buttonType -> {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            
            // Convert the stok value to int
            int currentStock = Integer.parseInt(foodItem.getStok());
            
            if (currentStock >= quantity) {
                int newStock = currentStock - quantity;
                foodItem.setStok(String.valueOf(newStock)); // Update stok

                // Update stok di database
                updateStockInDatabase(foodItem.getProductId(), newStock);

                // Add item to cart
                addToCart(foodItem);
                
                // Display total harga in a popup
                double hargaPerItem = Double.parseDouble(foodItem.getHarga());
                double totalHarga = quantity * hargaPerItem;
                Alert totalHargaAlert = new Alert(Alert.AlertType.INFORMATION);
                totalHargaAlert.setTitle("Total Harga");
                totalHargaAlert.setHeaderText(null);
                totalHargaAlert.setContentText("Anda berhasil membeli " + quantity + " " + foodItem.getFoodName() +
                                                "\nTotal Harga: " + totalHarga +
                                                "\nStok baru: " + foodItem.getStok());
                totalHargaAlert.showAndWait();
            } else {
                showAlert(Alert.AlertType.WARNING, "Stok Tidak Cukup", "Stok tidak mencukupi untuk pembelian ini.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Input Salah", "Masukkan jumlah yang valid.");
        }
    });

}
    private void updateStockInDatabase(int productId, int newStock) {
    String sql = "UPDATE produk SET stok = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/furniture", "root", "");
         PreparedStatement statement = connection.prepareStatement(sql)) {
        
        statement.setInt(1, newStock);
        statement.setInt(2, productId);
        statement.executeUpdate();
        
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Kesalahan", "Terjadi kesalahan saat memperbarui stok.");
    }
}
    
    private void displayFoodMenu() {
   
    ObservableList<FoodItem> foodItems = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/furniture", "root", "");
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM produk");
         ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
            String foodName = resultSet.getString("nama");
            String harga = resultSet.getString("harga");
            String detail = resultSet.getString("detail");
            String stok = resultSet.getString("stok");
            int productId = resultSet.getInt("id"); // Ambil id produk
            int userId = 1; // Gantilah ini dengan nilai yang sesuai (misalnya, ID pengguna yang sedang login)

            foodItems.add(new FoodItem(userId, productId, 1, foodName, harga, detail, stok)); // Default quantity = 1
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Set data ke dalam TableView
    foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("foodName"));
    hargaColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));
    detailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));
    stokColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));

    tableView.setItems(foodItems);
}
    
  @FXML
    private void backToBeranda() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardUser.fxml"));
        Parent berandaRoot = loader.load();
        Scene berandaScene = new Scene(berandaRoot);
        // Mendapatkan stage dari current scene
        Stage currentStage = (Stage) tableView.getScene().getWindow();
        // Set scene baru ke stage
        currentStage.setScene(berandaScene);
}
}
