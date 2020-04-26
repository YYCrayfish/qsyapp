package com.manyu.videoshare.util;

import android.app.DatePickerDialog;
import android.content.Context;

public class MyDateListener extends DatePickerDialog {
	public MyDateListener(Context context,DatePickerDialog.OnDateSetListener listener, int year, int month, int dayOfMonth) {
		super(context, listener, year, month, dayOfMonth);
	}

	@Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //super.onStop();
    
    }
}
