package io.rachelmunoz.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

/**
 * Created by rachelmunoz on 6/30/17.
 */

public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mCrimeSolved;
	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";
	private static final int REQUEST_DATE = 0;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);

	}

	@Override
	public void onPause() {
		super.onPause();

		CrimeLab.get(getActivity()).updateCrime(mCrime);
	}

	public static CrimeFragment newInstance(UUID id){ // called by CrimePagerActivity to get which Crime to show/host
		Bundle args = new Bundle();
		args.putSerializable(ARG_CRIME_ID, id);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				mCrime.setTitle(charSequence.toString());
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate()); // load bundled DatePickerFragment
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); // for lifecycle communication
				dialog.show(fragmentManager, DIALOG_DATE); // the AlertDialog calls #show and passes in the fm, with a unique identifier
			}
		});

		mCrimeSolved = (CheckBox) v.findViewById(R.id.crime_solved);
		mCrimeSolved.setChecked(mCrime.isSolved());
		mCrimeSolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});


		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}

		if (requestCode == REQUEST_DATE){
			if (data == null){
				return;
			}

			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		}
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}
}
