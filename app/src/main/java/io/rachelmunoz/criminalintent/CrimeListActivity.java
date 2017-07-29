package io.rachelmunoz.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by rachelmunoz on 7/2/17.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	} // alias resources for diff screen sizes

	@Override
	public void onCrimeSelected(Crime crime) {
		if (findViewById(R.id.detail_fragment_container) == null){ // searching to see if using two-pane layout
			Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
			startActivity(intent);
		} else {
			Fragment newDetail = CrimeFragment.newInstance(crime.getId());

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.detail_fragment_container, newDetail)
					.commit();
		}
	}

	@Override
	public void onCrimeUpdate(Crime crime) {
		CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		listFragment.updateUI();
	}

	@Override
	public void onDeleteCrime() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment crimeFragment = fm.findFragmentById(R.id.detail_fragment_container);

		fm.beginTransaction()
				.remove(crimeFragment)
				.commit();

		onCrimeUpdate(new Crime());
	}
}
