package io.rachelmunoz.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by rachelmunoz on 7/2/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}
}
