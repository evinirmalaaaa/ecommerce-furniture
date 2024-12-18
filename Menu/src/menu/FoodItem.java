package menu;

import javafx.beans.property.SimpleStringProperty;

public class FoodItem {
    private final SimpleStringProperty foodName;
    private final SimpleStringProperty harga;
    private final SimpleStringProperty detail;
    private final SimpleStringProperty stok;
    private int userId;
    private int productId;
    private int quantity;

    // Constructor
    public FoodItem(int userId, int productId, int quantity, String foodName, String harga, String detail, String stok) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.foodName = new SimpleStringProperty(foodName);
        this.harga = new SimpleStringProperty(harga);
        this.detail = new SimpleStringProperty(detail);
        this.stok = new SimpleStringProperty(stok);
    }

    // Getters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
    

    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    
    

    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    
    
    
    public String getFoodName() {
        return foodName.get();
    }
    
     public void setFoodName(String foodName) {
        this.foodName.set(foodName);
    }
    
    
    
    

    public String getHarga() {
        return harga.get();
    }
    
     public void setHarga(String harga) {
    this.harga.set(harga);
}
    
    
    
    
    
   
    public String getDetail() {
        return detail.get();
    }
    
    
    public void setDetail(String detail) {
        this.detail.set(detail);
    }


    
    
    
    public String getStok() {
        return stok.get();
    }

    public void setStok(String stok) {
        this.stok.set(stok);
    }
}