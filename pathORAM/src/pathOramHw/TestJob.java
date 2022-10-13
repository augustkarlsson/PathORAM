package pathOramHw;
    /*
 *  You can use this class to test your Oram (for correctness, not security).
 *  
 *  You can experiment modifying this class, but we will not take it into account (we will test your ORAM implementations on this as well as other Jobs)
 *  
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import pathOramHw.ORAMInterface.Operation;
import java.io.IOException;




public class TestJob {



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
		
        int sample_size = 10001000;
        try  {
            FileWriter file = new FileWriter("test_results.csv");
            file.write("-1, "+(sample_size-3000000)+"\n");
            file.close();
		    //Initialize a buffer value
		    byte[] write_bbuf = new byte[128];
		    for(int i = 0; i < 128; i++)
		    {
		    	write_bbuf[i] = (byte) 0xa;
		    }
            ArrayList<Pair> results = new ArrayList<Pair>();

		    for(int i = 0; i < sample_size; i++){
		    	oram.access(Operation.READ, i % num_blocks, write_bbuf);
		    	System.out.println("dbg written block " + i + " has stash size: " + oram.getStashSize());
                
				//if (i > 3000000) { //ONLY COLLECT DATA AFTER 3 MILLION ROUNDS AS WARMUP
					for (int j = 0; j <= oram.getStashSize(); ++j) {
						System.out.println("inne i loop");

						try {
							Pair j_val = results.get(j);
							j_val.y++;
							results.add(i, j_val);
						} catch (Exception e) {
							results.add(new Pair(j, 1));
						}

                	}
				//}
				//for (int val = 0; val < results.size(); ++val) {
					for (int j = 0; j <= oram.getStashSize(); ++j) {
					System.out.println("i is: "+j+ " and res is: "+results.get(j)); //change back to val
					results.get(j).printPair(file);
					}
				//}
		    }
            

        } catch (IOException e ) {
            System.out.println("An error occured"+e);
        }
        

				
	}

}