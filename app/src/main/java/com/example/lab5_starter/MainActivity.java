package com.example.lab5_starter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;
    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private ArrayAdapter<City> cityArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference citiesref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);


        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList);
        cityListView.setAdapter(cityArrayAdapter);

        db = FirebaseFirestore.getInstance();
        citiesref = db.collection("cities");

        listenForCities();
        setupDeleteOnLongPress();

        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(), "Add City");
        });

        // 6) Tap: open details dialog
        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = cityArrayAdapter.getItem(i);
            CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(), "City Details");
        });
    }

    @Override
    public void updateCity(City city, String title, String year) {
//        city.setName(title);
//        city.setProvince(year);
//        cityArrayAdapter.notifyDataSetChanged();

        // Updating the database using delete + addition
        if (city == null || city.getDocid() == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("name", title);
        data.put("province", year);

        citiesref.document(city.getDocid()).set(data)
                .addOnFailureListener(e -> Log.e("Firestore", "Update failed", e));
    }

    @Override
    public void addCity(City city){
//        cityArrayList.add(city);
//        cityArrayAdapter.notifyDataSetChanged();
        Map<String, Object> data = new HashMap<>();
        data.put("name", city.getName());
        data.put("province", city.getProvince());

        citiesref.document().set(data)
                .addOnFailureListener(e -> Log.e("firestore", "failed to add", e));

    }

    public void addDummyData(){
        City m1 = new City("Edmonton", "AB");
        City m2 = new City("Vancouver", "BC");
        cityArrayList.add(m1);
        cityArrayList.add(m2);
        cityArrayAdapter.notifyDataSetChanged();
    }

    private void listenForCities() {
        citiesref.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", "Listen failed", error);
                return;
            }
            if (value == null) return;

            cityArrayList.clear();
            for (QueryDocumentSnapshot doc : value) {
                String name = doc.getString("name");
                String province = doc.getString("province");

                City c = new City(doc.getId(), name, province);
                cityArrayList.add(c);
            }
            cityArrayAdapter.notifyDataSetChanged();
        });
    }

    private void setupDeleteOnLongPress() {
        cityListView.setOnItemLongClickListener((parent, view, position, id) -> {
            City selected = cityArrayAdapter.getItem(position);
            if (selected == null) return true;

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete city?")
                    .setMessage("Delete " + selected.getName() + " (" + selected.getProvince() + ")?")
                    .setPositiveButton("Delete", (d, which) -> deleteCity(selected))
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
    }

    private void deleteCity(City city) {
        String docId = city.getDocid();
        if (docId == null || docId.isEmpty()) return;

        citiesref.document(docId).delete()
                .addOnFailureListener(e -> Log.e("Firestore", "Delete failed", e));
    }
}
