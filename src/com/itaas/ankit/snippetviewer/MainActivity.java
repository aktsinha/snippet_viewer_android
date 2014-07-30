package com.itaas.ankit.snippetviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.itaas.ankit.snippetviewer.DataList.Snippet;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
				//TextView mainTextView = (TextView) MainActivity.this.findViewById(R.id.main_text_view);
				//mainTextView.setText(result.getTitle());
				final ListView listview = (ListView) MainActivity.this.findViewById(R.id.listview);
				
				ArrayAdapter<DataList.Snippet> adapter = new ArrayAdapter<DataList.Snippet>(
						MainActivity.this, R.layout.list_row, R.id.secondLine, result.getRows());
				
				listview.setAdapter(adapter);
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
	
	/**
	 * A custom Loader that loads the Data List
	 */
	public static class AppListLoader extends AsyncTaskLoader<List<Snippet>> {

	    //private List<Snippet> snippets;
	    private static final String TAG = AppListLoader.class.getName();

	    public AppListLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override public List<Snippet> loadInBackground() {
	    	DataList dataList = new HttpDataListProvider().getDataList();
	    	List<Snippet> result = Arrays.asList(dataList.getRows());
	    	Log.d(TAG, "Got result count: "+ result.size());
	        return result;
	    }


	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override protected void onStopLoading() {
	        // Attempt to cancel the current load task if possible.
	        this.cancelLoad();
	    }


	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override protected void onReset() {
	        super.onReset();
	        // Ensure the loader is stopped
	        onStopLoading();
	    }
	}
	
	

}
