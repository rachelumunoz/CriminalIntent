package io.rachelmunoz.criminalintent;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by rachelmunoz on 7/2/17.
 */

public class CrimeListFragment extends Fragment {
	private RecyclerView mCrimeRecyclerView;
	private CrimeAdapter mAdapter;
	private boolean mSubtitleVisible;

	private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_list, container, false); // inflate the fragment XML

		mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view); // find the RecyclerView widget
		mCrimeRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity())); // need to set LayoutManager to position items on sceen

		if (savedInstanceState != null){
			mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
		}
		updateUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible); // for rotation configuration rebuild
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

		MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
		if (mSubtitleVisible){
			subtitleItem.setTitle(R.string.hide_subtitle);
		} else {
			subtitleItem.setTitle(R.string.show_subtitle);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { // callback for menu interaction
		switch (item.getItemId()){
			case R.id.new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
				startActivity(intent);
				return true;
			case R.id.show_subtitle:
				mSubtitleVisible = !mSubtitleVisible;
				getActivity().invalidateOptionsMenu(); // recreates menu
				updateSubtitle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateSubtitle(){
		CrimeLab crimeLab = CrimeLab.get(getActivity());
		int crimeCount = crimeLab.getCrimes().size();
		String subtitle = getString(R.string.subtitle_format, crimeCount);

		if (!mSubtitleVisible){
			subtitle = null;
		}

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setSubtitle(subtitle);
	}

	private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private TextView mTitleTextView;
		private TextView mDateTextView;
		private Crime mCrime;
		private ImageView mSolvedImageView;

		// inflater passed in from adapter, which is the inflater from the Activity
		public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
			super(inflater.inflate(R.layout.list_item_crime, parent, false));
			// inflates and immediately passes it into  super, and holds onto the list_item xml
			itemView.setOnClickListener(this);

			mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
			mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
			mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

		}

		public void bind(Crime crime){
			mCrime = crime;
			mTitleTextView.setText(mCrime.getTitle());
			mDateTextView.setText(mCrime.getFormattedDate());
			mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
		}

		@Override
		public void onClick(View view) {
			Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
			startActivity(intent);
		}
	}

	private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
		private List<Crime> mCrimes;

		public CrimeAdapter(List<Crime> crimes){
			mCrimes = crimes;
		}

		@Override
		public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity()); // gets LayoutInflater from activity hosting the gragment

			return new CrimeHolder(layoutInflater, parent); // RecyclerView asks for new ViewHolder
		}

		@Override
		public void onBindViewHolder(CrimeHolder holder, int position) {
			Crime crime = mCrimes.get(position);
			holder.bind(crime);
		}

		@Override
		public int getItemCount() {
			return mCrimes.size();
		}

		public void setCrimes(List<Crime> crimes){ mCrimes = crimes; }

	}

	private void updateUI(){
		CrimeLab crimeLab = CrimeLab.get(getActivity());
		List<Crime> crimes = crimeLab.getCrimes();


		if (mAdapter == null){
			mAdapter = new CrimeAdapter(crimes);
			mCrimeRecyclerView.setAdapter(mAdapter);
		} else {
			mAdapter.setCrimes(crimes); // updates crimes incase it is different
			mAdapter.notifyDataSetChanged();
		}

		updateSubtitle();
	}
}
