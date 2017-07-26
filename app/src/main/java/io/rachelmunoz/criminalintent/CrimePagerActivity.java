package io.rachelmunoz.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by rachelmunoz on 7/4/17.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
	private static final String EXTRA_CRIME_ID = "io.rachelmunoz.android.criminalintent.crime_id";
	private ViewPager mViewPager;
	private List<Crime> mCrimes;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_pager);

		UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID); // to set the ViewPager to correct idx

		mViewPager = (ViewPager) findViewById(R.id.crime_view_pager); // swiper widget element

		mCrimes = CrimeLab.get(this).getCrimes(); // this because gets us correct context b/c we are in an Activity
		FragmentManager fragmentManager = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) { // ViewPager adapter needs FragmentManager
																				// b/c inserting different Fragments &
			@Override                                                          // not held on to by a ViewHolder
			public Fragment getItem(int position) {
				Crime crime = mCrimes.get(position);
				return CrimeFragment.newInstance(crime.getId());
			}

			@Override
			public int getCount() {
				return mCrimes.size();
			}
		});

		for (int i = 0; i < mCrimes.size(); i++){
			if (mCrimes.get(i).getId().equals(crimeId)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}

	}

	public static Intent newIntent(Context packageContext, UUID id){ // called by CrimeListFragment to know which Activity to launch
		Intent intent = new Intent(packageContext, CrimePagerActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, id);
		return intent;
	}

	@Override
	public void onCrimeUpdate(Crime crime) {

	}

	@Override
	public void onDeleteCrime() {
		this.finish();
	}
}
