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

	private ArrayList<Block> stash; // looks like it allocates memory dynamically
	private int[] position_map;
	private int num_buckets;
	private int num_blocks;
	private int num_levels;
	private int num_leaves;
	private int bucket_size;

	/*
	 * Student defined variables
	 */
	private int lowest_leave; // wanted_leave = lowest_leave + rand_gen.getRandomLeaf()


	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO complete the constructor
		this.storage = storage;
		this.rand_gen = rand_gen;
		this.bucket_size= bucket_size;
		this.num_blocks = num_blocks;

		this.num_buckets = num_blocks/bucket_size; // 4 blocks by buckets, need to be able to fit everythin
		this.num_levels = this.find_num_levels();
		int[] res_num_leaves = this.find_num_leaves();
		this.num_leaves = res_num_leaves[0];
		this.lowest_leave = res_num_leaves [1];
		this.rand_gen.setBound(this.num_leaves);

		this.position_map = this.init_map();
		Bucket.resetState();

		storage.setCapacity(num_buckets); // We set our capacity to the number of buckets we have

	}

	private int find_num_levels() {

		return (int) Math.ceil(Math.log10(num_buckets)/Math.log10(2))+1;
		
		//int n = 1;
		//while (this.num_buckets % Math.pow(2,n) >= 2) {
		//	// we search n such that 2^n <= nb_buckets < 2^(n+1)
		//	n++;
		//}
		//return n+1 ;
	
	}
	

	private int[] find_num_leaves() {
		/*
		 * ex : 01234567
					0           num_levels = 4  | num_buckets = 9
				  /   \         last_leaves = 2 = 9 - 7 = 9 - 8 + 1
				 1     2                    = num_buckets - 2^(num_levels-1) + 1
			    / \   / \
			   3   4 5   6		nodes_secondLast_level = 4 - 1 = 4 - 2/2 = 2^(num_levels-1) - 2^(num_levels-2)
			  / \               secondLast_leaves = 4 - 2 = 4 - 2/2 
			 7   8								  = nodes_secondLast_level - ceiling(last_leaves/2)
											/!\ ceiling is to not have issue when one leaf does not have siblings /!\
		 */
		int last_leaves = this.num_buckets -(int) Math.pow(2,this.num_levels-1) + 1 ;
		int secondLast_leaves = 0;
		int lowest_leave = 0;
		if (this.num_buckets != Math.pow(2,this.num_levels)) {
			int nodes_secondLast_level = (int) Math.pow(2,this.num_levels-1)- (int) Math.pow(2,this.num_levels-2) ;
			secondLast_leaves = nodes_secondLast_level - (int) Math.ceil(last_leaves/2) ;
			lowest_leave = (int) Math.pow(2,this.num_levels-1) - secondLast_leaves; // lowest leave = lowest leave of Last_level - leaves on secondLast_level
		}
		return new int[]{last_leaves + secondLast_leaves, lowest_leave};
	}

	private int[] init_map(){
		int[] map = new int[this.num_blocks]; // need to keep record of position for every possible doc
		for (int i = 0; i < this.num_blocks; i++) {
			map[i] = this.num_leaves-1;
		}
		return map;
	}

	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// Switch (op) {
		// 	case READ :
		// 		res = storage.ReadBucket(blockIndex)
		// 		return res;
		// 	case WRITE :
		// 		/*
		// 		 * What need to be done :
		// 		 *	- [understanding] create a map between bucket tree format (for us) and List format (in untrusted storage).
		// 		 *		| Also needed for function right below
		// 		 *	- [execution] need to implement algorithm from slides page 51 to 54
		// 		 *  - 
		// 		 */
		// 		return null;
		// 	default :
		// 		break;
		// }
		// REMAP BLOCK

		byte[] res = new byte[newdata.length] ;
		res = newdata;
		
		System.out.println("lowest leave" + this.lowest_leave);
		System.out.println("position map" + position_map[blockIndex]);

		int x =  position_map[blockIndex]; // indice starts from 0, not 1
		position_map[blockIndex] = rand_gen.getRandomLeaf();

		// READ PATH, i.e copy every block on every level to stash S
		// Traversing the path P with all values of L (levels)
		for (int level = 0; level < num_levels; ++level)
		{
			ArrayList<Block> all_blocks = storage.ReadBucket(P(x,level)).getBlocks();
			for (Block block : all_blocks)
				if (block.index != -1)
					stash.add(block);
		}

		boolean done= false;
		for (int i = 0; i < stash.size() || done ; ++i)
		{
			Block b = stash.get(i);
			if (b.index == blockIndex) {
				//UPDATE BLOCK, Read block from stash if OP code is WRITE
				if (op == Operation.WRITE) {
					b.data = res;
				}
				// READ BL 
				else { // op == Operation.READ
					res = b.data;
				}
				done = true;
			}
		}
		
		//WRITE PATH
		// I think you need to go up the levels, not down
		// Okay I'll write it out on paper and check!

		for (int level = num_levels; level > 0; level--) {
			ArrayList<Integer> to_be_written = new ArrayList<Integer>();
			Bucket bucket = new Bucket();
			int path_to_level = P(x, level);
			int counter = 0;
			for (Block b: stash) {
				if (counter >= bucket_size)
					break;
				Block to_write = b; 
				if (path_to_level == P(position_map[to_write.index],level)) {
					bucket.addBlock(to_write);
					to_be_written.add(to_write.index);
					counter++;
				}

			}

			//remove from stash
			for (int i = 0; i < to_be_written.size(); i++)
			{
				for (int j = 0; j < stash.size(); j++) {
					if (stash.get(j).index == to_be_written.get(i))
					{
						stash.remove(j);
					}
				}
			}

			while (counter < bucket_size) {
				bucket.addBlock(new Block());
				counter++;
			}
			
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
			 * ex : 012345678
			 			0
					  /   \
					 1     2        ==> parent of leave = Math.floor((leave-1)/2)
					/ \   / \
				   3   4 5   6
				  / \
				 7   8
			 */
		if (leaf > this.num_leaves) {
			throw new RuntimeException("[INVALID PARAM 'LEAF'] : should be between 0 (INCLUSIVE) and num_leaves (EXCLUSIVE)");
		}
		leaf += this.lowest_leave ;
		int current_level;
		if (leaf < Math.pow(2,this.num_levels)) {
			current_level = this.num_levels -1 ;
		} else {
			current_level = this.num_levels ;
		}

		int index = leaf;
		while (current_level > level) {
			index = (int) Math.floor((current_level-1)/2) ;
		}

		return index;
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
