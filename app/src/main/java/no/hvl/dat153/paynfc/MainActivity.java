package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class MainActivity extends Activity {

    //public static String MIME_TYPE = "application/no.hvl.dat153.nfc_project";
    private NfcAdapter nfcAdapter;
    private TextView balanceLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int currentBalance = getSharedPreferences("app_preferences", MODE_PRIVATE).getInt("balance", 100);

        balanceLabel = findViewById(R.id.balanceLabel);
        balanceLabel.setText(Integer.toString(currentBalance));

        Button paymentBtn = findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener((View v) -> {
            EditText amountField = findViewById(R.id.amountField);

            int amount = parseInt(amountField.getText().toString());

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


    @Override
    public void onResume() {
        super.onResume();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "You need to enable NFC to send/receive payments.", Toast.LENGTH_LONG).show();
        }

        // set message to null to avoid resending NDEF payload.
        nfcAdapter.setNdefPushMessage(null, this);

        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage message = (NdefMessage) rawMessages[0];

        SharedPreferences prefs = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // record 0 contains the MIME type, record 1 is the AAR, if present
        final int amount = Integer.parseInt(new String(message.getRecords()[0].getPayload()));
        final int currentBalance = prefs.getInt("balance", 100);

        final int newBalance = currentBalance + amount;

        // update preferences, and label.
        prefs.edit().putInt("balance", newBalance).apply();
        balanceLabel.setText(String.valueOf(newBalance));
    }
}
