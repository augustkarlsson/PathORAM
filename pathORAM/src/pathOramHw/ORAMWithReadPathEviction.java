package pathOramHw;

import java.util.ArrayList;

/*
 * Name: TODO
 * NetID: TODO
 */

public class ORAMWithReadPathEviction implements ORAMInterface{
	
	/**
	 * TODO add necessary variables 
	 */
	
	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO complete the constructor
	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public int P(int leaf, int level) {
		// TODO Must complete this method for submission
		return 0;
	}


	@Override
	public int[] getPositionMap() {
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public ArrayList<Block> getStash() {
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public int getStashSize() {
		// TODO Must complete this method for submission
		return 0;
	}

	@Override
	public int getNumLeaves() {
		// TODO Must complete this method for submission
		return 0;
	}


	@Override
	public int getNumLevels() {
		// TODO Must complete this method for submission
		return 0;
	}


	@Override
	public int getNumBlocks() {
		// TODO Must complete this method for submission
		return 0;
	}


	@Override
	public int getNumBuckets() {
		// TODO Must complete this method for submission
		return 0;
	}


	
}
