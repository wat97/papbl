package filkom.ub.getmeallocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import filkom.ub.getmeallocation.adapter.MenuAdapter;
import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

public class DetailRestoranActivity extends AppCompatActivity {

    private TextView tvNamaRestoran, tvNamaMenu, tvLokasi, tvHarga, tvTanggal;

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    private DatabaseReference databaseReference;
    private RestoranModel restoran;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        databaseReference = FirebaseDatabase.getInstance().getReference("restoran");

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        restoran = (RestoranModel) args.getSerializable("RESTORAN");

        tvNamaMenu = (TextView) findViewById(R.id.tv_nama_menu);
        tvNamaRestoran = (TextView) findViewById(R.id.tv_nama_restoran);
        tvLokasi = (TextView) findViewById(R.id.tv_lokasi);
        tvHarga = (TextView) findViewById(R.id.tv_harga);
        tvTanggal = (TextView) findViewById(R.id.tv_tanggal);

        tvNamaRestoran.setText(restoran.getNamaRestoran());
        tvLokasi.setText(restoran.getLokasi());
//        tvNamaMenu.setText(restoran.getMenuModel().getNamaMenu());
//        tvTanggal.setText(restoran.getMenuModel().getDate());
//        tvHarga.setText(restoran.getMenuModel().getHarga());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(this);

        getSpecificRestoran();
    }

    private void getSpecificRestoran() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            String restoranKey;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RestoranModel restoranModel = dataSnapshot1.getValue(RestoranModel.class);
                    if (restoranModel.getNamaRestoran().equals(restoran.getNamaRestoran())) {
                        restoranKey = dataSnapshot1.getKey();
                        //Toast.makeText(DetailRestoranActivity.this, dataSnapshot1.getKey(), Toast.LENGTH_SHORT).show();
                    }
                }

                getMenus(restoranKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getMenus(String restoranKey) {
        databaseReference.child(restoranKey).child("menu").addValueEventListener(new ValueEventListener() {
            int i = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MenuModel> menuModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuModel menuModel = snapshot.getValue(MenuModel.class);
                    //Toast.makeText(DetailRestoranActivity.this, i + " " + menuModel.getNamaMenu(), Toast.LENGTH_SHORT).show();
                    menuModels.add(menuModel);
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
}
