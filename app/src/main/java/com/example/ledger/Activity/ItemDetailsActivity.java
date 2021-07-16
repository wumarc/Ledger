package com.example.ledger.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ledger.Adapter.TransactionAdapter;
import com.example.ledger.Class.Item;
import com.example.ledger.Class.Transaction;
import com.example.ledger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {

    TextView name, total;
    RecyclerView transactionsListRecycler;
    TransactionAdapter transactionAdapter;
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
    ArrayList<Transaction> transactionsList =  new ArrayList<Transaction>();
    String keyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        keyName = getIntent().getStringExtra("KEY NAME");
        setContentView(R.layout.activity_item_details);
        name = findViewById(R.id.item_name);
        total = findViewById(R.id.item_total);
        transactionsListRecycler = findViewById(R.id.edit_item_recycler_view);

        // Item name listener
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = name.getText().toString();
                name.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                        if (keyevent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                            // update item name, get old item from firebase, then read the new one
                            userDbRef.child(keyName).child("name").setValue(newName);
                            closeKeyboard();
                            return true;
                        }
                        return false;
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load recycler view
        initRecyclerView();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(transactionsListRecycler);

        // Load the transactions
        userDbRef.child(keyName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsList.clear();
                if (snapshot.exists()) {
                    // Get the item data
                    Item item = snapshot.getValue(Item.class);
                    name.setText(item.getName());
                    if (item.getTransactions() != null) {
                        for (Transaction transaction : item.getTransactions()) { transactionsList.add(transaction); }
                        String totalStr;
                        if (item.getSum() >= 0) {
                            totalStr = "Balance: $" + String.valueOf(item.getSum());
                            total.setTextColor(Color.rgb(0, 102, 32));
                        } else {
                            totalStr = "Balance: -$" + String.valueOf(item.getSum()).replace("-", "");
                            total.setTextColor(Color.rgb(230, 0, 0));
                        }
                        total.setText(totalStr);
                    }
                    transactionAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void initRecyclerView() {
        transactionAdapter = new TransactionAdapter(transactionsList, this);
        transactionsListRecycler.setHasFixedSize(true);
        transactionsListRecycler.setLayoutManager(new LinearLayoutManager(this));
        transactionsListRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // Item divider
        transactionsListRecycler.setAdapter(transactionAdapter);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            transactionsList.remove(viewHolder.getAdapterPosition());
            userDbRef.child(keyName).child("transactions").setValue(transactionsList);
            transactionAdapter.notifyDataSetChanged();
        }
    };

}