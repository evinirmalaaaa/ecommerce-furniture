package menu;

public class Pesanan {
    private int idTransaksi;
    private String userName;
    private String tanggal;
    private double totalAmount;
    private String status;

    public Pesanan(int idTransaksi, String userName, String tanggal, double totalAmount, String status) {
        this.idTransaksi = idTransaksi;
        this.userName = userName;
        this.tanggal = tanggal;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
