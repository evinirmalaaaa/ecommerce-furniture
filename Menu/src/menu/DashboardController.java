
package menu;
import menu.FoodItem;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.DriverManager;
import	java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {
    private Stage stage;
    private Scene scene;
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

    @FXML
    private TextField newFoodNameField;
    @FXML
    private TextField newHargaField;
    @FXML
    private TextField newDetailField;
     @FXML
    private TextField newStokField;
    @FXML
    private Button addButton;

    private FoodItem selectedFoodItem;
    private boolean editMode = false;

    public void initialize() {
       
        displayFoodMenu();

        addButton.setOnAction(event -> addNewFoodItem());

        actionColumn.setCellFactory((TableColumn<FoodItem, Void> param) -> new TableCell<FoodItem, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    
                    FoodItem data = getTableView().getItems().get(getIndex());
                    editMode = true;
                    addButton.setText("Edit");
                    
                    // (Opsional) Clear input fields
                    newFoodNameField.clear();
                    newHargaField.clear();
                    newDetailField.clear();

                    // Isi data ke dalam TextField
                    newFoodNameField.setText(data.getFoodName());
                    newHargaField.setText(data.getHarga());
                    newDetailField.setText(data.getDetail());
                    newStokField.setText(data.getStok());
                    selectedFoodItem = data;
                    
                });

                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    FoodItem data = getTableView().getItems().get(getIndex());
                    deleteFoodItem(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void deleteFoodItem(FoodItem foodItem) {
        if (!editMode) {
            // Ambil data yang ingin dihapus
            String foodName = foodItem.getFoodName();

            // Konfirmasi pengguna untuk menghapus
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi");
            alert.setHeaderText("Hapus Furniture");
            alert.setContentText("Anda yakin ingin menghapus Furniture '" + foodName + "'?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Hapus data dari database
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/furniture", "root", "");
                     PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM produk WHERE nama = ?")) {

                    preparedStatement.setString(1, foodName);
                    preparedStatement.executeUpdate();

                    // Hapus data dari TableView
                    tableView.getItems().remove(foodItem);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

  private void updateFoodItem(FoodItem foodItem, String newFoodName, String newHarga, String newDetail, String newStok) {
    // Implementasi logika untuk memperbarui item
    String oldFoodName = foodItem.getFoodName();

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/furniture", "root", "");
         PreparedStatement preparedStatement = connection.prepareStatement("UPDATE produk SET nama=?, harga=?, detail=?, stok=? WHERE nama=?")) {

        preparedStatement.setString(1, newFoodName);
        preparedStatement.setString(2, newHarga);
        preparedStatement.setString(3, newDetail);
        preparedStatement.setString(4, newStok);
        preparedStatement.setString(5, oldFoodName);
        preparedStatement.executeUpdate();

        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data Di Perbarui");
        // Perbarui data di dalam TableView
        Platform.runLater(() -> {
            foodItem.setFoodName(newFoodName);
            foodItem.setHarga(newHarga);
            foodItem.setDetail(newDetail);
            foodItem.setStok(newStok);

            // (Opsional) Clear input fields
            newFoodNameField.clear();
            newHargaField.clear();
            newDetailField.clear();
            newStokField.clear();

            // Keluar dari mode edit
            editMode = false;
            addButton.setText("Tambah");

            // Manual refresh TableView
            refreshTableView();
        });

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void refreshTableView() {
    // Get the current items from the TableView
    ObservableList<FoodItem> items = FXCollections.observableArrayList(tableView.getItems());

    // Clear and re-add the items to the TableView
    tableView.getItems().clear();
    tableView.setItems(items);
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

            // Tambahkan nilai default untuk userId, productId, dan quantity
            foodItems.add(new FoodItem(0, 0, 0, foodName, harga, detail, stok));
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


    private void addNewFoodItem() {
        String newFoodName = newFoodNameField.getText();
    String newHarga = newHargaField.getText();
    String newDetail = newDetailField.getText();
    String newStok = newStokField.getText();

    if (!newFoodName.isEmpty() && !newHarga.isEmpty() && !newDetail.isEmpty() && !newStok.isEmpty()) {
        if (editMode) {
            // Jika sedang dalam mode edit, panggil metode updateFoodItem
            updateFoodItem(selectedFoodItem, newFoodName, newHarga, newDetail, newStok);
        } else {
            // Jika tidak, tambahkan data baru ke dalam database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/furniture", "root", "");
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO produk (nama, harga, detail, stok) VALUES (?, ?, ?, ?)")) {

                preparedStatement.setString(1, newFoodName);
                preparedStatement.setString(2, newHarga);
                preparedStatement.setString(3, newDetail);
                preparedStatement.setString(4, newStok);
                preparedStatement.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data Di Tambahkan");

                // Menambahkan data baru ke dalam TableView
                FoodItem newFoodItem = new FoodItem(0, 0, 0, newFoodName, newHarga, newDetail, newStok);
                tableView.getItems().add(newFoodItem);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

            // Clear input fields
            newFoodNameField.clear();
            newHargaField.clear();
            newDetailField.clear();
            newStokField.clear();
        } else {
            // Tampilkan pesan error jika input kosong
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Nama, harga, detail, dan Stok furniture tidak boleh kosong!");
            alert.showAndWait();
        }
    }
    
     private void showAlert(Alert.AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
     
      @FXML
    private void handleCekPesanan(ActionEvent event) throws IOException {
        // Load halaman cekpesanan.fxml
        Parent root = FXMLLoader.load(getClass().getResource("CekPesanan.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
     
   
    
    
}