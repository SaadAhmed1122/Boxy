package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TakingUserpermission extends AppCompatActivity {

    TextView showdoor,showlockstatus;
    Button add,cancel;
    FirebaseAuth mAuth;
    DatabaseReference userref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_userpermission);
        showdoor = findViewById(R.id.doornametext);
        add = findViewById(R.id.addbutton);
        cancel = findViewById(R.id.cancelbutton2);
        showlockstatus= findViewById(R.id.textView33);

        String aa= getIntent().getStringExtra("qr");
        showdoor.setText(aa);
        mAuth= FirebaseAuth.getInstance();
        userref= FirebaseDatabase.getInstance().getReference("Users");


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String ace= ""+ds.child("accessdoor").getValue();
                            if(ace.equals("null")){
//                                String bb = showdoor.getText().toString();
                                giveaccess();
                            }
                            else{
                                Toast.makeText(TakingUserpermission.this, "You Already have a Locker", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TakingUserpermission.this,MainActivity.class));
            }
        });
    }

    private void giveaccess() {
        String contents = showdoor.getText().toString();
        switch (contents){
            case "door1":
                checkcondition1(contents);
                break;
            case "door2":
                checkcondition2(contents);
                break;
            case "door3":
                checkcondition3(contents);
                break;
            case "door4":
                checkcondition4(contents);
                break;
            case "door5":
                checkcondition5(contents);
                break;
            default:
                Toast.makeText(TakingUserpermission.this, "QR Code is not matched", Toast.LENGTH_SHORT).show();

        }


    }
    private void checkcondition5(String contents) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door5");;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value2 = snapshot.child("ava").getValue().toString();
                if(value2.equals("true")) {
//                    Toast.makeText(TakingUserpermission.this, "Box No 5 is Available", Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    HashMap<String, Object> hasMapuser = new HashMap<>();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("ava", "false");
                    hasMapuser.put("accessdoor", "door5");
                    hashMap2.put("facial_recognition", 0);
                    hashMap2.put("FingerSensor", 0);
                    hashMap2.put("accessdoor", "door5");
                    hashMap2.put("uid", mAuth.getUid());
                    databaseReference.child("uid").setValue(hashMap2);
                    databaseReference.updateChildren(hashMap);
                    userref.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                    Toast.makeText(TakingUserpermission.this, "Data Saved", Toast.LENGTH_SHORT).show();

                    cleardata();
                }
                else{
//                    Toast.makeText(TakingUserpermission.this, "Box No 5 is not Available", Toast.LENGTH_SHORT).show();
                    showlockstatus.setText("Box No 5 is not Available");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkcondition4(String contents) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door4");;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value2 = snapshot.child("ava").getValue().toString();
                if(value2.equals("true")) {
//                    Toast.makeText(TakingUserpermission.this, "Box No 4 is Available", Toast.LENGTH_SHORT).show();
                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    HashMap<String, Object> hasMapuser =new HashMap<>();
                    HashMap<String,Object> hashMap= new HashMap<>();
                    hashMap.put("ava","false");
                    hasMapuser.put("accessdoor","door4");
                    hashMap2.put("facial_recognition",0);
                    hashMap2.put("FingerSensor",0);
                    hashMap2.put("accessdoor","door4");
                    hashMap2.put("uid",mAuth.getUid());
                    databaseReference.child("uid").setValue(hashMap2);
                    databaseReference.updateChildren(hashMap);
                    userref.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                    Toast.makeText(TakingUserpermission.this, "Data Saved", Toast.LENGTH_SHORT).show();
                    cleardata();

                }
                else{
                    showlockstatus.setText("Box No 4 is not Available");
//                    Toast.makeText(TakingUserpermission.this, "Box No 4 is not Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkcondition3(String contents) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door3");;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value2 = snapshot.child("ava").getValue().toString();
                if(value2.equals("true")) {
//                    Toast.makeText(TakingUserpermission.this, "Box No 3 is Available", Toast.LENGTH_SHORT).show();
                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    HashMap<String, Object> hasMapuser =new HashMap<>();
                    HashMap<String,Object> hashMap= new HashMap<>();
                    hashMap.put("ava","false");
                    hasMapuser.put("accessdoor","door3");
                    hashMap2.put("facial_recognition",0);
                    hashMap2.put("FingerSensor",0);
                    hashMap2.put("accessdoor","door3");
                    hashMap2.put("uid",mAuth.getUid());
                    databaseReference.child("uid").setValue(hashMap2);
                    databaseReference.updateChildren(hashMap);
                    userref.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                    Toast.makeText(TakingUserpermission.this, "Data Saved", Toast.LENGTH_SHORT).show();
                    cleardata();

                }

                else{
                    showlockstatus.setText("Box No 3 is not Available");
//                    Toast.makeText(TakingUserpermission.this, "Box No 3 is not Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkcondition2(String contents) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door2");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value2 = snapshot.child("ava").getValue().toString();

                if(value2.equals("true"))
                {
//                    Toast.makeText(TakingUserpermission.this, "Lock No 2 is Available", Toast.LENGTH_SHORT).show();

                            HashMap<String,Object> hashMap2 = new HashMap<>();
                            HashMap<String, Object> hasMapuser =new HashMap<>();
                            HashMap<String,Object> hashMap= new HashMap<>();
                            hashMap.put("ava","false");
                            hasMapuser.put("accessdoor","door2");
                            hashMap2.put("facial_recognition",0);
                            hashMap2.put("FingerSensor",0);
                            hashMap2.put("accessdoor","door2");
                            hashMap2.put("uid",mAuth.getUid());
                            databaseReference.child("uid").setValue(hashMap2);
                            databaseReference.updateChildren(hashMap);
                            userref.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                            Toast.makeText(TakingUserpermission.this, "Data Saved", Toast.LENGTH_SHORT).show();
                  cleardata();

                }
                else{
                    showlockstatus.setText("Box No 2 is not Available");
//                    Toast.makeText(TakingUserpermission.this, "Box No 2 is not Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkcondition1(String contents) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door1");;

        if(contents.equals("door1")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("ava").getValue().toString();
                    if(value2.equals("true")) {
//                        Toast.makeText(TakingUserpermission.this, "Box No 1 is Available", Toast.LENGTH_SHORT).show();
                        HashMap<String,Object> hashMap2 = new HashMap<>();
                        HashMap<String, Object> hasMapuser =new HashMap<>();
                        HashMap<String,Object> hashMap= new HashMap<>();
                        hashMap.put("ava","false");
                        hasMapuser.put("accessdoor","door1");
                        hashMap2.put("facial_recognition",0);
                        hashMap2.put("FingerSensor",0);
                        hashMap2.put("accessdoor","door1");
                        hashMap2.put("uid",mAuth.getUid());
                        databaseReference.child("uid").setValue(hashMap2);
                        databaseReference.updateChildren(hashMap);
                        userref.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                        Toast.makeText(TakingUserpermission.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        cleardata();
                    }
                    else{
                        showlockstatus.setText("Box No 1 is not Available");
//                        Toast.makeText(TakingUserpermission.this, "Box No 1 is not Available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void cleardata() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // super.onBackPressed(); calls finish(); for you

        // clear your SharedPreferences
        getSharedPreferences("preferenceName",0).edit().clear().commit();
        finish();
//        System.exit(0);
    }
}