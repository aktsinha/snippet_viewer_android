package com.itaas.ankit.snippetviewer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AsyncTask<URI, Integer, Void> fetchDataTask = new AsyncTask<URI, Integer, Void>(){

			@Override
			protected Void doInBackground(URI... params) {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(params[0]);
				BasicResponseHandler response = new BasicResponseHandler();
				String content;
				try {

					Log.d(TAG, "Param:" + params[0]);
					content = client.execute(get, response);
					Log.d(TAG, "Got Content" + content.substring(0,100));
					
					//Ty
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					content = null;
				} catch (IOException e) {
					e.printStackTrace();
					content = null;
				}

				Log.d(TAG, "After fetch execution");
				return null;
			}

		};
		try {
			fetchDataTask.execute(new URI("https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json?dl=1"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
