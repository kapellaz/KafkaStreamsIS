package org.example.Serializer;


public class Sale {
    private int ID;
    private String type;
    private double pricePerPair;
    private int quantity;
    private String supplierIdentifier;

    //empty constructor
    public Sale() {
    }
    public Sale(int ID, String type, double pricePerPair, int quantity, String supplierIdentifier) {
        this.ID = ID;
        this.type = type;
        this.pricePerPair = pricePerPair;
        this.quantity = quantity;
        this.supplierIdentifier = supplierIdentifier;
    }
    //getters
    public String getType() {
        return type;
    }

    public double getPricePerPair() {
        return pricePerPair;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplierIdentifier() {
        return supplierIdentifier;
    }

    public Integer getId() {
        return ID;
    }
    //setters
    public void setId(int ID) {
        this.ID = ID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPricePerPair(double pricePerPair) {
        this.pricePerPair = pricePerPair;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSupplierIdentifier(String supplierIdentifier) {
        this.supplierIdentifier = supplierIdentifier;
    }

    //toString
    @Override
    public String toString() {
        return "Sale{" +
                "ID=" + ID +
                ", " +
                "type='" + type + '\'' +
                ", pricePerPair=" + pricePerPair +
                ", quantity=" + quantity +
                ", supplierIdentifier='" + supplierIdentifier + '\'' +
                '}';
    }

}