package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static com.example.boxy.MainActivity.REQUEST_CODE;

public class FingerprintAct extends AppCompatActivity {
    Button scanfingerbtn,openlockbtn,closelockbtn;
    TextView msgtxt;
    LinearLayout accorcontrols;
    private Executor executor;
    String accessdoor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        scanfingerbtn =findViewById(R.id.scanfinger);
        msgtxt = findViewById(R.id.txtt);
        accorcontrols =findViewById(R.id.layoutdoor);
        openlockbtn =findViewById(R.id.openlockbtn);
        closelockbtn = findViewById(R.id.closelockbtn);
        accessdoor = getIntent().getExtras().getString("accdr");

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                msgtxt.setText("You can use the biomatric to open the door");

//                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                msgtxt.setText("No biometric features available on this device.");
                scanfingerbtn.setVisibility(View.INVISIBLE);
//                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msgtxt.setText("Biometric features are currently unavailable.");
                scanfingerbtn.setVisibility(View.INVISIBLE);
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                msgtxt.setText("Your device don't have any fingerprint save,please check your security setting.");
                scanfingerbtn.setVisibility(View.INVISIBLE);
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintAct.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                accorcontrols.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
    });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric for my app")
                .setSubtitle("Access Lock using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        scanfingerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });


    }

    public void opendoor(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(accessdoor);
        HashMap<String,Object> hashMap= new HashMap<>();
//                    hashMap.put("FingerSensor",1);
        hashMap.put("FingerSensor","1");

        databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Update Successfully
                Toast.makeText(FingerprintAct.this, "Locked Opened", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FingerprintAct.this, "Updation Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void closedoor(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors").child(accessdoor);
        HashMap<String,Object> hashMap= new HashMap<>();
//                    hashMap.put("FingerSensor",1);
        hashMap.put("FingerSensor","0");

        databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Update Successfully
                Toast.makeText(FingerprintAct.this, "Locked Close", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FingerprintAct.this, "Updation Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}