package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserConttol extends AppCompatActivity {
    TextView username,email,phone,accessdoor;
    FirebaseAuth mAuth;
    Button faceregbtn,fingerbtn,releasebtn;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_conttol);
        username = findViewById(R.id.usernametxt);
        email = findViewById(R.id.useremail);
        phone = findViewById(R.id.userphone);
        accessdoor = findViewById(R.id.useraccesslock);
        mAuth= FirebaseAuth.getInstance();
        faceregbtn = findViewById(R.id.facialregnitionbtn);
        fingerbtn = findViewById(R.id.fingerbtn);
        releasebtn = findViewById(R.id.releasebtn);
        loadMyInfo();

        faceregbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aa= accessdoor.getText().toString();
                if(aa == null){

                }
                else {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
                    HashMap<String,Object> hashMap= new HashMap<>();
//                    hashMap.put("FingerSensor",1);
                    hashMap.put("facial_recognition",1);

                    databaseReference.child("uid").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Update Successfully
//                            Toast.makeText(UserConttol.this, "Succesfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        fingerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aa= accessdoor.getText().toString();
                if(aa == null){

                }
                else {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
                    HashMap<String,Object> hashMap= new HashMap<>();
                    hashMap.put("FingerSensor",1);
//                    hashMap.put("facial_recognition",1);

                    databaseReference.child("uid").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Update Successfully
//                            Toast.makeText(UserConttol.this, "Succesfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        releasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aa= accessdoor.getText().toString();
                databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa).child("uid");
                DatabaseReference reference3;

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
//                        Intent intent = new Intent();
//                        intent.putExtra("QRCODE", "null");
//                        setResult(RESULT_OK, intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//Updating Availblity
                HashMap<String,Object> hashMap= new HashMap<>();
                hashMap.put("ava","true");

                //update value to db
                DatabaseReference reference2;
                reference2 = FirebaseDatabase.getInstance().getReference("Doors");
                reference2.child(aa).updateChildren(hashMap);

                //Updating access door to user

                HashMap<String,Object> hashMap3= new HashMap<>();
                hashMap3.put("accessdoor","null");
                reference3 = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                reference3.updateChildren(hashMap3);

                finish();
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot ds :dataSnapshot.getChildren()){
//                            if(ds.child("NewUser").child("userName").getValue(String.class).equals(userName)){
//                                ds.child("NewUser").child("userName").getRef().removeValue();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//
//
//                });
            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
//        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name= ""+ds.child("name").getValue();
                    String email1= ""+ds.child("email").getValue();
                    String phone1= ""+ds.child("phone").getValue();
                    String profileImage= ""+ds.child("profileImage").getValue();
                    String accountType= ""+ds.child("accountType").getValue();
                    String city= ""+ds.child("city").getValue();
                    String accessdoor1= ""+ds.child("accessdoor").getValue();
                    username.setText(name);
                    email.setText(email1);
                    phone.setText(phone1);
                    accessdoor.setText(accessdoor1);
//                    try {
//                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_user).into(profileIv);
//
//                    }
//                    catch (Exception ee){
//                        profileIv.setImageResource(R.drawable.ic_user);
//                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}