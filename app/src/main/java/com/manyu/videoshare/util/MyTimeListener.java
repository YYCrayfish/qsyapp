package com.manyu.videoshare.util;

import android.app.TimePickerDialog;
import android.content.Context;

public class MyTimeListener extends TimePickerDialog {

	public MyTimeListener(Context context, OnTimeSetListener callBack,
                          int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
	}
	@Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //super.onStop();
    
    }
}
