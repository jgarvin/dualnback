package org.dualnback.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import org.dualnback.android.Grid;
import android.widget.Button;

public class DualNBack extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("test1");

		LinearLayout topLayout = new LinearLayout(this);
		topLayout.setOrientation(LinearLayout.VERTICAL);
		topLayout.setGravity(Gravity.FILL);
		topLayout.setWeightSum(1.0f);

		Grid i = new Grid(this);

		topLayout.addView(i, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.1f));

		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

		Button audioButton = new Button(this);
		audioButton.setText("Audio");
		Button videoButton = new Button(this);
		videoButton.setText("Video");

		buttonLayout.addView(audioButton, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));
		buttonLayout.addView(videoButton, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));
		
		topLayout.addView(buttonLayout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.9f));
		setContentView(topLayout);
    }
}
