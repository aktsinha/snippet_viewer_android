package com.itaas.ankit.snippetviewer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AsyncTask<Void, Integer, DataList> fetchDataTask = new AsyncTask<Void, Integer, DataList>(){

			@Override
			protected DataList doInBackground(Void... params) {
				return new HttpDataListProvider().getDataList();
			}

			@Override
			protected void onPostExecute(DataList result) {
				super.onPostExecute(result);
				Log.d(TAG, "fetchDataTask.onPostExecute");
				TextView mainTextView = (TextView) MainActivity.this.findViewById(R.id.main_text_view);
				mainTextView.setText(result.getTitle());
			}

		};
		fetchDataTask.execute();
		Log.d(TAG, "fetchDataTask.execute triggered");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
