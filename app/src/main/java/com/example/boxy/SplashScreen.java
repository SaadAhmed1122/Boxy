package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        firebaseAuth= FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivity(new Intent(SplashScreen.this,

                            Login.class));
                    finish();
                }
                else{
                    //checking user
                    checkUserType();
                }

            }
        },500);

    }
    private void checkUserType() {
        //check if user is seller then open seller main screen
        //if user is buyer then open buyer screen
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String accountType= ""+snapshot.child("accountType").getValue();
                        if (accountType.equals("User")){
                            //user is seller
                            Intent ii = new Intent(SplashScreen.this, MainActivity.class);
//                            ii.putExtra("qrvalue","");
                            startActivity(new Intent(ii));
                            finish();
                        }
                        else{
                            Intent ii = new Intent(SplashScreen.this, MainActivity.class);
//                            ii.putExtra("qrvalue", "null");
                            startActivity(new Intent(ii));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
