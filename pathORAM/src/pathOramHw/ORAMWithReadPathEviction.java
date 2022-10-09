package pathOramHw;

import java.util.ArrayList;

/*
 * Name: TODO
 * NetID: TODO
 */

public class ORAMWithReadPathEviction implements ORAMInterface{
	
	/*
	 * Already present variables 
	 */
	private UntrustedStorageInterface storage;
	private RandForORAMInterface rand_gen;

	private ArrayList<Block> stash;
	private int[] position_map;
	private int num_buckets;// Need to be greater or equal to 30.000/4 = 7500 (to feat all write operations)
	private int num_blocks;
	private int num_levels;
	private int num_leaves;
	private int bucket_size;

	/*
	 * Student defined variables
	 */
	


	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO complete the constructor
		this.storage = storage;
		this.rand_gen = rand_gen;
		this.bucket_size= bucket_size;
		this.num_blocks = num_blocks;

		this.num_buckets = 8000;
		this.num_levels = this.find_num_levels() 
		this.num_leaves = this.find_num_leaves()

		Bucket.setMaxSize(bucket_size);
		Bucket.resetState();

		storage.setCapacity(num_buckets); // We set our capacity to the number of buckets we have

	}

	private int find_num_levels() {
		int n = 1;
		while (this.num_buckets % Math.pow(2,n) >= 2) {
			// we search n such that 2^n <= nb_buckets < 2^(n+1)
			n++;
		}
		return n+1 ;
	}

	private int find_num_leaves() {
		int last_leaves = this.num_buckets - (this.num_levels -1) ;
		int secondLast_leaves = 0;
		if (this.num_buckets != Math.pow(2,this.num_levels)) {
			int nodes_secondLast_level = Math.pow(2,this.num_levels-1)-1-Math.pow(2,this.num_levels-2)-1 ;
			secondLast_leaves = nodes_secondLast_level - Math.ceil(last_leaves/2) ;
		}
		return last_leaves + secondLast_leaves ;
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
				 *  - 
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
			 * ex : 123456789
			 			1
					  /   \
					 2     3
					/ \   / \
				   4   5 6   7
				   8   9
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
