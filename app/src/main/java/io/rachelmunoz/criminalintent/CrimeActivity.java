package io.rachelmunoz.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
	private static final String EXTRA_CRIME_ID = "io.rachelmunoz.android.criminalintent.crime_id";

	@Override
	protected Fragment createFragment() {
		UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeId);
	}

	public static Intent newIntent(Context packageContext, UUID crimeID){ // talking to Activity that starts this activity
		Intent intent = new Intent(packageContext, CrimeActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, crimeID);
		return intent;
	}

}
