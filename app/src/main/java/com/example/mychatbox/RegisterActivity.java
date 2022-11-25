package com.example.mychatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.MessageDigestSpi;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditName2;
    private EditText mEditPhone;
    private EditText mEditEmail;
    private EditText mEditSenha;
    private Button mButtonReg;
    private Button mButtonPhoto;
    private ImageView mImagePhoto;
    private Uri mSelectedReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEditName = findViewById(R.id.edit_name);
        mEditName2 = findViewById(R.id.edit_name2);
        mEditPhone = findViewById(R.id.edit_phone);
        mEditEmail = findViewById(R.id.edit_email);
        mEditSenha = findViewById(R.id.edit_senha);
        mButtonReg = findViewById(R.id.button_reg);
        mButtonPhoto = findViewById(R.id.button_photo);
        mImagePhoto = findViewById(R.id.image_photo);

        mButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        mButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            mSelectedReference = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedReference);
                mImagePhoto.setImageDrawable(new BitmapDrawable(bitmap));
                mButtonPhoto.setAlpha(0);
            } catch (IOException e) {
            }
        }
    }

    private void selectPhoto() {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        }

    public void createUser() {
        String nome = mEditName.getText().toString();
        String sobrenome = mEditName2.getText().toString();
        String telefone = mEditPhone.getText().toString();
        String email = mEditEmail.getText().toString();
        String senha = mEditSenha.getText().toString();


        if(nome == null || nome.isEmpty() || sobrenome == null || sobrenome.isEmpty() || telefone == null || telefone.isEmpty() || email == null || email.isEmpty() || senha == null || senha.isEmpty() ){
            Toast.makeText( this, "Todos os dados devem ser preenchidos!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            Log.i("Sucesso", task.getResult().getUser().getUid());

                        saveUserInFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste", e.getMessage());
                    }
                });

    }

    private void saveUserInFirebase() {
        String arquivo = UUID.randomUUID().toString();

        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + arquivo);
        ref.putFile(mSelectedReference)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Teste", uri.toString());

                                String id = FirebaseAuth.getInstance().getUid();
                                String nome = mEditName.getText().toString();
                                String sobrenome = mEditName2.getText().toString();
                                String telefone = mEditPhone.getText().toString();
                                String profileUrl = uri.toString();

                                User user = new User(id, nome, sobrenome, telefone, profileUrl);

                                FirebaseFirestore.getInstance().collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i("Teste", documentReference.getId());

                                                Intent intent= new Intent(RegisterActivity.this, MessengerActivity.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("ree", e.getMessage());
                                            }
                                        });
                            }


                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste",  e.getMessage());
                    }
                });
    }
}