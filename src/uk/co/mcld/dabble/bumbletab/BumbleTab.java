package uk.co.mcld.dabble.bumbletab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.supercollider.android.OscMessage;
import net.sf.supercollider.android.SCAudio;
import net.sf.supercollider.android.ScService;

public class BumbleTab extends Activity {
	private static final String TAG = "BumbleTab";
	private GtrTabView gtrTabView;
	private TabUpdateThread tabUpdateThread;
	
	public static final String dllDirStr = "/data/data/uk.co.mcld.dabble.bumbletab/lib"; // not very extensible, hard coded, generally sucks
	
	SCAudio superCollider = new SCAudio(dllDirStr);
	protected BSoundManager soundManager = new BSoundManager(superCollider); 

	public static final String[] mySynthDefs = {
		"bumbletab_rec.scsyndef",
		"bumbletab_playback.scsyndef",
	};

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	try {

    		File dataDir = new File(ScService.dataDirStr);
    		dataDir.mkdirs(); 
    		for (String synthdef : mySynthDefs )
    			pipeFile(synthdef, ScService.dataDirStr);
		} catch (IOException e) {
			Log.e(TAG,"Couldn't copy required files to the external storage device.");
			e.printStackTrace();
		}
        
		(new LaunchSCWhenFilesAreReady()).start();

		setContentView(R.layout.gtrtabview);
		gtrTabView = (GtrTabView) findViewById(R.id.gtrtabview);
		gtrTabView.backgroundPaint.setColor(getResources().getColor(android.R.color.background_light));
		gtrTabView.rowDivisionPaint.setColor(getResources().getColor(android.R.color.background_dark));
		gtrTabView.timeDivisionPaint.setColor(getResources().getColor(android.R.color.primary_text_light));
		gtrTabView.usrCircPaint.setColor(0x44668800);
    }

    private class TabUpdateThread extends Thread {
    	public void run(){
    		boolean recRunning = true;
    		while(recRunning){
    			try {
    				Thread.sleep(100L);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			soundManager.updateFromBusses(gtrTabView);
       	    }
    	}
    }
 
    @Override
    public void onPause() {
    	super.onPause();
		superCollider.sendMessage (OscMessage.quitMessage());

		while (!superCollider.isEnded()) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException err) {
				Log.e(TAG,"An interruption happened while ScanVox was waiting for SuperCollider to exit.");
				err.printStackTrace();
				break;
			}
		}
    }
    
    /**
     * Create files from assets, removing readonly state.
     * 
     * @param assetName
     * @param targetDir
     * @throws IOException
     * @author alex
     */
	protected void pipeFile(String assetName, String targetDir) throws IOException {
		InputStream is;
		if (assetName.startsWith("_")) is = getAssets().open("m"+assetName); // Android assets don't get copied out if they begin with _
		else is = getAssets().open(assetName);
		File target = new File(targetDir,assetName);
		OutputStream os = new FileOutputStream(target);
		byte[] buf = new byte[1024];
		int bytesRead = 0;
		while (-1 != (bytesRead = is.read(buf))) {
			os.write(buf,0,bytesRead);
		}
		is.close();
		os.close();
	}

	private class LaunchSCWhenFilesAreReady extends Thread {
		public static final int MAX_TRIES = 500;
		public static final long TIME_TWIXT_TRIES = 10;
		public void run () {
	    	try {

	    		File dataDir = new File(ScService.dataDirStr);
	    		dataDir.mkdirs(); 
	    		for (String synthdef : mySynthDefs )
	    			pipeFile(synthdef, ScService.dataDirStr);
			} catch (IOException e) {
				Log.e(TAG,"Couldn't copy required files to the external storage device.");
				e.printStackTrace();
			}
			
			try {
				for (String ass : getAssets().list("")) Log.d("TAG",String.format("Asset: '%s'",ass));
			} catch (IOException e) {
				Log.e(TAG,"Couldn't even LIST assets :(");
				e.printStackTrace();
			}

			boolean hasAllSynths = false;
			int remainingTries = MAX_TRIES;
    		File dataDir = new File(ScService.dataDirStr);

			while ((!hasAllSynths) && remainingTries-- > 0) {
				hasAllSynths = true;
				for (String synth : mySynthDefs) {
					if (!(new File(dataDir,synth).exists())) {
						hasAllSynths = false;
						break;
					}
				}
				if (!hasAllSynths) {
					try {
						sleep(TIME_TWIXT_TRIES);
					} catch (InterruptedException e) { }
				}
			}
			if (!hasAllSynths) {
				//errorMessage = "BumbleTab could not copy all of its data to this device's storage.  If you have removed your SD card, please re-insert it and try again.";
				//setUserActivity(UserActivity.FATAL_ERROR);
			} else {
				//superCollider.openUDP(57110); // TODO: can remove this when stable - using UDP for dev testing
				superCollider.start();
		    	soundManager.initialiseSCforInput();
		        
				tabUpdateThread = new TabUpdateThread();
				tabUpdateThread.start();
			}
		}
	}
}
