package com.example.uollostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostItemActivity extends AppCompatActivity {

    private EditText etItemName, etLocation, etContact;
    private RadioGroup rgType;
    private Button btnPost;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etItemName = findViewById(R.id.etItemName);
        etLocation = findViewById(R.id.etLocation);
        etContact = findViewById(R.id.etContact);
        rgType = findViewById(R.id.rgType);
        btnPost = findViewById(R.id.btnPost);

        // Reference to the shared LostFound node
        ref = FirebaseDatabase.getInstance().getReference("LostFound");

        btnPost.setOnClickListener(v -> postItem());
    }

    private void postItem() {
        String name = etItemName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        // Step 3: refuse empty fields with setError()
        if (name.isEmpty()) {
            etItemName.setError("Item name is required");
            etItemName.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        if (contact.isEmpty()) {
            etContact.setError("Contact is required");
            etContact.requestFocus();
            return;
        }

        String type = (rgType.getCheckedRadioButtonId() == R.id.rbFound)
                ? "Found" : "Lost";

        // Step 4: push() makes a new unique key
        String key = ref.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Could not create a key",
                    Toast.LENGTH_LONG).show();
            return;
        }

        LostItem item = new LostItem(key, name, type, location,
                contact, "Open");

        btnPost.setEnabled(false);   // avoid double posting
        ref.child(key).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Item posted successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                btnPost.setEnabled(true);
                String msg = (task.getException() != null)
                        ? task.getException().getMessage()
                        : "Unknown error";
                Toast.makeText(this, "Failed: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
