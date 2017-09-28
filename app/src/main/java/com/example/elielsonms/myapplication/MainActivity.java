package com.example.elielsonms.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;

import static android.provider.ContactsContract.CommonDataKinds.Identity.NAMESPACE;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity{

    private static int REQUEST_SOAP_TEST = 0;

    private TextView te;
    private TextView de;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("Calling service");
        PendingIntent pendingResult = createPendingResult(REQUEST_SOAP_TEST, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), FetchSoapService.class);
        intent.putExtra(FetchSoapService.PEDIDO, pendingResult);
        startService(intent);

        ((Button)findViewById(R.id.buttonToGo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Calling service");
                PendingIntent pendingResult = createPendingResult(REQUEST_SOAP_TEST, new Intent(), 0);
                Intent intent = new Intent(getApplicationContext(), FetchSoapService.class);
                intent.putExtra(FetchSoapService.PEDIDO, pendingResult);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Receiving PendingIntent");
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_SOAP_TEST == requestCode) {
            System.out.println("Data was "+data.getParcelableArrayListExtra(FetchSoapService.RESULTADO));
            te = (TextView) findViewById(R.id.helloText);
            de = (TextView) findViewById(R.id.debugText);

            te.setText(te.getText() + data.getParcelableArrayListExtra(FetchSoapService.RESULTADO).toString());
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
