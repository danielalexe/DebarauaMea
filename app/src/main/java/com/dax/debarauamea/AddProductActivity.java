package com.dax.debarauamea;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.dax.debarauamea.BarcodeScanner.FullScannerActivity;
import com.dax.debarauamea.Objects.DTOProducts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;

public class AddProductActivity extends AppCompatActivity {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                String returnValue = data.getStringExtra("BARCODE");
                ValueBarcode.setText(returnValue);
                Realm realm = Realm.getDefaultInstance();
                DTOProducts OldItem = realm.where(DTOProducts.class)
                        .equalTo("BARCODE", returnValue)
                        .findFirst();
                if (OldItem != null) {
                    ValueName.setText(OldItem.NAME);
                }
            }
        }
    }

    AppCompatEditText ValueBarcode;
    AppCompatEditText ValueName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //region Layout Elements Assignment

        ValueBarcode = findViewById(R.id.ValueBarcode);
//        AppCompatTextView LabelBarcode = findViewById(R.id.LabelBarcode);
        AppCompatButton ButtonBarcode = findViewById(R.id.ButtonScanBarcode);

        ValueName = findViewById(R.id.ValueName);
//        AppCompatTextView LabelName = findViewById(R.id.LabelName);

        final AppCompatCheckBox ValueHasExpirationDate = findViewById(R.id.CheckBoxHasExpirationDate);
//        AppCompatTextView LabelHasExpirationDate = findViewById(R.id.LabelHasExpirationDate);

        final AppCompatEditText ValueExpirationDate = findViewById(R.id.ValueExpirationDate);
        final AppCompatTextView LabelExpirationDate = findViewById(R.id.LabelExpirationDate);

        final AppCompatEditText ValueQuantity = findViewById(R.id.ValueQuantity);
//        AppCompatTextView LabelQuantity = findViewById(R.id.LabelQuantity);

        AppCompatButton ButtonAddProduct = findViewById(R.id.ButtonAddProduct);

        //endregion

        ButtonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = ActivityCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.CAMERA);
                if (status != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.CAMERA}, 4322);
                }else  {
                    Intent intent = new Intent(AddProductActivity.this, FullScannerActivity.class);
                    startActivityForResult(intent,101);
                }
            }
        });

        ValueHasExpirationDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    ValueExpirationDate.setEnabled(false);
                    LabelExpirationDate.setEnabled(false);
                }else{
                    ValueExpirationDate.setEnabled(true);
                    LabelExpirationDate.setEnabled(true);
                }
            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                ValueExpirationDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        ValueExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddProductActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ButtonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DTOProducts dto = new DTOProducts();
                dto.BARCODE= Objects.requireNonNull(ValueBarcode.getText()).toString();
                dto.QUANTITY=Float.parseFloat(Objects.requireNonNull(ValueQuantity.getText()).toString());
                dto.NAME= Objects.requireNonNull(ValueName.getText()).toString().toUpperCase();
                dto.HAS_EXPIRATION_DATE=ValueHasExpirationDate.isChecked();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
                try {
                    dto.EXPIRATION_DATE= format.parse(Objects.requireNonNull(ValueExpirationDate.getText()).toString());
//                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealm(dto); // Persist unmanaged objects
                realm.commitTransaction();
                onBackPressed();

//                // Asynchronously update objects on a background thread
////                realm.executeTransactionAsync(new Realm.Transaction() {
////                    @Override
////                    public void execute(Realm bgRealm) {
////                        // Persist your data in a transaction
////
////                    }
////                }, new Realm.Transaction.OnSuccess() {
////                    @Override
////                    public void onSuccess() {
////                        // Original queries and Realm objects are automatically updated.
////
////                    }
////                });

            }
        });
    }
}
