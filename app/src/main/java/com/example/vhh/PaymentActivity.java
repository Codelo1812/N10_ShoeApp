package com.example.vhh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mservice.momo.MoMoParameterNamePayment;
import com.mservice.momo.MoMoWallet;
import com.mservice.momo.PartnerResponse;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private EditText editTextPhone;
    private Spinner spinnerPaymentMethod;
    private Button buttonPay;
    private LinearLayout layoutMomoPayment;
    private Button buttonConfirmMomoPayment;
    private TextView textViewSuccessMessage;

    private static final int MOMO_PAYMENT_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editSĐT);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        buttonPay = findViewById(R.id.buttonPay);
        layoutMomoPayment = findViewById(R.id.layoutMomoPayment);
        buttonConfirmMomoPayment = findViewById(R.id.buttonConfirmMomoPayment);
        textViewSuccessMessage = findViewById(R.id.textViewSuccessMessage);

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String address = editTextAddress.getText().toString();
                String phone = editTextPhone.getText().toString();
                String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (paymentMethod.equals("Tiền mặt")) {
                    showSuccessMessage();
                } else if (paymentMethod.equals("Chuyển khoản Momo")) {
                    startMomoPayment();
                }
            }
        });

        buttonConfirmMomoPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMomoPayment.setVisibility(View.GONE);
                showSuccessMessage();
            }
        });
    }

    private void startMomoPayment() {
        // Configure parameters for the Momo payment
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(MoMoParameterNamePayment.MERCHANT_NAME, "Your Merchant Name");
        eventValue.put(MoMoParameterNamePayment.MERCHANT_CODE, "Your Merchant Code");
        eventValue.put(MoMoParameterNamePayment.AMOUNT, 10000); // Amount in VND
        eventValue.put(MoMoParameterNamePayment.DESCRIPTION, "Payment for order");

        // Start Momo payment
        Intent intent = new Intent(PaymentActivity.this, MoMoWallet.class);
        intent.putExtra(MoMoParameterNamePayment.REQUEST_TYPE, MoMoParameterNamePayment.PAYMENT_REQUEST);
        intent.putExtra(MoMoParameterNamePayment.EXTRA_DATA, eventValue);
        startActivityForResult(intent, MOMO_PAYMENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOMO_PAYMENT_REQUEST_CODE) {
            if (resultCode == MoMoParameterNamePayment.RESULT_SUCCESS) {
                PartnerResponse response = data.getParcelableExtra(MoMoParameterNamePayment.EXTRA_RESPONSE);
                if (response != null && response.getStatus() == 0) {
                    showSuccessMessage();
                } else {
                    Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSuccessMessage() {
        textViewSuccessMessage.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}