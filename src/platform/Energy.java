/**
 * 
 */
package platform;

import java.util.TreeMap;

/**
 * @author KHUSHKIRAN PAL
 *
 */
public class Energy {
	
	private final static double C_EFF = 1.52;
	private final static double Max_Freq_Dependent_Power =C_EFF*1*1*1;
	private final static double S_critical=0.50; //.15


	
	public double powerDynamic(double freq)
	{
		
		double power =( C_EFF * freq*freq*freq);
	//	System.out.println("  power D "+power);
		return power;
	}

	public double powerStatic()
	{
		
		
		return (5.0/100.0)*Max_Freq_Dependent_Power;
	}

	public double powerIND()
	{
	//	return 0.08;
		return(15.0/100.0)*Max_Freq_Dependent_Power;
	}
	
	public double powerIDLE ()
	{
		//return(0.08 +C_EFF *S_critical*S_critical*S_critical);
		//System.out.println("idle e"+((20.0/100.0)*Max_Freq_Dependent_Power));
		return (20.0/100.0)*Max_Freq_Dependent_Power;
	}
	
	public double powerSLEEP()
	{
		//return 0.1;
		return .00000080;
	}
	
	
	public double energyActive(long exec_time, double freq)
	{
		double total_power =0, activeEnergy;
		//total_power = powerDynamic(freq)+powerIND();
	//	System.out.println( "  total_power  "+total_power);
			total_power = powerDynamic(freq)+powerStatic()+powerIND();
//			System.out.println("powerDynamic(freq)  "+powerDynamic(freq)+"   powerStatic()  "+powerStatic()+"  powerIND()  "+powerIND());
		activeEnergy = total_power*exec_time;
	//	System.out.println("activeEnergy  "+activeEnergy);
		return activeEnergy;
	}

	public double energy_IDLE(long exec_time)
	{
		
//		System.out.println("idle  "+ (powerIDLE ()*(double)exec_time));
		return powerIDLE ()* exec_time;
		
	}
	
	public double energySLEEP (long exec_time)
	{
	//	System.out.println(" sleep  "+exec_time*powerSLEEP());
		return (exec_time*powerSLEEP());
		/*if (exec_time==0)
			return 0.0;
		else
			return 0.1;*/
		//return powerSLEEP() * exec_time;
	}

	public double energyOverhead ()
	{
		return 0.000000385;
	}
}

