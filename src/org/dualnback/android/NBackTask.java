package org.dualnback.android;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;

import android.media.MediaPlayer;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.widget.Button;

import java.util.Iterator;

public class NBackTask extends Activity
{
	private static final String TAG = "NBackTask";

	private Thread stimulus_thread_;

	private Iterator<Stimulus> current_stimuli_;
	private Level current_level_;
	private ScoreKeeper score_keeper_;
	private FeedbackRuler ruler_;

	private MediaPlayer sound_player_;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		Log.v(TAG, "Starting up...");
        super.onCreate(savedInstanceState);

		LinearLayout topLayout = new LinearLayout(this);
		topLayout.setOrientation(LinearLayout.VERTICAL);
		topLayout.setGravity(Gravity.FILL);
		topLayout.setWeightSum(1.0f);

		Grid grid = new Grid(this);
		topLayout.addView(grid, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.08f));

		ruler_ = new FeedbackRuler(this);
		ruler_.setBackgroundColor(0xFFFFFFFF);
		topLayout.addView(ruler_, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1));

		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

		Button audio_button = new Button(this);
		audio_button.setText("Aural");
		Button visual_button = new Button(this);
		visual_button.setText("Visual");

		audio_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ruler_.activate(score_keeper_.allegedAuralMatch());
				ruler_.invalidate();
			}
		});

		visual_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ruler_.activate(score_keeper_.allegedVisualMatch());
				ruler_.invalidate();
			}
		});

		buttonLayout.addView(audio_button, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));
		buttonLayout.addView(visual_button, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.5f));

		topLayout.addView(buttonLayout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.9f));
		setContentView(topLayout);

		sound_player_ = new MediaPlayer();
		play(grid, sound_player_);
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		stimulus_thread_.interrupt();
		setResult(RESULT_OK);
		finish();
	}

	public void play(Grid grid, MediaPlayer sound_player)
	{
		current_level_= new Level(this, 2);
		current_stimuli_ = current_level_.start();

		StimulusFlipper flipper = new StimulusFlipper(grid, sound_player, ruler_, current_stimuli_);
		score_keeper_ = new ScoreKeeper(current_level_, flipper);
		stimulus_thread_ = new Thread(flipper);

		stimulus_thread_.start();
	}
}
