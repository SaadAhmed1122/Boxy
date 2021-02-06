package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    EditText emailET,passwordET;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailET= (EditText) findViewById(R.id.emailEt);
        passwordET= (EditText) findViewById(R.id.passwordEt);
        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    public void registeruser(View view) {
        startActivity(new Intent(Login.this, Register.class));
    }

    public void forgetpass(View view) {
        startActivity(new Intent(this, ForgetPass.class));
    }

    public void login(View view) {
        loginuser();

    }
    String email,password;
    private void loginuser() {
        email= emailET.getText().toString().trim();
        password= passwordET.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email address", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                makeMeOnline();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void makeMeOnline() {
        //after logging make user online
        progressDialog.setMessage("Checking User...");
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("online","true");

        //update value to db
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Update Successfully
                checkUserType();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserType() {
        //check if user is seller then open seller main screen
        //if user is buyer then open buyer screen
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String accountType= ""+ds.child("accountType").getValue();
                    if (accountType.equals("User")){
                        progressDialog.dismiss();
                        //user is seller
                        Intent ii = new Intent(Login.this, MainActivity.class);
                        //ii.putExtra("qrvalue","");
                        startActivity(new Intent(ii));
                        finish();
                    }
                    else{
                        Intent ii = new Intent(Login.this, MainActivity.class);
                        //ii.putExtra("qrvalue","");
                        startActivity(new Intent(ii));
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}