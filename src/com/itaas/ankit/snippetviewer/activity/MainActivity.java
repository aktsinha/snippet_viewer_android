package com.itaas.ankit.snippetviewer.activity;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.itaas.ankit.snippetviewer.R;
import com.itaas.ankit.snippetviewer.data.DataList;
import com.itaas.ankit.snippetviewer.listcontroller.AppListAdapter;
import com.itaas.ankit.snippetviewer.listcontroller.AppListLoader;


/**
 * The entry point of the application
 * Displays the data (eg. https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json) in a list view 
 * @author Ankit Sinha
 */
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
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem settingsItem = menu.findItem(R.id.action_settings);
		settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  
	  Intent intent = new Intent();
	        intent.setClass(MainActivity.this, FragmentPreferences.class);
	        startActivity(intent); 
	  
	        return true;
	 }
	
	private class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.d(TAG, "onRefresh");
			MainActivity.this.getLoaderManager().getLoader(0).onContentChanged();
		}
	}

	private class AppListLoaderCallbacks implements LoaderManager.LoaderCallbacks<DataList> {
		
		@Override public Loader<DataList> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "onCreateLoader");
			// This is called when a new Loader needs to be created.
			return new AppListLoader(MainActivity.this);
		}

		@Override public void onLoadFinished(Loader<DataList> loader, DataList data) {
			Log.d(TAG, "onLoadFinished");

			//Dismiss the refresh indicator
			SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) MainActivity.this.findViewById(
					R.id.swipe_container);
			if(swipeLayout.isRefreshing()) {
				Log.d(TAG, "Swipe Refresh was in progress");
				swipeLayout.setRefreshing(false);
			}
			
			if(data == null){
				Log.w(TAG, "onLoadFinished: data is null");
				Toast.makeText(
						MainActivity.this, 
						MainActivity.this.getString(R.string.msg_failed_to_load_data), 
						Toast.LENGTH_LONG).show();
				return;
			}
			
			// Set the new data in the adapter.
			mAdapter.setData(data);
			
			MainActivity.this.getActionBar().setTitle(data.getTitle());
		}

		@Override public void onLoaderReset(Loader<DataList> loader) {
			Log.d(TAG, "onLoaderReset");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}

}
