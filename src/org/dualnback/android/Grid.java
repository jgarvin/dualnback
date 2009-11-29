package org.dualnback.android;

import android.view.View;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.content.Context;

public class Grid extends View {
    private ShapeDrawable mDrawable;

    public Grid(Context context) {
        super(context);

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 100;

        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff0000ff);
        mDrawable.setBounds(x, y, x + width, y + height);
    }

    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        mDrawable.draw(canvas);
    }
}
