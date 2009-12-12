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

import android.app.AlertDialog;

class Pair<T, S> {
  public Pair(T f, S s){ 
    first = f;
    second = s;   
  }
 
  public String toString()  { 
    return "(" + first.toString() + ", " + second.toString() + ")"; 
  }
 
  public T first;
  public S second;
}

class Stimulus
{
	private int box_loc_;
	private int sound_index_;

	public Stimulus(int box_loc, int sound_index)
	{
		box_loc_ = box_loc;
		sound_index_ = sound_index;
	}

	public boolean visual_match(Stimulus other) {
		return box_loc_ == other.box_loc_;
	}

	public boolean aural_match(Stimulus other) {
		return sound_index_ == other.sound_index_;
	}

	void display_visual(Grid x) {
		Pair<Integer, Integer> d = new Pair(0, 1);
		Pair<Integer, Integer> r = new Pair(1, 0);
		Pair<Integer, Integer> u = new Pair(0, -1);
		Pair<Integer, Integer> l = new Pair(-1, 0);

		ArrayList< Pair<Integer, Integer> > spiral = new ArrayList< Pair<Integer, Integer> >();
		spiral.add(d); spiral.add(d);
		spiral.add(r); spiral.add(r);
		spiral.add(u); spiral.add(u);
		spiral.add(l); spiral.add(l);

		Pair<Integer, Integer> loc = new Pair(0, 0);
		for(int i = 0; i < box_loc_; ++i) {
			loc.first += spiral.get(i).first;
			loc.second += spiral.get(i).second;
		}
		
		x.lightSquare(loc.first, loc.second);
	}
	
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
			Stimulus current = current_stimuli_.next();
			current.display_visual(grid);
			grid.postInvalidate();
			current.play_sound();
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

		sanityTest();

		playLevel(2, grid);
    }

	private boolean verifyStimulusChain(int n, ArrayList<Stimulus> chain)
	{
		Log.v(TAG, "Verifying chain...");
		
		int visual_matches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).visual_match(chain.get(i + n)) &&
			   !chain.get(i).aural_match(chain.get(i + n))) {
				Log.v(TAG, "Visual match");
				++visual_matches;
			}
		}

		if(visual_matches != 4) {
			Log.v(TAG, "Wrong number of visual matches");
			return false;
		}

		int aural_matches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).aural_match(chain.get(i + n)) &&
			   !chain.get(i).visual_match(chain.get(i + n))) {
				Log.v(TAG, "Aural match");
				++aural_matches;
			}
		}

		if(aural_matches != 4) {
			Log.v(TAG, "Wrong number of audio matches");
			return false;
		}

		int dual_matches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).aural_match(chain.get(i + n)) &&
			   chain.get(i).visual_match(chain.get(i + n))) {
				Log.v(TAG, "Dual match");
				++dual_matches;
			}
		}

		if(dual_matches != 2) {
			Log.v(TAG, "Wrong number of dual matches");
			return false;
		}

		if(chain.size() != n + 20) {
			Log.v(TAG, "Invalid size for chain");
			return false;
		}

		Log.v(TAG, "Success verifying chain");
		return true;
	}

	private ArrayList<Stimulus> buildTestChain()
	{
		ArrayList<Stimulus> test = new ArrayList<Stimulus>();

		// 4 visual matches, all on 0,0
		test.add(new Stimulus(0, 0));
		test.add(new Stimulus(1, 1));
		test.add(new Stimulus(0, 1));
		test.add(new Stimulus(3, 2));
		test.add(new Stimulus(0, 2));
		test.add(new Stimulus(5, 3));
		test.add(new Stimulus(0, 3));
		test.add(new Stimulus(7, 4));
		test.add(new Stimulus(0, 4));

		// 4 audio only matches, all on 0
		test.add(new Stimulus(0, 0));
		test.add(new Stimulus(1, 1));
		test.add(new Stimulus(1, 0));
		test.add(new Stimulus(2, 2));
		test.add(new Stimulus(2, 0));
		test.add(new Stimulus(3, 3));
		test.add(new Stimulus(3, 0));
		test.add(new Stimulus(4, 4));
		test.add(new Stimulus(4, 0));

		// 2 dual matches
		test.add(new Stimulus(2, 1));
		test.add(new Stimulus(2, 1));
		test.add(new Stimulus(2, 1));
		test.add(new Stimulus(2, 1));

		return test;
	}

    private void sanityTest()
	{
		ArrayList<Stimulus> test = buildTestChain();

		if(!verifyStimulusChain(2, test)) {
			new AlertDialog.Builder(this)
				.setMessage("Verifying stimulus chain failed! You found a bug!")
				.show();

			return;
		}
	}

	public void playLevel(int n, Grid grid)
	{
		ArrayList<Stimulus> test = buildTestChain();
		
		stimulus_timer_ = new Timer();
		stimulus_timer_.scheduleAtFixedRate(new NextStimulus(grid, test.iterator()), 0, 1000);
	}
}
