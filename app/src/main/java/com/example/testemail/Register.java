package com.example.testemail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    EditText mFullName,mEmail,mPassword,mPhone;
    TextView mLoginBtn;
    Button mRegisterBtn;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String UserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mLoginBtn = findViewById(R.id.createText);
        mRegisterBtn = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBar);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        //Kondisi Register Button
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email must be required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password must be required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password must be at least 6");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //register the user in firebase
                //buat cek sukses atau tidak untuk register akun nya ke firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //send email verifikasi
                            //need firebase user object
                            //ambil user yg dipilih
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                //kalau sukses send email verifikasi
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verification Email has Been Sent.",Toast.LENGTH_SHORT).show();
                                }

                                //kalau failed
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email Not Sent " + e.toString());
                                }
                            });

                            Toast.makeText(Register.this,"User Created",Toast.LENGTH_SHORT).show();
                            //database firebase
                            UserID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(UserID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullName);// fName itu nama atribut nya di db users
                            user.put("email",email);
                            user.put("phone",phone);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            //kalau sudah berhasil dia pindah ke window Utama
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Register.this,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}
