package com.example.ledger.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SwitchCompat;

import com.example.ledger.Class.Item;
import com.example.ledger.Class.Transaction;
import com.example.ledger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTransactionDialog extends AppCompatDialogFragment {

    private static FirebaseDatabase databaseRoot = FirebaseDatabase.getInstance();
    private static DatabaseReference itemsDbRef = databaseRoot.getReference().child("items");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater(); // Get the layout inflater
        View view = inflater.inflate(R.layout.activity_add_transaction_dialog, null);
        EditText amount = view.findViewById(R.id.amount);
        SwitchCompat switchBtn = view.findViewById(R.id.switch1);
        Spinner itemSpinner = view.findViewById(R.id.spinner);

        // Fill spinner with items
        itemsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> items = new ArrayList<String>();
                for(DataSnapshot itemSnapShot : snapshot.getChildren()) { // Get keys to display in the spinner
                  String itemName = itemSnapShot.getKey();
                  items.add(itemName);
                }

                // Add data to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemSpinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        builder.setView(view)
                .setMessage("Enter the details")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O) // TODO ?
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = itemSpinner.getSelectedItem().toString();

                        // Get transaction data
                        double amountDle = Double.parseDouble(amount.getText().toString());
                        boolean debit = switchBtn.isChecked();
                        long time = SystemClock.uptimeMillis();

                        Transaction transaction = new Transaction(amountDle, debit, time);

                        itemsDbRef.child(item).child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) { //rewrite
                                    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                                    for (DataSnapshot transactionSnapshot : snapshot.getChildren()) { // get the old list
                                        Transaction singleTransaction = transactionSnapshot.getValue(Transaction.class);
                                        transactions.add(singleTransaction);
                                    }
                                    transactions.add(transaction);
                                    itemsDbRef.child(item).child("transactions").setValue(transactions);
                                } else { // add a new list
                                    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                                    transactions.add(transaction);
                                    itemsDbRef.child(item).child("transactions").setValue(transactions);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return builder.create();
    }

}