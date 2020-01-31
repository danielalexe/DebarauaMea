package com.dax.debarauamea.Objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dax.debarauamea.AddProductActivity;
import com.dax.debarauamea.EditProductActivity;
import com.dax.debarauamea.MainMenuActivity;
import com.dax.debarauamea.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DTOProductsAdapter extends ArrayAdapter {

    public DTOProductsAdapter(Context context, List<DTOProducts> products) {

        super(context, 0, products);
    }
    @Override

    public View getView(int position, View convertView, final ViewGroup parent) {

        // Get the data item for this position

        final DTOProducts product = (DTOProducts) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.products_list_element, parent, false);
        }

        //region Layout Elements Assignment

        TextView ValueProduct = convertView.findViewById(R.id.ValueProduct);
        TextView LabelProduct = convertView.findViewById(R.id.LabelProduct);
        TextView ValueExpirationDate = convertView.findViewById(R.id.ValueExpirationDate);
        TextView LabelExpirationDate = convertView.findViewById(R.id.LabelExpirationDate);
        TextView ValueQuantity = convertView.findViewById(R.id.ValueQuantity);
        TextView LabelQuantity = convertView.findViewById(R.id.LabelQuantity);


        //endregion

        //region Layout color based on expiration date

        if (product.HAS_EXPIRATION_DATE==true)
        {
            if (Calendar.getInstance().getTime().compareTo(product.EXPIRATION_DATE)>=0)
            {
                convertView.setBackgroundColor(getContext().getResources().getColor(R.color.RED));

                ValueProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                LabelProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                ValueExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                LabelExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                ValueQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));
                LabelQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));

            }else{
                if (getDaysDifference(Calendar.getInstance().getTime(),product.EXPIRATION_DATE)<=2)
                {
                    convertView.setBackgroundColor(getContext().getResources().getColor(R.color.ORANGE));
                    ValueProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                    ValueExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                    ValueQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));
                }else if (getDaysDifference(Calendar.getInstance().getTime(),product.EXPIRATION_DATE)<=7)
                {
                    convertView.setBackgroundColor(getContext().getResources().getColor(R.color.YELLOW));
                    ValueProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelProduct.setTextColor(getContext().getResources().getColor(R.color.icons));
                    ValueExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelExpirationDate.setTextColor(getContext().getResources().getColor(R.color.icons));
                    ValueQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));
                    LabelQuantity.setTextColor(getContext().getResources().getColor(R.color.icons));
                }else{
                    ValueProduct.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                    LabelProduct.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                    ValueExpirationDate.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                    LabelExpirationDate.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                    ValueQuantity.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                    LabelQuantity.setTextColor(getContext().getResources().getColor(R.color.primary_text));
                }
            }
        }else {
            ValueProduct.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            LabelProduct.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            ValueExpirationDate.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            LabelExpirationDate.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            ValueQuantity.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            LabelQuantity.setTextColor(getContext().getResources().getColor(R.color.primary_text));
        }

        //endregion

        //region Data Load

        ValueProduct.setText(product.NAME);
        ValueQuantity.setText(Float.toString(product.QUANTITY));
        if (product.HAS_EXPIRATION_DATE==true)
        {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

            ValueExpirationDate.setText(sdf.format(product.EXPIRATION_DATE));
        }else{
            ValueExpirationDate.setText("N/A");
        }

        //endregion

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra("PRODUCT",product);
                getContext().startActivity(intent);
            }
        });



        return convertView;

    }



    public int getDaysDifference(Date fromDate,Date toDate)
    {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }


}
