package uk.co.mcld.dabble.bumbletab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GtrTabView extends View {
	private static final String TAG = "GtrTabView";

	private static final int rowSize = 20;
	private static final int rows = 6;
	private static final int colSize = 20;
	private static final int cols = 6;
	private static final int circSize = 20;

	protected int whichStrg = 3;
	protected int whichFret = 2;
	protected int usrStrg = 4;
	protected int usrFret = 8;


	/** Paint objects for styling various parts of the Arranger */
	public Paint 
		backgroundPaint, 
		rowDivisionPaint, 
		timeDivisionPaint,
		soundPaint,
		usrCircPaint;

	public GtrTabView(Context c, AttributeSet s) {
		super(c,s);
		init();
	}
	
	public GtrTabView(Context c) {
		super(c);
		init();
	}
	
	private void init() {
		
		backgroundPaint = new Paint();
		timeDivisionPaint = new Paint();
		rowDivisionPaint = new Paint();
		usrCircPaint = new Paint();
		soundPaint = new Paint(); // used only on bitmaps, doesn't do very much
		soundPaint.setAntiAlias(false);
		
		invalidate();
	}
	
	/**
	 * Refresh the user graphics
	 */
	@Override
	protected void onDraw(Canvas c) {

		c.drawRect(0,0,c.getWidth(),rowSize * rows,backgroundPaint);
		drawGrid(c);

		c.drawOval(new RectF(
				usrFret * rowSize,
				usrStrg * colSize,
				usrFret * rowSize + circSize,
				usrStrg * colSize + circSize
			),usrCircPaint);

		c.drawOval(new RectF(
				whichFret * rowSize,
				whichStrg * colSize,
				whichFret * rowSize + circSize,
				whichStrg * colSize + circSize
			),rowDivisionPaint);

//		int rowNum=0;
//		for (Arrangement.Row row : arrangement.rows) drawRow(c,row,rowNum++); // crow!
//		if (soundBeingMoved != null) 
//			drawSound(
//					c,
//					soundBeingMoved,
//					soundBeingMovedX,
//					soundBeingMovedY);
//		dashboard.draw(c);
	}
	
	/**
	 * Set up the size of the 
	 */
	
	/**
	 * Draw a visual mesh for the user sounds to fit in
	 * @param c The canvas to draw to
	 */
	private void drawGrid (Canvas c) {
		//int beyondLastRow = rowHeight*arrangement.rows.size() + 1;
		int width = c.getWidth();
		int height = c.getHeight();
		//int blockDivision = (int) pixelsPerAudioTick * ticksPerDivision;
		for ( int i = rowSize ; i < rows * rowSize ; i += rowSize) 
			c.drawLine(0, i, width, i, rowDivisionPaint);
		for ( int i = colSize; i < width ; i += colSize)
			c.drawLine(i, 0, i, height, timeDivisionPaint);
	}
	

}
