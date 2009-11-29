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

import java.util.*;

class Stimulus
{
	private int box_x_;
	private int box_y_;
	private int sound_index_;

	Stimulus(int box_x, int box_y, int sound_index)
	{
		box_x_ = box_x;
		box_y_ = box_y;
		sound_index_ = sound_index;
	}

	void display_visual(Grid x) { x.lightSquare(box_x_, box_y_); }
	void play_sound() {}
}

class NextStimulus extends TimerTask
{
	private Grid grid;
	private View to_update;

	private int x;
	private Iterator<Stimulus> current_stimuli_;
	
	public NextStimulus(Grid to_light, Iterator<Stimulus> stimuli) {
		super();
		
		grid = to_light;
		current_stimuli_ = stimuli;
	}
	
	public void run() {
		if(!current_stimuli_.hasNext()) {
			cancel();
		}
		else {
			current_stimuli_.next().display_visual(grid);
			grid.postInvalidate();
		}
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
		audioButton.setText("Aural");
		Button videoButton = new Button(this);
		videoButton.setText("Visual");

		buttonLayout.addView(audioButton, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));
		buttonLayout.addView(videoButton, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));
		
		topLayout.addView(buttonLayout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.9f));
		setContentView(topLayout);

		ArrayList<Stimulus> test = new ArrayList<Stimulus>();
		test.add(new Stimulus(0, 0, 0));
		test.add(new Stimulus(1, 0, 0));
		test.add(new Stimulus(2, 0, 0));

		stimulus_timer_ = new Timer();
		stimulus_timer_.scheduleAtFixedRate(new NextStimulus(grid, test.iterator()), 0, 1000);
    }
}
