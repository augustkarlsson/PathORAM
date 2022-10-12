/*
 *  You can use this class to test your Oram (for correctness, not security).
 *  
 *  You can experiment modifying this class, but we will not take it into account (we will test your ORAM implementations on this as well as other Jobs)
 *  
 */

package pathOramHw;

import java.util.Arrays;

import pathOramHw.ORAMInterface.Operation;

public class Job {

	public static void main(String[] args) {
		int bucket_size = 4;
		int num_blocks = (int) Math.pow(2, 20);
		
		//Set the Bucket size for all the buckets.
		Bucket.setMaxSize(bucket_size);
				
		//Initialize new UntrustedStorage
		UntrustedStorageInterface storage = new ServerStorageForHW();

		//Initialize a randomness generator
		RandForORAMInterface rand_gen = new RandomForORAMHW();
		
		//Initialize a new Oram
		ORAMInterface oram = new ORAMWithReadPathEviction(storage, rand_gen, bucket_size, num_blocks);
		
		//Initialize a buffer value
		byte[] write_bbuf1 = new byte[128];
		for(int i = 0; i < 128; i++)
		{
			write_bbuf1[i] = (byte) 0xa;
		}
		byte[] write_bbuf2 = new byte[128];
		for(int i = 0; i < 128; i++)
		{
			write_bbuf2[i] = (byte) 0xb;
		}
		byte[] write_bbuf3 = new byte[128];
		for(int i = 0; i < 128; i++)
		{
			write_bbuf3[i] = (byte) 0xE;
		}
		byte[] write_bbuf4 = new byte[128];
		for(int i = 0; i < 128; i++)
		{
			write_bbuf4[i] = (byte) 0xF;
		}
		oram.access(Operation.WRITE, 0, write_bbuf1);
		System.out.println("111111111111111111111111");
		oram.access(Operation.WRITE, 1, write_bbuf2);
		System.out.println("111111111111111111111111");
		oram.access(Operation.WRITE, 2, write_bbuf3);
		System.out.println("111111111111111111111111");
		oram.access(Operation.WRITE, 3, write_bbuf4);
		System.out.println("111111111111111111111111");
		System.out.println("dbg read from 0 value is :" + Arrays.toString(oram.access(Operation.READ, 0, new byte[128])));
		System.out.println("dbg read from 1 value is :" + Arrays.toString(oram.access(Operation.READ, 1, new byte[128])));
		System.out.println("dbg read from 2 value is :" + Arrays.toString(oram.access(Operation.READ, 2, new byte[128])));
		System.out.println("dbg read from 3 value is :" + Arrays.toString(oram.access(Operation.READ, 3, new byte[128])));

		System.out.println("dbg read from 2 value is :" + Arrays.toString(oram.access(Operation.READ, 2, new byte[128])));
		System.out.println("dbg read from 3 value is :" + Arrays.toString(oram.access(Operation.READ, 3, new byte[128])));

//		Do same sample computation: fill an array with numbers, then read it back.
		//for(int i = 0; i < 30000; i++){
			//oram.access(Operation.WRITE, i % num_blocks, write_bbuf);
			//System.out.println("dbg written block " + i + " has stash size: " + oram.getStashSize());
		//}
		
//		for(int i = 0; i < num_blocks; i++){
//			break;
			//System.out.println("dbg read from " + i + " value is :" + Arrays.toString(oram.access(Operation.READ, i, new byte[128])));
		//}
				
	}
}
