package com.zhideel.tapathon;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MultiTouchView extends View {
	// Usually this can be a field rather than a method variable
    private static Random rand = new Random();
    private static final int SIZE = 60;
    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.RED, Color.YELLOW};
    private boolean isSelected;
    private Paint textPaint;
    private String currentText;


    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // set painter color to a color you like
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(5.0f, 5.0f, 5.0f, Color.BLACK);
        textPaint.setTextSize(100);

        randomPaint();
        randText();
    }

    private void randomPaint() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(!isSelected){
                	MultiTouchView.this.setBackgroundColor(colors[randInt(0, 5)]);
                }
                else
                {
                	MultiTouchView.this.setBackground(MultiTouchView.this.getResources().getDrawable(R.drawable.fire_alert));
                }
                invalidate();
                randomPaint();
                randText();
            }
        }, getRandomDelay());
    }

    private void randomShock() {
        Bitmap fireAlert = BitmapFactory.decodeResource(getResources(),
                R.drawable.fire_alert);
    }

    private int getRandomDelay() {
       return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? 500 : 1000;
    }


    public int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    private void randText() {
    	if(!isSelected) {
    		int rand = randInt(0, 13);
    		if (rand == 10){
    			currentText = "+";
    		}
    		else if (rand == 11){
    			currentText = "-";
    		}
    		else if (rand == 12){
    			currentText = "X";
    		}
    		else if (rand == 13){
    			currentText = "/";
    		}
    		else {
    			currentText = Integer.toString(rand);
    		}
    	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
            	this.isSelected = true;
            	try{
            		((GamePadActivity) super.getContext()).addOperand(Integer.parseInt(currentText));
            	}catch(NumberFormatException e)
            	{
            		((GamePadActivity) super.getContext()).setOperator(currentText);
            	}
                // We have a new pointer. Lets add it to the list of pointers
            	
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                Toast.makeText(getContext(), currentText, Toast.LENGTH_SHORT).show();
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                break;
            }
        }
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null)
                mPaint.setColor(colors[i % 9]);
            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
        }
        if (mActivePointers.size() >= 3) {
            this.setBackgroundColor(getResources().getColor(R.color.tappad_green));

            Bitmap fireAlert = BitmapFactory.decodeResource(getResources(),
                    R.drawable.fire_alert);

            canvas.drawBitmap(getResizedBitmap(fireAlert, canvas.getHeight(), canvas.getWidth()), 0 , 0, null);
        }
       canvas.drawText(currentText, this.getWidth()/2 - 20, this.getHeight()/2 + 40, textPaint);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

} 