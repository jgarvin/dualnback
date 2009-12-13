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
		return equivalent(box_loc_, other.box_loc_);
	}

	public boolean aural_match(Stimulus other) {
		return equivalent(sound_index_, other.sound_index_);
	}

	private static boolean equivalent(int a, int b) {
		// -1 counts as not filled in, and never counts as a match
		return a != -1 && b != -1 && a == b;
	}

	public static Stimulus make_random(Stimulus other, boolean match_visual, boolean match_audio) {
		int sound_index;
		int box_loc;

		Random generator = new Random();

		if(match_visual)
			box_loc = other.box_loc_;
		else
			do {
				box_loc = generator.nextInt(8);
			} while(box_loc == other.box_loc_);

		if(match_audio)
			sound_index = other.sound_index_;
		else
			do {
				sound_index = generator.nextInt(8);
			} while(sound_index == other.sound_index_);

		return new Stimulus(box_loc, sound_index);
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

class NextStimulus extends Thread
{
	private static final String TAG = "NextStimulus";
	
	private Grid grid_;
	private View to_update;

	private int x;
	private Iterator<Stimulus> current_stimuli_;

	public NextStimulus(Grid to_light, Iterator<Stimulus> stimuli) {
		super();

		grid_ = to_light;
		current_stimuli_ = stimuli;
	}

	private void realWait(long ms) {
		Log.v(TAG, String.format("%dms wait", ms));
		try {
			sleep(ms);
		} catch(InterruptedException e) {}		
	}

	public void run() {
		Log.v(TAG, "Starting stimulus thread...");
		
		while(current_stimuli_.hasNext()) {
			Log.v(TAG, "Begin stimulus display loop iteration");
			
			Stimulus current = current_stimuli_.next();
			current.play_sound();
			current.display_visual(grid_);
			grid_.postInvalidate();

			realWait(500);
			
			grid_.clear();
			grid_.postInvalidate();

			realWait(2500);
		}

		Log.v(TAG, "Finished playing back stimulus.");
	}
}

public class DualNBack extends Activity
{
	private static final String TAG = "DualNBack";

	private Thread stimulus_thread_;

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

	@Override
	protected void onPause()
	{
		super.onPause();
		setResult(RESULT_OK);
		finish();
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

	private ArrayList<Stimulus> genLevel(int n)
	{
		ArrayList<Stimulus> level = new ArrayList<Stimulus>();
		Random generator = new Random();

		// Before n stimulus, it's impossible to have a match
		for(int i = 0; i < n; ++i) {
			level.add(new Stimulus(generator.nextInt(8), generator.nextInt(8)));
		}

		// Represents the space left between stimulus n and n + 20
		ArrayList<Integer> matches = new ArrayList<Integer>();

		matches.add(1);
		matches.add(1);
		matches.add(1);
		matches.add(1);

		matches.add(2);
		matches.add(2);
		matches.add(2);
		matches.add(2);

		matches.add(3);
		matches.add(3);

		// n to n + 20 is the range, but we've already asserted there will be 10 matches.
		// Now we pad with non-matches
		for(int i = n; i < n + 20 - 10; ++i) {
			matches.add(0);
		}

		Collections.shuffle(matches);

		Iterator<Integer> current_match = matches.iterator();
		for(int i = n; i < n + 20; ++i) {
			int match_type = current_match.next();

			boolean match_visual = false;
			boolean match_aural = false;

			switch(match_type) {
				case 0:
					match_visual = false;
					match_aural = false;
					break;
				case 1:
					match_visual = true;
					match_aural = false;
					break;
				case 2:
					match_visual = false;
					match_aural = true;
					break;
				case 3:
					match_visual = true;
					match_aural = true;
					break;
			}

			level.add(Stimulus.make_random(level.get(i - n), match_visual, match_aural));
		}

		return level;
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
		if(!verifyStimulusChain(2, buildTestChain())) {
			debugAlert("Verifying stimulus chain sanity test failed! You found a bug!");
			return;
		}

		if(!verifyStimulusChain(2, genLevel(2))) {
			debugAlert("buildTestChain sanity test failed! You found a bug!");
			return;
		}
	}

	private void debugAlert(String alert) {
		new AlertDialog.Builder(this)
			.setMessage(alert)
			.show();
	}

	public void playLevel(int n, Grid grid)
	{
		ArrayList<Stimulus> level = genLevel(n);

		if(!verifyStimulusChain(n, genLevel(n))) {
			debugAlert("level generation sanity test failed! You found a bug!");
			return;
		}

		stimulus_thread_ = new Thread(new NextStimulus(grid, level.iterator()));
		stimulus_thread_.start();
	}
}
