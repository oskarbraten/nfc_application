package no.hvl.dat153.paynfc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast

class PaymentActivity : Activity() {

    var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        val intent : Intent = getIntent();
        val amount = intent?.extras?.getInt("amount", 0);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }
}
