package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class CategoriesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText mCategoryName, mCategoryDailyBudget, mCategoryWeeklyBudget, mCategoryMonthlyBudget, mCategoryYearlyBudget, mCategoryNote;

    private LinearLayout mLinearDaily, mLinearWeekly, mLinearMonthly, mLinearYearly;
    private Button  mCategoryCancel, mCategorySave;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch budgetSwitch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        
        initializeViews();
        setupSwitch();
        setupButtons();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
          String category_name = String.valueOf(firebaseDatabase.getReference("Users").child(uid).child("UserCategories").child("name"));
            // Replace with the correct categoryId you are monitoring
            String categoryId = "category_id_placeholder";
            fetchCategoryId(uid, category_name);
        }
    }

    private void initializeViews() {
        mCategoryName = findViewById(R.id.category_name);
        mCategoryDailyBudget = findViewById(R.id.category_daily);
        mCategoryWeeklyBudget = findViewById(R.id.category_weekly);
        mCategoryMonthlyBudget = findViewById(R.id.category_monthly);
        mCategoryYearlyBudget = findViewById(R.id.category_yearly);
        mLinearDaily = findViewById(R.id.linear_daily);
        mLinearWeekly = findViewById(R.id.linear_weekly);
        mLinearMonthly = findViewById(R.id.linear_monthly);
        mLinearYearly = findViewById(R.id.linear_yearly);
        mCategoryNote = findViewById(R.id.category_note);
        mCategoryCancel = findViewById(R.id.btn_categorycancel);
        mCategorySave = findViewById(R.id.btn_categorysave);
        budgetSwitch = findViewById(R.id.category_switch);
    }

    private void setupButtons() {
        mCategorySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryData();
            }
        });

        mCategoryCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFieldsAndExit();
                CategoriesActivity.super.onBackPressed();
            }
        });
    }

    private void saveCategoryData() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();
        String name = mCategoryName.getText().toString().trim();
        String dailyBudget = mCategoryDailyBudget.getText().toString().trim();
        String weeklyBudget = mCategoryWeeklyBudget.getText().toString().trim();
        String monthlyBudget = mCategoryMonthlyBudget.getText().toString().trim();
        String yearlyBudget = mCategoryYearlyBudget.getText().toString().trim();
        String categoryNote = mCategoryNote.getText().toString().trim();

        if (name.isEmpty() || (budgetSwitch.isChecked() &&
                (dailyBudget.isEmpty() || weeklyBudget.isEmpty() || monthlyBudget.isEmpty() || yearlyBudget.isEmpty()))) {
            Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if category already exists
        firebaseDatabase.getReference("Users")
                .child(uid)
                .child("UserCategories")
                .orderByChild("name")
                .equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(CategoriesActivity.this, "Category already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Category doesn't exist, proceed to save
                            createNewCategory(uid, name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CategoriesActivity.this, "Error checking category: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewCategory(String uid, String name) {

        String dailyBudget = mCategoryDailyBudget.getText().toString().trim();
        String weeklyBudget = mCategoryWeeklyBudget.getText().toString().trim();
        String monthlyBudget = mCategoryMonthlyBudget.getText().toString().trim();
        String yearlyBudget = mCategoryYearlyBudget.getText().toString().trim();
        String categoryNote = mCategoryNote.getText().toString().trim();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference categoriesRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");

// Generate a unique key for the category
        String categoryId = categoriesRef.push().getKey();

        // Prepare the category data
        Map<String, Object> category = new HashMap<>();
        category.put("categoryId", categoryId);
        category.put("name", name);
        category.put("dailyBudget", dailyBudget);
        category.put("weeklyBudget", weeklyBudget);
        category.put("monthlyBudget", monthlyBudget);
        category.put("yearlyBudget", yearlyBudget);
        category.put("note", categoryNote);

        // Save the new category
        firebaseDatabase.getReference("Users")
                .child(uid)
                .child("UserCategories")
                .push()
                .setValue(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CategoriesActivity.this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                    clearFieldsAndExit();
                })
                .addOnFailureListener(e -> Toast.makeText(CategoriesActivity.this, "Error saving category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupSwitch() {
        budgetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                mLinearDaily.setVisibility(visibility);
                mLinearWeekly.setVisibility(visibility);
                mLinearMonthly.setVisibility(visibility);
                mLinearYearly.setVisibility(visibility);
            }
        });
        // Set the initial state of the switch and associated views
        budgetSwitch.setChecked(false);
        mLinearDaily.setVisibility(View.GONE);
        mLinearWeekly.setVisibility(View.GONE);
        mLinearMonthly.setVisibility(View.GONE);
        mLinearYearly.setVisibility(View.GONE);
    }



    //send notification
    private void sendNotification(String message) {
        String channelId = "budget_notifications";
        String channelName = "Budget Alerts";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0+ devices
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, CategoriesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.new_icon) // Replace with your app's notification icon
                .setContentTitle("Budget Exceeded")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }

    //monitor and compare budget
    private void monitorDailyBudget(String uid, String categoryId) {
        DatabaseReference categoryRef = firebaseDatabase.getReference("Users").child(uid).child("UserCategories").child(categoryId);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dailyBudget = snapshot.child("dailyBudget").getValue(String.class);
                    String dailyExpenses = snapshot.child("dailyExpenses").getValue(String.class);

                    if (dailyBudget != null && dailyExpenses != null) {
                        try {
                            double budget = Double.parseDouble(dailyBudget);
                            double expenses = Double.parseDouble(dailyExpenses);

                            if (expenses > budget) {
                                sendNotification("You have exceeded your daily budget of " + dailyBudget);
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(CategoriesActivity.this, "Invalid budget or expense data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoriesActivity.this, "Error monitoring budget: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fetch category id
    private void fetchCategoryId(String uid, String categoryName) {
        DatabaseReference categoriesRef = firebaseDatabase.getReference("Users").child(uid).child("UserCategories");

        categoriesRef.orderByChild("name").equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String categoryId = categorySnapshot.getKey(); // Get the category ID (Firebase unique key)
                        monitorDailyBudget(uid, categoryId); // Pass the category ID to monitor the budget
                        break; // Use the first matching category
                    }
                } else {
                    Toast.makeText(CategoriesActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoriesActivity.this, "Error fetching category ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void clearFieldsAndExit() {
        mCategoryName.setText("");
        mCategoryDailyBudget.setText("");
        mCategoryWeeklyBudget.setText("");
        mCategoryMonthlyBudget.setText("");
        mCategoryYearlyBudget.setText("");
        budgetSwitch.setChecked(false);
    }
}