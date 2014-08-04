package com.itaas.ankit.snippetviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itaas.ankit.snippetviewer.DataList.Snippet;

/**
 * The entry point of the application
 * Displays the data (eg. https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json) in a list view 
 * @author Ankit Sinha
 *
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
				Log.d(TAG, "onLoadFinished: data is null");
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

	/**
	 * A custom Loader for the Data List
	 */
	public static class AppListLoader extends AsyncTaskLoader<DataList> {

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

		public void setData(DataList data) {
			Log.d(TAG, "setData");
			clear();
			if (data != null) {
				addAll(Arrays.asList(data.getRows()));
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
			final URI imageURI = item.getImageHref();
			final ImageView imageView = ((ImageView)view.findViewById(R.id.row_image));
			
			imageView.setTag(imageURI);
			
			if(imageURI == null){
				imageView.setImageDrawable(null);	
			} else {
				Drawable loadingIndicatorDrawable = getContext().getResources().getDrawable(
						android.R.drawable.progress_indeterminate_horizontal);
				imageView.setImageDrawable(loadingIndicatorDrawable);
				
				//load the real image in a background thread
				this.loadImage(imageURI, imageView);
				
			}
			((TextView)view.findViewById(R.id.row_title)).setText(item.getTitle());
			((TextView)view.findViewById(R.id.row_description)).setText(item.getDescription());

			return view;
		}

		private void loadImage(final URI imageURI, final ImageView imageView) {
			
			AsyncTask<Object, Void, Bitmap> fetchImageTask = new AsyncTask<Object, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Object... params) {
					URI imageURI = (URI) params[0];
					ImageView imageView = (ImageView) params[1];
					
					Log.d(TAG, "doInBackground: imageURI: "+ imageURI + ", tag: " + imageView.getTag());
					
					if(!imageURI.equals(imageView.getTag())){
						Log.d(TAG, "imageView has been reused. Quit");
						return null;
					}
					
					try {
						Bitmap bitmap = BitmapFactory.decodeStream(
								(InputStream) imageURI.toURL().getContent());
						return bitmap;
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(Bitmap bm) {
					if(bm == null){
						Log.d(TAG, "Got null bitmap. Quit");
						return;
					}
					if(!imageURI.equals(imageView.getTag())){
						Log.d(TAG, "imageView has been reused. Quit");
						return;
					}
					
					imageView.setImageBitmap(bm);
				};
				
			};
			fetchImageTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, imageURI, imageView);
		}
	}

}
