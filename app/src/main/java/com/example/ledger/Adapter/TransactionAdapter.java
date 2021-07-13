package com.example.ledger.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ledger.Class.Transaction;
import com.example.ledger.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    ArrayList<Transaction> transactionsList;
    private Context context;
    OnItemClickListener mListener;


    public TransactionAdapter(ArrayList<Transaction> transactions, Context context) {
        this.transactionsList = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_single_transaction, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionsList.get(position);
        if (transaction.isDebit()) {
            String text = "-$" + String.valueOf(transaction.getAmount());
            holder.amount.setText(text);
        } else {
            String text = " $" + String.valueOf(transaction.getAmount());
            holder.amount.setText(text);
        }

    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(TransactionAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description, amount;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);
            amount = view.findViewById(R.id.transaction_amount);

        }


    }

}
