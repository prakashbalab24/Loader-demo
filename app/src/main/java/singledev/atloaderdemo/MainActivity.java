package singledev.atloaderdemo;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(0,null,(LoaderManager.LoaderCallbacks<String>)this);
    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        return new MyDataloader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        if (data == null)
            return;
        Log.i("DataLoaded",data);
        Toast.makeText(this,data,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }


    private static class MyDataloader extends android.support.v4.content.AsyncTaskLoader<String> {
        private static final AtomicInteger sCurrentUniqueId = new AtomicInteger(0);
        private String mData;
        public boolean hasResult = false;

        public MyDataloader(Context context) {
            super(context);
            onContentChanged();
        }

        public static int getNewUniqueLoaderId() {
            return sCurrentUniqueId.getAndIncrement();
        }

        @Override
        protected void onStartLoading() {
            if (takeContentChanged())
                forceLoad();
            else if (hasResult)
                deliverResult(mData);
        }

        @Override
        public void deliverResult(String data) {
            mData = data;
            hasResult = true;
            super.deliverResult(data);
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            if (hasResult) {
                onReleaseResources(mData);
                mData = null;
                hasResult = false;
            }
        }
        protected void onReleaseResources(String data) {
            //nothing to do.
        }

        public String getResult() {
            return mData;
        }

        @Override
        public String loadInBackground() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            String line;
            try {
                URL url = new URL("https://itunes.apple.com/search?term=classic");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) buffer.append(line);

                if (buffer.length() == 0) return null;
                jsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            return jsonStr;
        }
    }
}
