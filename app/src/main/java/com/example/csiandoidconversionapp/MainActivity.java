package com.example.csiandoidconversionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.BreakIterator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, ConversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, ConvertToValue, conversionValue;
    String[] country = {"AFN", "ALL", "DZD","USD","ADA","XCD","ARS","AMD","AWG","AUD","AZN","BSD","BHD","BDT","BBD","BYN","BZD","XDF","BMD","INR","BTN"};
    private BreakIterator conversionRateText;
    private String convertToValue;
    private String convertFrom;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        convertButton = findViewById(R.id.conversionButton);
        ConversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amountToConvertValueText);

        arrayList = new ArrayList<>();
        for(String i : country){
            arrayList.add(i);
        }
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.getWindow().setLayout(650,800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edit_text);
                EditText listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                EditText listView1 = listView;

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnClickListener((View.OnClickListener) (parent, view, position, id) -> {
                    convertFromDropdownTextView.setText(adapter.getItem(position));
                    fromDialog.dismiss();
                    convertFromValue = adapter.getItem(position);
                });

            }
        });
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDialog = new Dialog(MainActivity.this);
                toDialog.setContentView(R.layout.to_spinner);
                toDialog.getWindow().setLayout(650, 800);
                toDialog.show();

                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnClickListener((View.OnClickListener) (parent, view, position, id) -> {
                    convertToDropdownTextView.setText(adapter.getItem(position));
                    toDialog.dismiss();
                    convertToValue = adapter.getItem(position);
                });
            }
        });
        convertButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try{
                    Double amountToConvert = Double.valueOf(MainActivity.this.amountToConvert.getText().toString());
                    getConversionRate(convertFromValue, convertToValue, amountToConvert);
                }
                catch (Exception e){

                }
                return false;
            }
        });

    }

    public String getConversionRate(String convertFromValue, String convertTo, Double amountToConvert){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://free.currconv.com/api/v7/convert?q="+convertFrom+" "+convertTo+"&compact=ultra&apikey=22e91lab924eb2aa6f7a4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Double conversionRateValue = round(((Double) jsonObject.get(convertFrom + " " + convertTo)), 2);
                    conversionValue = "" + round((conversionRateValue * amountToConvert), 2);
                    conversionRateText.setText(conversionValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places){
        if(places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value); bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}