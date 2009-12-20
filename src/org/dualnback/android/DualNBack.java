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
import org.dualnback.android.Pair;
import org.dualnback.android.Stimulus;

import android.widget.Button;

import java.util.*;

import android.app.AlertDialog;

class StimulusFlipper extends Thread
{
	private static final String TAG = "StimulusFlipper";
	
	private Grid grid_;

	private Iterator<Stimulus> stimuli_iter_;
	private int current_stimulus_index_;

	StimulusFlipper(Grid to_light, Iterator<Stimulus> start_iterator) {
		super();

		grid_ = to_light;
		stimuli_iter_ = start_iterator;
		current_stimulus_index_ = -1;
	}

	private void realWait(long ms) {
		Log.v(TAG, String.format("%dms wait", ms));
		try {
			sleep(ms);
		} catch(InterruptedException e) {}		
	}

	public void run() {
		Log.v(TAG, "Starting stimulus thread...");
		
		while(stimuli_iter_.hasNext()) {
			Log.v(TAG, "Begin stimulus display loop iteration");

			Stimulus current;
			synchronized(stimuli_iter_) {
				current = stimuli_iter_.next();
				current_stimulus_index_ += 1;
			}
			
			current.playSound();
			current.displayVisual(grid_);
			grid_.postInvalidate();

			realWait(500);
			
			grid_.clear();
			grid_.postInvalidate();

			realWait(2500);
		}

		Log.v(TAG, "Finished playing back stimulus.");
	}

	public int currentStimulusIndex() {
		synchronized(stimuli_iter_) {
			return current_stimulus_index_;
		}
	}
}

class Scorer extends Object
{
	private int visualMatches;
	private int auralMatches;

	Level level_;
	private StimulusFlipper flipper_;

	Scorer(Level level, StimulusFlipper flipper)
	{
		level_ = level;
		flipper_ = flipper;
	}

	public void allegedVisualMatch() {
		if(level_.visualMatch(flipper_.currentStimulusIndex()))
			visualMatches += 1;
	}

	public void allegedAuralMatch() {
		if(level_.auralMatch(flipper_.currentStimulusIndex()))
			auralMatches += 1;
	}
}

class Level extends Object
{
	private static final String TAG = "DualNBack";

	private ArrayList<Stimulus> stimuli_;
	private int n_;

	private DualNBack game_;

	Level(DualNBack game, int n)
	{
		n_ = n;
		game_ = game;
		stimuli_ = genLevel(n);

		sanityTest();
	}

	public Iterator<Stimulus> start() {
		return stimuli_.iterator();
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

			level.add(Stimulus.makeRandom(level.get(i - n), match_visual, match_aural));
		}

		if(!verifyStimulusChain(n, level)) {
			debugAlert("level generation sanity test failed! You found a bug!");
			return null;
		}

		return level;
	}

	public boolean visualMatch(int i) {
		if(i < n_) {
			return false;
		}

		return stimuli_.get(i).visualMatch(stimuli_.get(i - n_));
	}

	public boolean auralMatch(int i) {
		if(i < n_) {
			return false;
		}

		return stimuli_.get(i).auralMatch(stimuli_.get(i - n_));
	}

	private static boolean verifyStimulusChain(int n, ArrayList<Stimulus> chain)
	{
		Log.v(TAG, "Verifying chain...");

		int visualMatches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).visualMatch(chain.get(i + n)) &&
			   !chain.get(i).auralMatch(chain.get(i + n))) {
				Log.v(TAG, "Visual match");
				++visualMatches;
			}
		}

		if(visualMatches != 4) {
			Log.v(TAG, "Wrong number of visual matches");
			return false;
		}

		int auralMatches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).auralMatch(chain.get(i + n)) &&
			   !chain.get(i).visualMatch(chain.get(i + n))) {
				Log.v(TAG, "Aural match");
				++auralMatches;
			}
		}

		if(auralMatches != 4) {
			Log.v(TAG, "Wrong number of audio matches");
			return false;
		}

		int dual_matches = 0;
		for(int i = 0; i < chain.size() - n; ++i) {
			if(chain.get(i).auralMatch(chain.get(i + n)) &&
			   chain.get(i).visualMatch(chain.get(i + n))) {
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

	private static ArrayList<Stimulus> buildTestChain()
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
		new AlertDialog.Builder(game_)
			.setMessage(alert)
			.show();
	}
}

public class DualNBack extends Activity
{
	private static final String TAG = "DualNBack";

	private Thread stimulus_thread_;

	private Iterator<Stimulus> current_stimuli_;
	private Level current_level_;

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

		play(grid);
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		setResult(RESULT_OK);
		finish();
	}

	public void play(Grid grid)
	{
		current_level_= new Level(this, 2);
		
		current_stimuli_ = current_level_.start();
		stimulus_thread_ = new Thread(new StimulusFlipper(grid, current_stimuli_));
		
		stimulus_thread_.start();
	}
}
