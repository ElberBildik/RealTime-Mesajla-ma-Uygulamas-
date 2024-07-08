package com.example.mobilapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private EditText kadiText,sifreText;
    private String txtEmail,txtSifre;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kadiText=findViewById(R.id.kadiText);
        sifreText=findViewById(R.id.sifreText);

        mAuth=FirebaseAuth.getInstance();
    }
    public void GirisYap(View view)
    {
        txtEmail=kadiText.getText().toString();
        txtSifre=sifreText.getText().toString();

        if (!TextUtils.isEmpty(txtEmail )&& !TextUtils.isEmpty(txtSifre))
        {
            mAuth.signInWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult)
                        {
                            mUser=mAuth.getCurrentUser();
                            Intent intent=new Intent(MainActivity.this,GirisSayfasi.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(this, new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else
        {
            Toast.makeText(MainActivity.this,"Email veya Sifre boş olamaz..!",Toast.LENGTH_LONG).show();
        }
    }

    public void KayitOl(View view)
    {
        Intent intent=new Intent(MainActivity.this, kayitOl.class);
        startActivity(intent);
    }


}