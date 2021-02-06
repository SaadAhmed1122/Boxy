package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    CardView cardView1,cardView2,cardView3,cardView4,cardView5;
    FirebaseDatabase database;
    TextView nameTv,emailTv,phoneTv;

    ImageButton logoutbtn;
    private ImageView profileIv;
    DatabaseReference myRef;
    String qrvalue=null;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseUser currentuser;
    TextView qrCodeData;
    DatabaseReference databaseReference;
    public static final int REQUEST_CODE = 1;
    int savee=0;
    public static final int PERMISSION_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkUser();
        cardView1 = findViewById(R.id.cardbox1);
        cardView2 = findViewById(R.id.cardbox2);
        cardView3 = findViewById(R.id.cardbox3);
        cardView4 = findViewById(R.id.cardbox4);
        cardView5 = findViewById(R.id.cardbox5);
        logoutbtn= (ImageButton) findViewById(R.id.logout_btn);
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        nameTv= (TextView) findViewById(R.id.nameTV);
        emailTv= (TextView) findViewById(R.id.emailtv);
        phoneTv = (TextView) findViewById(R.id.phonenoTV);

        profileIv= (ImageView) findViewById(R.id.profileIv);

        mAuth= FirebaseAuth.getInstance();
//        currentuser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
        qrCodeData = findViewById(R.id.qrcodedata);

        setUi();
        loadMyInfo();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffline();
            }
        });


//        qrvalue = getIntent().getExtras().getString("qrvalue");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

    }

    private void makeMeOffline() {
        progressDialog.setMessage("Loging out...");
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Update Successfully
                mAuth.signOut();

                startActivity(new Intent(MainActivity.this,Login.class));
                finish();
//                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void checkUser() {
//        FirebaseUser user= mAuth.getCurrentUser();
        if(currentuser == null){
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }
    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name= ""+ds.child("name").getValue();
                    String email= ""+ds.child("email").getValue();
                    String phone= ""+ds.child("phone").getValue();
                    String profileImage= ""+ds.child("profileImage").getValue();
                    String accountType= ""+ds.child("accountType").getValue();
                    String city= ""+ds.child("city").getValue();
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_user).into(profileIv);

                    }
                    catch (Exception ee){
                        profileIv.setImageResource(R.drawable.ic_user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setUi() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value1 = snapshot.child("door1").child("ava").getValue().toString();
                String value2 = snapshot.child("door2").child("ava").getValue().toString();
                String value3 = snapshot.child("door3").child("ava").getValue().toString();
                String value4 = snapshot.child("door4").child("ava").getValue().toString();
                String value5 = snapshot.child("door5").child("ava").getValue().toString();
                if(!value1.equals("") && value1.equals("true")){
                    cardView1.setCardBackgroundColor(Color.GREEN);
                }
                else {
                    cardView1.setCardBackgroundColor(Color.RED);
                }
                if(!value2.equals("") && value2.equals("true")){
                    cardView2.setCardBackgroundColor(Color.GREEN);
                }
                else {
                    cardView2.setCardBackgroundColor(Color.RED);
                }
                if(!value3.equals("") && value3.equals("true")){
                    cardView3.setCardBackgroundColor(Color.GREEN);
                }
                else {
                    cardView3.setCardBackgroundColor(Color.RED);
                }
                if(!value4.equals("") && value4.equals("true")){
                        cardView4.setCardBackgroundColor(Color.GREEN);
                    }
                else {
                        cardView4.setCardBackgroundColor(Color.RED);
                    }
                if(!value5.equals("") && value5.equals("true")){
                        cardView5.setCardBackgroundColor(Color.GREEN);
                    }
                else {
                        cardView5.setCardBackgroundColor(Color.RED);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("QRCODE");
                qrCodeData.post(new Runnable() {
                    @Override
                    public void run() {
                        qrCodeData.setText( barcode.displayValue);
                        Toast.makeText(MainActivity.this, "QR Code Scanned", Toast.LENGTH_SHORT).show();
                        String data = barcode.displayValue.toString();

                        if(qrCodeData.getText().toString().equals("null")){

                        }
                        else {
//                            String data= qrCodeData.getText().toString();
                            checkaccess(data);
                        }


//                        checkaccess(data);
                        //Toast.makeText(MainActivity.this, "DAtaValue" + data, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }
    private void checkaccess(String data2) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
//        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String ace= ""+ds.child("accessdoor").getValue();
                   String aa= qrCodeData.getText().toString();

                    if(!(aa.equals("null"))){
                        if(ace.equals("null")){
//                        matchdata(data2);
                            checkdata(data2);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "You Already have a door.", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkdata(String data2) {
        String qrvalue= qrCodeData.getText().toString();


        if(qrvalue.equals("door1")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("door1").child("ava").getValue().toString();
                    if(value2.equals("true")) {
                        Toast.makeText(MainActivity.this, "Box No 1 is Available", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Box No 1 is not Available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else if(qrvalue.equals("door2")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("door2").child("ava").getValue().toString();

                    if(value2.equals("true"))
                    {
                        Toast.makeText(MainActivity.this, "Lock No 2 is Available", Toast.LENGTH_SHORT).show();
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
                        builder.setMessage("Did you went to get this Lock.");

                        builder.setCancelable(true);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                HashMap<String,Object> hashMap2 = new HashMap<>();
                                HashMap<String, Object> hasMapuser =new HashMap<>();
                                HashMap<String,Object> hashMap= new HashMap<>();
                                hashMap.put("ava","false");
                                hasMapuser.put("accessdoor","door2");
                                hashMap2.put("facial_recognition",0);
                                hashMap2.put("FingerSensor",0);
                                hashMap2.put("accessdoor","door2");
                                hashMap2.put("uid",mAuth.getUid());

                                databaseReference.child("door2").child("uid").setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        databaseReference.child("door2").updateChildren(hashMap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Data Insertion Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
                                myRef.child(mAuth.getUid()).updateChildren(hasMapuser);
//                                databaseReference.child("door2").child("uid").setValue(hashMap);
                                Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                qrCodeData.setText("null");
                                dialog.dismiss();
                                showmsg();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                           dialog.dismiss();
                            }

                        });
// Set other dialog properties


// Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Box No 2 is not Available", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(qrvalue.equals("door3")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("door3").child("ava").getValue().toString();
                    if(value2.equals("true")) {
                        Toast.makeText(MainActivity.this, "Box No 3 is Available", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Box No 3 is not Available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else if(qrvalue.equals("door4")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("door4").child("ava").getValue().toString();
                    if(value2.equals("true")) {
                        Toast.makeText(MainActivity.this, "Box No 4 is Available", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Box No 4 is not Available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else if(qrvalue.equals("door5")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value2 = snapshot.child("door5").child("ava").getValue().toString();
                    if(value2.equals("true")) {
                        Toast.makeText(MainActivity.this, "Box No 5 is Available", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Box No 5 is not Available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        else{
            Toast.makeText(MainActivity.this, "QR Code is not matched", Toast.LENGTH_SHORT).show();

        }
    }

    private void showmsg() {
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }

//    private void matchdata(String data) {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            int check=0,check2=0;
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    for (int i = 1; i < 6; i++) {
//                        String value = snapshot.child("door" + i).child("qrcode").getValue().toString();
//                        String value2 = snapshot.child("door" + i).child("ava").getValue().toString();
//                       // String available = snapshot.child("door" + i).child("Availability").getValue().toString();
//                        if (value.equals(data)) {
//                            Toast.makeText(MainActivity.this, "QR Code Matched", Toast.LENGTH_SHORT).show();
//                            if (value2.equals("true")) {
//                                Toast.makeText(MainActivity.this, "Box No:" + i + "is Available", Toast.LENGTH_SHORT).show();
//                                check = i;
//                                check2=1;
//                                showdialogbox(i);
//                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//                                builder1.setMessage("Did you went to get this box.");
//
//                                builder1.setCancelable(true);
//
//                                builder1.setPositiveButton(
//                                        "Yes",
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//
//                                                HashMap<String,Object> hashMap2 = new HashMap<>();
//                                                HashMap<String, Object> hasMapuser =new HashMap<>();
//                                                HashMap<String,Object> hashMap= new HashMap<>();
//                                                hashMap.put("ava","false");
//                                                hasMapuser.put("accessdoor","door"+check);
//                                                hashMap2.put("facial_recognition",0);
//                                                hashMap2.put("FingerSensor",0);
//                                                hashMap2.put("accessdoor","door"+check);
//                                                hashMap2.put("uid",mAuth.getUid());
//
//                                                databaseReference.child("door"+check).child("uid").setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        databaseReference.child("door"+check).updateChildren(hashMap);
//                                                        dialog.dismiss();
//                                                        savee++;
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(MainActivity.this, "Data Insertion Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        dialog.cancel();
//                                                    }
//                                                });
//                                                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
//                                                myRef.child(mAuth.getUid()).updateChildren(hasMapuser);
//                        databaseReference.child("door"+check).child("uid").setValue(hashMap);
//
////                        databaseReference.child("door"+i).child("uid").child("facial_recognition").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("FingerSensor").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("accessdoor").setValue("door"+i);
////
//                                                if(savee == 1){
//                                                    Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
//                                                    dialog.dismiss();
//                                                    dialog.cancel();
//                                                }
//                                            }
//                                        });
//
//                                builder1.setNegativeButton(
//                                        "No",
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                dialog.cancel();
//                                            }
//                                        });
////
//                                AlertDialog alert11 = builder1.create();
//                                alert11.show();
//                                break;
//                            } else {
//                                Toast.makeText(MainActivity.this, "This Box is not Available", Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//                        }
//
//                    }
//                    if (check2 == 0) {
//                        Toast.makeText(MainActivity.this, "Value Not Matched", Toast.LENGTH_SHORT).show();
//                    }
//
//                }catch (Exception aa){
//                    Toast.makeText(MainActivity.this, ""+aa.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void showdialogbox(int i) {
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//        builder1.setMessage("Did you went to get this box.");
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        HashMap<String,Object> hashMap2 = new HashMap<>();
//                        HashMap<String, Object> hasMapuser =new HashMap<>();
//                        HashMap<String,Object> hashMap= new HashMap<>();
//                        hashMap.put("ava","false");
//                        hasMapuser.put("accessdoor","door"+i);
//                        hashMap2.put("facial_recognition",0);
//                        hashMap2.put("FingerSensor",0);
//                        hashMap2.put("accessdoor","door"+i);
//                        hashMap2.put("uid",mAuth.getUid());
//
//                        databaseReference.child("door"+i).child("uid").setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                databaseReference.child("door"+i).updateChildren(hashMap);
//                                dialog.dismiss();
//                                savee++;
//                                }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(MainActivity.this, "Data Insertion Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                dialog.cancel();
//                            }
//                        });
//                        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
//                        myRef.child(mAuth.getUid()).updateChildren(hasMapuser);
//                        Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                        dialog.cancel();
////                        databaseReference.child("door"+i).child("uid").setValue(hashMap);
//
////                        databaseReference.child("door"+i).child("uid").child("facial_recognition").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("FingerSensor").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("accessdoor").setValue("door"+i);
////
//                        if(savee == 1){
//
//                        }
//                        }
//                });
//
//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
////
////        AlertDialog alert11 = builder1.create();
////        alert11.show();
//    }


    private void checkcondition() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 1; i < 6; i++) {

                    boolean queries = myRef.child("door" + i).equals(qrvalue);
                    if (queries) {
                        Toast.makeText(MainActivity.this, "qr Match to box" + i, Toast.LENGTH_SHORT).show();
                    }
//                    if(qrvalue ==  Map<String, Object> snapshot.child("door"+i).getValue()){
//                        Toast.makeText(MainActivity.this, "This is match", Toast.LENGTH_SHORT).show();
//                    }
                    Map<String, Object> map = (Map<String, Object>) snapshot.child("door" + i).getValue();
                    //Toast.makeText(MainActivity.this, "" + map, Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showcam(View view) {
        Intent intent = new Intent(MainActivity.this, ScannerAct.class);
        startActivityForResult(intent, REQUEST_CODE);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkcondition();
    }

    public void controluser(View view) {
        startActivity(new Intent(MainActivity.this,UserConttol.class));
    }
}