package com.example.ledger.Class;
import java.util.ArrayList;

public class Item {

    private String name;
    private ArrayList<Transaction> transactions;

    public Item() {};

    public Item(String name, ArrayList<Transaction> transaction) {
        this.name = name;
        this.transactions = transaction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(int index) {
        transactions.remove(index);
    }

    public double getSum() {
        double sum = 0;
        if (!(transactions == null)) {
            for (Transaction transaction : transactions) {
                if (transaction.isDebit()) {
                    sum -= transaction.getAmount();
                } else {
                    sum += transaction.getAmount();
                }
            }
        }
        return sum;
    }

}
