package filkom.ub.getmeallocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TambahActivity extends AppCompatActivity  {

    String lattitude, longitude;
    private EditText etNamamenu, etHarga, etDate;
    private AutoCompleteTextView actvNamaRestoran;
    private Button btnSubmitMenu, btnSubmitImage;
    private ImageView imageView;

    private ArrayList<RestoranModel> namaRestoran = new ArrayList<RestoranModel>();
    private String[] arrayNamaRestoran;

    private DatabaseReference databaseRestoran;
    private StorageReference storageReference;

    private ArrayAdapter<String> arrayAdapter;

    public static final String TAG = "tambahM";
    public static String imageUrl = "null";
    public static String imagePath = "null";

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    // Animation
    private LocationManager mLocationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
//            Toast.makeText(TambahActivity.this, "lat" + String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
            lattitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                2000, mLocationListener);

        databaseRestoran = FirebaseDatabase.getInstance().getReference("restoran");

        actvNamaRestoran = (AutoCompleteTextView) findViewById(R.id.actv_nama_restoran);
        etNamamenu = (EditText) findViewById(R.id.et_nama_makanan);
        etHarga = (EditText) findViewById(R.id.et_harga);
        etDate = (EditText) findViewById(R.id.et_date);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnSubmitMenu = (Button) findViewById(R.id.button_submit_menu);
        btnSubmitImage = (Button) findViewById(R.id.btn_submit_iamge);



        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        btnSubmitMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMenu();
            }
        });
        btnSubmitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {

                    takePhoto();
                }
            }
        });

        checkGpsEnabled(getApplicationContext());

        etDate.setText(setCurrentDate());
        getAllRestoran();
//        getLocation();
    }


    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        //FileProvider.getUriForFile(TambahActivity.this, BuildConfig.APPLICATION_ID, photo));
        imageUri = Uri.fromFile(photo);
        //imageUri = FileProvider.getUriForFile(TambahActivity.this, BuildConfig.APPLICATION_ID, photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 1, out);

                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 1, out2);

                        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 1, out3);

                        imageView.setImageBitmap(bitmap);

                        imagePath = selectedImage.getPath();
                        int cut = imagePath.lastIndexOf('/');
                        if (cut != -1) {
                            imagePath = imagePath.substring(cut + 1);
                        }

                    } catch (Exception e) {
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    /**
     * Show dialog warning when GPS off
     */
    private void checkGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled || !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("gps network disable");
            dialog.setPositiveButton(("open location setting"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //get gps
                }
            });
            dialog.setNegativeButton(("cancel"), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    private void getAllRestoran() {
        databaseRestoran.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RestoranModel restoranModel = dataSnapshot1.getValue(RestoranModel.class);
                    namaRestoran.add(restoranModel);
                }

                /*ArrayList to Array Conversion */
                arrayNamaRestoran = new String[namaRestoran.size()];
                for (int j = 0; j < namaRestoran.size(); j++) {
                    arrayNamaRestoran[j] = namaRestoran.get(j).getNamaRestoran();
                }

                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, arrayNamaRestoran);

                actvNamaRestoran.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    boolean restoranExist;
    String namaRestoranExist;
    private void insertMenu() {

        uploadImage("menu", imagePath);
    }

    private void uploadImage(String type, String key) {

        storageReference = FirebaseStorage.getInstance().getReference(String.valueOf(new Random().nextInt(999999)));

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downkloadUrl = taskSnapshot.getDownloadUrl();
                        imageUrl = downkloadUrl.toString();
                        Log.d(TAG, "onSuccess: "+downkloadUrl);
                        submitMenu(imageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void submitMenu(String imageUrl) {
        restoranExist = false;
        namaRestoranExist = "";
        for (int i = 0; i < namaRestoran.size(); i++) {
            if (actvNamaRestoran.getText().toString().equals(namaRestoran.get(i).getNamaRestoran())) {
                restoranExist = true;
                namaRestoranExist = namaRestoran.get(i).getNamaRestoran();
                break;
            }
        }

        final MenuModel menuModel = new MenuModel(firebaseAuth.getUid(), etNamamenu.getText().toString(), etHarga.getText().toString(), etDate.getText().toString(), imageUrl);
        if (restoranExist) {
            //query get dataaseRestoran REFERENCE
            //update REFERENCE menu1
            databaseRestoran.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (namaRestoranExist.equals(snapshot.child("namaRestoran").getValue(String.class))) {
                            String key = databaseRestoran.push().getKey();
                            snapshot.getRef().child("menu").child(key).setValue(menuModel);
                            Toast.makeText(TambahActivity.this, "Berhasil menambahkan menu", Toast.LENGTH_SHORT).show();

                            goToMain();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(TambahActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            RestoranModel restoranModel = new RestoranModel(actvNamaRestoran.getText().toString(), lattitude, longitude);
            String key = databaseRestoran.push().getKey();
            databaseRestoran.child(key).setValue(restoranModel);
            databaseRestoran.child(key).child("menu").child(key).setValue(menuModel);
            Toast.makeText(this, "Berhasil menambahkan warung", Toast.LENGTH_SHORT).show();
            goToMain();
        }
    }

    private String setCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.toString();
    }

    private void goToMain() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}

