package uk.co.mcld.dabble.bumbletab;

import java.util.Vector;

import android.util.Log;

import net.sf.supercollider.android.OscMessage;
import net.sf.supercollider.android.SCAudio;
import net.sf.supercollider.android.ScService;

public class BSoundManager {
	public static final int bufferSize = (int) (SCAudio.sampleRateInHz * 0.1 * 64); // MAKE IT MATCH NUMBER OF NOTES
	private static final String TAG = "BSoundManager";

	protected SCAudio superCollider;
	
	// hardwired node IDs, bus IDs, buffer IDs, for scsynth
    protected static final int recNode = 1998;
    protected static final int playNode = 1999;
    protected static final int targetBus = 0;
    protected static final int inpitchBus = 1;
    protected static final int recBuf = 0;
    
    public BSoundManager(SCAudio s) {
    	superCollider = s;
    	initialiseSCforInput();
    }
    
    private void initialiseSCforInput() {
    	OscMessage bufferAllocMsg = new OscMessage( new Object[] {
        		"b_alloc",recBuf,bufferSize
        	});
    	Log.d(TAG,bufferAllocMsg.toString());
    	
    	while (SCAudio.hasMessages()) SCAudio.getMessage(); // clean out mailbox
    	superCollider.sendMessage( bufferAllocMsg );

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
		if (!firstToken.equals("/done")) {
			Log.e(TAG, "Bumble failed to receive /done for buffer alloc");
		}
    	superCollider.sendMessage(new OscMessage( new Object[] {
    			"s_new","bumbletab_rec", recNode, 0, 1, "targetBus", targetBus, "inpitchBus", inpitchBus, "recBuf", recBuf}));
    }

    
    
}
