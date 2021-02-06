package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScannerAct extends AppCompatActivity {
    SurfaceView cameraViewQR;

    BarcodeDetector barcodeQR;

    CameraSource cameraSourcePhone;

    SurfaceHolder qrHolderScreen;
    FirebaseAuth mAuth;
    int savee=0;
    DatabaseReference databaseReference;


//    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        cameraViewQR = findViewById(R.id.cameraView);
        cameraViewQR.setZOrderMediaOverlay(true);
        qrHolderScreen = cameraViewQR.getHolder();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
        mAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
        barcodeQR = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if (!barcodeQR.isOperational()) {
            Toast.makeText(getApplicationContext(), "Problem Occurred in Setup", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        cameraSourcePhone = new CameraSource.Builder(this, barcodeQR)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(480, 480)
                .build();
        cameraViewQR.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(ScannerAct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSourcePhone.start(cameraViewQR.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcodeQR.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("QRCODE", barcode.valueAt(0));
                    setResult(RESULT_OK, intent);
//                    final Barcode bacodee  = barcode.valueAt(0);
//                    String data = bacodee.displayValue;
//                    checkaccess(data);
                    playBeep();
                    finish();
//                    databaseReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (int i = 1; i < 6; i++) {
//                                String value = snapshot.child("door" + i).child("qrcode").getValue().toString();
//                                if (value.equals((barcode.valueAt(0)).displayValue)) {
//                                    Toast.makeText(ScannerAct.this, "QR Code Matched", Toast.LENGTH_SHORT).show();
//                                    setResult(RESULT_OK, intent);
//                                    finish();
//                                    break;
//                                } else {
//                                    Toast.makeText(ScannerAct.this, "Values: is not matched ", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });


//                    finish();
                }
            }
        });
    }
    private void playBeep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

//    private void checkaccess(String data2) {
//        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
////        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds: snapshot.getChildren()){
//                    String ace= ""+ds.child("accessdoor").getValue();
//                    if(ace.equals("null")){
//                        matchdata(data2);
//                    }
//                    else{
//                        Toast.makeText(ScannerAct.this, "You Already have a door.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void matchdata(String data) {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            int check=0;
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    for (int i = 1; i < 6; i++) {
//                        String value = snapshot.child("door" + i).child("qrcode").getValue().toString();
//                        String value2 = snapshot.child("door" + i).child("ava").getValue().toString();
//                        // String available = snapshot.child("door" + i).child("Availability").getValue().toString();
//                        if (value.equals(data)) {
//                            Toast.makeText(ScannerAct.this, "QR Code Matched", Toast.LENGTH_SHORT).show();
//                            if (value2.equals("true")) {
//                                Toast.makeText(ScannerAct.this, "Box No:" + i + "is Available", Toast.LENGTH_SHORT).show();
//                                check = 1;
//                                showdialogbox(i);
//                                break;
//                            } else {
//                                Toast.makeText(ScannerAct.this, "This Box is not Available", Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//                        }
//
//                    }
//                    if (check == 0) {
//                        Toast.makeText(ScannerAct.this, "Value Not Matched", Toast.LENGTH_SHORT).show();
//                    }
//
//                }catch (Exception aa){
//                    Toast.makeText(ScannerAct.this, ""+aa.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void showdialogbox(int i) {
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(ScannerAct.this);
//        builder1.setMessage("Did you went to get this box.");
//
//        builder1.setCancelable(true);
//
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
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(ScannerAct.this, "Data Insertion Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                dialog.cancel();
//                            }
//                        });
//                        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
//                        myRef.child(mAuth.getUid()).updateChildren(hasMapuser);
////                        databaseReference.child("door"+i).child("uid").setValue(hashMap);
//
////                        databaseReference.child("door"+i).child("uid").child("facial_recognition").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("FingerSensor").setValue(0);
////                        databaseReference.child("door"+i).child("uid").child("accessdoor").setValue("door"+i);
////
//                        if(savee == 1){
//                            Toast.makeText(ScannerAct.this, "Data Saved", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            dialog.cancel();
//                        }
//                    }
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
//        AlertDialog alert11 = builder1.create();
//        alert11.show();
//    }

}

//    private void openCamandSave() {
//        try {
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Doors");
//
//            CodeScannerView scannerView = findViewById(R.id.scanner_view);
//            mCodeScanner = new CodeScanner(this, scannerView);
//            mCodeScanner.setDecodeCallback(new DecodeCallback() {
//                @Override
//                public void onDecoded(@NonNull final Result result) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String qrvalue = result.getText().toString();
////                        for (int i = 1; i < 6; i++) {
////                            databaseReference.child("door3").orderByChild("qrcode").equalTo(qrvalue).addValueEventListener(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                    if(snapshot.exists()){
////                                        Toast.makeText(ScannerAct.this, "QR COde match", Toast.LENGTH_SHORT).show();
////                                    }
////                                    else {
////                                        Toast.makeText(ScannerAct.this, "QR Code is not match", Toast.LENGTH_SHORT).show();
////                                    }
////                                }
////
////                                @Override
////                                public void onCancelled(@NonNull DatabaseError error) {
////
////                                }
////                            });
//
////                        }
//                            databaseReference.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for (int i = 1; i < 6; i++) {
//
//                                        String value = snapshot.child("door" + i).child("qrcode").getValue().toString();
//                                        if (value.equals(qrvalue)) {
//                                            Toast.makeText(ScannerAct.this, "QR Code Matched", Toast.LENGTH_SHORT).show();
//                                            Intent ww = new Intent(ScannerAct.this, MainActivity.class);
//                                            ww.putExtra("qrvalue", qrvalue);
//                                            startActivity(ww);
//                                            finish();
//                                            i = 0;
//                                        } else {
//                                            Toast.makeText(ScannerAct.this, "Values: is not matched ", Toast.LENGTH_SHORT).show();
//                                        }
//
////                                    boolean queries = databaseReference.child("door" + i).orderByChild("qrcode").equals(qrvalue);
////                                    if (queries) {
////                                        Toast.makeText(ScannerAct.this, "qr Match to box" + i, Toast.LENGTH_SHORT).show();
////                                    }
////                    if(qrvalue ==  Map<String, Object> snapshot.child("door"+i).getValue()){
////                        Toast.makeText(MainActivity.this, "This is match", Toast.LENGTH_SHORT).show();
////                    }
//                                        Map<String, Object> map = (Map<String, Object>) snapshot.child("door" + i).getValue();
//                                        //Toast.makeText(MainActivity.this, "" + map, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//
////                        Toast.makeText(ScannerAct.this, result.getText(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                }
//            });
//            scannerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mCodeScanner.startPreview();
//                }
//            });
//        }
//        catch (Exception aa){
//            Toast.makeText(this, ""+aa.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//}