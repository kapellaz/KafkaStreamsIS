package org.example.Serializer;

public class AggregateSale {
    private double total;
    private long count;

    public AggregateSale(){
    }

    //constructor
    public AggregateSale(double total, long count) {
        this.total = total;
        this.count = count;
    }
    public void addAmount(double amount){
        this.total+=amount;
    }

    public void incrementCount(){
        count++;
    }

    public double Average(){
        return count == 0 ? 0 : total/count;
    }

    //getters
    public double getTotal() {
        return total;
    }

    public long getCount() {
        return count;
    }

    //setters

    public void setTotal(double total) {
        this.total = total;
    }

    public void setCount(long count) {
        this.count = count;
    }

    //toString
    @Override
    public String toString() {
        return "AggregateSale{" +
                "total=" + total +
                ", count=" + count +
                '}';
    }
}
