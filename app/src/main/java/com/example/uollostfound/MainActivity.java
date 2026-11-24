package com.example.uollostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private TextView tvEmpty;
    private ExtendedFloatingActionButton fabPost;

    private final ArrayList<LostItem> itemList = new ArrayList<>();
    private LostItemAdapter adapter;

    private DatabaseReference ref;
    private ValueEventListener listener;   // kept so it can be removed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvItems = findViewById(R.id.rvItems);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabPost = findViewById(R.id.fabPost);

        ref = FirebaseDatabase.getInstance().getReference("LostFound");

        adapter = new LostItemAdapter(itemList,
                item -> markAsReturned(item));
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        fabPost.setOnClickListener(v ->
                startActivity(new Intent(this, PostItemActivity.class)));

        // Scenario 2, step 2: live read
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    LostItem item = child.getValue(LostItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                Collections.reverse(itemList);       // newest first
                adapter.notifyDataSetChanged();

                // Scenario 2, step 4: empty board message
                tvEmpty.setVisibility(snapshot.hasChildren()
                        ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Database error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };
        ref.addValueEventListener(listener);
    }

    // Scenario 2, step 3: write only the status child
    private void markAsReturned(LostItem item) {
        if ("Returned".equals(item.getStatus())) {
            Toast.makeText(this, "Already returned",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ref.child(item.getId()).child("status").setValue("Returned");
        Toast.makeText(this, "Marked as Returned",
                Toast.LENGTH_SHORT).show();
    }

    // Scenario 2, step 4: remove the listener
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
