package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
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
    Switch faceswitch,fingerswitch;
    MaterialButton faceregbtn,fingerbtn,releasebtn;
    DatabaseReference databaseReference,ref2;

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
        faceswitch = findViewById(R.id.faceswitch);
        fingerswitch =findViewById(R.id.fingerswitch);
        releasebtn = findViewById(R.id.releasebtn);
        loadMyInfo();
        ref2= FirebaseDatabase.getInstance().getReference("Doors");



//        fingerbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(UserConttol.this,FingerprintAct.class));
//            }
//        });

        releasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rr;
                String aa= accessdoor.getText().toString();
                rr = FirebaseDatabase.getInstance().getReference("Doors").child(aa).child("uid");
                DatabaseReference reference3;

                rr.addListenerForSingleValueEvent(new ValueEventListener() {
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
                hashMap.put("FingerSensor","0");
                hashMap.put("facial_recognition","0");


                //update value to db
                DatabaseReference reference2;
                reference2 = FirebaseDatabase.getInstance().getReference("Doors");
                reference2.child(aa).updateChildren(hashMap);

                //Updating access door to user

                HashMap<String,Object> hashMap3= new HashMap<>();
                hashMap3.put("accessdoor","null");
                reference3 = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                reference3.updateChildren(hashMap3);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // super.onBackPressed(); calls finish(); for you

                // clear your SharedPreferences
                getSharedPreferences("preferenceName",0).edit().clear().commit();
                finish();
                System.exit(0);
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

    private void setswitches(String accessdoor1) {
        ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean mBool = true;
                    boolean mboolfalse = false;

                    String fingervalue = snapshot.child(accessdoor1).child("FingerSensor").getValue().toString();
                    String facevalue = snapshot.child(accessdoor1).child("facial_recognition").getValue().toString();


                   if (fingervalue.equals("1")) {
                        fingerswitch.setChecked(mBool);
                    }
                   else {
                       fingerswitch.setChecked(mboolfalse);
                   }

//                   else if (facevalue.equals(0)) {
//                        faceswitch.setChecked(mboolfalse);
//
//                    }
//                    else if(facevalue.equals(1)){
//                        faceswitch.setChecked(mBool);
//
//                    }

                if(facevalue.equals("0")){
                    faceswitch.setChecked(mboolfalse);
                }
                else {
                    faceswitch.setChecked(mBool);
                }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadMyInfo() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
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

                    if(accessdoor1.equals("null")){
                        fingerswitch.setVisibility(View.INVISIBLE);
                        faceswitch.setVisibility(View.INVISIBLE);
                        faceregbtn.setVisibility(View.INVISIBLE);
                        fingerbtn.setVisibility(View.INVISIBLE);
                        releasebtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        setswitches(accessdoor1);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}

    public void fingerswitchclick(View view) {
        try {

            if (fingerswitch.isChecked()) {
                String aa = accessdoor.getText().toString();
                if (aa == null) {

                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("FingerSensor", 1);
//                    hashMap.put("facial_recognition",1);

                    databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Update Successfully
                            Toast.makeText(UserConttol.this, "Lock Opened", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                String aa = accessdoor.getText().toString();
                if (aa == null) {

                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("FingerSensor", 0);
//                    hashMap.put("facial_recognition",1);

                    databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Update Successfully
                            Toast.makeText(UserConttol.this, "Lock Close", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }catch (Exception aa){
            Toast.makeText(this, ""+aa.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void facceswitchclick(View view) {
        String aa= accessdoor.getText().toString();
        if(faceswitch.isChecked()){
        if(aa == null){
        }
        else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
            HashMap<String,Object> hashMap= new HashMap<>();
//                    hashMap.put("FingerSensor",1);
            hashMap.put("facial_recognition",1);

            databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Update Successfully
                            Toast.makeText(UserConttol.this, "Locked Opened", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        }else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(aa);
            HashMap<String,Object> hashMap= new HashMap<>();
//                    hashMap.put("FingerSensor",1);
            hashMap.put("facial_recognition",0);

            databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Update Successfully
                            Toast.makeText(UserConttol.this, "Lock Close", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void fingeract(View view) {
        String aab= accessdoor.getText().toString();
        Intent hh= new Intent(UserConttol.this,FingerprintAct.class);
        hh.putExtra("accdr",aab);
        startActivity(hh);

    }
}
