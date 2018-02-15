package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddGrunkerActivity extends Activity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grunker);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.msg_noNFCSupport, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, R.string.msg_NFCDisabled, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set message to null to avoid resending NDEF payload.
        nfcAdapter.setNdefPushMessage(null, this);
    }

    public void onClickAdd(View v) {
        EditText addGrunkerAmount = findViewById(R.id.addGrunkerAmount);
        String addAmount = addGrunkerAmount.getText().toString();
        SharedPreferences appPref = getSharedPreferences("app_preferences", MODE_PRIVATE);

        if (addAmount.equals("")) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
        } else {
            Integer addAmountInt = Integer.valueOf(addAmount);

            if (addAmountInt < 0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            } else {
                final int currentBalance = appPref.getInt("balance", 100);

                appPref.edit().putInt("balance", currentBalance + addAmountInt).apply();

                Toast.makeText(this, addAmount + " grunker was added to your account", Toast.LENGTH_SHORT).show();

                finish();
            }
        }
    }
}
