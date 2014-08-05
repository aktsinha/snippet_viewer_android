package com.itaas.ankit.snippetviewer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.itaas.ankit.snippetviewer.R;

/**
 * A simple settings screen that allows the data URL to configured
 * @author Ankit Sinha
 */
public class FragmentPreferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }


    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }
    }

}
