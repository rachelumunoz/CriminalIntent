package io.rachelmunoz.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.rachelmunoz.criminalintent.database.CrimeBaseHelper;
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
		mContext = context.getApplicationContext();
		mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

//		mCrimes = new ArrayList<>();
	}

	public List<Crime> getCrimes(){
//		return mCrimes;
		return new ArrayList<>();
	}

	public Crime getCrime(UUID id){
//		for (Crime crime : mCrimes){
//			if (crime.getId().equals(id)){
//				return crime;
//			}
//		}

		return null;
	}

	public void updateCrime(Crime crime){
		String uuidString = crime.getId().toString();
		ContentValues values = getContentValues(crime);

		mDatabase.update(
			CrimeTable.NAME,
			values,
			CrimeTable.Cols.UUID + " = ?",  new String[]{ uuidString } // where clause of which row to update
		);
	}

	private static ContentValues getContentValues(Crime crime){
		ContentValues values = new ContentValues();
		values.put(CrimeTable.Cols.UUID, crime.getId().toString());
		values.put(CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

		return values;

	}

}
