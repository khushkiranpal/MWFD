/**
 * 
 */
package platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 * @author KHUSHKIRAN PAL
 *
 */
public class Fault {
	//private final  double lamda_0 = (0.000001);
	
	public ArrayList<Integer>  lamda_0 ( long time)
	
	{
		int count = 0;
	
		ArrayList<Integer> faults = new ArrayList<Integer>();
		PoissonDistribution poisson = new PoissonDistribution(0.000001) 	;//0.000001
		for(int i= 1; i<= time; i++)
		{
			if (poisson.sample()==1)
			{
				count++;
				
				faults.add(i);
			}
			
		//	System.out.println("  sample   "+ i);
		
		}
	//	System.out.println("count  "+count);
		
	//	double sample = poisson.sample();
	//	System.out.println("faults   "+faults.size());
		return faults;
		
	}
	
	public ArrayList<Integer>  lamda_F( long time, double fMin, double freq, int d)
		{
		int count = 0;
			ArrayList<Integer> faults = new ArrayList<Integer>();
		double mean , exponent;
		exponent = (d*(1-freq))/(1-fMin);
		mean = Math.pow(10, exponent);
	//	System.out.println("  mean   "+mean*0.000001 +"  exponent "+exponent);
		PoissonDistribution poisson = new PoissonDistribution(0.000001*mean) 	;//0.000001
		for(int i= 1; i<= time; i++)
		{
			if (poisson.sample()==1)
			{
				count++;
				
				faults.add(i);
			}
			
		//	System.out.println("  sample   "+ i);
		
		}
		//System.out.println("count  "+count);
		
	//	double sample = poisson.sample();
		
		return faults;
		
	}
	
	
	public void  writeInFile(ArrayList<Integer> faults) throws IOException
	{
		 // FILE NAME SETTING
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String filename;
        DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 2 decimal points
        filename =  "D:/CODING/MIXED ALLOCATION/DUAL PROCESSOR/31-1-18/TASKSET/fault_"+date+".txt";
        Writer faultWrite = new FileWriter(filename);
        Iterator<Integer> faultitr = faults.iterator();
        while (faultitr.hasNext())
        {
        	faultWrite.write(""+faultitr.next()+" ");
        }
        
        faultWrite.close();
	}
	
	public void  writeInFile(ArrayList<Integer> faults, String filename) throws IOException
	{
		 // FILE NAME SETTING
       
        Writer faultWrite = new FileWriter(filename);
        Iterator<Integer> faultitr = faults.iterator();
        while (faultitr.hasNext())
        {
        	faultWrite.write(""+faultitr.next()+" ");
        }
        
        faultWrite.close();
	}
	
	public ArrayList<Integer> readFromFile(String filename) throws FileNotFoundException
	{
		ArrayList<Integer> faults = new ArrayList<Integer>();
		Scanner scanner;
		int time;
		scanner = new Scanner(new File (filename));
		while (scanner.hasNext()) {
					
			 time = scanner.nextInt();
			 faults.add(time);
		//	 System.out.println(time);
		
		}
		scanner.close();
		return faults;
		
	}

}
