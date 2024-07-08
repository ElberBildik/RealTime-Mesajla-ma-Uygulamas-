package com.example.mobilapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.storage.UploadTask;

public class PaylasimYap extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView imageView;
    private EditText yorumText;
    private Uri imageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private void saveImageInfoToFirestore(String userId, String yorum, String downloadUrl) {
        // Firestore referansı
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Verileri içeren bir Map oluştur
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("yorum", yorum);
        imageData.put("imageUrl", downloadUrl);

        // Verileri Firestore'a kaydet
        db.collection("users")
                .document(userId)
                .collection("uploads")
                .add(imageData)
                .addOnSuccessListener(documentReference -> Log.d("Firebase", "Belge eklendi ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Firebase", "Belge eklenemedi", e));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasim_yap);

        imageView = findViewById(R.id.imageView);
        yorumText = findViewById(R.id.yorumText);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                }
        );
    }

    public void Submit(View view) {
        if (imageUri != null) {
            String yorum = yorumText.getText().toString();
            String userId = "userId123"; // Bu, oturum açmış kullanıcının ID'si olmalıdır
            uploadImageToFirebase(imageUri, yorum, userId);
        } else {
            Snackbar.make(view, "Lütfen bir resim seçin", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void resimSec(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeriye erişim izni gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("izin ver", v -> requestPermission())
                        .show();
            } else {
                requestPermission();
            }
        } else {
            openGallery();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intentGallery);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "İzin reddedildi", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String yorum, String userId) {
        StorageReference ref = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Resim yüklendi: " + downloadUrl);
                    Toast.makeText(PaylasimYap.this, "Resim yüklendi", Toast.LENGTH_SHORT).show();

                    // Resim URL'sini ve açıklamayı Firestore'a kaydet
                    saveImageInfoToFirestore(userId, yorum, downloadUrl);

                    // Resim URL'sini ve açıklamayı Firebase Veritabanına kaydet
                    Upload upload = new Upload(yorum, downloadUrl);
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Resim yüklemesi başarısız oldu", e);
                    Toast.makeText(PaylasimYap.this, "Resim yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Hatanın tam olarak anlaşılması için hata yığın izini yazdıralım
                    e.printStackTrace();
                });
    }

    public static class Upload {
        public String yorum;
        public String imageUrl;

        public Upload() {
            // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
        }

        public Upload(String yorum, String imageUrl) {
            this.yorum = yorum;
            this.imageUrl = imageUrl;
        }

        public String getYorum() {
            return yorum;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    public void geridön(View view)
    {
        Intent intent=new Intent(PaylasimYap.this, GirisSayfasi.class);
        startActivity(intent);
    }
}
