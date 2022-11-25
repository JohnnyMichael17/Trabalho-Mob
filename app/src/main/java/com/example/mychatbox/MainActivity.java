package com.example.mychatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditSenha;
    private Button mButtonLogin;
    private TextView mTxtNoAccount;
    private TextView mTxtHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEditEmail = findViewById(R.id.edit_email);
        mEditSenha = findViewById(R.id.edit_senha);
        mButtonLogin = findViewById(R.id.button_login);
        mTxtNoAccount = findViewById(R.id.text_noaccount);
        mTxtHere = findViewById(R.id.txt_here);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEditEmail.getText().toString();
                String senha = mEditSenha.getText().toString();


                Log.i("Teste", email);
                Log.i("Teste", senha);

                if(email == null || email.isEmpty() || senha == null || senha.isEmpty() ) {
                    Toast.makeText(MainActivity.this, "Todos os dados devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("Teste", task.getResult().getUser().getUid());

                                Intent intent = new Intent(MainActivity.this, MessengerActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Teste", e.getMessage());
                            }
                        });
            }
        });

        mTxtHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}