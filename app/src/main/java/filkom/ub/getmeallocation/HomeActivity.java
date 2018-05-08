package filkom.ub.getmeallocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.ResourceBundle;

import filkom.ub.getmeallocation.adapter.RestoranAdapter;
import filkom.ub.getmeallocation.model.RestoranModel;

public class HomeActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout, buttonTambah;

    private DatabaseReference databaseRestoran;

    private RecyclerView recyclerView;
    private RestoranAdapter restoranAdapter;
    private ArrayList<RestoranModel> restoranModels;

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
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonTambah = (Button) findViewById(R.id.buttonTambah);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restoranAdapter = new RestoranAdapter(this);

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

        getAllRestoran();
    }

    private void getAllRestoran() {
        databaseRestoran.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<RestoranModel> restorans = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    RestoranModel restoranModel = dataSnapshot1.getValue(RestoranModel.class);
                    restorans.add(restoranModel);
                }
                restoranAdapter.addItem(restorans);
                    recyclerView.setAdapter(restoranAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
