package org.example.Serializer;


public class Sale {
    private String type;
    private double pricePerPair;
    private int quantity;
    private String supplierIdentifier;

    //empty constructor
    public Sale() {
    }
    public Sale(String type, double pricePerPair, int quantity, String supplierIdentifier) {
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

    //setters

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
                "type='" + type + '\'' +
                ", pricePerPair=" + pricePerPair +
                ", quantity=" + quantity +
                ", supplierIdentifier='" + supplierIdentifier + '\'' +
                '}';
    }
}