package io.rachelmunoz.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

/**
 * Created by rachelmunoz on 6/30/17.
 */

public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private File mPhotoFile;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mCrimeSolved;
	private Button mReportButton;
	private Button mSuspectButton;
	private ImageView mPhotoView;
	private ImageButton mPhotoButton;
	private Callbacks mCallbacks;

	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";

	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;
	private static final int REQUEST_PHOTO = 2;

	public interface Callbacks {
		void onCrimeUpdate(Crime crime);
		void onDeleteCrime();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
		mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(mCrime);
				mCallbacks.onDeleteCrime();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mCallbacks = (Callbacks) context;
	}

	@Override
	public void onPause() {
		super.onPause();

		CrimeLab.get(getActivity()).updateCrime(mCrime);
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
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
				updateCrime();
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
				updateCrime();
			}
		});

		mReportButton = (Button) v.findViewById(R.id.crime_report);
		mReportButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});

		final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

		mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
		mSuspectButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				startActivityForResult(pickContact, REQUEST_CONTACT);
			}
		});

		if (mCrime.getSuspect() != null){
			mSuspectButton.setText(mCrime.getSuspect());
		}

		PackageManager packageManager = getActivity().getPackageManager();
		if (packageManager.resolveActivity(pickContact, packageManager.MATCH_DEFAULT_ONLY) == null){
			mSuspectButton.setEnabled(false);
		}

		mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);


		final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // implicit intent action

		boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null; // place to save && activity to use
		mPhotoButton.setEnabled(canTakePhoto);

		mPhotoButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Uri uri = FileProvider.getUriForFile(
						getActivity(),
						"io.rachelmunoz.criminalintent.fileprovider", // URI pointing to where to save large res photo
						mPhotoFile
					);
				captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

				List<ResolveInfo> cameraActivities = getActivity()
														.getPackageManager()
														.queryIntentActivities(  // find all possible activities
																captureImage,
																PackageManager.MATCH_DEFAULT_ONLY);

				for (ResolveInfo activity : cameraActivities){
					getActivity()
						.grantUriPermission(
							activity.activityInfo.packageName,       // give them permission
							uri,
							Intent.FLAG_GRANT_WRITE_URI_PERMISSION
						);
				}

				startActivityForResult(captureImage, REQUEST_PHOTO);
			}
		});

		updatePhotoView();

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
			updateCrime();
			updateDate();
		} else if (requestCode == REQUEST_CONTACT && data != null){
			Uri contactURI = data.getData();
			String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

			Cursor c = getActivity()
							.getContentResolver()
							.query(contactURI, queryFields, null, null, null);

			try {
				if (c.getCount() == 0){
					return;
				}

				c.moveToFirst();
				String suspect = c.getString(0);
				mCrime.setSuspect(suspect);
				updateCrime();
				mSuspectButton.setText(suspect);
			} finally {
				c.close();
			}


		} else if (requestCode == REQUEST_PHOTO) {
			Uri uri = FileProvider.getUriForFile(getActivity(), "io.rachelmunoz.criminalintent.fileprovider", mPhotoFile);

			getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

			updateCrime();
			updatePhotoView();
		}
	}

	private void updateCrime(){
		CrimeLab.get(getActivity()).updateCrime(mCrime);
		mCallbacks.onCrimeUpdate(mCrime);
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getFormattedDate());
	}

	private String getCrimeReport(){
		String solvedString = null;
		if (mCrime.isSolved()){
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}

		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

		String suspect = mCrime.getSuspect();
		if (suspect == null){
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

		return report;
	}

	private void updatePhotoView(){
		if (mPhotoFile == null || !mPhotoFile.exists()){
			mPhotoView.setImageDrawable(null);
		} else {
			Bitmap bitmap = PictureUtils.getScaledBitmap(
					mPhotoFile.getPath(),
					getActivity()
				);
			mPhotoView.setImageBitmap(bitmap);
		}
	}
}
