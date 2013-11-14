package com.zhideel.tapathon.ui;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.zhideel.tapathon.R;

import java.util.ArrayList;
import java.util.Random;

public class MultiTouchView extends View {

    public enum GameLevel
    {
        EASY, MEDIUM, HARD;
    }

    // Usually this can be a field rather than a method variable
	private static Random rand = new Random();
	private static final int SIZE = 60;
	private SparseArray<PointF> mActivePointers;
	private Paint mPaint;
	private int[] colors = { Color.BLUE, Color.MAGENTA, Color.RED, Color.YELLOW };
	private boolean isSelected;
	private static boolean isContinue = true;
	private Paint textPaint;
	private String currentText;
	private static GameLevel selectedLevel;
	private int minDelay, maxDelay;

	public MultiTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public static void setLevel(GameLevel level){
		selectedLevel = level;
	}
	
	public static void setContinue(boolean cont){
		isContinue = cont;
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
		
		if (selectedLevel == GameLevel.EASY){
			minDelay = 3000;
			maxDelay = 5000;
		}
		else if (selectedLevel == GameLevel.MEDIUM){
			minDelay = 2000;
			maxDelay = 4000;
		}else if (selectedLevel == GameLevel.HARD){
			minDelay = 1500;
			maxDelay = 2500;
		}
		
		randomPaint();
		randText();
	}

	private void randomPaint() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if ((!isSelected) && (isContinue)) {
					MultiTouchView.this.setBackgroundColor(colors[randInt(0, 3)]);
				} else if (isContinue){
					MultiTouchView.this.setBackgroundColor(Color.WHITE);
					//MultiTouchView.this.setBackground(MultiTouchView.this.getResources().getDrawable(R.drawable.fire_alert));
				} else {
					MultiTouchView.this.setBackgroundColor(Color.BLACK);
				}
				invalidate();
				randomPaint();
				randText();
			}
		}, getRandomDelay());
	}

	private void randomShock() {
		Bitmap fireAlert = BitmapFactory.decodeResource(getResources(), R.drawable.fire_alert);
	}

	private int getRandomDelay() {
		return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? minDelay : maxDelay;
	}

	public int randInt(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private void randText() {
		if ((!isSelected) && (isContinue)) {
			int rand = randInt(0, 13);
			if (rand == 10) {
				currentText = "+";
			} else if (rand == 11) {
				currentText = "-";
			} else if (rand == 12) {
				currentText = "X";
			} else if (rand == 13) {
				currentText = "/";
			} else {
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
			ArrayList<Integer> operands = ((GamePadActivity) super.getContext()).getGameBoard().getOperands();
			String operator = ((GamePadActivity) super.getContext()).getGameBoard().getOperator();
			
			try {
				int number = Integer.parseInt(currentText);
				if (operands.size() < 2){
					((GamePadActivity) super.getContext()).getGameBoard().addOperand(number);
					this.isSelected = true;
					if ((operands.size() == 2) && (operator != null)) {
						int result = ((GamePadActivity) super.getContext()).getGameBoard().doCalc();
						Toast.makeText(getContext(), Integer.toString(result), Toast.LENGTH_SHORT).show();
						((GamePadActivity) super.getContext()).getGameBoard().resetCurrent();
					}
				}
				
			} catch (NumberFormatException e) {
				String cOperator = currentText;
				if (operator == null){
					((GamePadActivity) super.getContext()).getGameBoard().setOperator(cOperator);
					this.isSelected = true;
					if (operands.size() == 2) {
						int result = ((GamePadActivity) super.getContext()).getGameBoard().doCalc();
						Toast.makeText(getContext(), Integer.toString(result), Toast.LENGTH_SHORT).show();
						((GamePadActivity) super.getContext()).getGameBoard().resetCurrent();
					}
				}
			}
			// We have a new pointer. Lets add it to the list of pointers
			PointF f = new PointF();
			f.x = event.getX(pointerIndex);
			f.y = event.getY(pointerIndex);
			mActivePointers.put(pointerId, f);
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
			if (point != null) {
				mPaint.setColor(colors[i % 9]);
			}
			canvas.drawCircle(point.x, point.y, SIZE, mPaint);
		}
		if (mActivePointers.size() >= 3) {
			this.setBackgroundColor(getResources().getColor(R.color.tappad_green));
			Bitmap fireAlert = BitmapFactory.decodeResource(getResources(), R.drawable.fire_alert);
			canvas.drawBitmap(getResizedBitmap(fireAlert, canvas.getHeight(), canvas.getWidth()), 0, 0, null);
		}
		canvas.drawText(currentText, this.getWidth() / 2 - 20, this.getHeight() / 2 + 40, textPaint);
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