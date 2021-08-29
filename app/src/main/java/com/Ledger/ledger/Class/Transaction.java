package com.Ledger.ledger.Class;

public class Transaction {

    double amount;
    boolean debit;
    String description;
    long time;

    public Transaction() {}

    public Transaction(double amount, boolean debit, String description, long time) {
        this.amount = amount;
        this.debit = debit;
        this.time = time;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isDebit() {
        return debit;
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTransactionType(boolean transactionType) {
        this.debit = transactionType;
    }


}
