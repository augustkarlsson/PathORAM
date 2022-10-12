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
	private int lowest_leave; // wanted_leave = lowest_leave + rand_gen.getRandomLeaf()


	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		this.storage = storage;
		this.rand_gen = rand_gen;
		this.bucket_size= bucket_size;
		this.num_blocks = num_blocks;

		this.num_buckets = (int) Math.ceil(((double) num_blocks)/((double) bucket_size)); // 4 blocks by buckets, need to be able to fit everything
		this.num_levels = this.find_num_levels();
		int[] res_num_leaves = this.find_num_leaves();
		this.num_leaves = res_num_leaves[0];
		this.lowest_leave = res_num_leaves [1];
		this.rand_gen.setBound(this.num_leaves);

		this.position_map = this.init_map();
		Bucket.resetState();
		Bucket.setMaxSize(bucket_size);
		storage.setCapacity(num_buckets); // We set our capacity to the number of buckets we have

		stash = new ArrayList<Block>();

		for (int i = 0; i < num_buckets; i++) {
			Bucket bucket = new Bucket();
			for (int j = 0; j < bucket_size; j++) {
				bucket.addBlock(new Block());
			}
			storage.WriteBucket(i, bucket);
		}
	}

	private int find_num_levels() {
		return (int) Math.ceil(Math.log10(num_buckets)/Math.log10(2))+1;
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
			map[i] =  rand_gen.getRandomLeaf();//this.num_leaves-1;
		}
		return map;
	}

	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// 		 *	- [execution] need to implement algorithm from slides page 51 to 54

		//System.out.println("newdata is " + newdata + "\n");
	
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
		for (int i = 0; i < stash.size(); ++i) {
			Block b = stash.get(i);
			if (b.index == blockIndex);
				index_a = i;
				System.out.println("NEED TO TRANSFORM INTO A COPY LIGN 134");
				res = b.data; // NEED TO TRANSFORM INTO A COPY
		}
		if  (op == Operation.WRITE){
			if (index_a == -1) {
				stash.add(new Block(blockIndex, newdata));
				System.out.println(""+ Arrays.toString(stash.get(stash.size()-1).data));
			}
			else {
				Block b = stash.get(index_a); // you had put this part inside the loop and you were using i
				for (int i = 0; i < newdata.length; ++i) {
					b.data[index_a] = newdata[i];
				}
				System.out.println(Arrays.toString(stash.get(index_a).data) +"----------------"+Integer.toString(index_a));
			}
		}


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
	/* 
		byte[] res = new byte[newdata.length] ;
		
		int x =  position_map[blockIndex]; // indice starts from 0, not 1
		System.out.println("x is " + x + "\n");
		position_map[blockIndex] = rand_gen.getRandomLeaf();

		// READ PATH, i.e copy every block on every level to stash S
		// Traversing the path P with all values of L (levels)
		for (int level = 0; level < num_levels; ++level)
		{
			int pxl = P(x,level);
			//System.out.println("int pxl is " + pxl + "\n");
			if (pxl >= num_buckets)
				break;
			//System.out.println("test");
			Bucket bucket = storage.ReadBucket(pxl);
			// If bucket is null then it is not in the tree but in the stash

			//System.out.println("what does get blocks give " + bucket.getBlocks().size());

			ArrayList<Block> all_blocks= bucket.getBlocks();
			//System.out.println("all_blocks size are " + all_blocks.size());
			if (all_blocks != null) {
				//System.out.println("First in all-blocks is "+ Arrays.toString(all_blocks.get(0).data));
				for (Block block : all_blocks) {
					//System.out.println("Ready to write to stash, the block index is " + block.index);
					if (block.index != -1) {
						stash.add(block);
					}
					//System.out.println("block are nu"+ Arrays.toString(block.data));
				}
			}
					
		}
		//boolean done= false;
		int write_to = -10;
		for (int i = 0; i < stash.size(); ++i) {
			Block b = stash.get(i);
			if (b.index == blockIndex);
				write_to = i;
		}


		if (op == Operation.WRITE) {
			System.out.println("in WRITE");
			if (write_to == -10) {
				Block b = new Block(blockIndex, newdata);
				stash.add(b);
			} else {
				Block b = new Block();
				for (int i = 0; i < newdata.length; ++i) {
					b = stash.get(i);
					b.data[write_to] = newdata[i];
				}
				stash.add(write_to, b);
			}
		} else {
			if (write_to == -10) {
				res = null;
			} else {
				for (int i = 0; i < newdata.length; ++i) {
					res[i] = stash.get(write_to).data[i];
				}
			}

		}



		//DOESN'T WORK AS STASH SIZE IS EMPTY IN THE BEGINNING
		// for (int i = 0; i < stash.size()  ; ++i) //|| done
		// {
		// 	Block b = stash.get(i);
		// 	System.out.println("IN FOR LOOP");
		// 	if (b.index == blockIndex) {
		// 		System.out.println("Ready to write to block\n\n");
		// 		//UPDATE BLOCK, Read block from stash if OP code is WRITE
		// 		if (op == Operation.WRITE) {
		// 			b.data = newdata;
		// 			stash.add(i, b);
		// 			System.out.println("stash that's written is now " + stash.get(i));
		// 		}
		// 		// READ if op == Operation.READ
		// 		else {  
		// 			res = b.data;
		// 		}
		// 		break;
		// 		//done = true;
		// 	}
		// }

		for (int level = num_levels; level > 0; level--) {
			ArrayList<Integer> to_be_written = new ArrayList<Integer>();
			Bucket bucket = new Bucket();
			int path_to_level = P(x, level);
			if (path_to_level >= num_buckets)
				break;
			int counter = 0;
			for (Block b: stash) {
				if (counter < bucket_size) {
					Block to_write = b; 
					if (path_to_level == P(position_map[to_write.index],level)) {
						bucket.addBlock(to_write);
						to_be_written.add(to_write.index);
						counter++;
					}
				}
			}

			//remove from stash
			for (int i = 0; i < to_be_written.size(); i++)
			{
				for (int j = 0; j < stash.size(); j++) 
				{
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
			storage.WriteBucket(path_to_level, bucket);
		}
		return res;
	*/
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
