package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

public class testscanner extends AppCompatActivity {
    Button btnscan;
    FirebaseAuth mAuth;
    DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testscanner);
        btnscan = findViewById(R.id.scanbtn);
        mAuth= FirebaseAuth.getInstance();
        userref= FirebaseDatabase.getInstance().getReference("Users");

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator aa= new IntentIntegrator(testscanner.this);
                aa.setPrompt("for Flash use volume up");
                aa.setBeepEnabled(true);
                aa.setOrientationLocked(true);
                aa.setCaptureActivity(Capture.class);
                aa.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled.....Please Scan Again", Toast.LENGTH_LONG).show();
            } else {
                if(result.getContents().equals("door1") || result.getContents().equals("door2") || result.getContents().equals("door3")|| result.getContents().equals("door4")||result.getContents().equals("door5")){
                    Intent oi= new Intent(testscanner.this,TakingUserpermission.class);
                    oi.putExtra("qr",result.getContents());
                    startActivity(oi);
                    finish();
                }
                else{
                    Toast.makeText(this, "You Scan Wrong QR Code", Toast.LENGTH_SHORT).show();
                }
//                userref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot ds: snapshot.getChildren()){
//                            String ace= ""+ds.child("accessdoor").getValue();
//                            if(ace.equals("null")){
//                                giveaccess(result.getContents());
//                            }
//                            else{
//                                Toast.makeText(testscanner.this, "You Already have a Locker", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
                        }
        } else {
            Toast.makeText(this, "Scanned Again..!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // super.onBackPressed(); calls finish(); for you

        // clear your SharedPreferences
        getSharedPreferences("preferenceName",0).edit().clear().commit();
        finish();
    }
//    private void giveaccess(String contents) {
//
//        switch (contents){
//            case "door1":
//                checkcondition1(contents);
//                break;
//            case "door2":
//                checkcondition2(contents);
//                break;
//            case "door3":
//                checkcondition3(contents);
//                break;
//            case "door4":
//                checkcondition4(contents);
//                break;
//            case "door5":
//                checkcondition5(contents);
//                break;
//            default:
//                Toast.makeText(testscanner.this, "QR Code is not matched", Toast.LENGTH_SHORT).show();
//
//        }
//
//    }
//
//    private void checkcondition5(String contents) {
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value2 = snapshot.child("door5").child("ava").getValue().toString();
//                if(value2.equals("true")) {
//                    Toast.makeText(testscanner.this, "Box No 5 is Available", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(testscanner.this, "Box No 5 is not Available", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void checkcondition4(String contents) {
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value2 = snapshot.child("door4").child("ava").getValue().toString();
//                if(value2.equals("true")) {
//                    Toast.makeText(testscanner.this, "Box No 4 is Available", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(testscanner.this, "Box No 4 is not Available", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void checkcondition3(String contents) {
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value2 = snapshot.child("door3").child("ava").getValue().toString();
//                if(value2.equals("true")) {
//                    Toast.makeText(testscanner.this, "Box No 3 is Available", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(testscanner.this, "Box No 3 is not Available", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void checkcondition2(String contents) {
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child("door2");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value2 = snapshot.child("ava").getValue().toString();
//
//                if(value2.equals("true"))
//                {
//                    Toast.makeText(testscanner.this, "Lock No 2 is Available", Toast.LENGTH_SHORT).show();
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(testscanner.this);
//// Add the buttons
//                    builder.setMessage("Did you went to get this Lock.");
//
//                    builder.setCancelable(true);
//                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            HashMap<String,Object> hashMap2 = new HashMap<>();
//                            HashMap<String, Object> hasMapuser =new HashMap<>();
//                            HashMap<String,Object> hashMap= new HashMap<>();
//                            hashMap.put("ava","false");
//                            hasMapuser.put("accessdoor","door2");
//                            hashMap2.put("facial_recognition",0);
//                            hashMap2.put("FingerSensor",0);
//                            hashMap2.put("accessdoor","door2");
//                            hashMap2.put("uid",mAuth.getUid());
//                            databaseReference.child("uid").setValue(hashMap2);
//                            databaseReference.updateChildren(hashMap);
//                            userref.child(mAuth.getUid()).updateChildren(hasMapuser);
////                                databaseReference.child("door2").child("uid").setValue(hashMap);
//                            Toast.makeText(testscanner.this, "Data Saved", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                            dialog.dismiss();
//                        }
//
//                    });
//// Set other dialog properties
//
//// Create the AlertDialog
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//
//                }
//                else{
//                    Toast.makeText(testscanner.this, "Box No 2 is not Available", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void checkcondition1(String contents) {
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
//
//        if(contents.equals("door1")){
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    String value2 = snapshot.child("door1").child("ava").getValue().toString();
//                    if(value2.equals("true")) {
//                        Toast.makeText(testscanner.this, "Box No 1 is Available", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(testscanner.this, "Box No 1 is not Available", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//    }
}