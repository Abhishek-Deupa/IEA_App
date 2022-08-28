package com.example.ieaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.PaymentResultListener;

public class payment extends AppCompatActivity implements PaymentResultListener {

    private AppCompatButton paynow;
    private String memberfees;


    String fullname, email, phoneNo, companyName, Department, Turnover,Gstno,renew;

    FirebaseDatabase memberDirectoryRoot;
    DatabaseReference memberDirectoryRef;
    String paymentReceiverName;
    EditText gstNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        EditText amount_payingnow = findViewById(R.id.amount_paynow);
        TextView amount_payinglater = findViewById(R.id.amount_paylater);
        gstNo = findViewById(R.id.Gstno);


        Intent intent = getIntent();
        renew = intent.getStringExtra("renewal");
        email = intent.getStringExtra("email");
        memberfees = intent.getStringExtra("memberfee");
        fullname = intent.getStringExtra("name");
        companyName = intent.getStringExtra("cname");
        if(renew.equals("0")){
            phoneNo = intent.getStringExtra("phoneno");
            Department = intent.getStringExtra("department");
            Turnover = intent.getStringExtra("annual_turn");
        }


        Log.d("Useremail", "onCreate: "+email.toLowerCase());

        String[] payment = getResources().getStringArray(R.array.paymethod);
        ArrayAdapter<String> arrayAdapterPaymethod = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, payment);
        AutoCompleteTextView autoCompleteTextViewPayment = findViewById(R.id.autocomplete_payment);
        autoCompleteTextViewPayment.setAdapter(arrayAdapterPaymethod);

        AppCompatButton PaymentBackButton = findViewById(R.id.payment_back_button);
        AutoCompleteTextView autoCompletePayment = findViewById(R.id.autocomplete_payment);

        PaymentBackButton.setOnClickListener(view -> {
            finish();
        });


        TextView memberprice = findViewById(R.id.member_price);
        memberprice.setText(memberfees);
        paynow = findViewById(R.id.paynow_btn);
        String finalAmount = feeConverter(memberfees);

        Thread amount_calculation = new Thread() {

            @Override
            public void run() {
                while (!isInterrupted()) {

                    try {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!amount_payingnow.getText().toString().isEmpty()) {
                                    Long paying_now = Long.parseLong(amount_payingnow.getText().toString());
                                    Long final_amount_integer = Long.parseLong(finalAmount);
                                    Long paying_later_integer = final_amount_integer - paying_now;
                                    amount_payinglater.setText(paying_later_integer.toString());
                                } else {
                                    amount_payinglater.setText("0");
                                }


                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        amount_calculation.start();

        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(payment.this, payment_proof.class);

                TextView amountleft = findViewById(R.id.amount_paylater);
                intent.putExtra("email", email.toLowerCase());
                intent.putExtra("renewal", renew);
                intent.putExtra("memberfee", memberfees);
                intent.putExtra("costleft", amountleft.getText().toString());
                intent.putExtra("paymentMethod", autoCompletePayment.getText().toString());
                intent.putExtra("GstNo",gstNo.getText().toString());
                intent.putExtra("name", fullname);
                intent.putExtra("cname", companyName);
                if(renew.equals("0")){
                    intent.putExtra("phoneno", phoneNo);
                    intent.putExtra("department", Department);
                    intent.putExtra("annual_turn", Turnover);
                }



                if (TextUtils.isEmpty(amount_payingnow.getText().toString())) {
                    amount_payingnow.setError("Amount cannot be empty!");
                    amount_payingnow.requestFocus();
                } else if (Long.parseLong(amount_payingnow.getText().toString()) < 3500) {
                    amount_payingnow.setError("Enter More Than 3500");
                    amount_payingnow.requestFocus();
                } else if(autoCompletePayment.getText().toString().equals("Payment Method")) {
                    autoCompletePayment.setError("Select a payment method");
                    autoCompletePayment.requestFocus();
                } else{
                    startActivity(intent);
                }
            }
        });
        //Payment

//        paynow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // on below line we are getting
//                // amount that is entered by user.
//                String samount="100";
//
//                // rounding off the amount.
//                int amounts = Math.round(Float.parseFloat(samount) * 100);
//
//                // initialize Razorpay account.
//                Checkout checkout = new Checkout();
//
//                // set your id as below
//                checkout.setKeyID("rzp_test_USimbGClz1VOxE");
//
//                // set image
//                checkout.setImage(R.drawable.members);
//
//                // initialize json object
//                JSONObject object = new JSONObject();
//                try {
//                    // to put name
//                    object.put("name", "IEA");
//
//                    // put description
//                    object.put("description", "Membership fees");
//
//                    // to set theme color
//                    object.put("theme.color", "");
//
//                    // put the currency
//                    object.put("currency", "INR");
//
//                    // put amount
//                    object.put("amount", amounts);
//
//                    // put mobile number
//                    object.put("prefill.contact", "9284064503");
//
//                    // put email
//                    object.put("prefill.email", "chaitanyamunje@gmail.com");
//
//                    // open razorpay to checkout activity
//                    checkout.open(payment.this, object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    public String feeConverter(String s) {
        if (s.equals("Rs. 3,658/Yearly *including gst*")) {
            return "3658";
        }
        if (s.equals("Rs. 6,018/Yearly *including gst*")) {
            return "6018";
        }
        if (s.equals("Rs. 12,980/Yearly *including gst*")) {
            return "12980";
        }
        return "0";
    }


    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment is unsucessful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(this, "Payment is sucessful", Toast.LENGTH_SHORT).show();
        memberDirectoryRoot = FirebaseDatabase.getInstance();
        memberDirectoryRef = memberDirectoryRoot.getReference("Members Directory");

        UserRegistrationHelperClass userRegistrationHelperClass = new UserRegistrationHelperClass();

        memberDirectoryRef.child(email.replaceAll("\\.", "%7")).setValue(userRegistrationHelperClass);
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] payment = getResources().getStringArray(R.array.paymethod);
        ArrayAdapter<String> arrayAdapterPaymethod = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, payment);
        AutoCompleteTextView autoCompleteTextViewPayment = findViewById(R.id.autocomplete_payment);
        autoCompleteTextViewPayment.setAdapter(arrayAdapterPaymethod);

    }

}