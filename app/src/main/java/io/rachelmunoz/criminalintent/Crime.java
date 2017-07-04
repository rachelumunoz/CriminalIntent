package io.rachelmunoz.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rachelmunoz on 7/1/17.
 */

public class Crime {
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;

	public Crime(){
		mDate = new Date();
		mId = UUID.randomUUID();
	}

	public Date getDate() {
		return mDate;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public UUID getId(){
		return mId;
	}
}
