package com.itaas.ankit.snippetviewer.listcontroller;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.itaas.ankit.snippetviewer.R;
import com.itaas.ankit.snippetviewer.data.DataList;
import com.itaas.ankit.snippetviewer.data.HttpDataListProvider;

/**
 * A custom Loader for the Data List
 */
public class AppListLoader extends AsyncTaskLoader<DataList> {

	private static final String TAG = AppListLoader.class.getName();

	public AppListLoader(Context context) {
		super(context);
		Log.d(TAG, "constructor");
	}

	/**
	 * This is where the bulk of our work is done.  This function is
	 * called in a background thread and should generate a new set of
	 * data to be published by the loader.
	 */
	@Override public DataList loadInBackground() {
		Log.d(TAG, "loadInBackground");
		URI dataURI = getDataURI();
		DataList dataList = new HttpDataListProvider(dataURI).getDataList();
		
		String numRows = dataList == null? "null" : ""+dataList.getRows().length;
		Log.d(TAG, "Got result count: " + numRows);
		
		return dataList;
	}

	private URI getDataURI() {
		URI dataURI = null;
		try {
			String dataURLKey = this.getContext().getString(R.string.preference_key_url);
			String defaultDataURL = this.getContext().getString(R.string.default_data_url);
			
			String dataURIString = PreferenceManager.getDefaultSharedPreferences(
					this.getContext()).getString(dataURLKey, defaultDataURL);
			
			dataURI = new URI(dataURIString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataURI;
	}

	/**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
    	Log.d(TAG, "onStartLoading");
    	forceLoad();
    }

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override protected void onStopLoading() {
		Log.d(TAG, "onStopLoading");
		// Attempt to cancel the current load task if possible.
		this.cancelLoad();
	}


	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override protected void onReset() {
		Log.d(TAG, "onReset");
		super.onReset();
		// Ensure the loader is stopped
		onStopLoading();
	}
}