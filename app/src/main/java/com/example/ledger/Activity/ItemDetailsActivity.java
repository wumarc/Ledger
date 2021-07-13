package com.example.ledger.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ledger.Adapter.TransactionAdapter;
import com.example.ledger.Class.Item;
import com.example.ledger.Class.Transaction;
import com.example.ledger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;
import android.widget.TextView;
import java.util.ArrayList;

public class ItemDetailsActivity extends AppCompatActivity {

    TextView name, total;
    RecyclerView transactionsListRecycler;
    TransactionAdapter transactionAdapter;
    DatabaseReference itemsDbRef = FirebaseDatabase.getInstance().getReference("items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        String itemName = getIntent().getStringExtra("ITEM NAME");
        name = findViewById(R.id.item_name);
        total = findViewById(R.id.item_total);
        transactionsListRecycler = findViewById(R.id.edit_item_recycler_view);

        // Recycler View / Adapter set up
        ArrayList<Transaction> transactionsList =  new ArrayList<Transaction>();
        transactionAdapter = new TransactionAdapter(transactionsList, this);

        transactionsListRecycler.setHasFixedSize(true);
        transactionsListRecycler.setLayoutManager(new LinearLayoutManager(this));
        transactionsListRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // Item divider
        transactionsListRecycler.setAdapter(transactionAdapter);

        // Load the data
        itemsDbRef.child(itemName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsList.clear();
                // Get the item data
                Item item = snapshot.getValue(Item.class);
                name.setText(item.getName());
                String totalStr = "Balance: $" + String.valueOf(item.getSum());
                total.setText(totalStr);
                for (Transaction transaction : item.getTransactions()) {
                    transactionsList.add(transaction);
                }
                transactionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });




    }

}