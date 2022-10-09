package pathOramHw;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

/*
 * Name: Antoine Gansel, August Karlsson
 * StudentID: TODO
 */

public class Bucket{
	private static boolean is_init = false;
	private static int max_size_Z = -1;
	
	//TODO Add necessary variables

	//A
	private ArrayList<Block> content;
	private int real_size = 0;
	
	Bucket(){
		if(is_init == false)
		{
			throw new RuntimeException("Please set bucket size before creating a bucket");
		}
		//TODO Must complete this method for 
		content = new ArrayList<Block>();
	}
	
	// Copy constructor
	Bucket(Bucket other)
	{
		if(other == null)
		{
			throw new RuntimeException("the other bucket is not malloced.");
		}


		//TODO Must complete this method for submission

		content = new ArrayList<Block>(max_size_Z);
		int i;
		for (i = 0; i < other.returnRealSize(); ++i) {
			//Correct way to add new block??
			content.add(new Block(other.getBlockByKey(i)));
		}
		//Indices start on 0, hence size is one more
		real_size = ++i;
	}
	
	//Implement and add your own methods.
	Block getBlockByKey(int key){
		// TODO Must complete this method for submission
		return content.get(key);
		//return null;
	}
	
	void addBlock(Block new_blk){
		// TODO Must complete this method for submission
		content.add(new_blk);
		real_size++;
	}
	
	boolean removeBlock(Block rm_blk)
	{
		// TODO Must complete this method for submission
		if (content.remove(rm_blk)) {
			real_size--;
			return true;
		} else {
			return false;
		}
	}
	
	
	ArrayList<Block> getBlocks(){
		// TODO Must complete this method for submission
		return content;
	}
	
	int returnRealSize(){
		// TODO Must complete this method for submission
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