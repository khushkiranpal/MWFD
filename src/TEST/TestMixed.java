package TEST;

import java.io.IOException;

import scheduleRMS.EASS_HAQUE_BACKUP_DELAY;
import scheduleRMS.EASS_HAQUE_OVERLOADING;
import scheduleRMS.MixedAllocation;
import scheduleRMS.MixedAllocationOverloading;
import scheduleRMS.ScheduleRMS_EASS_MWFD_amity;

public class TestMixed {
	public static final  long hyperperiod_factor= 1;	//
	public static final   double  CRITICAL_TIME=  1.5*hyperperiod_factor;///1500;  //
	public static final   double  CRITICAL_freq= 0.50;   //0.50;//

	public static final int d = 2;  // FAULT TOLERANCE PARAMETER
	private double freq=1; // TEMP PARAMETER
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String filename= "testhaque";
		 String inputFolder = "D:/CODING/MIXED OVERLOADING/14-2-18/DUAL PROCESSOR/TASKSET/";
		String outputFolder= "D:/CODING/MIXED OVERLOADING/14-2-18/";
		String inputfilename=  filename+".txt";
	//	EASS_HAQUE schedul1 = new  EASS_HAQUE();
		EASS_HAQUE_BACKUP_DELAY schedul1 = new EASS_HAQUE_BACKUP_DELAY();
		EASS_HAQUE_OVERLOADING schedul2 = new EASS_HAQUE_OVERLOADING();
		ScheduleRMS_EASS_MWFD_amity schedul3 = new ScheduleRMS_EASS_MWFD_amity();
		MixedAllocation schedul4 = new MixedAllocation();
		MixedAllocationOverloading schedul5 = new MixedAllocationOverloading();
		// can not add fault here because frequency of operation is unknown
		// the rate of fault will be same as equal to "d". so no major concrn or difference in fault occurrence 
//	//	schedul5.schedule(inputfilename,outputFolder,inputFolder,hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
//		schedul4.schedule(inputfilename,outputFolder,inputFolder,hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
//		schedul3.schedule(inputfilename,outputFolder,inputFolder, hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
//		schedul2.schedule(inputfilename,outputFolder,inputFolder,hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
		schedul1.schedule(inputfilename,outputFolder,inputFolder,hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
		
	}

}
