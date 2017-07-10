package io.rachelmunoz.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.rachelmunoz.criminalintent.database.CrimeBaseHelper;
import io.rachelmunoz.criminalintent.database.CrimeCursorWrapper;
import io.rachelmunoz.criminalintent.database.CrimeDbSchema;
import io.rachelmunoz.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by rachelmunoz on 7/2/17.
 */

public class CrimeLab {
	private static CrimeLab sCrimeLab;
	private List<Crime> mCrimes;
	private Context mContext;
	private SQLiteDatabase mDatabase;

	public static CrimeLab get(Context context){
		if (sCrimeLab == null){
			sCrimeLab = new CrimeLab(context);
		}
		return sCrimeLab;
	}

	public void addCrime(Crime c){
//		mCrimes.add(c);

		ContentValues values = getContentValues(c);
		mDatabase.insert(CrimeTable.NAME, null, values);
	}


	private CrimeLab(Context context){
		mContext = context.getApplicationContext(); // goes up to whole Application context ?
		mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

//		mCrimes = new ArrayList<>();
	}

	public List<Crime> getCrimes(){
//		return mCrimes;
		List<Crime> crimes = new ArrayList<>();
		CrimeCursorWrapper cursor = queryCrimes(null, null);

		try {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()){
				crimes.add(cursor.getCrime());
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}

		return crimes;
	}

	public Crime getCrime(UUID id){
//		for (Crime crime : mCrimes){
//			if (crime.getId().equals(id)){
//				return crime;
//			}
//		}

		CrimeCursorWrapper cursor = queryCrimes( // sends in where clause to specify which crime
				CrimeTable.Cols.UUID + " = ?",
				new String[] { id.toString() }
		);

		try {
			if (cursor.getCount() == 0){
				return null;
			}

			cursor.moveToFirst();
			return cursor.getCrime();
		} finally {
			cursor.close();
		}
	}

	public void updateCrime(Crime crime){
		String uuidString = crime.getId().toString();
		ContentValues values = getContentValues(crime);

		mDatabase.update(
			CrimeTable.NAME,
			values,
			CrimeTable.Cols.UUID + " = ?",  // where clause of which row to update
			new String[]{ uuidString } // where args
		);
	}

	//	private Cursor queryItems(String whereClause, String[] whereArgs){
	private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
		Cursor cursor = mDatabase.query(
				CrimeTable.NAME,
				null,
				whereClause,
				whereArgs,
				null,
				null,
				null
		);

//		returns a cursor;
		return new CrimeCursorWrapper(cursor);
	}

	private static ContentValues getContentValues(Crime crime){ // sql-izes crime
		ContentValues values = new ContentValues();
		values.put(CrimeTable.Cols.UUID, crime.getId().toString());
		values.put(CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

		return values;

	}

}
