package menu;
import javafx.beans.property.*;
import java.sql.Date;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class Transaction {
    private IntegerProperty transactionId;
    private DoubleProperty totalAmount;
    private ObjectProperty<Date> date;
    
    @FXML
private TableColumn<Transaction, Date> dateColumn;

    public Transaction(int transactionId, double totalAmount) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
    }
    

    public IntegerProperty transactionIdProperty() {
        return transactionId;
    }

     public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

   
}
