package com.example.boxy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity  implements LocationListener {
    private ImageButton backbtn, gpsbtn;
    private ImageView profileImg;
    private EditText nameEt, phoneEt, countryEt, stateET, cityEt, addressEt,emailEt, passwordEt, cpasswordEt;
    private Button registerbtn;
    private TextView regiseter_saller;

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission Array
    private String[] localpermission;
    private String[] camerapermission;
    private String[] storagepermission;
    //image picked
    private Uri Imageuri;

    private LocationManager locationManager;
    private double longitude, lattitude;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
// Initalize UI views

        backbtn = findViewById(R.id.back_btn);
        gpsbtn = findViewById(R.id.gps_btn);
        profileImg = (findViewById(R.id.profile_imagetv));
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        countryEt = findViewById(R.id.countryEt);
        stateET = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cpasswordEt = findViewById(R.id.cpasswordEt);
        registerbtn = findViewById(R.id.register);

        localpermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth= FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        gpsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get gps location of the user
                if (checklocalPermission()) {
                    detectlocation();
                } else {
                    requestlocalpermission();
                }

            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagepickdialog();
            }
        });

//        regiseter_saller.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegisterUserAct.this, RegisterSellerAct.class
////                ));
//            }
//        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }
    private String fullName,ShopName,phoneNumber,country,state,city,address,email,password,confirmpassword;
    private void inputData() {
        fullName= nameEt.getText().toString().trim();
        phoneNumber= phoneEt.getText().toString().trim();
        country= countryEt.getText().toString().trim();
        state= stateET.getText().toString().trim();
        city= cityEt.getText().toString().trim();
        address= addressEt.getText().toString().trim();
        email= emailEt.getText().toString().trim();
        password= passwordEt.getText().toString().trim();
        confirmpassword= cpasswordEt.getText().toString().trim();
        //validate data

        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Plz enter Full Name..", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Plz enter Phone Number..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(lattitude == 0.0 || longitude==0.0){
            Toast.makeText(this, "Plz Click GPS button on Toolbar ..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email Pattern..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this, "Password must be 6 character long..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmpassword)){
            Toast.makeText(this, "Password doesn't matched..", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();

    }
    private void createAccount() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        //creating Account
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account created
                saverFirebaseData();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed to create account
                progressDialog.dismiss();
//                Toast.makeText(RegisterUserAct.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void showImagepickdialog() {

        String[] options = {"Camera", "Gallery"};
        //dialogbox
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle click
                if (which == 0) {
                    //Camera Clicked;
                    if (checkcameraPermission()) {
                        pickfromCamera();
                    } else {
                        requestCameraePermission();
                    }
                } else {
                    //Gallery Clicked;
                    if (checkstoragePermission()) {
                        pickfromGallery();
                    } else {
                        requestStoragePermission();
                    }
                }

            }
        }).show();
    }
    private void pickfromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickfromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Title");
        Imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Imageuri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }
    private void detectlocation() {
        Toast.makeText(this, "Please Wait....", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
    }
    private void findAddress() {
        //finding address, country ,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());

        try {
            addresses= geocoder.getFromLocation(lattitude,longitude,1);
            String address= addresses.get(0).getAddressLine(0);//complete Address
            String city= addresses.get(0).getLocality();
            String state= addresses.get(0).getAdminArea();
            String country= addresses.get(0).getCountryName();

            countryEt.setText(country);
            stateET.setText(state);
            cityEt.setText(city);
            addressEt.setText(address);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checklocalPermission(){
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;
        return result;
    }
    private void requestlocalpermission(){
        ActivityCompat.requestPermissions(this,localpermission,LOCATION_REQUEST_CODE);
    }

    private boolean checkstoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagepermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkcameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }
    private void requestCameraePermission(){
        ActivityCompat.requestPermissions(this,camerapermission,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {
        //detect loction
        longitude= location.getLongitude();
        lattitude = location.getLatitude();

        findAddress();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Turn on Location ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        detectlocation();
                    } else {
                        Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean CameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAccepted && StorageAccepted) {
                        pickfromCamera();
                    } else {
                        Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAccepted) {
                        pickfromGallery();
                    } else {
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                //get Picked Image
                Imageuri= data.getData();
                //set to imageview
                profileImg.setImageURI(Imageuri);
            }
            else if(requestCode== IMAGE_PICK_CAMERA_CODE){
                profileImg.setImageURI(Imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account info...");
        final String timestamp = ""+System.currentTimeMillis();
        if(Imageuri == null){
            //save data without image
            HashMap<String,Object> hashMap= new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullName);
            hashMap.put("phone",""+phoneNumber);
            hashMap.put("country",""+country);
            hashMap.put("accessdoor","null");
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("lattitude",""+lattitude);
            hashMap.put("Longitude",""+longitude);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("accountType","User");
            hashMap.put("online","true");
            hashMap.put("profileImage","");
            //save to db
            DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
            myRef.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    startActivity(new Intent(Register.this,MainActivity.class));
                    finish();
                }
            });
        }
        else{
            //save data with image
            String filepathandName= "profile_images/"+ ""+firebaseAuth.getUid();
            //upload image to firebase
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filepathandName);
            storageReference.putFile(Imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri= uriTask.getResult();
                    if(uriTask.isSuccessful()){
                        HashMap<String,Object> hashMap= new HashMap<>();
                        hashMap.put("uid",""+firebaseAuth.getUid());
                        hashMap.put("email",""+email);
                        hashMap.put("name",""+fullName);
                        hashMap.put("phone",""+phoneNumber);
                        hashMap.put("country",""+country);
                        hashMap.put("state",""+state);
                        hashMap.put("city",""+city);
                        hashMap.put("accessdoor","null");
                        hashMap.put("address",""+address);
                        hashMap.put("lattitude",""+lattitude);
                        hashMap.put("Longitude",""+longitude);
                        hashMap.put("timestamp",""+timestamp);
                        hashMap.put("accountType","User");
                        hashMap.put("online","true");
                        hashMap.put("profileImage",""+downloadImageUri);//url of uploded image


                        //save to db
                        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
                        myRef.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Register.this,MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startActivity(new Intent(Register.this,MainActivity.class));
                                finish();
                            }
                        });


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}