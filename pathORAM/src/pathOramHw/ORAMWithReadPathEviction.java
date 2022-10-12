package pathOramHw;

import java.util.ArrayList;
import java.util.Arrays;
/*
 * Name: Antoine Gansel, August Karlsson
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
	//private int lowest_leave; // wanted_leave = lowest_leave + rand_gen.getRandomLeaf()
	private int stash_size;


	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		this.storage = storage;
		this.rand_gen = rand_gen;
		this.num_levels = (int) (Math.log(num_blocks)/Math.log(2));
		this.num_leaves = (int) Math.pow(2,num_levels-1);
		this.bucket_size= bucket_size;
		this.num_blocks = num_blocks;
		this.num_buckets = 0;
		for (int i = 0; i<num_levels; i++) {
			this.num_buckets = this.num_buckets + (int) Math.pow(2,i);
		}
		this.num_blocks = (int) this.num_buckets * this.bucket_size;
		this.stash_size = -1;
		this.rand_gen.setBound(this.num_leaves);

		this.position_map = this.init_map();
		Bucket.resetState();
		Bucket.setMaxSize(bucket_size);
		storage.setCapacity(this.num_buckets); // We set our capacity to the number of buckets we have

		stash = new ArrayList<Block>();

		for (int i = 0; i < num_buckets; i++) {
			Bucket bucket = new Bucket();
			for (int j = 0; j < bucket_size; j++) {
				bucket.addBlock(new Block());
			}
			storage.WriteBucket(i, bucket);
		}
	}

	private int[] init_map(){
		int[] map = new int[this.num_blocks]; // need to keep record of position for every possible doc
		for (int i = 0; i < this.num_blocks; i++) {
			map[i] =  rand_gen.getRandomLeaf();//this.num_leaves-1;
		}
		return map;
	}

	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// 		 *	- [execution] need to implement algorithm from slides page 51 to 54
	
		byte[] res = new byte[newdata.length] ;

		int a = position_map[blockIndex];
		position_map[blockIndex] = rand_gen.getRandomLeaf();

		/*
		 * for l in {0,1,...,L}
		 * 		S <-- S U ReadBucket(P(x,l))
		 * end for
		 */
		for (int level = 1; level < num_levels; level++) {
			int current = P(a,level);
			Bucket current_bucket = storage.ReadBucket(current);
			ArrayList<Block> current_content = current_bucket.getBlocks();
			if (current_content != null) {
				for (Block block : current_content) {
					if (block.index != -1) {
						stash.add(block);
					}
				}
			}
		}
		

		
		int index_a = -1;
		stash_size = stash.size();
		for (int i = 0; i < stash_size; ++i) {
			Block b = stash.get(i);
			if (b.index == blockIndex){
				index_a = i;
				res = b.data; // NEED TO TRANSFORM INTO A COPY
			}
		}
		if  (op == Operation.WRITE){
			if (index_a == -1) { //the index was not used
				stash.add(new Block(blockIndex, newdata));
			}
			else {
				Block b = stash.get(index_a); // you had put this part inside the loop and you were using i
				for (int i = 0; i < newdata.length; ++i) {
					b.data[blockIndex] = newdata[i];
				}
				b.index = blockIndex; // set new blockIndex
			}
		}

		
		stash_size = stash.size();

		for (int level = num_levels; level > 0; level--) {
			ArrayList<Integer> stash_ToWrite = new ArrayList<Integer>();
			int current = P(a,level);
			int counter = 0;
			Bucket bucket = new Bucket();
			// S′←{(a0,data0) ∈ S : P(x,l) = P(position[a0],l)}
			// S′←Select min(|S′|,Z) blocks from S′
			for (Block b: stash) {
				if (counter < bucket_size) {
					Block to_write = b; 
					if (current == P(position_map[to_write.index],level)) {
						bucket.addBlock(to_write);
						stash_ToWrite.add(to_write.index);
						counter++;
					}
				}
			}


			// S ←S −S′
			for (int i = 0; i < stash_ToWrite.size(); i++)
			{
				for (int j = 0; j < stash.size(); j++) 
				{
					if (stash.get(j).index == stash_ToWrite.get(i))
					{
						stash.remove(j);
					}
				}
			}
			//WriteBucket(P(x,l),S′)
			while (counter < bucket_size) {
				bucket.addBlock(new Block());
				counter++;
			}
			storage.WriteBucket(current, bucket);
		}


		return res;
	
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

		leaf = leaf + (int) Math.pow(2,this.num_levels-1)-1 ;
		//should be correct
		if (level == this.num_levels) return leaf;

		int current_level = this.num_levels;

		int index = leaf;
		while (current_level > level) {
			index = (int) Math.floor((index-1)/2) ;
			current_level--;
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
		return this.stash_size;
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
