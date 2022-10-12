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
		//this.num_buckets = (int) Math.ceil(((double) num_blocks)/((double) bucket_size)); // 4 blocks by buckets, need to be able to fit everything
		//this.num_levels = this.find_num_levels();
		//int[] res_num_leaves = this.find_num_leaves();
		//this.num_leaves = res_num_leaves[0];
		//this.lowest_leave = res_num_leaves [1];
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

	//	if (P(0,1) != 0) throw new RuntimeException("P incorrect 1");
	//	if (P(0,2) != (int) Math.pow(2,1)-1) throw new RuntimeException("P incorrect 2");
	//	if (P(0,3) != (int) Math.pow(2,2)-1) throw new RuntimeException("P incorrect 3");
	//	if (P(0,4) != (int) Math.pow(2,3)-1) throw new RuntimeException("P incorrect 4");
	//	if (P(0,5) != (int) Math.pow(2,4)-1) throw new RuntimeException("P incorrect 5");
	//	if (P(0,6) != (int) Math.pow(2,5)-1) throw new RuntimeException("P incorrect 6");
	//	if (P(0,7) != (int) Math.pow(2,6)-1) throw new RuntimeException("P incorrect 7");
	//	if (P(0,8) != (int) Math.pow(2,7)-1) throw new RuntimeException("P incorrect 8");
	//	if (P(0,9) != (int) Math.pow(2,8)-1) throw new RuntimeException("P incorrect 9");
	//	if (P(0,10) !=(int) Math.pow(2,9)-1) throw new RuntimeException("P incorrect 10");
	//	if (P(0,11) != (int) Math.pow(2,10)-1) throw new RuntimeException("P incorrect 11");
	//	if (P(0,12) != (int) Math.pow(2,11)-1) throw new RuntimeException("P incorrect 12");
	//	if (P(0,13) != (int) Math.pow(2,12)-1) throw new RuntimeException("P incorrect 13");
	//	if (P(0,14) != (int) Math.pow(2,13)-1) throw new RuntimeException("P incorrect 14");
	//	if (P(0,15) != (int) Math.pow(2,14)-1) throw new RuntimeException("P incorrect 15");
	//	if (P(0,16) != (int) Math.pow(2,15)-1) throw new RuntimeException("P incorrect 16");
	//	if (P(0,17) != (int) Math.pow(2,16)-1) throw new RuntimeException("P incorrect 17");
	//	if (P(0,18) != (int) Math.pow(2,17)-1) throw new RuntimeException("P incorrect 18");
	//	if (P(0,19) != (int) Math.pow(2,18)-1) throw new RuntimeException("P incorrect 19");
	//	if (P(0,20) != (int) Math.pow(2,19)-1) throw new RuntimeException("P incorrect 20");
	}
/* 
	private int find_num_levels() {
		return (int) Math.ceil(Math.log10(num_buckets)/Math.log10(2))+1;
	}
	

	private int[] find_num_leaves() {
		
	//	 * ex : 01234567
	//				0           num_levels = 4  | num_buckets = 9
	//			  /   \         last_leaves = 2 = 9 - 7 = 9 - 8 + 1
	//			 1     2                    = num_buckets - 2^(num_levels-1) + 1
	//		    / \   / \
	//		   3   4 5   6		nodes_secondLast_level = 4 - 1 = 4 - 2/2 = 2^(num_levels-1) - 2^(num_levels-2)
	//		  / \               secondLast_leaves = 4 - 2 = 4 - 2/2 
	//		 7   8								  = nodes_secondLast_level - ceiling(last_leaves/2)
	//										/!\ ceiling is to not have issue when one leaf does not have siblings /!\
		 
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
 */
	private int[] init_map(){
		int[] map = new int[this.num_blocks]; // need to keep record of position for every possible doc
		for (int i = 0; i < this.num_blocks; i++) {
			map[i] =  rand_gen.getRandomLeaf();//this.num_leaves-1;
		}
		// map[0] = 0;
		// map[1] = num_leaves-5;
		// map[2] = num_leaves-2;
		return map;
	}

	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// 		 *	- [execution] need to implement algorithm from slides page 51 to 54

		//System.out.println("newdata is " + newdata + "\n");
	
		byte[] res = new byte[newdata.length] ;

		int a = position_map[blockIndex];
		position_map[blockIndex] = rand_gen.getRandomLeaf();
		//System.out.println("leaves-2="+Integer.toString(num_leaves-2));

		/*
		 * for l in {0,1,...,L}
		 * 		S <-- S U ReadBucket(P(x,l))
		 * end for
		 */
		for (int level = 1; level < num_levels; level++) {
			int current = P(a,level);
			//System.out.println(Integer.toString(current));
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
		//System.out.println(Integer.toString(stash_size));
		for (int i = 0; i < stash_size; ++i) {
			Block b = stash.get(i);
			if (b.index == blockIndex){
				index_a = i;
				//System.out.println("NEED TO TRANSFORM INTO A COPY LIGN 164");
				res = b.data; // NEED TO TRANSFORM INTO A COPY
			}
		}
		if  (op == Operation.WRITE){
			if (index_a == -1) { //the index was not used
				stash.add(new Block(blockIndex, newdata));
				//System.out.println(""+ Arrays.toString(stash.get(stash.size()-1).data)+"######################"+Integer.toString(index_a));
				//System.out.println(Integer.toString(stash.get(stash.size()-1).index));
			}
			else {
				Block b = stash.get(index_a); // you had put this part inside the loop and you were using i
				for (int i = 0; i < newdata.length; ++i) {
					b.data[blockIndex] = newdata[i];
				}
				b.index = blockIndex; // set new blockIndex
				//System.out.println(Arrays.toString(stash.get(index_a).data) +"######################"+Integer.toString(index_a));
			}
		}

		
		stash_size = stash.size();
		//System.out.println(Integer.toString(stash_size));

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
						System.out.println("P="+Integer.toString(P(position_map[to_write.index],level))+";leaf="+Integer.toString(position_map[to_write.index])+"level="+Integer.toString(level));
						//System.out.println(Arrays.toString(b.data));
						//System.out.println(Arrays.toString(to_write.data));
						bucket.addBlock(to_write);
						stash_ToWrite.add(to_write.index);
						counter++;
					}
				}
			}

			//System.out.println(Arrays.toString(bucket.getBlockByKey(0).data));


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

		leaf = leaf + (int) Math.pow(2,this.num_levels-1)-1 ;
		//System.out.println("---------------"+Integer.toString(level)+"  " + Integer.toString(this.num_levels));
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
