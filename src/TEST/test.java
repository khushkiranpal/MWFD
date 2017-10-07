package TEST;

import java.io.IOException;

import platform.Processor;
import scheduleRMS.ScheduleRMS_EASS;
import scheduleRMS.ScheduleRMS_EASS_MWFD;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
	
		ScheduleRMS_EASS_MWFD schedul = new ScheduleRMS_EASS_MWFD();
		schedul.schedule();
	/*	DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 2 decimal points
		double time= 0.01, c=4.15;
		while(time <=4.16)
		{
			System.out.println(" time   "+Double.valueOf(twoDecimals.format(time)));
			time=time+0.01;
			if (Double.valueOf(twoDecimals.format(time))==c)
				System.out.println(" hello   "+Double.valueOf(twoDecimals.format(time)));
		}*/
		
		/*long val = 1000000000;
		BigInteger b1 = new BigInteger("1");
		BigInteger b2 = BigInteger.valueOf(val);
		while (b1.compareTo(b2)==-1 )
		{
		b1= b1.add(BigInteger.valueOf(1));	
		//System.out.println(b1);
		}
		System.out.println(b1);*/
		
		/*ArrayList<Integer> fault = new ArrayList<Integer>();
		Fault f = new Fault();
	//	fault = f.lamda_0(10000000);
		fault = f.lamda_F(1000000, 0.42, 0.5,1 );
		for (int i=0; i<fault.size();i++)
			System.out.println("f  "+ fault.get(i));
		
		for(int time =0;time<=1000000 && fault.size()>0;time++)
		{
		
			if (time==fault.get(0))
			{
		//	lastExecutedJob.setCompletionSuccess(false);
			System.out.println("time  "+time);
				fault.remove(0);
			
			}
		}*/
		ScheduleRMS_EASS test = new ScheduleRMS_EASS();
	//	ScheduleRMS test = new ScheduleRMS();
	//	test.schedule();
    /*	ParameterSetting ps = new ParameterSetting();

	
		 String inputfilename= "testhaque";
	    FileTaskReaderTxt reader = new FileTaskReaderTxt("D:/CODING/TASKSETS/uunifast/"+inputfilename+".txt"); // read taskset from file
	    SysClockFreq frequency = new SysClockFreq();
	    ITask[] set;
	    double freq;
	    while ((set = reader.nextTaskset()) != null)
	    {
	    	
	    	ISortedQueue queue = new SortedQueuePeriod ();
	    	queue.addTasks(set);
	    	ArrayList<ITask> taskset = new ArrayList<ITask>();
	    	taskset = queue.getSortedSet();
	    	ScheduleRMS_EASS.prioritize(taskset);
	    	//freq = frequency.SysClockF(taskset);
	    	//System.out.println("freq    "+freq);
	    	ArrayList<ITask> taskset_copy = new ArrayList<ITask>();
		    
	    
	 		double set_fq = frequency.SysClockF(taskset), fq = 0;
	 
	     		
	     	System.out.println("frequency   " +fq);
	    	
	    	
	     	 //taskset.remove(2);
	   	freq = 0.5;
	   
	    
	    	ps.setResponseTime(taskset);
	    	ps.setResponseTime(taskset_copy);
	    	ps.setPromotionTime(taskset_copy);
	    	ps.setPromotionTime(taskset);
	    	//  		  	ps.setBCET(taskset_copy, 0.5);
	    	  	ps.setBCET(taskset, 0.5);
	    //	  	ps.setACET(taskset_copy);
	    	  	ps.setACET(taskset);
	    		for (int i = 0 ; i<taskset.size();i++){
		    	    taskset_copy.add(taskset.get(i).cloneTask_RMS_double()) ;
		    	   
		    	}
	    		ps.set_freq(taskset, freq);
	    		ps.setParameterDouble(taskset);
	    	for (ITask t: taskset)
	    	{
				System.out.println("task i "+t.getId()+" wcet  "+t.getWcet()+"  response  "+t.getResponseTime()+"  promotion time "+t.getSlack());

	    		System.out.println("id  "+t.getId()+" c  "+t.getWCET_orginal()+" freq  "+freq +"  wcet  "+t.getWcet()+
	    				"  bcet  "+t.getBCET()+"   acet   "+t.getACET());
	    	}
	    	
	    	for (ITask t: taskset_copy)
	    	{
				System.out.println("       in copy  task i "+t.getId()+" wcet  "+t.getWcet()+"  response  "+t.getResponseTime()+"  promotion time "+t.getSlack());

	    		System.out.println("        id  "+t.getId()+" c  "+t.getWCET_orginal()+" freq  "+freq +"  wcet  "+t.getWcet()+
	    				"  bcet  "+t.getBCET()+"   acet   "+t.getACET());
	    	}
	    	
	    	System.out.println(GenerateTaskSetTxtUUnifast.worstCaseResp_TDA_RMS(taskset));
   
	    }*/
	}

}
