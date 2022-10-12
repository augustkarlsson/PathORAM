package pathOramHw;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;
import javax.xml.namespace.QName;

/*
 * Name: Antoine Gansel, August Karlsson
 * StudentID: TODO
 */

public class Bucket{
	private static boolean is_init = false;
	private static int max_size_Z = -1;

	private ArrayList<Block> content;
	private int real_size = 0;
	
	Bucket(){
		if(is_init == false)
		{
			throw new RuntimeException("Please set bucket size before creating a bucket");
		}
		content = new ArrayList<Block>();
	}
	
	Bucket(Bucket other)
	{
		if(other == null)
		{
			throw new RuntimeException("the other bucket is not malloced.");
		}

		content = new ArrayList<Block>(max_size_Z);
		int i;
		for (i = 0; i < max_size_Z; ++i) {
			content.add(new Block(other.content.get(i)));
		}
		real_size = ++i;
	}
	
	Block getBlockByKey(int key){
		for (Block b: content) {
			if (b.index == key)
				return new Block(b);
		}
		return null;
	}
	
	void addBlock(Block new_blk){
		if (content.size() < max_size_Z){
		content.add(new Block(new_blk));
		real_size++;
		}
	}
	
	boolean removeBlock(Block rm_blk)
	{
		int index = 0;
		for (Block b: content) {
			if (b.index == rm_blk.index) {
				content.remove(index);
				real_size--;
				return true;
			}
			index++;
		}
		return false;	
	}
	
	
	ArrayList<Block> getBlocks(){
		return content;
		// ArrayList<Block> blocks = new ArrayList<Block>();
		// for (Block b : content){
		// 	if (b.index != -1){
		// 		blocks.add(new Block(b));
		// 	}
		// }
		// return blocks;
	}
	
	int returnRealSize(){
		return real_size;
	}

	static void resetState()
	{
		is_init = false;
	}

	static void setMaxSize(int maximumSize)
	{
		if(is_init == true)
		{
			throw new RuntimeException("Max Bucket Size was already set");
		}
		max_size_Z = maximumSize;
		is_init = true;
	}

}