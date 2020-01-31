package com.dax.debarauamea;

import android.content.Intent;
import android.os.Bundle;

import com.dax.debarauamea.BarcodeScanner.FullScannerActivity;
import com.dax.debarauamea.Objects.DTOProducts;
import com.dax.debarauamea.Objects.DTOProductsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainMenuActivity extends AppCompatActivity {

    Realm realm;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Realm.init(MainMenuActivity.this);
        realm = Realm.getDefaultInstance();

        AppCompatImageView settings = findViewById(R.id.Settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.AddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        // Get a Realm instance for this thread


        // Query Realm for all dogs younger than 2 years old
//        final RealmResults<DTOProducts> Products = realm.where(DTOProducts.class).findAll();
//        final List<DTOProducts> list = new ArrayList<>(Products);
//
////        DTOProducts product1 = new DTOProducts();
////        product1.BARCODE="3123131";
////        product1.NAME="Seminte";
////        product1.HAS_EXPIRATION_DATE=true;
////        product1.EXPIRATION_DATE = new Date(2019,1,1);
////        product1.QUANTITY=1;
//////        Calendar currentdate = Calendar.getInstance();
//////        currentdate.set(2019,1,1);
//////        product1.EXPIRATION_DATE = currentdate.getTime();
////        list.add(product1);
////        DTOProducts product2 = new DTOProducts();
////        product2.BARCODE="8793239";
////        product2.NAME="Argon";
////        product2.HAS_EXPIRATION_DATE=false;
////        product2.EXPIRATION_DATE = null;
////        product2.QUANTITY=2;
////        list.add(product2);
//
        listView = findViewById(R.id.ListViewProducts);
//        listView.setAdapter( new DTOProductsAdapter(MainMenuActivity.this,list));

        SearchView searchView = findViewById(R.id.SearchViewProducts);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query))
                {
                    RefreshList();
                }else{

                   FilterList(query);

//                    List<DTOProducts> listnew = new ArrayList<>();
//                    for (DTOProducts product:list
//                         ) {
//                        if (product.NAME.toUpperCase().contains(query.toUpperCase()))
//                        {
//                            listnew.add(product);
//                        }
//                    }
//                    listView.setAdapter( new DTOProductsAdapter(MainMenuActivity.this,listnew));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //DAX not implemented for now
                if (TextUtils.isEmpty(newText))
                {
                    RefreshList();
                }else{
                    FilterList(newText);
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
    }

    private void RefreshList()
    {
        RealmResults<DTOProducts> Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",true).sort("EXPIRATION_DATE", Sort.ASCENDING).findAll();
        List<DTOProducts> list = realm.copyFromRealm(Products);
        Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",false).sort("NAME", Sort.ASCENDING).findAll();
        list.addAll(realm.copyFromRealm(Products));
        listView.setAdapter( new DTOProductsAdapter(MainMenuActivity.this,list));
    }

    private void FilterList(String query)
    {
        RealmResults<DTOProducts> Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",true).contains("NAME",query.toUpperCase()).sort("EXPIRATION_DATE", Sort.ASCENDING).findAll();
        List<DTOProducts> list = realm.copyFromRealm(Products);
        Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",false).contains("NAME",query.toUpperCase()).sort("NAME", Sort.ASCENDING).findAll();
        list.addAll(realm.copyFromRealm(Products));
        listView.setAdapter( new DTOProductsAdapter(MainMenuActivity.this,list));
    }
}
