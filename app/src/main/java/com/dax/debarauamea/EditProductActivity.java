package com.dax.debarauamea;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class EditProductActivity extends AppCompatActivity {
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (101) : {
                if (resultCode == Activity.RESULT_OK) {
                    String returnValue = data.getStringExtra("BARCODE");
                    ValueBarcode.setText(returnValue);
                }
                break;
            }
        }
    }

    AppCompatEditText ValueBarcode;
    DTOProducts EditedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //region Layout Elements Assignment

        EditedProduct = (DTOProducts) getIntent().getSerializableExtra("PRODUCT");

        ValueBarcode = findViewById(R.id.ValueBarcode);
        AppCompatTextView LabelBarcode = findViewById(R.id.LabelBarcode);
        AppCompatButton ButtonBarcode = findViewById(R.id.ButtonScanBarcode);

        final AppCompatEditText ValueName = findViewById(R.id.ValueName);
        AppCompatTextView LabelName = findViewById(R.id.LabelName);

        final AppCompatCheckBox ValueHasExpirationDate = findViewById(R.id.CheckBoxHasExpirationDate);
        AppCompatTextView LabelHasExpirationDate = findViewById(R.id.LabelHasExpirationDate);

        final AppCompatEditText ValueExpirationDate = findViewById(R.id.ValueExpirationDate);
        final AppCompatTextView LabelExpirationDate = findViewById(R.id.LabelExpirationDate);

        final AppCompatEditText ValueQuantity = findViewById(R.id.ValueQuantity);
        AppCompatTextView LabelQuantity = findViewById(R.id.LabelQuantity);

        AppCompatButton ButtonEditProduct = findViewById(R.id.ButtonEditProduct);

        //endregion

        ButtonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = ActivityCompat.checkSelfPermission(EditProductActivity.this, Manifest.permission.CAMERA);
                if (status != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProductActivity.this, new String[]{Manifest.permission.CAMERA}, 4322);
                }else  {
                    Intent intent = new Intent(EditProductActivity.this, FullScannerActivity.class);
                    startActivityForResult(intent,101);
                }
            }
        });

        ValueHasExpirationDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==false)
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
                new DatePickerDialog(EditProductActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ButtonEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DTOProducts dto = new DTOProducts();
                dto.BARCODE=ValueBarcode.getText().toString();
                dto.QUANTITY=Float.parseFloat(ValueQuantity.getText().toString());
                dto.NAME=ValueName.getText().toString().toUpperCase();
                dto.HAS_EXPIRATION_DATE=ValueHasExpirationDate.isChecked();

                if (TextUtils.isEmpty(ValueExpirationDate.getText().toString()))
                {
                    dto.EXPIRATION_DATE=null;
                }else{
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = format.parse(ValueExpirationDate.getText().toString());
                        dto.EXPIRATION_DATE=date;
//                    System.out.println(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }



                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        DTOProducts OldItem = realm.where(DTOProducts.class)
                        .equalTo("BARCODE",EditedProduct.BARCODE)
                        .equalTo("QUANTITY",EditedProduct.QUANTITY)
                        .equalTo("HAS_EXPIRATION_DATE",EditedProduct.HAS_EXPIRATION_DATE)
                        .equalTo("NAME",EditedProduct.NAME)
                        .equalTo("EXPIRATION_DATE",EditedProduct.EXPIRATION_DATE)
                        .findFirst();
                        OldItem.HAS_EXPIRATION_DATE=dto.HAS_EXPIRATION_DATE;
                        OldItem.EXPIRATION_DATE = dto.EXPIRATION_DATE;
                        OldItem.NAME=dto.NAME;
                        OldItem.QUANTITY=dto.QUANTITY;
                        OldItem.BARCODE=dto.BARCODE;
                        onBackPressed();
                    }
                });

//                realm.beginTransaction();

//                final DTOProducts managedDog = realm.copyToRealm(dto); // Persist unmanaged objects
//                realm.commitTransaction();


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

        //region LoadData

        ValueBarcode.setText(EditedProduct.BARCODE);
        ValueName.setText(EditedProduct.NAME);
        ValueHasExpirationDate.setChecked(EditedProduct.HAS_EXPIRATION_DATE);
        if (EditedProduct.HAS_EXPIRATION_DATE==true)
        {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            ValueExpirationDate.setText(sdf.format(EditedProduct.EXPIRATION_DATE));
        }
        ValueQuantity.setText(Float.toString(EditedProduct.QUANTITY));

        //endregion
    }
}
