package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import retrofit2.*;

import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Retrofit call must be inside a method, such as onCreate()
        Call<List<User>> call = RetrofitClient.getInstance().getApi().getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    // Do something with the users
                } else {
                    // Handle response error
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
