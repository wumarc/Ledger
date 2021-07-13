package com.example.ledger.Class;


import java.util.HashMap;
import java.util.Map;

public class Transaction {

    double amount;
    boolean debit;
    long time;

    public Transaction() {};

    public Transaction(double amount, boolean debit, long time) {
        this.amount = amount;
        this.debit = debit;
        this.time = time;
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

    public void setTransactionType(boolean transactionType) {
        this.debit = transactionType;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("debit", debit);
        result.put("time", time);

        return result;
    }
}
