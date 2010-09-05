package uk.co.mcld.dabble.bumbletab;

import android.app.Activity;
import android.os.Bundle;
import java.util.Random;

import net.sf.supercollider.android.SCAudio;

public class BumbleTab extends Activity {
	
	private GtrTabView gtrTabView;
	private TabUpdateThread tabUpdateThread;
	
	public static final String dllDirStr = "/data/data/uk.co.mcld.dabble.bumbletab/lib"; // TODO: not very extensible, hard coded, generally sucks
	
	SCAudio superCollider = new SCAudio(dllDirStr);
	protected BSoundManager soundManager = new BSoundManager(superCollider); 

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: pipeFile stuff
        
        superCollider.openUDP(57110);
        superCollider.start();
    	soundManager.initialiseSCforInput();


		setContentView(R.layout.gtrtabview);
		gtrTabView = (GtrTabView) findViewById(R.id.gtrtabview);
		gtrTabView.backgroundPaint.setColor(getResources().getColor(android.R.color.background_light));
		gtrTabView.rowDivisionPaint.setColor(getResources().getColor(android.R.color.background_dark));
		gtrTabView.timeDivisionPaint.setColor(getResources().getColor(android.R.color.primary_text_light));
		gtrTabView.usrCircPaint.setColor(getResources().getColor(android.R.color.primary_text_light));
        
		tabUpdateThread = new TabUpdateThread();
		tabUpdateThread.start();
    }

    private class TabUpdateThread extends Thread {
    	public void run(){
    		for(int i=0; i < 100; i++){
    			try {
    				Thread.sleep(100L);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			soundManager.updateFromBusses(gtrTabView);
    			
    	    }
    	}
    	public void runOLD(){
    		Random generator = new Random();
    		for(int i=0; i < 100; i++){
    			try {
    				Thread.sleep(100L);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			gtrTabView.whichStrg = generator.nextInt(6);
    			gtrTabView.whichFret = generator.nextInt(10);
    			gtrTabView.postInvalidate();
    	    }
    	}
    }

}
