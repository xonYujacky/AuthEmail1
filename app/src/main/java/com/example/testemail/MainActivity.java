package com.example.testemail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    TextView fullname,email,phone,verifyMsg,verifyMsgSucces;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID;
    Button resendCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //User profile
        phone = findViewById(R.id.profilePhone);
        fullname = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Verify status
        verifyMsg = findViewById(R.id.verifyMsg);
        verifyMsgSucces = findViewById(R.id.verifyMsgSuccess);
        resendCode = findViewById(R.id.resendCode);

        //akun user yg digunakan
        UserID = fAuth.getCurrentUser().getUid();
        final FirebaseUser user = fAuth.getCurrentUser();

        //kalau email nya belum ke verif
        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        //kalau sukses send email verifikasi
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this,"Verification Email has Been Sent.",Toast.LENGTH_SHORT).show();
                        }

                        //kalau failed
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email Not Sent " + e.toString());
                        }
                    });
                }
            });
        }else{
            verifyMsgSucces.setVisibility(View.VISIBLE);
        }

        //narik data dari db
        DocumentReference documentReference = fStore.collection("users").document(UserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //jika data nya ada di table users
                if(documentSnapshot.exists()){
                    phone.setText(documentSnapshot.getString("phone"));
                    fullname.setText(documentSnapshot.getString("fName"));
                    email.setText(documentSnapshot.getString("email"));
                }else{
                    Log.d("TAG","onEvent : Document do not exist");
                }

            }
        });


    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
