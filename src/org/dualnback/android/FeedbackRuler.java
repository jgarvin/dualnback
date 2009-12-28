package org.dualnback.android;

import android.util.Log;

import android.view.View;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.drawable.shapes.RectShape;

import java.util.*;

public class FeedbackRuler extends View {
	private static final String TAG = "FeedbackRuler";
	private ShapeDrawable drawable_;
	private Timer feedback_clear_;

	public FeedbackRuler(Context context) {
		super(context);

		drawable_ = new ShapeDrawable(new RectShape());
		drawable_.getPaint().setColor(0xff0000ff);
	}

	public void deactivate() {
		drawable_.getPaint().setColor(0xff0000ff);
	}

	public void activate(boolean good) {
		if(good)
			drawable_.getPaint().setColor(0xff00ff00);
		else
			drawable_.getPaint().setColor(0xffff0000);

		// feedback_clear_ = new Timer();

		// feedback_clear_.schedule(new TimerTask() {
		// 	public void run() {
		// 		try {
		// 			drawable_.getPaint().setColor(0xff0000ff);
		// 			postInvalidate();
		// 		} catch(Exception e) {
		// 			Log.v(TAG, e.toString());
		// 		}
		// 	}
		// }, 210);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawable_.setBounds(0, 0, getWidth(), getHeight());
        drawable_.draw(canvas);
    }
}
