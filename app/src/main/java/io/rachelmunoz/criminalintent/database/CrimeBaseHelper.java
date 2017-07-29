package io.rachelmunoz.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.rachelmunoz.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by rachelmunoz on 7/8/17.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper { // creates DB and configures, and updates stuff
	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "crimeBase.db";

	public CrimeBaseHelper(Context context){
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
		" _id integer primary key autoincrement, " +
			CrimeTable.Cols.UUID + ", " +
			CrimeTable.Cols.TITLE + ", " +
			CrimeTable.Cols.DATE + ", " +
			CrimeTable.Cols.SOLVED + ", " +
			CrimeTable.Cols.SUSPECT +
			")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}
}
