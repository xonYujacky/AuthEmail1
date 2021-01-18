package com.example.testemail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


public class Login extends AppCompatActivity {

    FirebaseAuth fAuth;
    EditText mEmail,mPassword;
    TextView mCreateBtn,mForgetPass;
    Button mLoginBtn;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        mForgetPass = findViewById(R.id.forgotPassword);

        //firebase
        fAuth = FirebaseAuth.getInstance();

        //funsi tombol login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

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
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Logged is sucessfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        //fungsi textView ini kalau si user blm punya akun di alihkan ke win register
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        mForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                //buat alert dulu
                final AlertDialog.Builder ResetPassDialog = new AlertDialog.Builder(v.getContext());
                ResetPassDialog.setTitle("Do you want reset your password?");
                ResetPassDialog.setMessage("Enter your email to receive the link");
                ResetPassDialog.setView(resetEmail);

                //kalau case nya "yes" maka ke proses selanjutnya
                ResetPassDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        //need firebase Auth

                        String mail = resetEmail.getText().toString();
                        //kalau berhasil
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this,"Reset Link Send to Your Email",Toast.LENGTH_SHORT).show();
                            }
                            //kalau failed
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error! Reset Link is not Sent"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //kalau case nya "no" ttp di halaman itu
                ResetPassDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing and close the dialog
                    }
                });
                ResetPassDialog.create().show();
            }
        });
    }
}
