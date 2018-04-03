package singledev.atloaderdemo;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by batman on 03/04/18.
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MM.:  .:'   `:::  .:`MMMMMMMMMMM|`MMM'|MMMMMMMMMMM':  .:'   `:::  .:'.MM
 * MMMM.     :          `MMMMMMMMMM  :*'  MMMMMMMMMM'        :        .MMMM
 * MMMMM.    ::    .     `MMMMMMMM'  ::   `MMMMMMMM'   .     ::   .  .MMMMM
 * MMMMMMM    ;::         ;::         ;::         ;::         ;::   MMMMMMM
 * MMMMMMM .:'   `:::  .:'   `:::  .:'   `:::  .:'   `:::  .:'   `::MMMMMMM
 * MMMMM'______::____      ::    .     ::    .     ::     ___._::____`MMMMM
 * MMMMMMMMMMMMMMMMMMM`---._ :: ::'  :   :: ::'  _.--::MMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMM::.         ::  .--MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM-.     ;::-MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM. .:' .M:F_P:MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.   .MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\ /MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMVMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 */

public class MyDataloader extends android.support.v4.content.AsyncTaskLoader<String> {
        private final WeakReference<Activity> mActivity;
        private final AtomicInteger sCurrentUniqueId = new AtomicInteger(0);
        private String mData;
        public boolean hasResult = false;

        public MyDataloader(Activity activity) {
            super(activity);
            mActivity = new WeakReference<>(activity);
            onContentChanged();
        }

        public int getNewUniqueLoaderId() {
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
            Log.i("loadInBackground","act:"+mActivity.get());
            if(mActivity.get()==null){
                return "";

            }
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
