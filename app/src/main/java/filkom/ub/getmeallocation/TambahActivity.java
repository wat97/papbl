package filkom.ub.getmeallocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import filkom.ub.getmeallocation.model.MenuModel;
import filkom.ub.getmeallocation.model.RestoranModel;

public class TambahActivity extends AppCompatActivity {

    private EditText etNamamenu, etHarga, etDate, etLokasi;
    private AutoCompleteTextView actvNamaRestoran;
    private Button btnSubmitMenu;

    private ArrayList<RestoranModel> namaRestoran = new ArrayList<RestoranModel>();
    private String[] arrayNamaRestoran;

    private DatabaseReference databaseRestoran;
    private DatabaseReference databaseMenu;

    private ArrayAdapter<String> arrayAdapter;

    public static final String TAG = "tambahM";

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
        btnSubmitMenu = (Button) findViewById(R.id.button_submit_menu);

        btnSubmitMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMenu();
            }
        });

        etDate.setText(setCurrentDate());
        getAllRestoran();
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
                            snapshot.getRef().child("menu").child(databaseRestoran.push().getKey()).setValue(menuModel);
                            Toast.makeText(TambahActivity.this, snapshot.child("namaRestoran").getValue(String.class), Toast.LENGTH_SHORT).show();
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
            databaseRestoran.child(key).child("menu").child(databaseRestoran.push().getKey()).setValue(menuModel);
        }

    }

    private String setCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.toString();
    }
}
