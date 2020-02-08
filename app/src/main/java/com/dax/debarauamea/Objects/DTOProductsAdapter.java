package com.dax.debarauamea.Objects;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dax.debarauamea.EditProductActivity;
import com.dax.debarauamea.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DTOProductsAdapter extends ArrayAdapter<DTOProducts> {

    public DTOProductsAdapter(Context context, List<DTOProducts> products) {

        super(context, 0, products);
    }
    @Override
    @NonNull
    public View getView(int position, View convertView,@NonNull final ViewGroup parent) {

        // Get the data item for this position

        final DTOProducts product = getItem(position);

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

        convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.icons));
        ValueProduct.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));
        LabelProduct.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));
        ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));
        LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));
        ValueQuantity.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));
        LabelQuantity.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text));

        //region Layout color based on expiration date

        if (product!=null)
        {
            if (product.HAS_EXPIRATION_DATE)
            {
                if (Calendar.getInstance().getTime().compareTo(product.EXPIRATION_DATE)>=0)
                {
                    convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.RED));

                    ValueProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    LabelProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    ValueQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    LabelQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));

                }else{
                    if (getDaysDifference(Calendar.getInstance().getTime(),product.EXPIRATION_DATE)<=2)
                    {
                        convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ORANGE));
                        ValueProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        ValueQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    }else if (getDaysDifference(Calendar.getInstance().getTime(),product.EXPIRATION_DATE)<=7)
                    {
                        convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.YELLOW));
                        ValueProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        ValueQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                        LabelQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
                    }else{
                        ValueProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                        LabelProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                        ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                        LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                        ValueQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                        LabelQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                    }
                }
            }else {
                ValueProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                LabelProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                ValueExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                LabelExpirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                ValueQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                LabelQuantity.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
            }

            //endregion

            //region Data Load

            ValueProduct.setText(product.NAME);
            ValueQuantity.setText(String.format(Locale.US,"%s",Float.toString(product.QUANTITY)));
            if (product.HAS_EXPIRATION_DATE)
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
        }
        return convertView;

    }



    private int getDaysDifference(Date fromDate, Date toDate)
    {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }


}
