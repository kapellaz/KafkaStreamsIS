package org.example.Serializer;


public class Sale_Operation {
    private int ID;
    private String type;
    private String Operation;
    private int pricePerPair;
    private int quantity;
    private String supplierIdentifier;

    private int socktypeid;
    //empty constructor
    public Sale_Operation() {
    }


    public Sale_Operation(int ID, String type, int pricePerPair, int quantity, String supplierIdentifier, String Operation, int socktypeid) {
        this.ID = ID;
        this.type = type;
        this.pricePerPair = pricePerPair;
        this.quantity = quantity;
        this.supplierIdentifier = supplierIdentifier;
        this.Operation = Operation;
        this.socktypeid = socktypeid;
    }
    //getters
    public int getSocktypeid() {
        return socktypeid;
    }
    public String getOperation() {
        return Operation;
    }
    public String getType() {
        return type;
    }

    public int getPricePerPair() {
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
    public void setSocktypeid(int socktypeid) {
        this.socktypeid = socktypeid;
    }
    public void setOperation(String Operation) {
        this.Operation = Operation;
    }
    public void setId(int ID) {
        this.ID = ID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPricePerPair(int pricePerPair) {
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
                ", Operation='" + Operation + '\'' +
                ", " +
                "socktypeid='" + socktypeid + '\'' +
                ", " +
                "type='" + type + '\'' +
                ", pricePerPair=" + pricePerPair +
                ", quantity=" + quantity +
                ", supplierIdentifier='" + supplierIdentifier + '\'' +
                '}';
    }

}