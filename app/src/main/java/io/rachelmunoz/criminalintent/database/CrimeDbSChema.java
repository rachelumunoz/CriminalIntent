package io.rachelmunoz.criminalintent.database;

/**
 * Created by rachelmunoz on 7/8/17.
 */

public class CrimeDbSChema {

	public static final class CrimeTable {
		public static final String NAME = "crimes";

		public static final class Cols {
			public static final String UUID = "uuid";
			public static final String Title = "title";
			public static final String DATE = "date";
			public static final String SOLVED = "solved";
		}
	}
}
