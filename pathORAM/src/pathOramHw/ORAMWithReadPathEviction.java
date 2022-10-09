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



	private UntrustedStorageInterface storage;
	private RandForORAMInterface rand_gen;

	private ArrayList<Block> stash;
	private int[] position_map;
	private int num_buckets;
	private int num_blocks;
	private int num_levels;
	private int num_leaves;
	private int bucket_size;


	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO complete the constructor
		this.storage = storage;
		this.rand_gen = rand_gen;
		this.bucket_size= bucket_size;
		this.num_blocks = num_blocks;

		Bucket.setMaxSize(bucket_size);
		Bucket.resetState();

	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		Switch (op) {
			case READ :
				res = storage.ReadBucket(blockIndex)
				return res;
			case WRITE :
				/*
				 * What need to be done :
				 *	- [understanding] create a map between bucket tree format (for us) and List format (in untrusted storage).
				 *		| Also needed for function right below
				 *	- [execution] need to implement algorithm from slides page 51 to 54
				 */
				return null;
			default :
				break;
		}
		
		return null;
	}


	@Override
	public int P(int leaf, int level) {
		// TODO Must complete this method for submission
			/*
			 * What need to be done :
			 *	- create a map between bucket tree format (for us) and List format (in untrusted storage)
			 *	- output the result of this map
			 */
		return 0;
	}


	@Override
	public int[] getPositionMap() {
		return this.position_map;
	}


	@Override
	public ArrayList<Block> getStash() {
		return this.stash;
	}


	@Override
	public int getStashSize() {
		return this.stash.size();
	}

	@Override
	public int getNumLeaves() {
		return this.num_leaves;
	}


	@Override
	public int getNumLevels() {
		return this.num_levels;
	}


	@Override
	public int getNumBlocks() {
		return this.num_blocks;
	}


	@Override
	public int getNumBuckets() {
		return this.num_buckets;
	}


	
}
