package filkom.ub.getmeallocation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TambahActivity extends AppCompatActivity {

    private EditText etNamamenu, etHarga, etDate, etLokasi;
    private AutoCompleteTextView actvNamaRestoran;
    private Button btnSubmitMenu, btnSubmitImage;
    private ImageView imageView;

    private ArrayList<RestoranModel> namaRestoran = new ArrayList<RestoranModel>();
    private String[] arrayNamaRestoran;

    private DatabaseReference databaseRestoran;
    private DatabaseReference databaseMenu;
    private StorageReference storageReference;

    private ArrayAdapter<String> arrayAdapter;

    public static final String TAG = "tambahM";


    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);

        databaseRestoran = FirebaseDatabase.getInstance().getReference("restoran");
        databaseMenu = FirebaseDatabase.getInstance().getReference("menu");

        actvNamaRestoran = (AutoCompleteTextView) findViewById(R.id.actv_nama_restoran);
        etNamamenu = (EditText) findViewById(R.id.et_nama_makanan);
        etHarga = (EditText) findViewById(R.id.et_harga);
        etDate = (EditText) findViewById(R.id.et_date);
        etLokasi = (EditText) findViewById(R.id.et_lokasi);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnSubmitMenu = (Button) findViewById(R.id.button_submit_menu);
        btnSubmitImage = (Button) findViewById(R.id.btn_submit_iamge);

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

        etDate.setText(setCurrentDate());
        getAllRestoran();
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

                        imageView.setImageBitmap(bitmap);
                        //Toast.makeText(this, selectedImage.toString(),Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        //Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
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

        restoranExist = false;
        namaRestoranExist = "";
        for (int i = 0; i < namaRestoran.size(); i++) {
            if (actvNamaRestoran.getText().toString().equals(namaRestoran.get(i).getNamaRestoran())) {
                restoranExist = true;
                namaRestoranExist = namaRestoran.get(i).getNamaRestoran();
                break;
            }
        }

        final MenuModel menuModel = new MenuModel(etNamamenu.getText().toString(), etHarga.getText().toString(), etDate.getText().toString());
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
                            //Toast.makeText(TambahActivity.this, snapshot.child("namaRestoran").getValue(String.class), Toast.LENGTH_SHORT).show();
                            Toast.makeText(TambahActivity.this, "Berhasil menambahkan menu", Toast.LENGTH_SHORT).show();

                            uploadImage("menu", key);

                            goToMain();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            RestoranModel restoranModel = new RestoranModel(actvNamaRestoran.getText().toString(), etLokasi.getText().toString());
            String key = databaseRestoran.push().getKey();
            databaseRestoran.child(key).setValue(restoranModel);
            databaseRestoran.child(key).child("menu").child(key).setValue(menuModel);
            Toast.makeText(this, "Berhasil menambahkan warung", Toast.LENGTH_SHORT).show();
            uploadImage("menu", key);
            goToMain();
        }

    }

    private void uploadImage(String type, String key) {

        storageReference = FirebaseStorage.getInstance().getReference(type + "/" + key + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downkloadUrl = taskSnapshot.getUploadSessionUri();
                        //Toast.makeText(TambahActivity.this, downkloadUrl.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String setCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.toString();
    }

    private void goToMain() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
