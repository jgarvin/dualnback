package org.dualnback.android;

import android.view.View;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.content.Context;

import android.util.Log;

public class Grid extends View {
    private ShapeDrawable mDrawable;

	private static final String TAG = "Grid";

	private int lit_square_x;
	private int lit_square_y;

    public Grid(Context context) {
        super(context);

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 100;

        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff0000ff);
        lightSquare(0, 0);
    }

	public void lightSquare(int x, int y) {
		lit_square_x = x;
		lit_square_y = y;
	}

    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int square_width = getWidth() / 3;
		final int square_height = getHeight() / 3;
		
		int pos_x = lit_square_x * square_width;
		int pos_y = lit_square_y * square_height;

		mDrawable.setBounds(pos_x, pos_y, pos_x + square_width, pos_y + square_height);

        mDrawable.draw(canvas);
    }
}
