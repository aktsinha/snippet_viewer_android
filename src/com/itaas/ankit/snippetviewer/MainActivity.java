package com.itaas.ankit.snippetviewer;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
		/*AsyncTask<Void, Integer, DataList> fetchDataTask = new AsyncTask<Void, Integer, DataList>(){

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
		Log.d(TAG, "fetchDataTask.execute triggered");*/

		final ListView listview = (ListView) MainActivity.this.findViewById(R.id.listview);

		// Create an adapter we will use to display the loaded data.
		mAdapter = new AppListAdapter(this);
		listview.setAdapter(mAdapter);

		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, new AppListLoaderCallbacks());
		Log.d(TAG, "onCreate end");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			//((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
			((TextView)view.findViewById(R.id.firstLine)).setText(item.getTitle());
			((TextView)view.findViewById(R.id.secondLine)).setText(item.getDescription());

			return view;
		}
	}

}
