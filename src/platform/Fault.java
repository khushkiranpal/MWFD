/**
 * 
 */
package platform;

import java.util.ArrayList;

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
		PoissonDistribution poisson = new PoissonDistribution(0.000000001*mean) 	;//0.000001
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
		
		return faults;
		
	}

}
