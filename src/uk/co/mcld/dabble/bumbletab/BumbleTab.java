package uk.co.mcld.dabble.bumbletab;

import android.app.Activity;
import android.os.Bundle;

public class BumbleTab extends Activity {
	
	private GtrTabView gtrTabView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.gtrtabview);
		gtrTabView = (GtrTabView) findViewById(R.id.gtrtabview);
		gtrTabView.backgroundPaint.setColor(getResources().getColor(android.R.color.background_light));
		gtrTabView.rowDivisionPaint.setColor(getResources().getColor(android.R.color.background_dark));
		gtrTabView.timeDivisionPaint.setColor(getResources().getColor(android.R.color.primary_text_light));
        
    }
}