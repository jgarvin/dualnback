package org.dualnback.android;

import android.view.View;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.content.Context;

import android.util.Log;

public class Grid extends View {
    private ShapeDrawable drawable_;

	private static final String TAG = "Grid";

	private int lit_square_x_;
	private int lit_square_y_;

    public Grid(Context context) {
        super(context);

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 100;

        drawable_ = new ShapeDrawable(new RectShape());
        drawable_.getPaint().setColor(0xff0000ff);
        lightSquare(0, 0);
    }

	public void lightSquare(int x, int y) {
		lit_square_x_ = x;
		lit_square_y_ = y;
	}

	public void clear() {
		lit_square_x_ = -1;
		lit_square_y_ = -1;
	}

    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int square_width = getWidth() / 3;
		final int square_height = getHeight() / 3;
		
		int pos_x = lit_square_x_ * square_width;
		int pos_y = lit_square_y_ * square_height;

		drawable_.setBounds(pos_x, pos_y, pos_x + square_width, pos_y + square_height);

        drawable_.draw(canvas);
    }
}
