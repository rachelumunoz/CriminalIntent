package io.rachelmunoz.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import io.rachelmunoz.criminalintent.Crime;
import io.rachelmunoz.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by rachelmunoz on 7/8/17.
 */

public class CrimeCursorWrapper extends CursorWrapper {

	public CrimeCursorWrapper(Cursor cursor) {
		super(cursor);
	}


	public Crime getCrime(){ // translates SQL stuff into a Crime object
		String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
		String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
		long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
		int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

		Crime crime = new Crime(UUID.fromString(uuidString));
		crime.setTitle(title);
		crime.setDate(new Date(date));
		crime.setSolved(isSolved != 0);

		return crime;
	}
}
