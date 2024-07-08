package com.example.mobilapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class kayitOl extends AppCompatActivity {
    private EditText kaditext,ksifretext;
    private String kadi,ksifre;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);
        kaditext=findViewById(R.id.kullaniciAdi);
        ksifretext=findViewById(R.id.sifre);

        mAuth=FirebaseAuth.getInstance();


    }

    public void kayitOl(View view)
    {
        kadi=kaditext.getText().toString();
        ksifre=ksifretext.getText().toString();
        if (!TextUtils.isEmpty(kadi)&& !TextUtils.isEmpty(ksifre))
        {
            mAuth.createUserWithEmailAndPassword(kadi,ksifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(kayitOl.this,"Kayit islemi basarili",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(kayitOl.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else
        { Toast.makeText(this,"email ve sifre bos olmaz",Toast.LENGTH_LONG).show(); }
    }
    public void anaSayfa(View view)
    {
        Intent intent=new Intent(kayitOl.this,MainActivity.class);
        startActivity(intent);
    }
}