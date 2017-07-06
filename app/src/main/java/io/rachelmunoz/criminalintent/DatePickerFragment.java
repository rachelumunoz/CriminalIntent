package io.rachelmunoz.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by rachelmunoz on 7/5/17.
 */

public class DatePickerFragment extends DialogFragment {
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_date, null);


		return new AlertDialog.Builder(getActivity())
			.setView(view)
			.setTitle(R.string.date_picker_title)
			.setPositiveButton(android.R.string.ok, null)
			.create();
	}
}
