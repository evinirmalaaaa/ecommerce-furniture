package menu;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CartController {
    // FXML Components
    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button checkoutButton;

    // Cart data
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    
     private int loggedInUserId = LoginController.getLoggedInUserId();

    // Database variables
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/furniture";
    private final String dbUsername = "root";
    private final String dbPassword = "";

    @FXML
    public void initialize() {
        // Set table columns
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Load cart data from database
        loadCartData();

        // Calculate total price
        updateTotalPrice();

        // Event handler for checkout button
        checkoutButton.setOnAction(event -> handleCheckout());
    }

    private void loadCartData() {
          cartItems.clear(); // Clear old data
    try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
        String sql = "SELECT p.nama AS nama_produk, c.quantity, p.harga " +
                     "FROM cart c " +
                     "JOIN produk p ON c.product_id = p.id " +
                     "WHERE c.user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, loggedInUserId);  // Set the logged-in user ID
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String productName = resultSet.getString("nama_produk");
            int quantity = resultSet.getInt("quantity");
            double price = resultSet.getDouble("harga");
            double totalPrice = price * quantity;

            cartItems.add(new CartItem(productName, quantity, price, totalPrice));
        }

        cartTable.setItems(cartItems); // Set the data in the table

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        totalPriceLabel.setText(String.format("Rp %.2f", total));
    }

    private void handleCheckout() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
        // Insert transaction data into database (simplified example)
        String insertTransactionSql = "INSERT INTO transactions (user_id, total_amount) VALUES (?, ?)";
        PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionSql, Statement.RETURN_GENERATED_KEYS);
        transactionStmt.setInt(1, loggedInUserId);
        transactionStmt.setDouble(2, getTotalPrice());  // Get the total price from cart items
        transactionStmt.executeUpdate();

        // Optionally, clear the cart for the user after checkout
        String deleteCartSql = "DELETE FROM cart WHERE user_id = ?";
        PreparedStatement deleteStmt = connection.prepareStatement(deleteCartSql);
        deleteStmt.setInt(1, loggedInUserId);
        deleteStmt.executeUpdate();

        // Show confirmation message
        showAlert("Checkout successful!", "Your order has been placed successfully.");

        // Reload the cart data (clear the table and reload)
        loadCartData();
        updateTotalPrice();

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
    
private double getTotalPrice() {
    double total = 0;
    for (CartItem item : cartItems) {
        total += item.getTotalPrice();
    }
    return total;
}

 @FXML
    private void backToBeranda() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardUser.fxml"));
        Parent berandaRoot = loader.load();
        Scene berandaScene = new Scene(berandaRoot);
        // Mendapatkan stage dari current scene
        Stage currentStage = (Stage) cartTable.getScene().getWindow();
        // Set scene baru ke stage
        currentStage.setScene(berandaScene);
}

private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
}

    // Inner class for cart item
    public static class CartItem {
        private final String productName;
        private final int quantity;
        private final double price;
        private final double totalPrice;

        public CartItem(String productName, int quantity, double price, double totalPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.totalPrice = totalPrice;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getTotalPrice() {
            return totalPrice;
        }
    }
}
