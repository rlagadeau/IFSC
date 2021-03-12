package com.example.ifsc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText ifscCodeEdt;
    private TextView bankDetailsTV;

    String ifscCode;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ifscCodeEdt = findViewById(R.id.code);
        Button getBankDetailsBtn = findViewById(R.id.generate);
        bankDetailsTV = findViewById(R.id.results);

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        getBankDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifscCode = ifscCodeEdt.getText().toString();

                if (TextUtils.isEmpty(ifscCode)) {
                    Toast.makeText(MainActivity.this, "Please enter valid IFSC code", Toast.LENGTH_SHORT).show();
                } else {
                    getDataFromIFSCCode(ifscCode);
                }
            }
        });
    }

    private void getDataFromIFSCCode(String ifscCode) {

        mRequestQueue.getCache().clear();

        String url = "http://api.techm.co.in/api/v1/ifsc/" + ifscCode;

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        @SuppressLint("SetTextI18n") JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
                if (response.getString("status").equals("failed")) {
                    bankDetailsTV.setText("Invalid IFSC Code");
                } else {
                    JSONObject dataObj = response.getJSONObject("data");
                    String state = dataObj.optString("STATE");
                    String bankName = dataObj.optString("BANK");
                    String branch = dataObj.optString("BRANCH");
                    String address = dataObj.optString("ADDRESS");
                    String contact = dataObj.optString("CONTACT");
                    String micrcode = dataObj.optString("MICRCODE");
                    String city = dataObj.optString("CITY");

                    bankDetailsTV.setText("Bank Name : " + bankName + "\nBranch : " + branch + "\nAddress : " + address + "\nMICR Code : " + micrcode + "\nCity : " + city + "\nState : " + state + "\nContact : " + contact);
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                bankDetailsTV.setText("Invalid IFSC Code");
            }
        }, error -> bankDetailsTV.setText(error.getMessage()));
        queue.add(objectRequest);
    }
}