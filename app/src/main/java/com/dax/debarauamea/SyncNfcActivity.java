package com.dax.debarauamea;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dax.debarauamea.Objects.DTOProducts;
import com.google.gson.Gson;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SyncNfcActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

//    private TextView tvOutcomingMessage;
//    private EditText etOutcomingMessage;
//    private Button btnSetOutcomingMessage;

    private NfcAdapter nfcAdapter;
    private String ProductsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_nfc);

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        initViews();

        // encapsulate sending logic in a separate class
        OutcomingNfcManager outcomingNfccallback = new OutcomingNfcManager(this);
        this.nfcAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.nfcAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);
    }

    private void initViews() {
//        this.tvOutcomingMessage = findViewById(R.id.tv_out_message);
//        this.etOutcomingMessage = findViewById(R.id.et_message);
//        this.btnSetOutcomingMessage = findViewById(R.id.btn_set_out_message);
//        this.btnSetOutcomingMessage.setOnClickListener((v) -> setOutGoingMessage());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<DTOProducts> Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",true).sort("EXPIRATION_DATE", Sort.ASCENDING).findAll();
        List<DTOProducts> list = realm.copyFromRealm(Products);
        Products = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",false).sort("NAME", Sort.ASCENDING).findAll();
        list.addAll(realm.copyFromRealm(Products));

        ProductsJson = new Gson().toJson(list);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

//    private void setOutGoingMessage() {
//        String outMessage = this.etOutcomingMessage.getText().toString();
//        this.tvOutcomingMessage.setText(outMessage);
//    }

    @Override
    public String getOutcomingMessage() {
        return this.ProductsJson;
    }

    @Override
    public void signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SyncNfcActivity.this)
                        .setTitle("Data Sync")
                        .setMessage("Syncronization Succesfull")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onBackPressed();
                            }}).show();
//                Toast.makeText(SyncNfcActivity.this, "Product Transfer Completed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

