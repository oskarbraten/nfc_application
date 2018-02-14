package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private TextView balanceLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences appPref = getSharedPreferences("app_preferences", MODE_PRIVATE);

        final int currentBalance = appPref.getInt("balance", 100);

        balanceLabel = findViewById(R.id.balanceLabel);
        balanceLabel.setText(Integer.toString(currentBalance));


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "You need to enable NFC to send/receive payments.", Toast.LENGTH_LONG).show();
        }

        Button paymentBtn = findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener((View v) -> {
            EditText amountField = findViewById(R.id.amountField);


            int amount = Integer.parseInt(amountField.getText().toString());

            if (amount < 0) {
                Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
            } else if (currentBalance - amount < 0) {
                Toast.makeText(this, "Not enough funds.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, PayActivity.class);
                intent.putExtra("amount", amount);

                startActivity(intent);
            }
        });
    }
}
