package org.logic;

public class SplitTransaction extends Transaction {
    private double splitAmount;

    public SplitTransaction(String date, String postedDate, String description, double amount, String category, double splitAmount) {
        super(date, postedDate, description, amount, category, "Split");
        this.splitAmount = splitAmount;
    }

    // Copy constructor
    public SplitTransaction(SplitTransaction other) {
        super(other);
        this.splitAmount = other.splitAmount;
    }

    public double getSplitAmount() {
        return splitAmount;
    }

    public double getGeneralAmount() {
        return getAmount() - splitAmount;
    }
}