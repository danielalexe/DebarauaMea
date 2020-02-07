package com.dax.debarauamea;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.dax.debarauamea.Objects.DTOProducts;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //region Layout Elements Assignment

        AppCompatTextView LabelNotifications = findViewById(R.id.LabelNotifications);
        SwitchCompat ValueNotifications = findViewById(R.id.ValueNotifications);

        AppCompatButton ClearData = findViewById(R.id.ClearData);
        AppCompatButton SyncData = findViewById(R.id.SyncData);

        //endregion

        SharedPreferences sharedPref = GetSharedPreferences(SettingsActivity.this);
        ValueNotifications.setChecked(sharedPref.getBoolean("Notifications",false));

        ValueNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true)
                {
                    SharedPreferences sharedPref = GetSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Notifications",true);
                    editor.commit();

                    PeriodicWorkRequest saveRequest =
                            new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                                    .addTag("Debara")
                                    .build();
                    WorkManager.getInstance(SettingsActivity.this).enqueue(saveRequest);
//                    showNotification(SettingsActivity.this,"Expira","Produs Expira",null);

                }else{
                    SharedPreferences sharedPref = GetSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Notifications",false);
                    editor.commit();

                    WorkManager.getInstance(SettingsActivity.this).cancelAllWorkByTag("Debara");
                }
            }
        });

        ClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Clear All Data")
                        .setMessage("Do you really want to delete all products data?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.delete(DTOProducts.class);
                                realm.commitTransaction();
                                onBackPressed();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        SyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status = ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.NFC);
                if (status != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.NFC}, 4322);
                }else {
                    Intent intent = new Intent(SettingsActivity.this, SyncNfcActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private SharedPreferences GetSharedPreferences(Activity activity)
    {
        return activity.getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
    }
}
