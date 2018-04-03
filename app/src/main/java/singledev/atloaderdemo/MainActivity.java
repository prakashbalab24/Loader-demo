package singledev.atloaderdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });
        getSupportLoaderManager().initLoader(0,null,(LoaderManager.LoaderCallbacks<String>)this);
        textView.performClick();

    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        return new MyDataloader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        if (data == null) {
            Log.i("DataLoaded", "null");
            return;
        }
        Log.i("DataLoaded",data);
        Toast.makeText(this,data,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }




    private static class MyAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
