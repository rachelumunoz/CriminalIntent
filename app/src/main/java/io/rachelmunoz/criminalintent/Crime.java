package io.rachelmunoz.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rachelmunoz on 6/30/17.
 */

public class Crime {
	private String mTitle;
	private Date mDate;
	private UUID mId;
	private boolean mSolved;

	public Crime(){
		this(UUID.randomUUID());
	}

	public  Crime(UUID id){
		mId = id;
		mDate = new Date();
	}

	public UUID getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Date getDate() {
		return mDate;
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
}
