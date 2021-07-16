package com.example.ledger.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ledger.Class.Item;
import com.example.ledger.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    // Create variables
    ArrayList<Item> itemsList;
    private Context context;
    OnItemClickListener mListener;

    public ItemAdapter (ArrayList<Item> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_single_item_display, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.name.setText(item.getName());
        double number = item.getSum();
        if (item.getSum() >= 0) {
            String total = "$" + String.valueOf(item.getSum());
            holder.sum.setText(total);
        } else {
            String total = "-$" + String.valueOf(item.getSum()).replace("-", "");
            holder.sum.setText(total);
        }

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, sum;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            name = view.findViewById(R.id.adapter_name);
            sum = view.findViewById(R.id.adapter_total);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }


}
