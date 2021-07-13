package com.example.ledger.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ledger.Class.Item;
import com.example.ledger.Adapter.ItemAdapter;
import com.example.ledger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    FloatingActionButton addTransactionBtn;
    EditText amount;
    SwitchCompat typeBtn;
    FirebaseAuth mAuth;
    RecyclerView itemsListRecycler;
    FirebaseDatabase databaseRoot;
    DatabaseReference itemsDbRef;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        databaseRoot = FirebaseDatabase.getInstance();
        itemsDbRef = databaseRoot.getReference("items");
        amount = findViewById(R.id.amount);
        typeBtn = findViewById(R.id.switch1);
        addTransactionBtn = findViewById(R.id.fab);
        itemsListRecycler = findViewById(R.id.recyclerView);

        // Recycler View / Adapter set up
        ArrayList<Item> itemsList = new ArrayList<Item>();
        itemAdapter = new ItemAdapter(itemsList, this);

        itemsListRecycler.setHasFixedSize(true);
        itemsListRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemsListRecycler.setAdapter(itemAdapter);

        // Load the data
        itemsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //TODO get the newly added items instead of requesting the whole list again and again
                itemsList.clear();
                for (DataSnapshot itemSnapShot : snapshot.getChildren()) {
                    Item item = itemSnapShot.getValue(Item.class);
                    itemsList.add(item);
                }
                itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent item = new Intent(MenuActivity.this, ItemDetailsActivity.class);
                item.putExtra("ITEM NAME", itemsList.get(position).getName());
                startActivity(item);

            }
        });

        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionDialog showDialog = new AddTransactionDialog();
                showDialog.show(getSupportFragmentManager(), "AddNewItem");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override // Top right menu
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText input = new EditText(this);
        input.setHeight(130);
        input.setWidth(200);
        input.setGravity(Gravity.LEFT);
        if (R.id.newItem == item.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enter the new item name")
                    .setView(input)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemsDbRef.child(input.getText().toString()).setValue(new Item(input.getText().toString(), null));
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
            builder.show();
            return true;
        } else if (R.id.logout == item.getItemId()) {
            mAuth.signOut();
            finish();
            return true;
        }
        return true;
    }

    @Override // disable back button on going back to login page
    public void onBackPressed(){}

}