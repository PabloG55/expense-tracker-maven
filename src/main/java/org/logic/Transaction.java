package org.logic;

public class Transaction {
    protected final String date;
    protected final String postedDate;
    protected final String description;
    protected double amount;
    protected final String category;
    protected String type;

    // Constructor
    public Transaction(String date, String postedDate, String description, double amount, String category, String type) {
        this.date = date;
        this.postedDate = postedDate;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = type;
    }

    // Copy constructor
    public Transaction(Transaction other) {
        this.date = other.date;
        this.postedDate = other.postedDate;
        this.description = other.description;
        this.amount = other.amount;
        this.category = other.category;
        this.type = other.type;
    }

    // toString method
    @Override
    public String toString() {
        return String.format("Date: %s, Posted: %s, Description: %s, Amount: %.2f, Category: %s",
                date, postedDate, description, amount, category);
    }

    // Getter and setter methods
    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}