package scheduleRMS;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import energy.ParameterSetting;
import platform.Energy;
import queue.ISortedJobQueue;
import queue.ISortedQueue;
import queue.SortedJobQueue;
import queue.SortedQueuePeriod;
import taskGeneration.FileTaskReaderTxt;
import taskGeneration.ITask;
import taskGeneration.IdleSlot;
import taskGeneration.Job;
import taskGeneration.SystemMetric;

public class NoPowerManag {
	
	public  double[] schedule(ArrayList<ITask> taskset, ArrayList<Integer> fault ) throws IOException
	{

	    double[] result = new double[5];
		
		Job[] current= new Job[2];  // FOR SAVING THE NEWLY INTIAlIZED JOB  FROM JOBQUEUE SO THAT IT 
		// IS VISIBLE OUTSIDE THE BLOCK
	    int id = 0;  // idle slot id 
	     int total_no_tasks=0;
	    ITask task;
	    ITask[] set = null;
	    double U_SUM;
	    long endTime = 0; // endtime of job
	    long idle = 0;  // idle time counter for processor idle slots
	    Job lastExecutedJob= null;
	    Energy energyConsumed = new Energy();
	    
	    double primaryEnergy=0;
	    DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 1 decimal points

	    // IDLE SLOTS QUEUE
	    IdleSlot slot = new IdleSlot(); // idle slot
	    List <IdleSlot> slots = new ArrayList<IdleSlot>();
	 
	 //   writer2.write("TASKSET UTILIZATION P_ACTIVE P_IDLE PRIMARY_ENERGY \n");
	    
	    
	   
	    	   long time=0;
	    	   
	    	boolean busy=false;
	    	long activeTime=0;
	    	U_SUM= (SystemMetric.utilisation(taskset));
	    	long hyper = SystemMetric.hyperPeriod(taskset);  // HYPER PERIOD
	   // 	System.out.println(" hyper  "+hyper);  
	    //	total_no_tasks=total_no_tasks+ tasks.size();
	    	long temp=0;
			ISortedJobQueue activeJobQ = new SortedJobQueue(); // dynamic jobqueue 
			Job j; //job
			TreeSet activationTimes = new TreeSet();
			long nextActivationTime=0 , executedTime=0;
			ParameterSetting ps = new ParameterSetting();
		//	ps.setParameterDouble(taskset);
			/*for(ITask t : taskset)
	    	{
	    	System.out.println("taskset copy  id  "+t.getId()+" wcet  "+t.getWcet()+"  bcet  "+t.getBCET()+"  acet  "+t.getACET());
	    	
	    	}
			*/
			
			hyper = 1000000;//0000;   ////////////////hyper////////////
			
			// ACTIVATE ALL TASKS AT TIME 0 INITIALLY IN QUEUE  
					
			
			for(ITask t : taskset)  // activate all tasks at time 0
			{
						temp=0;
						j =  t.activateRMS(0);
						j.setPriority(t.getPriority());
						activeJobQ.addJob(j);
						while (temp<=hyper)
						{
							
						//	System.out.println(" Period   "+t.getPeriod());
							temp+=t.getPeriod();
							activationTimes.add(temp);
						}
							
			}
			
		/*	Iterator itr = activationTimes.iterator();
			while(itr.hasNext())
				System.out.println("activationTimes   "+itr.next());
		  */	
	     //   writer.write("\n\nSCHEDULE\nTASK ID  JOBID  ARRIVAL  WCET DEADLINE  isPreempted STARTTIME ENDTIME  \n");
	        nextActivationTime= (long) activationTimes.pollFirst();
	  //  System.out.println("nextActivationTime  "+nextActivationTime);
	    
	       
	//        System.out.println("  total_no_tasks   "+total_no_tasks);
	        while(time<hyper)
	    	{
	   // 	System.out.println("hyper  "+hyper+"  time  "+time+"  busy "+busy);
	    		
	    		if( time== nextActivationTime) // AFTER 0 TIME JOB ACTIVAIONS
				{
		
	    			if (!activationTimes.isEmpty())
	    			nextActivationTime= (long) activationTimes.pollFirst();
	    			else
	    				break;
	 //   		    System.out.println("nextActivationTime  "+nextActivationTime);

	    			for (ITask t : taskset) 
					{
						
						Job n = null;
						long activationTime;
						activationTime = t.getNextActivation(time-1);  //GET ACTIVATION TIME
						if (activationTime==time)
							n= t.activateRMS(time);
						if (n!=null)
						{
							activeJobQ.addJob(n);  // add NEW job to queue
														
						}
					}
					
				} 
	    		
	    	//	System.out.println("activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate());
	    		//PREEMPTION
	    		if(time>0 && !activeJobQ.isEmpty() && time==activeJobQ.first().getActivationDate() && current[0]!=null )
	    		{
	   //     		System.out.println("activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate());

	    			if (activeJobQ.first().getPeriod()<current[0].getPeriod())
	    			{
	     //   			System.out.println("preemption  ");

	    				busy=false;
	    			//	 writer.write("\t"+time+"\t preempted\n");
	    				executedTime = time - current[0].getStartTime();
	    	//			System.out.println("time   "+time+"  executedTime  "+executedTime);

	    				current[0].setRemainingTime(current[0].getRemainingTime()-executedTime);
	    				if (current[0].getRemainingTime()>0)
	    				activeJobQ.addJob(current[0]);
	    		//		System.out.println("preempted job  "+current[0].getTaskId()+" remaining time "+current[0].getRemainingTime()+ "   wcet "+
	    			//			current[0].getRomainingTimeCost());
	    			}
	    		}
	    		
	    		
	    		
	    		if ((busy == false ) )// SELECT JOB FROM QUEUE ONLY if processor is free
		        	 {
		                	
		        		j = activeJobQ.pollFirst(); // get the job at the top of queue
		        		// QUEUE MAY BE EMPTY , SO CHECK IF IT IS  NOT NULL
		        		if (j!=null)      // if job in queue is null 
		        		{
		        			
		                		//  IDLE SLOTS RECORD
		                			if (idle!=0)
		                			{
		              //  				writer.write("endtime  "+time+"\n");
		                				slot.setLength(idle);  // IF PROCESSOR IS IDLE FROM LONF TIME, RECORD LENGTH OF IDLESLOT
		                				IdleSlot cloneSlot = (IdleSlot) slot.cloneSlot(); // CLONE THE SLOT
		                				slots.add(cloneSlot); // ADD THE SLOT TO LIST OR QUEUE
		                			}
		                			//RE- INITIALIZE IDLE VARIABLE FOR IDLE SLOTS
		                			idle =0;   // if job on the queue is not null, initialize  processor idle VARIABLE to 0
		                			
		        			current[0]=j;  // TO MAKE IT VISIBLE OUTSIDE BLOCK
	    				//	System.out.println("current[0]"+current[0].getTaskId()+" start time "+time);

		        	//		writer.write(j.getTaskId()+"\t  "+j.getJobId()+"\t"+j.getActivationDate()+
		              //  			  "\t"+j.getRomainingTimeCost()+"\t"+j.getAbsoluteDeadline()+"\t"+j.isPreempted+"\t\t"+time+"\t");
		          			
		        			
		        				j.setStartTime(time);  // other wise start time is one less than current time 
	        											// BCOZ START TIME IS EQUAL TO END OF LAST EXECUTED JOB
	        				
		        		//		activeTime++;
		        				endTime = time+j.getRomainingTimeCost();//j.getRemainingTime();
		        		//	System.out.println("current[0]"+current[0].getTaskId()+"endTime  "+endTime + "   active time  "+activeTime);
		        			   busy = true;   //set  processor busy
		        			
		        			   lastExecutedJob = j;    
		        		}
		        		else  // if no job in jobqueue
		        		{
		        			
		        			if (idle==0)  // if starting of idle slot
		        			{
		        			//	writer.write("\nIDLE SLOT");
		        				slot.setId(id++); // SET ID OF SLOT
		                        slot.setStartTime(time);// START TIME OF SLOT
		                        current[0] = null;
		                      //  writer.write("\tstart time\t"+time+"\t");
		                	}
		        			
		        			idle++; // IDLE SLOT LENGTH 
		        			
		        			slot.setEndTime(idle + slot.getStartTime()); // SET END TIME OF SLOT
		                 } //end else IDLE SLOTS
		               
		        	 }
	    		if (busy == true)	
	        		activeTime++;
			
	    		
	    		
	    		
	    	
	    			
		        //		System.out.println("hyper  "+hyper+"  time  "+time+"  busy "+busy);

						// IF NOW TIME IS EQUAL TO ENDTIME OF JOB
			        	if ((time)==(endTime-1)) // if current time == endtime 
			        	{
			    
			        	//	Job k =  executedList.get(noOfJobsExec-1);// get last executed job added to list or job at the top of executed list
			        		busy = false;  // set processor free
			        		lastExecutedJob.setEndTime(endTime);  // set endtime of job
			     //   		writer.write(endTime+"    endtime\n");
			       		
			       
			    //     		System.out.println("hyper  "+hyper+"  time  "+time+"  busy "+busy);
			        	}
			       
			        	 
			   //   System.out.println("time    "+time+" active   "+activeTime);  
			    	time++;
	    	}
	    	/* Iterator<Job> itr = jobQ.iterator();
	    	 while (itr.hasNext())
	    	 {
	    		 
	    		 j = itr.next();
	    		 System.out.println("task  "+j.getTaskId()+"  job  "+j.getJobId()+"   period   "+j.getPeriod()+"   prio   " +j.getPriority()
	    		 +"  start time  "+j.getActivationDate());
	    	 }*/
	    System.out.println("end NPM active time  "+activeTime);
	    primaryEnergy = energyConsumed.energyActive(activeTime, 1)+energyConsumed.energy_IDLE(hyper-activeTime);
		result[0]= energyConsumed.energyActive(activeTime, 1);
		result[1] =energyConsumed.energy_IDLE(hyper-activeTime);
		result[2] = primaryEnergy;
	    return result;
	    
	 //   writer2.write(total_no_tasks++ +" "+ Double.valueOf(twoDecimals.format(U_SUM))+" "+activeTime+" "+ (hyper-activeTime) 
	   // 		+" "+Double.valueOf(twoDecimals.format(primaryEnergy))+"\n");
	    
	
		
	}

}
