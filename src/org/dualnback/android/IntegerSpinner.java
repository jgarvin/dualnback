package org.dualnback.android;

import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import android.view.Gravity;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.graphics.Canvas;

import android.util.Log;

public class IntegerSpinner extends ViewGroup {
	private static final String TAG = "IntegerSpinner";
	
	private int val_;

	private LinearLayout topLayout_;
	
    public IntegerSpinner(Context context) {
        super(context);

		topLayout_ = new LinearLayout(context);
		// topLayout_.setOrientation(LinearLayout.VERTICAL);
		// topLayout_.setGravity(Gravity.FILL);
		// topLayout_.setWeightSum(1.0f);

		Button plus_button = new Button(context);
		plus_button.setText("+");
		Button minus_button = new Button(context);
		minus_button.setText("-");

		final TextView field = new TextView(context);
		field.setText("2");

		plus_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				++val_;
				field.setText(String.format("%d", val_));
				field.invalidate();
			}
		});

		minus_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				--val_;
				field.setText(String.format("%d", val_));
				field.invalidate();
			}
		});
		
		// topLayout_.addView(plus_button, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.1f));
		// topLayout_.addView(field, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.8f));
		// topLayout_.addView(minus_button, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.1f));
		topLayout_.addView(plus_button);
		topLayout_.addView(field);
		topLayout_.addView(minus_button);

		invalidate();
    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.v(TAG, "Ran layout");
		if(changed) {
			topLayout_.layout(left, top, right, bottom);
			topLayout_.invalidate();
			invalidate();
		}
	}

	public int value() {
		return val_;
	}
}
