package com.dax.debarauamea;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.dax.debarauamea.Objects.DTOProducts;
import com.dax.debarauamea.Objects.DTOProductsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.dax.debarauamea.OutcomingNfcManager.MIME_TEXT_PLAIN;

public class MainMenuActivity extends AppCompatActivity {

    Realm realm;
    ListView listView;
    private NfcAdapter nfcAdapter;

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

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

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

    // need to check NfcAdapter for nullability. Null means no NFC support on the device
    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, this.nfcAdapter);
        receiveMessageFromDevice(getIntent());
    }
    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, this.nfcAdapter);
    }

    private void receiveMessageFromDevice(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdefMessage;
            if (parcelables != null) {
                inNdefMessage = (NdefMessage) parcelables[0];
                NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
                NdefRecord ndefRecord_0 = inNdefRecords[0];

                String inMessage = new String(ndefRecord_0.getPayload());
                Type listType = new TypeToken<ArrayList<DTOProducts>>(){}.getType();
                List<DTOProducts> list = new Gson().fromJson(inMessage,listType);

                realm.beginTransaction();
                realm.delete(DTOProducts.class);
                realm.commitTransaction();

                for (DTOProducts product:list)
                {
                    realm.beginTransaction();
                    realm.copyToRealm(product); // Persist unmanaged objects
                    realm.commitTransaction();
                }

                RefreshList();

                new AlertDialog.Builder(MainMenuActivity.this)
                        .setTitle("Data Sync")
                        .setMessage("Data received succesfully")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }}).show();
            }


//            this.tvIncomingMessage.setText(inMessage);
        }
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

    @Override
    protected void onNewIntent(Intent intent) {
        // also reading NFC message from here in case this activity is already started in order
        // not to start another instance of this activity
        super.onNewIntent(intent);
        receiveMessageFromDevice(intent);
    }


    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED

    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well

    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

}
