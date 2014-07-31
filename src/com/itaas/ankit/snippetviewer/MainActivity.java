package com.itaas.ankit.snippetviewer;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itaas.ankit.snippetviewer.DataList.Snippet;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();

	// This is the Adapter being used to display the list's data.
	private AppListAdapter mAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "setContentView");

		SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshListener());
        
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
        
        //Show that data is being loaded
        swipeLayout.setRefreshing(true);
        
        
		final ListView listview = (ListView) MainActivity.this.findViewById(R.id.listview);
		// Create an adapter we will use to display the loaded data.
		mAdapter = new AppListAdapter(this);
		listview.setAdapter(mAdapter);
		
		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, new AppListLoaderCallbacks());
        
		Log.d(TAG, "onCreate done!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.d(TAG, "onRefresh");
			MainActivity.this.getLoaderManager().getLoader(0).onContentChanged();
		}
	}

	private class AppListLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Snippet>> {
		
		@Override public Loader<List<Snippet>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "onCreateLoader");
			// This is called when a new Loader needs to be created.
			return new AppListLoader(MainActivity.this);
		}

		@Override public void onLoadFinished(Loader<List<Snippet>> loader, List<Snippet> data) {
			Log.d(TAG, "onLoadFinished");
			// Set the new data in the adapter.
			mAdapter.setData(data);
			
			SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) MainActivity.this.findViewById(
					R.id.swipe_container);
			if(swipeLayout.isRefreshing()) {
				Log.d(TAG, "Swipe Refresh was in progress");
				swipeLayout.setRefreshing(false);
			}
		}

		@Override public void onLoaderReset(Loader<List<Snippet>> loader) {
			Log.d(TAG, "onLoaderReset");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}

	/**
	 * A custom Loader for the Data List
	 */
	public static class AppListLoader extends AsyncTaskLoader<List<Snippet>> {

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
		@Override public List<Snippet> loadInBackground() {
			Log.d(TAG, "loadInBackground");
			DataList dataList = new HttpDataListProvider().getDataList();
			List<Snippet> result = Arrays.asList(dataList.getRows());
			Log.d(TAG, "Got result count: "+ result.size());
			return result;
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

	/**
	 * A simple adapter for the list view
	 */
	public static class AppListAdapter extends ArrayAdapter<Snippet> {
		private static final String TAG = AppListAdapter.class.getName();
		private final LayoutInflater mInflater;

		public AppListAdapter(Context context) {
			super(context, R.layout.list_row);
			Log.d(TAG, "constructor");
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(List<Snippet> data) {
			Log.d(TAG, "setData");
			clear();
			if (data != null) {
				addAll(data);
			}
		}

		/**
		 * Populate new items in the list.
		 */
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			//Log.d(TAG, "getView");
			View view;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.list_row, parent, false);
			} else {
				view = convertView;
			}

			Snippet item = getItem(position);
			if(item.getImageHref() == null){
				((ImageView)view.findViewById(R.id.icon)).setImageDrawable(null);	
			} else {
				Drawable d = getContext().getResources().getDrawable(R.drawable.ic_launcher);
				((ImageView)view.findViewById(R.id.icon)).setImageDrawable(d);
			}
			((TextView)view.findViewById(R.id.firstLine)).setText(item.getTitle());
			((TextView)view.findViewById(R.id.secondLine)).setText(item.getDescription());

			return view;
		}
	}

}
