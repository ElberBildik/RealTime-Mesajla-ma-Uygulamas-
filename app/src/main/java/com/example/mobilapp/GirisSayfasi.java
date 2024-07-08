package com.example.mobilapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GirisSayfasi extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> resimUrlListesi;
    private ImageAdapter imageAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_sayfasi);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        resimUrlListesi = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, resimUrlListesi);
        recyclerView.setAdapter(imageAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserID = currentUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("kullanıcılar").child(currentUserID).child("resimler");

        fetchResimUrlListesi();
    }

    private void fetchResimUrlListesi() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resimUrlListesi.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String resimUrl = postSnapshot.getValue(String.class);
                    resimUrlListesi.add(resimUrl);
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GirisSayfasi.this, "Resim URL'leri alınamadı: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void paylasim(View view) {
        Intent intent = new Intent(GirisSayfasi.this, PaylasimYap.class);
        startActivity(intent);
    }

    public void cikisYap(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(GirisSayfasi.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
