package com.example.advertisingid;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView deviceId;
    private TextView advertisingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        deviceId = (TextView) findViewById(R.id.device_id);
        advertisingId = (TextView) findViewById(R.id.advertising_id);

        // Get device ID
        // http://developer.android.com/reference/android/provider/Settings.Secure.html#ANDROID_ID
        deviceId.setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        // Get advertising ID
        new GetAdvertisingIdAsyncTask().execute();
    }

    // We have to fetch the Advertising ID off the main thread
    // https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient
    private class GetAdvertisingIdAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                return adInfo.getId();
            } catch (IOException e) {
                Log.e(TAG, "Failed to connect to Google Play Services", e);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, "Google Play Services not available", e);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Don't call me on the main thread", e);
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG, "In a real app we could recover from this", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String adId) {
            advertisingId.setText(adId);
        }
    }
}
