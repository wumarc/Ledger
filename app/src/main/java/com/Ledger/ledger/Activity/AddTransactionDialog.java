package com.Ledger.ledger.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SwitchCompat;
import com.Ledger.ledger.Class.Transaction;
import com.Ledger.ledger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.*;

public class AddTransactionDialog extends AppCompatDialogFragment {

    private static FirebaseDatabase databaseRoot = FirebaseDatabase.getInstance();
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userDbRef = databaseRoot.getReference().child(currentUser.getUid());

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater(); // Get the layout inflater
        View view = inflater.inflate(R.layout.activity_add_transaction_dialog, null);
        EditText amount = view.findViewById(R.id.amount);
        EditText description = view.findViewById(R.id.description);
        SwitchCompat switchBtn = view.findViewById(R.id.switch1);
        Spinner itemSpinner = view.findViewById(R.id.spinner);
        List<String> items = new ArrayList<String>();
        List<String> keys = new ArrayList<String>();

        // Fill spinner with items
        userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapShot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) itemSnapShot.getValue(); //map each variable of the item
                    items.add(map.get("name").toString());
                    keys.add(itemSnapShot.getKey());
                }

                // Add data to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemSpinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        builder.setView(view)
                .setMessage("Enter the transaction details")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O) // TODO ?
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get transaction data
                        String item = itemSpinner.getSelectedItem().toString();
                        String key = keys.get(items.indexOf(item));
                        String descriptionStr = description.getText().toString();
                        double amountDle = Double.parseDouble(amount.getText().toString());
                        boolean debit = !switchBtn.isChecked();
                        Transaction transaction = new Transaction(amountDle, debit, descriptionStr, System.currentTimeMillis());

                        userDbRef.child(key).child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) { //rewrite
                                    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                                    for (DataSnapshot transactionSnapshot : snapshot.getChildren()) { // get the old list
                                        Transaction singleTransaction = transactionSnapshot.getValue(Transaction.class);
                                        transactions.add(singleTransaction);
                                    }
                                    transactions.add(transaction);
                                    userDbRef.child(key).child("transactions").setValue(transactions);
                                } else { // add a new list
                                    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                                    transactions.add(transaction);
                                    userDbRef.child(key).child("transactions").setValue(transactions);
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