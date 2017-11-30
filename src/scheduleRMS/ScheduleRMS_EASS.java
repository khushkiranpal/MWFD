package scheduleRMS;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import energy.ParameterSetting;
import energy.SysClockFreq;
import platform.Energy;
import platform.Fault;
import platform.Processor;
import platform.ProcessorState;
import queue.ISortedJobQueue;
import queue.ISortedQueue;
import queue.SortedJobQueue;
import queue.SortedQueuePeriod;
import taskGeneration.FileTaskReaderTxt;
import taskGeneration.ITask;
import taskGeneration.IdleSlot;
import taskGeneration.Job;
import taskGeneration.SystemMetric;

public class ScheduleRMS_EASS {
	/*	 public static final   double  CRITICAL_TIME=  1500;  //1.5;///
		 public static final   double  CRITICAL_freq= 0.50;//0.42;   //
	*/	 private double freq=1;
	
	/**
	 * @throws IOException
	 */
	/**
	 * @throws IOException
	 */
	/**
	 * @throws IOException
	 */
	public void schedule(String IP_filename, long hyperperiod_factor, int d,double CRITICAL_TIME,double CRITICAL_freq) throws IOException
	{
	String inputfilename= IP_filename;
	FileTaskReaderTxt reader = new FileTaskReaderTxt("D:/CODING/TASKSETS/uunifast/"+inputfilename+".txt"); // read taskset from file
	 
	//FileTaskReaderTxt reader = new FileTaskReaderTxt("D:/CODING/MWFD results/taskset/"+inputfilename+".txt"); // read taskset from file
    DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
    Calendar cal = Calendar.getInstance();
    String date = dateFormat.format(cal.getTime());
 // String filename= "D:/CODING/TEST/EESP/primary"+"_"+inputfilename+"_"+date+".txt";
   //String filename1= "D:/CODING/TEST/EESP/spare"+"_"+inputfilename+"_"+date+".txt";
   
   String filename2= "D:/CODING/TEST/EESP/energyEASS"+"_"+inputfilename+"_"+date+".txt";
   String filename3= "D:/CODING/TEST/EESP/tasksEASS"+"_"+inputfilename+"_"+date+".txt";
 
   /* String filename2= "D:/CODING/MWFD results/output/energyEASS"+"_"+inputfilename+"_"+date+".txt";
    String filename3= "D:/CODING/MWFD results/output/tasksEASS"+"_"+inputfilename+"_"+date+".txt";
 */
  //    Writer writer_primary = new FileWriter(filename);
  //    Writer writer_spare = new FileWriter(filename1);
    Writer writer_energy = new FileWriter(filename2);
    Writer writer_tasks = new FileWriter(filename3);
    DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 1 decimal points

    Energy energyConsumed = new Energy();
    Job[] current= new Job[2], spare_current = new Job[2];  // FOR SAVING THE NEWLY INTIAlIZED JOB  FROM JOBQUEUE SO THAT IT 
	// IS VISIBLE OUTSIDE THE BLOCK
    
  ITask task;
    ITask[] set = null;
    double U_SUM;
    // final   long  CRITICAL_TIME= 4;
      
    // IDLE SLOTS QUEUE
    IdleSlot slot = new IdleSlot(); // idle slot
    List <IdleSlot> slots = new ArrayList<IdleSlot>();
    int total_no_tasksets=1;
 //    writer2.write("TASKSET UTILIZATION SYS_FREQ FREQ P_ACTIVE P_IDLE P_SLEEP S_ACTIVE S_IDLE S_SLEEP PRIMARY_ENERGY SPARE_ENERGY NPM TOTAL(S+P) \n");
    writer_energy.write("TASKSET UTILIZATION SYS_FREQ FREQ PRIMARY_ENERGY SPARE_ENERGY NPM TOTAL(S+P) \n");
    writer_tasks.write("EASSfullBackupsExecuted partialBackupsExecuted fullBackupsCancelled"
    		+ "	 cancelledPrimariesFull   cancelledPrimariesPartial  fullPrimariesExecuted noOfFaults");
   SysClockFreq frequency = new SysClockFreq();
    
    while ((set = reader.nextTaskset()) != null)
    {
    	long fullBackupsExecuted=0;
    	long partialBackupsExecuted=0;
    	long fullBackupsCancelled=0;
    	long cancelledPrimariesFull=0;
    	long cancelledPrimariesPartial=0;
    	long fullPrimariesExecuted=0;
    	
    	long noOfFaults=0;
    	boolean primaryBusy=false;
    	boolean spareBusy= true;
    	boolean deadlineMissed = false;
    	Job lastExecutedJob= null;
        ProcessorState proc_state = null;
        
    	  int id = 0;  // idle slot id 
    	 long time=0 ;
    	     long spareIdleTime = 0,timeToNextPromotion=0, spareActiveTime = 0;
			long timeToNextArrival=0;
    	     long endTime = 0; // endtime of job
			long spareEndTime=0;
    	     long idle = 0;  // idle time counter for processor idle slots
    	     SchedulabilityCheck schedule = new SchedulabilityCheck();
    	
    	 Processor primary = new Processor();
    	 Processor spare = new Processor();
    	 
    	 spare.setBusy(false);
			spareBusy=false;
			spare.setProc_state(proc_state.SLEEP);
			
			primary.setBusy(false);
			primary.setProc_state(proc_state.SLEEP);
    
    	
    	ISortedQueue queue = new SortedQueuePeriod ();
    	queue.addTasks(set);
    	ArrayList<ITask> taskset = new ArrayList<ITask>();
    	ArrayList<Job> completedJobs = new ArrayList<Job>();
    	taskset = queue.getSortedSet();
    	U_SUM= (SystemMetric.utilisation(taskset));
    	   //	total_no_tasks=total_no_tasks+ tasks.size();
    	prioritize(taskset);
    	ArrayList<Integer> fault = new ArrayList<Integer>();
		Fault f = new Fault();
		 long hyper = SystemMetric.hyperPeriod(taskset);  /////////////// HYPER PERIOD////////////
	    	System.out.println(" hyper  "+hyper);  

	    	
	    	for(ITask t : taskset)
	    	{
	    		t.setWcet(t.getWcet()*hyperperiod_factor);
	    		t.setWCET_orginal(t.getWCET_orginal()*hyperperiod_factor);
	    		t.setPeriod(t.getPeriod()*hyperperiod_factor);
	    		t.setDeadline(t.getDeadline()*hyperperiod_factor);
	    		t.setD(t.getD()*hyperperiod_factor);
	    		t.setT(t.getT()*hyperperiod_factor);
	    		t.setC(t.getC()*hyperperiod_factor);
	    	}
	       // if(hyper>100000000)
	   //////// 	hyper = hyperP;
		
			
    	ParameterSetting ps = new ParameterSetting();
    
    	ps.setBCET(taskset, 0.9);
    	ps.setACET(taskset);
    	
    	
    	
		// NPM RESULT////////////////////
		  double[] npmResult = new double[5];
		  NoPowerManag npm = new NoPowerManag();
		  ArrayList<ITask> taskset_copy = new ArrayList<ITask>();
			for (int i = 0 ; i<taskset.size();i++){
	    	    taskset_copy.add(taskset.get(i).cloneTask_RMS_double()) ;
	    	   
	    	}
		/*	for(ITask t : taskset_copy)
	    	{
	    	System.out.println("in tasksetcopy id  "+t.getId()+" wcet  "+t.getWcet()+"  bcet  "+t.getBCET()+"  acet  "+t.getACET());
	    	
	    	}*/    	
		 npmResult = npm.schedule(taskset_copy, hyper*hyperperiod_factor);
	//	ps.setParameterDouble(taskset);	  
    	double set_fq = frequency.SysClockF(taskset), fq = 0;
    
    	fq=Math.max(set_fq, CRITICAL_freq);
    	
    	System.out.println("frequency   " +fq);
    	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
    	
    	boolean schedulability = schedule.worstCaseResp_TDA_RMS(taskset);
    	  System.out.println(schedulability);
   
    	while(schedulability==false)
       {
    	
    	
         fq=fq+0.01;

  	   System.out.println("while  frequency   " +fq);
     	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
        
    	   schedulability = schedule.worstCaseResp_TDA_RMS(taskset);   
    	   System.out.println(schedulability);
        
    	   if (fq>1)
    	   {
    		   System.out.println("breaking   "+fq);
    		   break;
    	  
    	   }
    	   
       }
    	 if (fq>1)
    	 {
    		 System.out.println("unfeasible  "+total_no_tasksets);
    		 continue;
    	 }
  
    	ps.setResponseTime(taskset);    
    	ps.setPromotionTime(taskset);       //SET PROMOTION TIMES
    
    	/*for(ITask t : taskset)
    	{
    	System.out.println("in taskset id  "+t.getId()+" wcet  "+t.getWcet()+"  bcet  "+t.getBCET()+"  acet  "+t.getACET());
    	System.out.println("promotion    "+t.getSlack()+"   response  "+t.getResponseTime());
    	}*/
    	
    	
    	////////////FAULT/////////////////////////FAULT////////////
    	
    	fault = f.lamda_F(hyper, CRITICAL_freq, fq, d);        //////////////FAULT////////////
	//	fault.add(253);
		//fault.add(5);
	//	fault.add(11);
	
    	
    	
    	long temp=0;
		ISortedJobQueue activeJobQ = new SortedJobQueue(); // dynamic jobqueue 
		TreeSet<Job> spareQueue = new TreeSet<Job>(new Comparator<Job>() {
	          @Override
	          public int compare(Job t1, Job t2) {
	                         
	              if( t1.getPromotionTime()!= t2.getPromotionTime())
	                  return (int)( t1.getPromotionTime()- t2.getPromotionTime());
	              
	              return (int) (t1.getPeriod() - t2.getPeriod());
	          }
	      }); 
		
	
			
			Job j,  spareJob = null, spareJobNew = null; //job
		TreeSet<Long> activationTimes = new TreeSet<Long>();
	//	TreeSet<Long> promotionTimes = new TreeSet<Long>();
	ArrayList <Long> promotionTimes = new ArrayList<Long>();
	
		long nextActivationTime=0;
		
		long executedTime=0;
		
	
		
    	// ACTIVATE ALL TASKS AT TIME 0 INITIALLY IN QUEUE  
		
		for(ITask t : taskset)  // activate all tasks at time 0
		{
					temp=0;
					j =  t.activateRMS_energy_ExecTime(time);  ////	remainingTime =  (long)ACET;  ////////////////
					j.setPriority(t.getPriority());
					spareJobNew = j.cloneJob();
				//	spareJob.setACET(aCET);
				//	System.out.println("spare acet  "+spareJob.getAverage_CET()+"  primary   "+j.getACET());
					spareJobNew.setCompletionSuccess(false);
					activeJobQ.addJob(j);  //////ADD TO PRIMARY QUEUE
					j.setCompletionSuccess(false);
					spareQueue.add(spareJobNew);   /////ADD TO SPARE  QUEUE
				//	System.out.println("time   "+time+"out  activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate());
					//System.out.println("  wcet  "+t.getC()+"  Bcet  "+t.getBCET()+ " ACET "+t.getACET());
					
					
				//	System.out.println("active  job  "+j.getTaskId()+"  acet  "+spareJob.getRemainingTime()+  
				  //  		"  spare   "+ spareJob.getTaskId()+"  acet  "+j.getACET());
					while (temp<=hyper*hyperperiod_factor)
					{
						
						
						temp+=t.getPeriod();
						activationTimes.add(temp);
						promotionTimes.add((long) (t.getSlack()));

						promotionTimes.add((long) (t.getSlack()+temp));
					}
						
		}
		
	//	System.out.println("activationTimes  "+activationTimes.size()+"  promotionTimes  "+promotionTimes.size());
		promotionTimes.sort(new Comparator <Long>() {
	          @Override
	          public int compare(Long t1, Long t2) {
	        	  if(t1!=t2)
	        		  return (int) (t1-t2);
	        	  else 
	        		  return 0;
	        	 
	          }
	          });
	/*	Iterator itr = promotionTimes.iterator();
		while(itr.hasNext())
			System.out.println("promotionTimes   "+itr.next());
	  	*/
	/* writer_primary.write("\nTASKID av_ACET BCET ACET WCET_or WCET PROM DEADLINE\n");
		for (ITask t: taskset)
		{
			writer_primary.write("\n"+t.getId()+" "+t.getAverage_CET()+" "
		+t.getBCET()+" "+t.getACET()+" "+t.getWCET_orginal()+" " +t.getWcet()+
		" "+t.getSlack()+" "+t.getDeadline());
		}
		
               writer_primary.write("\nSCHEDULE\nTASKID  JOBID  ARRIVAL  ACET WCET prom DEADLINE  isPreempted STARTTIME ENDTIME EASSfullBackupsExecuted partialBackupsExecuted fullBackupsCancelled"
               		+ "	 cancelledPrimariesFull   cancelledPrimariesPartial  fullPrimariesExecuted noOfFaults\n");
              writer_spare.write("\nSCHEDULE\nTASKID  JOBID  ARRIVAL Av_CET WCETor DEADLINE  isPreempted STARTTIME ENDTIME  \n");
*/
        nextActivationTime=  activationTimes.pollFirst();
        
  // System.out.println("nextActivationTime  "+nextActivationTime);
    	
      /* Iterator<Job> itr = spareQueue.iterator();
       while(itr.hasNext())
       {
			Job task1 = itr.next();
    	   System.out.println("task   "+task1.getTaskId()+"  promotion  "+ task1.getPromotionTime());
       }*/

    //  int c=0;
        timeToNextPromotion = promotionTimes.get(0);
        
        
        
        while(time<hyper*hyperperiod_factor)
    	{
    	//	if(time>21000)
       // 	System.out.println("hyper  "+hyper+"  time  "+time+"  spareEndTime  "+spareEndTime);
    		
    		
      
    	//	System.out.println("   time   "+time+"  spareBusy   "+spareBusy+ "  task  id  "+spareQueue.first().getTaskId());
    		
    		
        	
        	if ( (long)time == (long)spareEndTime && (time>0) && spare.getProc_state()== ProcessorState.ACTIVE
        			)// here because after this new spareEndTime will be reinialized
        	{
        		 fullBackupsExecuted++;
        //		System.out.println("time   "+time+" current[0].getTaskId()  "+current[0].getTaskId());
        		
        		// DELETE THE COMPLETED JOB FROM ACTIVE QUEUE
    			 Iterator<Job> acticeItr = activeJobQ.iterator();
    			 while(acticeItr.hasNext())
    			 {
    				 Job temp1;
    				 temp1  = acticeItr.next();
    		//		 System.out.println("primaary pending  task  "+temp1.getTaskId());
    		    		 
    				 if(temp1.getTaskId()== spare_current[0].getTaskId() && temp1.getJobId()== spare_current[0].getJobId())
    				 {
    					 temp1.setCompletionSuccess(true);
    					
    					 
    					if(temp1.isPreempted==true)
    						cancelledPrimariesPartial++;    						
    					else
    						cancelledPrimariesFull++;
    					 activeJobQ.remove(temp1);
    			//		 System.out.println("time    "+time+" primaary pending task  "+temp1.getTaskId()+"  spare"+spare_current[0].getTaskId() );
    				  
    				//	 System.out.println("temp1.isPreempted   "+temp1.isPreempted+"   cancelledPrimariesPartial  "+cancelledPrimariesPartial+"  cancelledPrimariesFull  "+cancelledPrimariesFull);
    					 break;
    				 }
    			 }
        		 
        		 // DELETE THE RUNNING primary  JOB  
 				
 				if(current[0]!=null && current[0].getTaskId()== spare_current[0].getTaskId() &&
 						current[0].getJobId()== spare_current[0].getJobId() 
 						  && primary.getProc_state()==ProcessorState.ACTIVE && spareBusy == true)
 				{
 					primary.setProc_state(proc_state.IDLE);//-------------------
 			primaryBusy = false;  // set processor free 22/9/17
 					current[0].setEndTime(spareEndTime);  // set endtime of job
	        		current[0].setCompletionSuccess(true);//-------------------
	        		
	        		cancelledPrimariesPartial++;
	        	
	        		//	completedJobs.add(lastExecutedJob);
	       	   //   System.out.println("time   "+time+"  primary   task  "+current[0].getTaskId()+ "  success of primary and spare  "+current[0].isCompletionSuccess());
	        /*		   writer_primary.write(spareEndTime+"spareEndTime");
	        		   writer_primary.write(" " +fullBackupsExecuted +" "+partialBackupsExecuted +" "+fullBackupsCancelled+" "+
	   		  	    	    cancelledPrimariesFull +" "+  cancelledPrimariesPartial +" "+ fullPrimariesExecuted +" "+noOfFaults+"\n");
	   		*/      	
 				}
 					     // System.out.println("time   "+time+"spare first" +spareQueue.first().getTaskId()+" completion "+spareQueue.first().isCompletionSuccess());
        		
 				if( (timeToNextPromotion<=CRITICAL_TIME) && !spareQueue.first().isCompletionSuccess())
    				spare.setProc_state(proc_state.IDLE);
    			else
    				spare.setProc_state(proc_state.SLEEP);
        		
        		spare.setBusy(false);
    			spareBusy=false;
    	//		primaryBusy = false;  // set processor free
    			
        	}
        	
        	
        	//START SPARE
    		if (!spareQueue.isEmpty() && spareBusy==false  )
    		{
   /* 		System.out.println("   time   "+time+"  spareBusy   "+spareBusy+ "  task "+spareQueue.first().getTaskId()+" "+spareQueue.first().getJobId()
    				+"  promotion time "+spareQueue.first().getPromotionTime()+" SIZE  "+spareQueue.size());
    */		
    			while (!spareQueue.isEmpty() && time >= spareQueue.first().getPromotionTime()   )
    		//	if( time >= spareQueue.first().getPromotionTime() )
    			{
    				
    	/*	System.out.println("  promotion time "+spareQueue.first().getPromotionTime()+"  task  "+spareQueue.first().getTaskId() 
    						+"  completion  "+spareQueue.first().isCompletionSuccess() );
    	*/			
    				spareJob= 	spareQueue.pollFirst();
    	//			System.out.println("SPARE   JOB POLLED   "+spareJob.getPromotionTime()+"  task "+spareJob.getTaskId()+" wcet "+spareJob.getRomainingTimeCost());
    			//	promotionTimes.remove(0);
    				//System.out.println("spare job is faulty  "+spareJob.isFaulty());
    				if (!spareQueue.isEmpty())
    					timeToNextPromotion		= spareQueue.first().getPromotionTime();
    				else
    				{
    					//int tempcount = 0;
    					if (spareJob.getPromotionTime()<promotionTimes.get(0))
    					{
    						System.out.println("     preempted          spareJob.getPromotionTime()  "+spareJob.getPromotionTime());
    					System.out.println("                promotionTimes.get(0)            "+promotionTimes.get(0));
    					timeToNextPromotion= promotionTimes.get(0);
    				//	System.exit(0);
    					}
    					else{
    					
    					while (spareJob.getPromotionTime()>=promotionTimes.get(0))
    					{
    					//	System.out.println(time+"   spareQueue.size()  "+spareQueue.size()+ "     promotionTimes.get(0)  "+promotionTimes.get(0)+"   spareJob.getPromotionTime()  "+spareJob.getPromotionTime());
    						promotionTimes.remove(0);
    						/*if (spareJob.getPromotionTime()<promotionTimes.get(0))
    						{
    							System.out.println("tempcount  "+tempcount++);
    							if(tempcount>5)
    								System.exit(0);
    						}*/
    					}
				//		System.out.println("promotionTimes.get(0)  "+promotionTimes.get(0)+"   spareJob.getPromotionTime()  "+spareJob.getPromotionTime());

    				//	promotionTimes.remove(0);
    					timeToNextPromotion = promotionTimes.get(0);
    				//	System.out.println("  timeToNextPromotion  "+timeToNextPromotion);
    				}}
    				
    			//	timeToNextPromotion = promotionTimes.get(0);
    			/*System.out.println("time  "+time  +"  spare job  "+ spareJob.getTaskId()+"  size  "+promotionTimes.size()+
    						"  timeToNextPromotion  "+timeToNextPromotion+" "+spareJob.getPromotionTime()
    						+"  isCompletionSuccess  "+spareJob.isCompletionSuccess()+"  faulty"+spareJob.isFaulty());
    			*/	if (spareJob.isCompletionSuccess()==false || spareJob.isFaulty()==true)
    				{ 
    			//	System.out.println("time    "+time  +"  spare job executed  "+ spareJob.getTaskId());
    		    			
    					
    					spareBusy=true;
    					spare.setBusy(true);
    					spareJob.setStartTime(time);
    				//	System.out.println("spare job "+spareJob.getTaskId()+" start time "+spareJob.getStartTime());
    					spare_current[0]= spareJob;/////to make it visible
    					spare.setProc_state(ProcessorState.ACTIVE);
    					
    					
    					
    	/*		    writer_spare.write(spareJob.getTaskId()+"\t  "+spareJob.getJobId()+"\t"+spareJob.getActivationDate()+"\t"+spareJob.getAverage_CET()+
	  	  "\t"+spareJob.getRomainingTimeCost()+"\t"+spareJob.getAbsoluteDeadline()+"\t"+spareJob.isPreempted+"\t\t"+time+"\t");
	   */       			
    	 //			System.out.println(" time  "+time+"  spareBusy   "+spareBusy+"  promotion time "+spareJob.getPromotionTime());
    					//spare.setActiveTime(spareActiveTime++);
    				spareEndTime = (long)time + (long) spareJob.getRomainingTimeCost();//.getAverage_CET()*1000 ;////////.getRomainingTimeCost();
    				
    	/*		System.out.println("  time "+time+"  job id "+spareJob.getJobId()+"  task id  "+spareJob.getTaskId()+
    					"  job completed  "+spareJob.isCompletionSuccess()+"   spareEndTime with acet "+spareEndTime);
    	*/		
    				break;
    				}
    				else
    				{
    				//System.out.println(" continue  ");
    					continue;
    				}
    				
    				}
    		}
    		else if (spareBusy)
    		{
    			if(!spareQueue.isEmpty() && time==spareQueue.first().getPromotionTime() && 
    					spareJob.getPeriod()>spareQueue.first().getPeriod() && (!spareQueue.first().isCompletionSuccess()
    					|| spareQueue.first().isFaulty()))
    			{
    			/*	System.out.println(time  + "   preempt spare" );
    				System.out.println("spareJob.getPeriod()  "+spareJob.getPeriod()+"   spareQueue.first().getPromotionTime()  "+spareQueue.first().getPromotionTime()
    						+"\n   spareQueue.first().getPeriod()   "+spareQueue.first().getPeriod()
    						+"   spareQueue.first().isCompletionSuccess()   "+ spareQueue.first().isCompletionSuccess()
    						+"   spareQueue.first().isFaulty()  "+spareQueue.first().isFaulty());
    	    		
    		*/		Job newSpare =  spareQueue.pollFirst();
    				long exetime = time-spareJob.getStartTime();
    		//		System.out.println("time   "+time  +"spare job "+spareJob.getTaskId()+"  spareJob.getStartTime()  "+spareJob.getStartTime()+"  exetime  "+exetime);
    				spareJob.setRomainingTimeCost(spareJob.getRomainingTimeCost()-exetime);
    		/*		writer_spare.write("\n"+spareJob.getTaskId()+"\t  "+spareJob.getJobId()+"\t"
    				+spareJob.getActivationDate()+"\t"+spareJob.getAverage_CET()+
  					  	  "\t"+spareJob.getRomainingTimeCost()+"\t"+spareJob.getAbsoluteDeadline()+
  					  	  "\t"+spareJob.isPreempted+"\t\t"+spareJob.getStartTime()+"\t");
  					writer_spare.write("\t "+time+"\t  spare  preempted \n");
  			*/		spareJob.isPreempted=true;
  					spareQueue.add(spareJob);
    				
    				Iterator<Job> spareit = spareQueue.iterator();
    				while(spareit.hasNext())
    				{
    					Job nextJ= spareit.next();
    			//		System.out.println("task  "+nextJ.getTaskId()+"  jiob "+nextJ.getJobId() );
    				}
    				
    				//start the high prio job
    				spareJob= newSpare;
    				spareJob.setStartTime(time);
    				spare_current[0]= spareJob;
    				spareEndTime = (long)time + (long) spareJob.getRomainingTimeCost();
    		/*		writer_spare.write(spareJob.getTaskId()+"\t  "+spareJob.getJobId()+"\t"+spareJob.getActivationDate()+"\t"+spareJob.getAverage_CET()+
    					  	  "\t"+spareJob.getRomainingTimeCost()+"\t"+spareJob.getAbsoluteDeadline()+"\t"+spareJob.isPreempted+"\t\t"+time+"\t");
    		*/			        
    			}
    			
    		//	System.out.println(time + "   preempted in else"+"  spare empty  "+spareQueue.isEmpty()+"    spareBusy "+ spareBusy);
    			/*System.out.println("spareJob.getPeriod()  "+spareJob.getPeriod()+"   spareQueue.first().getPromotionTime()  "+spareQueue.first().getPromotionTime()
						+"   spareQueue.first().getPeriod()   "+spareQueue.first().getPeriod()
						+"\n   spareQueue.first().isCompletionSuccess()   "+ spareQueue.first().isCompletionSuccess()
						+"   spareQueue.first().isFaulty()  "+spareQueue.first().isFaulty());
	    		*/
    		
    		}
    		
    		if ( (long)time == (long)spareEndTime-1 && (time>0) && spare.getProc_state()== ProcessorState.ACTIVE)
			{
			
		///////no	 ///    writer1.write(spareEndTime+"    spareEndTime\n");
	//	 System.out.println("time   "+time  +" spare queue   "+spareQueue.size());
	//		+"  task  "+spareQueue.first().getTaskId()+   					"  job  "+spareQueue.first().getJobId());
			 spare_current[0].setCompletionSuccess(true);
		//	 System.out.println("time   "+time  +" active primary job task  "+ current[0].getTaskId());
		//		System.out.println("time    "+time+"  size  "+activeJobQ.size());
	//  writer_spare.write(spareEndTime+"    spareEndTime\n");
	//		 fullBackupsExecuted++;
		    		    	
				
				// DELETE THE COMPLETED JOB FROM ACTIVE QUEUE
			 Iterator<Job> acticeItr = activeJobQ.iterator();
			 while(acticeItr.hasNext())
			 {
				 Job temp1;
				 temp1  = acticeItr.next();
	//			 System.out.println("primaary pending  task  "+temp1.getTaskId());
		    		 
				 if(temp1.getTaskId()== spare_current[0].getTaskId() && temp1.getJobId()== spare_current[0].getJobId())
				 {
					 temp1.setCompletionSuccess(true);
					 activeJobQ.remove(temp1);
					 
					
				    	 cancelledPrimariesFull++;
				    
		//			 System.out.println("time    "+time+" primaary pending task  "+temp1.getTaskId()+"  spare"+spare_current[0].getTaskId() );
				    break;
				 }
			 }
			 
			 
			 
			 
			//	timeToNextPromotion = promotionTimes.pollFirst();//spareQueue.first().getPromotionTime()- (long)time;
		//	System.out.println("timeToNextPromotion   "+timeToNextPromotion+" time "+time);//+"  spareQueue.first().getPromotionTime()   "+spareQueue.first().getPromotionTime());
			/*if( (timeToNextPromotion<=CRITICAL_TIME) && !spareQueue.first().isCompletionSuccess())
				spare.setProc_state(proc_state.IDLE);
			else
				spare.setProc_state(proc_state.SLEEP);
			*/
			
			/*spare.setBusy(false);
			spareBusy=false;
			*/
			}
		
    		
    		if (spareBusy && spare.getProc_state()==ProcessorState.ACTIVE)
    		{
    			spare.activeTime++;
		//		System.out.println( "time   "+time+" active   "+spare.getActiveTime()+ " proc state  "+spare.getProc_state());
				
    		}
    		else if (!spareBusy && spare.getProc_state()==ProcessorState.SLEEP)
    		{
    			spare.sleepTime++;
    		}
    		else if (spare.getProc_state()==ProcessorState.IDLE)
    			spare.idleTime++;
    		
    		
    		if( (long)time== (long)nextActivationTime) // AFTER 0 TIME JOB ACTIVAIONS
			{
	
    			if (!activationTimes.isEmpty())
    			nextActivationTime=  activationTimes.pollFirst();
    		/*	else
    				break;*/
   	//	    System.out.println("nextActivationTime  "+nextActivationTime+" size  "+activationTimes.size());

    			for (ITask t : taskset) 
				{
					
					Job n = null;
					long activationTime;
					activationTime = t.getNextActivation(time-1);  //GET ACTIVATION TIME
			//		System.out.println("  activationTime  "+activationTime);
					long temp1= (long) activationTime, temp2 =(long) time;
					if (temp1==temp2)
						n= t.activateRMS_energy_ExecTime(time); ///	remainingTime =  (long)ACET;  ////////////////
					
					if (n!=null)
					{
						n.setCompletionSuccess(false);
						activeJobQ.addJob(n);  // add NEW job ///////////ADD TO PRIMARY QUEUE
						// System.out.println("time   "+time+"  task  "+activeJobQ.first().getTaskId()+
				//				"  out  activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate()
					//			+"  period  "+activeJobQ.first().getPeriod());
			    		// System.out.println(" size  "+activeJobQ.size());
 
						spareJobNew = n.cloneJob();
						spareJobNew.setCompletionSuccess(false);
						spareQueue.add(spareJobNew);	    ///////////ADD TO SPARE QUEUE
						// System.out.println("spare size  "+spareQueue.size());
						 
						
						// System.out.println("spareJob  p time"+spareJob.getPromotionTime());
						
						//  System.out.println("active  job  "+n.getTaskId()+"  acet  "+n.getRemainingTime()+  
						 //   		"  spare   "+ spareJob.getTaskId()+"  acet  "+spareJob.getACET());
						
					}
				}
				
			} 
    		
    //		System.out.println("time   "+time+"out  activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate());
    		
    		
    		//////////////////PREEMPTION in PRIMARY////////////////////////
    		
    		if(time>0 && !activeJobQ.isEmpty() && time==activeJobQ.first().getActivationDate() && current[0]!=null )
    		{
        		// System.out.println("activeJobQ.first().getActivationDate()  "+activeJobQ.first().getActivationDate());

    			if (activeJobQ.first().getPeriod()<current[0].getPeriod())
    			{
        			// System.out.println("preemption  ");

    				primaryBusy=false;
   // 			writer_primary.write("\t"+time+"\t preempted\n");
    				executedTime = time - current[0].getStartTime();
    				// System.out.println("time   "+time+"  executedTime  "+executedTime);

    				
    				current[0].setRemainingTime(current[0].getRemainingTime()-executedTime);
    				current[0].isPreempted=true;
    				if (current[0].getRemainingTime()>0)
    				activeJobQ.addJob(current[0]);
    				// System.out.println("preempted job  "+current[0].getTaskId()+" remaining time "+current[0].getRemainingTime()+ "   wcet "+
    		//			current[0].getRomainingTimeCost());
    			}
    		}
    		
    		
    		
    		if ((primaryBusy == false ) )// SELECT JOB FROM QUEUE ONLY if processor is free
	        	 {
	                	
	        		j = activeJobQ.pollFirst(); // get the job at the top of queue
	        		
	        		// QUEUE MAY BE EMPTY , SO CHECK IF IT IS  NOT NULL
	        		if (j!=null && j.isCompletionSuccess()==false)      // if job in queue is null 
	        		{
	        		/*	System.out.println("time   "+time +"  size  "+activeJobQ.size()+"  task  "+j.getTaskId()+
		        				"  success active job   "+j.isCompletionSuccess());
	              */  	primary.setProc_state(proc_state.ACTIVE);
	        			
	                		
	        			
	                //	System.out.println("time   "+time+"   active   "+primary.getActiveTime());
	        			//  IDLE SLOTS RECORD
	                			if (idle!=0)
	                			{
	 //               				writer_primary.write("endtime  "+time+"\n");
	                				slot.setLength(idle);  // IF PROCESSOR IS IDLE FROM LONF TIME, RECORD LENGTH OF IDLESLOT
	                				IdleSlot cloneSlot = (IdleSlot) slot.cloneSlot(); // CLONE THE SLOT
	                				slots.add(cloneSlot); // ADD THE SLOT TO LIST OR QUEUE
	                			}
	                			//RE- INITIALIZE IDLE VARIABLE FOR IDLE SLOTS
	                			idle =0;   // if job on the queue is not null, initialize  processor idle VARIABLE to 0
	                			
	        			current[0]=j;  // TO MAKE IT VISIBLE OUTSIDE BLOCK
    		//		System.out.println("current[0]  "+current[0].getTaskId()+" start time "+(long)time);

	    /*    			writer_primary.write(j.getTaskId()+"\t  "+j.getJobId()+"\t"+j.getActivationDate()+"\t"+j.getACET()+
	           		  "\t"+j.getRemainingTime()+"\t"+j.getPromotionTime()+"\t"+j.getAbsoluteDeadline()+"\t"+j.isPreempted+"\t\t"+time+"\t");
	    */      			
	        			
	        				j.setStartTime(time);  // other wise start time is one less than current time 
        											// BCOZ START TIME IS EQUAL TO END OF LAST EXECUTED JOB
        				
	        			endTime =  (time+j.getRemainingTime());
	        	//		System.out.println("current[0]  "+current[0].getTaskId()+"   endTime  "+(long)endTime);
	        			   primaryBusy = true;   //set  processor busy
	        			   lastExecutedJob = j;    
	        		}
	        		else  // if no job in jobqueue
	        		{

		        		timeToNextArrival= nextActivationTime-lastExecutedJob.getEndTime(); 
		        	//	System.out.println("nextActivationTime  "+nextActivationTime+"  lastExecutedJob.getEndTime   "+lastExecutedJob.getEndTime());
		        	//	System.out.println("time   "+time+"timeToNextArrival   "+timeToNextArrival);
		        	
		        		if (timeToNextArrival<CRITICAL_TIME)
		        		{
	        			primary.setProc_state(proc_state.IDLE);
		        		primary.idleTime++;  ///-------------------
		        		}
	        			else
	        			{
	        				primary.setProc_state(proc_state.SLEEP);
			        		primary.sleepTime++;//-------------------
	        			}
		        			
	        			if (idle==0)  // if starting of idle slot
	        			{
	        //			writer_primary.write("\nIDLE SLOT");
	        				slot.setId(id++); // SET ID OF SLOT
	                        slot.setStartTime(time);// START TIME OF SLOT
	                        current[0] = null;
	        //       writer_primary.write("\tstart time\t"+time+"\t");
	                	}
	        			
	        			idle++; // IDLE SLOT LENGTH 
	        			
	        			slot.setEndTime(idle + slot.getStartTime()); // SET END TIME OF SLOT
	                 } //end else IDLE SLOTS
	               
	        	 }
    		
		//	System.out.println("out fault time  "+time+"  task  "+lastExecutedJob.getTaskId()+" job  "+lastExecutedJob.getJobId());

    		
    		/////////////////////////////FAULT INDUCTION
    	//	if(time == 			11000)
    		//{
    	if ( fault.size()>0 )
    		{
		//	System.out.println("out fault time  "+time+"  task  "+lastExecutedJob.getTaskId()+" job  "+lastExecutedJob.getJobId());

    		if(time==fault.get(0)*hyperperiod_factor)
    		
    			{
    				if (primary.getProc_state()==proc_state.ACTIVE )
    				{	
    			System.out.println("                       fault time  "+time+"                task  "
    				+lastExecutedJob.getTaskId()+" job  "+lastExecutedJob.getJobId()+"   prom time   "+lastExecutedJob.getPromotionTime());
    				
    				lastExecutedJob.setCompletionSuccess(false);
    				lastExecutedJob.setFaulty(true);
    			
    				 Iterator<Job> spareItr = spareQueue.iterator();
        			 while(spareItr.hasNext())
        			 {
        				 Job temp1;
        				 temp1  = spareItr.next();
        		//		 System.out.println("primaary pending  task  "+temp1.getTaskId());
        		    		 
        				 if(temp1.getTaskId()== lastExecutedJob.getTaskId() && temp1.getJobId()== lastExecutedJob.getJobId())
        				 {
        					 temp1.setFaulty(true);
        						noOfFaults++;
        	//				 System.out.println("time    "+time+" primaary pending task  "+temp1.getTaskId()+"  spare"+spare_current[0].getTaskId() );
        				    break;
        				 }
        			 }
    				
    				}
    				fault.remove(0);
    			}
    	}
    	
    	
    			// CHECK DEADLINE MISS
    			Iterator<Job> it = activeJobQ.iterator();
				while (it.hasNext()) //CHECK FOR ALL ACTIVE JOBS
				{
					Job j1 = it.next();
					if (j1.getAbsoluteDeadline()<time) // IF TIME IS MORE THAN THE DEADLINE, ITS A MISSING DEADLINE
					{
				//		System.out.println("deadline missed  task id "+j1.getTaskId()+"job id " + j1.getJobId()+"  deadline time  "+j1.getAbsoluteDeadline()+"  time "+time);
						writer_energy.write("\ndeadline     missed  task id "+j1.getTaskId()+" job id "+j1.getJobId()+
								"  deadline time  "+j1.getAbsoluteDeadline()+"  time "+time+" ");
						writer_tasks.write("\ndeadline            missed  task id "+j1.getTaskId()+" job id "+j1.getJobId()+
								"  deadline time  "+j1.getAbsoluteDeadline()+"  time "+time+"  ");
						
						deadlineMissed= true;
						
						/*	writer.close();
						System.exit(0);*/
					}
				}
    			
	        	//	System.out.println("hyper  "+hyper+"   time  "+Double.valueOf(twoDecimals.format((time)))+"  end time "+Double.valueOf(twoDecimals.format((endTime-1))));

					// IF NOW TIME IS EQUAL TO ENDTIME OF JOB
				
			//	double temp1 = Double.valueOf(twoDecimals.format(time)), temp2= Double.valueOf(twoDecimals.format(endTime-1));
		        	if ((long)time==(long)endTime-1 && lastExecutedJob.isCompletionSuccess()==false ) // if current time == endtime 
		        	{
		      
		      //  			System.out.println("                time  "+time+"  end time "+ (endTime-1));
		        		//	Job k =  executedList.get(noOfJobsExec-1);// get last executed job added to list or job at the top of executed list
		        		primaryBusy = false;  // set processor free
		        		lastExecutedJob.setEndTime(endTime);  // set endtime of job
		        		
		        		 fullPrimariesExecuted++;
		        		
		        		lastExecutedJob.setCompletionSuccess(true);//-------------------
		        	//	completedJobs.add(lastExecutedJob);
		        	//     System.out.println("time   "+time+"   task  "+lastExecutedJob.getTaskId()+ "  success   "+lastExecutedJob.isCompletionSuccess());
		/*  		    writer_primary.write(endTime+"endtime ");
		  		  writer_primary.write(fullBackupsExecuted +" "+partialBackupsExecuted +" "+fullBackupsCancelled+" "+
		  	    	    cancelledPrimariesFull +" "+  cancelledPrimariesPartial +" "+ fullPrimariesExecuted +" "+noOfFaults+"\n");
		*/      	
		  		    // STOP THE RUNNING JOB ON SPARE IF PRIMARY HAS FINISHED IT SUCCESSFULLY
		        		
		        		if( spare.getProc_state()==ProcessorState.ACTIVE && lastExecutedJob.getTaskId()== spare_current[0].getTaskId() &&
		        				lastExecutedJob.getJobId()== spare_current[0].getJobId() 
		 						  )
		 				{
		 					spare.setProc_state(proc_state.IDLE);//-------------------
		 				//	spareBusy = false;  // set processor free
		 					spare_current[0].setEndTime(endTime);  // set endtime of job
			        		spare_current[0].setCompletionSuccess(true);//-------------------
			        		
			        		
			            	partialBackupsExecuted++;
			            	
			        	//	completedJobs.add(lastExecutedJob);
			        	  /*   System.out.println("time   "+time+"  spare   task  "+spare_current[0].getTaskId()+
			        	    		 "  success of  spare and primary  "+spare_current[0].isCompletionSuccess());//+"  id "+spareQueue.first().getTaskId());
			        	*/  /*   writer_spare.write("\n"+spare_current[0].getTaskId()+"\t  "+spare_current[0].getJobId()+"\t"+spare_current[0].getActivationDate()+"\t"+spare_current[0].getAverage_CET()+
			  					  	  "\t"+spare_current[0].getRomainingTimeCost()+"\t"+spare_current[0].getAbsoluteDeadline()+
			  					  	  "\t"+spare_current[0].isPreempted+"\t\t"+spare_current[0].getStartTime()+"\t");
			        	     writer_spare.write(endTime+"    endTime\n");
			        */		if( (timeToNextPromotion<=CRITICAL_TIME) && !spareQueue.isEmpty()&& !spareQueue.first().isCompletionSuccess() )//&& !spareQueue.isEmpty()
			    				spare.setProc_state(proc_state.IDLE);
			    			else
			    				spare.setProc_state(proc_state.SLEEP);
			        		
			        		spare.setBusy(false);
			    			spareBusy=false;
			        		
			        		
		 				}
		        		
		        		
		        		// DELETE JOB FROM SPARE QUEUE OR SET COMPLETION = SUCCESS
		        	if(!lastExecutedJob.isFaulty())
		        	{
		        		Iterator<Job> spareitr = spareQueue.iterator();
		        		while(spareitr.hasNext())
		        		{
		        			Job spar = spareitr.next();
		        			if (spar.getTaskId()==lastExecutedJob.getTaskId() && spar.getJobId()==lastExecutedJob.getJobId())
		        			{
		        				
		        			if (spar.isPreempted==true)
		        				partialBackupsExecuted++;
		        			else
		        		    	 fullBackupsCancelled++;
		        		    	
		        				if (lastExecutedJob.isCompletionSuccess()==false)
		        					spar.setCompletionSuccess(false);
		        				else
		        				spar.setCompletionSuccess(true);
		        				spareQueue.remove(spar);
		        				break;
		        			}
		        		}
		        	}
		        		
		        		
		        		
		    //     		System.out.println("hyper  "+hyper+"  time  "+time+"  busy "+busy);
		        	}
		        	
		        /*	if(activeJobQ.isEmpty())
		        	{
		        	
		        		timeToNextArrival= activationTimes.first()-lastExecutedJob.getEndTime(); 
		        		System.out.println("activationTimes.first()  "+activationTimes.first()+"  lastExecutedJob.getEndTime   "+lastExecutedJob.getEndTime());
		        		System.out.println("time   "+time+"timeToNextArrival   "+timeToNextArrival);
		        	
		        	}
		        		*/
		       if (primary.getProc_state()==proc_state.ACTIVE)
		    	   primary.activeTime++;
		        	
		        	
		        	if(!spareBusy)
		        	spareIdleTime++;
		        	
				
		    	time=time+1;
		    	if (deadlineMissed)
		    		break;
    	}
    /*	System.out.println(" spare active time "+spare.getActiveTime()+"  sleep "+spare.getSleepTime()+"  idle  "+spare.getIdleTime());
    	System.out.println("primary  active time "+primary.getActiveTime()+"  sleep "+primary.getSleepTime()+"  idle  "+primary.getIdleTime());
   */ 	/*Iterator<Job> itr1 = spareQueue.iterator();
    	 while (itr1.hasNext())
    	 {
    		 
    		 j = itr1.next();
    		 System.out.println("task  "+j.getTaskId()+"  job  "+j.getJobId()+"   period   "+j.getPeriod()+"   prio   " +j.getPriority()
    		 +"  start time  "+j.getActivationDate()+"  promotion "+j.getPromotionTime());
    	 }*/
    
    	double primaryEnergy, spareEnergy;
    	primaryEnergy = energyConsumed.energyActive(primary.activeTime, fq)+energyConsumed.energy_IDLE(primary.idleTime) +energyConsumed.energySLEEP(primary.sleepTime) ;
    	spareEnergy = energyConsumed.energyActive(spare.getActiveTime(), 1)+energyConsumed.energy_IDLE(spare.idleTime) +energyConsumed.energySLEEP(spare.sleepTime) ;
    	
    /*	System.out.println("primary  active energy"+energyConsumed.energyActive(primary.activeTime, fq)+"  idle  "+energyConsumed.energy_IDLE(primary.idleTime)
    	+" sleep  "+energyConsumed.energySLEEP(primary.sleepTime));
    	System.out.println("spare  active energy "+energyConsumed.energyActive(spare.getActiveTime(), 1)+"  idle  "+energyConsumed.energy_IDLE(spare.idleTime)
    	+" sleep  "+energyConsumed.energySLEEP(spare.sleepTime));
    
    	
    	System.out.println("primaryEnergy   "+primaryEnergy +" spareEnergy  "+spareEnergy);
    */
    	writer_energy.write(total_no_tasksets++ + " "+Double.valueOf(twoDecimals.format(U_SUM))+" "+Double.valueOf(twoDecimals.format(set_fq))+" "
	    	    +" "+ Double.valueOf(twoDecimals.format(fq)) +" "+Double.valueOf(twoDecimals.format(primaryEnergy))+
	    	    " "+Double.valueOf(twoDecimals.format(spareEnergy))+" "+Double.valueOf(twoDecimals.format(npmResult[2] ))
	    	    + " "+ Double.valueOf(twoDecimals.format(spareEnergy+primaryEnergy))+"\n");
    	 writer_tasks.write("\n"+fullBackupsExecuted +" "+partialBackupsExecuted +" "+fullBackupsCancelled+" "+
	    	    cancelledPrimariesFull +" "+  cancelledPrimariesPartial +" "+ fullPrimariesExecuted +" "+noOfFaults);
    	
    	
    	/* writer2.write(total_no_tasksets++ + " "+Double.valueOf(twoDecimals.format(U_SUM))+" "+Double.valueOf(twoDecimals.format(set_fq))+" "
    	    	    +" "+ Double.valueOf(twoDecimals.format(fq))+" "+(double)primary.activeTime +" "+(double)primary.idleTime +" "+(double)primary.sleepTime 
    	    	    +" "+(double)spare.activeTime +" "+(double)spare.idleTime +" "+(double)spare.sleepTime +" "+Double.valueOf(twoDecimals.format(primaryEnergy))+
    	    	    " "+Double.valueOf(twoDecimals.format(spareEnergy))+" "+npmResult[2]  + " "+ Double.valueOf(twoDecimals.format(spareEnergy+primaryEnergy))+"\n");
    	*/
    System.out.println("   tasksets  "+total_no_tasksets);
    
    }
    
  /* writer_primary.close();
   writer_spare.close();
 */   writer_energy.close();
     writer_tasks.close();
    System.out.println("success ScheduleRMS_EASS");
	}
	
	public static void prioritize(ArrayList<ITask> taskset)
	{
		int priority =1;
				
		for(ITask t : taskset)
		{
			t.setPriority(priority++);
			
		}
		
//		return taskset;
		
	}
	
}
	

	