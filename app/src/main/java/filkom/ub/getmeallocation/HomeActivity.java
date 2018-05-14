package filkom.ub.getmeallocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.ResourceBundle;

import filkom.ub.getmeallocation.adapter.MenuAdapter;
import filkom.ub.getmeallocation.adapter.RestoranAdapter;
import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

public class HomeActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private EditText et_cari;
    private Button buttonLogout, buttonTambah, buttonCari;

    private DatabaseReference databaseRestoran;
    private Query cariHarga;

    private RecyclerView recyclerView;
    private RestoranAdapter restoranAdapter;
    private MenuAdapter menuAdapter;

    private ArrayList<String> imageKey = new ArrayList<>();
    private ArrayList<MenuModel> menus = new ArrayList<>();
    private ArrayList<MenuModel> carimenu = new ArrayList<>();

    ArrayList<String> restoranKey = new ArrayList<>();
    ArrayList<RestoranModel> restorans = new ArrayList<>();

    public static final String TAG = "getMeal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseRestoran = FirebaseDatabase.getInstance().getReference("restoran");

        //initializing views
        et_cari = (EditText) findViewById(R.id.EditCari);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonTambah = (Button) findViewById(R.id.buttonTambah);
        buttonCari = (Button) findViewById(R.id.buttonCari);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restoranAdapter = new RestoranAdapter(this);
        menuAdapter = new MenuAdapter(this);

        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+user.getDisplayName()+" "+user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });


        buttonTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TambahActivity.class));
            }
        });

        buttonCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_cari.getText().toString().equals("")){
                    getAllRestoran();
                }else{
                    Cari(et_cari.getText().toString());
                }
            }
        });


        //Tampil Menu
        getAllRestoran();

    }

    private void Cari(String harga){
        carimenu.clear();
        for(int i=0; i<menus.size(); i++){
            if(menus.get(i).getHarga().equals(harga) ) {
                String nama = menus.get(i).getNamaMenu();
                String hargaa = menus.get(i).getHarga();
                String date = menus.get(i).getDate();
                String image = menus.get(i).getImageUrl();
                MenuModel mm = new MenuModel(nama, hargaa, date, image);
                carimenu.add(mm);
            }
        }
        menuAdapter.addItem(carimenu);
        recyclerView.setAdapter(menuAdapter);
    }



    private void getAllRestoran() {
        databaseRestoran.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restoranKey.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RestoranModel restoranModel = dataSnapshot1.getValue(RestoranModel.class);
                    restorans.add(restoranModel);
                    //Log.d(TAG, "onDataChange: "+dataSnapshot1.getKey());
                    restoranKey.add(dataSnapshot1.getKey());
                }
                restoranAdapter.addItem(restorans);
                getAllMenu();
                //Log.d(TAG, "onDataChange: "+restoranKey.size());
                    //recyclerView.setAdapter(restoranAdapter);

                    //getAllMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAllMenu() {
        menus.clear();
        imageKey.clear();
        for (int i = 0; i < restoranKey.size(); i++) {
            databaseRestoran.child(restoranKey.get(i)).child("menu").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MenuModel menuModel = snapshot.getValue(MenuModel.class);
                        menus.add(menuModel);
                        Log.d(TAG, "onDataChange: "+snapshot.getKey());
                        imageKey.add(snapshot.getKey());
                    }
                    menuAdapter.addItem(menus);
                    recyclerView.setAdapter(menuAdapter);
                    Log.d(TAG, "onDataChange: "+imageKey.get(0));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
