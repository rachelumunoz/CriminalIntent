package io.rachelmunoz.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rachelmunoz on 7/2/17.
 */

public class CrimeListFragment extends Fragment {
	private RecyclerView mCrimeRecyclerView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_list, container, false); // inflate the fragment XML

		mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view); // find the RecyclerView widget
		mCrimeRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity())); // need to set LayoutManager to position items on sceen

		return view;

	}
}
