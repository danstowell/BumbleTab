package uk.co.mcld.dabble.bumbletab;

import java.util.Vector;

import net.sf.supercollider.android.OscMessage;
import net.sf.supercollider.android.SCAudio;
import net.sf.supercollider.android.ScService;

public class BSoundManager {
	public static final long bufferSize = (long) (SCAudio.sampleRateInHz * 0.1 * 64); // MAKE IT MATCH NUMBER OF NOTES
	private static final String TAG = "BSoundManager";

	protected SCAudio superCollider;
	
	// hardwired node IDs, bus IDs, buffer IDs, for scsynth
    protected static final int recNode = 1998;
    protected static final int playNode = 1999;
    protected static final int targetBus = 0;
    protected static final int inpitchBus = 1;
    
    
    
    
    
    
    
}
