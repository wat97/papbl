package filkom.ub.getmeallocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import filkom.ub.getmeallocation.adapter.MenuAdapter;
import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

public class DetailRestoranActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvNamaRestoran, tvNamaMenu, tvLokasi, tvHarga, tvTanggal;
    static String restoranKey;

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private ImageView imageView;

    private DatabaseReference databaseReference;
    private MenuModel menu;
    private static RestoranModel restoran;

    private GoogleMap mGoogleMap;

    private ArrayList<MenuModel> menuModels;

    private MarkerOptions mRestaurantMarker;
    private DatabaseReference mDatabaseReference;
    private ArrayList<RestoranModel> mRestaurants;
    private MarkerOptions mLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("restoran");
        if (mLocationMarker == null) mLocationMarker = new MarkerOptions();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        menu = (MenuModel) args.getSerializable("MENU");
        mRestaurants = new ArrayList<>();

        tvNamaMenu = (TextView) findViewById(R.id.tv_nama_menu);
        tvNamaRestoran = (TextView) findViewById(R.id.tv_nama_restoran);
        tvLokasi = (TextView) findViewById(R.id.tv_lokasi);
        tvHarga = (TextView) findViewById(R.id.tv_harga);
        tvTanggal = (TextView) findViewById(R.id.tv_tanggal);
        imageView = (ImageView) findViewById(R.id.iv_menu);

        tvNamaRestoran.setText(menu.getNamaRestoran());
        tvNamaMenu.setText(menu.getNamaMenu());
        tvLokasi.setText(menu.getHarga());
        Picasso.get().load(menu.getImageUrl()).into(imageView);

        SupportMapFragment mapFragment = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        getSpecificRestoran();
    }

    private void getSpecificRestoran() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RestoranModel restoranModel = dataSnapshot1.getValue(RestoranModel.class);
                    //Toast.makeText(getApplicationContext(), restoranModel.getLat(), Toast.LENGTH_SHORT).show();
                    //Log.d("hoammm resto", "onDataChange: "+restoranModel.getNamaRestoran());
                    //Log.d("hoammm menu", "onDataChange: "+menu.getNamaMenu());
                    if (restoranModel.getNamaRestoran().equals(menu.getNamaRestoran())) {
                        restoranKey = dataSnapshot1.getKey();
                        restoran = dataSnapshot1.getValue(RestoranModel.class);
                    }
                }

                //Toast.makeText(getApplicationContext(), restoran.getLat(), Toast.LENGTH_SHORT).show();

                onLocationReady();
                //getMenus(restoranKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<String> imageKey = new ArrayList<>();
    private ArrayList<String> imageUri = new ArrayList<>();

    private void getMenus(String restoranKey) {
        databaseReference.child(restoranKey).child("menu").addValueEventListener(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menuModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuModel menuModel = snapshot.getValue(MenuModel.class);
                    //Toast.makeText(DetailRestoranActivity.this, i + " " + menuModel.getNamaMenu(), Toast.LENGTH_SHORT).show();
                    menuModels.add(menuModel);
                    imageKey.add(snapshot.getKey());
                    i++;
                }
                menuAdapter.addItem(menuModels);
                recyclerView.setAdapter(menuAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setupRestaurantMarker() {
        if (mRestaurantMarker == null) mRestaurantMarker = new MarkerOptions();
        mDatabaseReference.child("restoran").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot restaurantsSnapShot : dataSnapshot.getChildren()) {
                    RestoranModel restaurant = restaurantsSnapShot.getValue(RestoranModel.class);
                    if (restaurant != null) {
                        if (restaurant.getNamaRestoran().equals(menu.getNamaRestoran())){
                        double restaurantLat = Double.parseDouble(restaurant.getLat());
                        double restaurantLong = Double.parseDouble(restaurant.getLng());
                        addLocationMarker(mGoogleMap, new LatLng(restaurantLat, restaurantLong), restaurant.getNamaRestoran());
                        mRestaurants.add(restaurant);
                        //printLog("HomeFragment", "setupRestaurantMarker: " + restaurant.getLat() + "," + restaurant.getLong());
                            }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //printLog("HomeFragment", "onCancelled: " + databaseError.getMessage());
            }
        });

    }

    private void addLocationMarker(GoogleMap mapObject, LatLng location, String title) {
        if (mapObject != null) {
            mapObject.addMarker(new MarkerOptions().position(location).title(title));
            mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        mGoogleMap = googleMap;
//        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(DetailRestoranActivity.this, "xc", Toast.LENGTH_SHORT).show();
//                AlertDialog.Builder builderSingle = new AlertDialog.Builder(DetailRestoranActivity.this);
//                builderSingle.setIcon(R.drawable.common_google_signin_btn_icon_dark);
//                builderSingle.setTitle("Pilih menu lainnya:-");
//
//                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DetailRestoranActivity.this, android.R.layout.select_dialog_singlechoice);
//                for (int i = 0; i < menuModels.size(); i++) {
//                    arrayAdapter.add(menuModels.get(i).getNamaMenu());
//                }
//
//                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String strName = arrayAdapter.getItem(which);
//                        AlertDialog.Builder builderInner = new AlertDialog.Builder(DetailRestoranActivity.this);
//                        builderInner.setMessage(strName);
//                        builderInner.setTitle("Pilihanmu adalah");
//                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builderInner.show();
//                    }
//                });
//                builderSingle.show();
//                return false;
//            }
//        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    public void onLocationReady() {
        LatLng loc = new LatLng(Double.parseDouble(restoran.getLat()), Double.parseDouble(restoran.getLng()));

        addLocationMarker(mGoogleMap, loc, "Current Location");
        mLocationMarker.position(loc);
        addCameraToMap(mGoogleMap, loc);
        setupRestaurantMarker();
    }

    public static void addCameraToMap(GoogleMap googleMap, LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(4).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
