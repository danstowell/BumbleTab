package uk.co.mcld.dabble.bumbletab;

import java.util.Vector;

import android.util.Log;

import net.sf.supercollider.android.OscMessage;
import net.sf.supercollider.android.SCAudio;
import net.sf.supercollider.android.ScService;

public class BSoundManager {
	public static final int bufferSize = (int) (SCAudio.sampleRateInHz * 0.1 * 4); //16); //128); // MAKE IT MATCH NUMBER OF NOTES
	private static final String TAG = "BSoundManager";

	protected SCAudio superCollider;
	
	// hardwired node IDs, bus IDs, buffer IDs, for scsynth
    protected static final int recNode = 1998;
    protected static final int playNode = 1999;
    protected static final int targetBus = 20;
    protected static final int inpitchBus = 21;
    protected static final int recBuf = 0;
    
    public BSoundManager(SCAudio s) {
    	superCollider = s;
    	//initialiseSCforInput();
    }
    
    protected void initialiseSCforInput() {
    	//TODO: bus-set just for test
    	OscMessage busSetMsg = new OscMessage( new Object[] {
        		"c_set", targetBus, 74, inpitchBus, 77
        	});
    	superCollider.sendMessage( busSetMsg );

    	OscMessage bufferAllocMsg = new OscMessage( new Object[] {
        		"b_alloc",recBuf,bufferSize
        	});
    	Log.d(TAG,bufferAllocMsg.toString());
    	
    	while (SCAudio.hasMessages()) SCAudio.getMessage(); // clean out mailbox
    	superCollider.sendMessage( bufferAllocMsg );

    	// Wait on a positive response from SCAudio
    	OscMessage msgFromServer=null;
    	int triesToFail = 1500;
		while (msgFromServer==null && --triesToFail>0) {
    		if (SCAudio.hasMessages()) msgFromServer = SCAudio.getMessage();
    		try {
    			Thread.sleep(5);
    		} catch (InterruptedException e) {
    			break;
    		}
		}
		if (msgFromServer==null) {
			//return -1;
		}
		String firstToken = msgFromServer.get(0).toString();
		if (!firstToken.equals("/done")) {
			Log.e(TAG, "Bumble failed to receive /done for buffer alloc");
		}
    	superCollider.sendMessage(new OscMessage( new Object[] {
    			"notify", 1})); // register for notifications, so we know when synths end
    	superCollider.sendMessage(new OscMessage( new Object[] {
    			"s_new","bumbletab_rec", recNode, 0, 1, "targetBus", targetBus, "inpitchBus", inpitchBus, "recBuf", recBuf}));
    }

    protected void updateFromBusses(GtrTabView gtv){
    	int targetNote, usrNote;
    	
    	// STEP 1: get target note
    	
    	OscMessage busGetMsg = new OscMessage( new Object[] {
        		"c_get",targetBus
        	});
    	Log.d(TAG,busGetMsg.toString());
    	
    	while (SCAudio.hasMessages()) SCAudio.getMessage(); // clean out mailbox
    	superCollider.sendMessage( busGetMsg );

    	// Wait on a positive response from SCAudio
    	OscMessage msgFromServer=null;
    	int triesToFail = 500;
		while (msgFromServer==null && --triesToFail>0) {
    		if (SCAudio.hasMessages()) msgFromServer = SCAudio.getMessage();
    		try {
    			Thread.sleep(5);
    		} catch (InterruptedException e) {
    			break;
    		}
		}
		if (msgFromServer==null) {
			//return -1;
		}
		String firstToken = msgFromServer.get(0).toString();
		if (!firstToken.equals("/c_set")) {
			Log.e(TAG, "Bumble failed to receive /c_set - instead got " + firstToken);
		}else{
			targetNote = (int) ((Float)msgFromServer.get(2)).floatValue();
			
			Log.d(TAG, "Bumble RECEIVED pos from SC: " + targetNote + "; " + msgFromServer);
			
			// Convert midinote to guitar fret position
			// TODO: surely can be eleganter!
			switch(targetNote){
			case 52: case 53: case 54: case 55: case 56:
				gtv.whichStrg=5;
				gtv.whichFret=targetNote - 52;
				break;
			case 57: case 58: case 59: case 60: case 61:
				gtv.whichStrg=4;
				gtv.whichFret=targetNote - 57;
				break;
			case 62: case 63: case 64: case 65: case 66:
				gtv.whichStrg=3;
				gtv.whichFret=targetNote - 62;
				break;
			case 67: case 68: case 69: case 70:
				gtv.whichStrg=2;
				gtv.whichFret=targetNote - 67;
				break;
			case 71: case 72: case 73: case 74: case 75:
				gtv.whichStrg=1;
				gtv.whichFret=targetNote - 71;
				break;
			default:
				gtv.whichStrg=0;
				gtv.whichFret=targetNote - 76;
				break;
			}
		}
		
		// STEP 2 the user value
    	
    	busGetMsg = new OscMessage( new Object[] {
        		"c_get",inpitchBus
        	});
    	Log.d(TAG,busGetMsg.toString());
    	
    	while (SCAudio.hasMessages()) SCAudio.getMessage(); // clean out mailbox
    	superCollider.sendMessage( busGetMsg );

    	// Wait on a positive response from SCAudio
    	msgFromServer=null;
    	triesToFail = 500;
		while (msgFromServer==null && --triesToFail>0) {
    		if (SCAudio.hasMessages()) msgFromServer = SCAudio.getMessage();
    		try {
    			Thread.sleep(5);
    		} catch (InterruptedException e) {
    			break;
    		}
		}
		if (msgFromServer==null) {
			//return -1;
		}
		firstToken = msgFromServer.get(0).toString();
		if (!firstToken.equals("/c_set")) {
			Log.e(TAG, "Bumble failed to receive /c_set - instead got " + firstToken);
		}else{
			usrNote = (int) ((Float)msgFromServer.get(2)).floatValue();
			
			Log.d(TAG, "Bumble RECEIVED pos from SC: " + usrNote + "; " + msgFromServer);
			
			// Convert midinote to guitar fret position
			// TODO: surely can be eleganter!
			switch(usrNote){
			case 52: case 53: case 54: case 55: case 56:
				gtv.usrStrg=5;
				gtv.usrFret=usrNote - 52;
				break;
			case 57: case 58: case 59: case 60: case 61:
				gtv.usrStrg=4;
				gtv.usrFret=usrNote - 57;
				break;
			case 62: case 63: case 64: case 65: case 66:
				gtv.usrStrg=3;
				gtv.usrFret=usrNote - 62;
				break;
			case 67: case 68: case 69: case 70:
				gtv.usrStrg=2;
				gtv.usrFret=usrNote - 67;
				break;
			case 71: case 72: case 73: case 74: case 75:
				gtv.usrStrg=1;
				gtv.usrFret=usrNote - 71;
				break;
			default:
				gtv.usrStrg=0;
				gtv.usrFret=usrNote - 76;
				break;
			}
		}
		

		// STEP 3 update gui
		gtv.postInvalidate();
    }

	protected void startPlayback() {
		superCollider.sendMessage(new OscMessage( new Object[] {
    			"s_new","bumbletab_playback", playNode, 0, 1, "recBuf", recBuf}));
	}

	protected boolean detectRecEnd() {
		while (SCAudio.hasMessages()){
			Log.d(TAG, "BumbleTab - there are messages");
			OscMessage msgFromServer = SCAudio.getMessage();
			if (msgFromServer==null) {
				continue;
			}
			String firstToken = msgFromServer.get(0).toString();
			if (firstToken.equals("/n_end")) {
				Log.d(TAG, "BumbleTab detected /n_end");
				//TODO: not yet checking node ID
				return true;
			}else{
				Log.d(TAG, "BumbleTab detected something it would rather ignore: " + firstToken);
				//return false;
			}
		}
		return false;
	}
    
}
