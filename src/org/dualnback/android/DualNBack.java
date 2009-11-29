package org.dualnback.android;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import org.dualnback.android.Grid;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

class NextStimulus extends TimerTask
{
	private Grid grid;
	private View to_update;

	private int x;
	
	public NextStimulus(Grid to_light, View to_update) {
		super();
		
		grid = to_light;
		x = 0;
	}
	
	public void run() {
		x = (x + 1) % 3;
		
		grid.lightSquare(x, 0);
		grid.postInvalidate();
	}
}

public class DualNBack extends Activity
{
	private static final String TAG = "DualNBack";

	private Timer stimulus_timer_;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		Log.v(TAG, "Starting up...");
		
        super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("test1");

		LinearLayout topLayout = new LinearLayout(this);
		topLayout.setOrientation(LinearLayout.VERTICAL);
		topLayout.setGravity(Gravity.FILL);
		topLayout.setWeightSum(1.0f);

		Grid grid = new Grid(this);
		topLayout.addView(grid, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.1f));

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

		stimulus_timer_ = new Timer();
		stimulus_timer_.scheduleAtFixedRate(new NextStimulus(grid, topLayout), 0, 1000);
    }
}
