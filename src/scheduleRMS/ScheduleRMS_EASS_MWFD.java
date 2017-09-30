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

public class ScheduleRMS_EASS_MWFD {
		 public static final   double  CRITICAL_TIME=  1.5;///1500;  //1.5;///
		 public static final   double  CRITICAL_freq= 0.50;//0.42;   //
	private double freq=1;
	
	/**
	 * @throws IOException
	 */
	/**
	 * @throws IOException
	 */
	/**
	 * @throws IOException
	 */
	public void schedule() throws IOException
	{
	String inputfilename= "testhaque";
    FileTaskReaderTxt reader = new FileTaskReaderTxt("D:/CODING/TASKSETS/uunifast/"+inputfilename+".txt"); // read taskset from file
    DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
    Calendar cal = Calendar.getInstance();
    String date = dateFormat.format(cal.getTime());
  String filename= "D:/CODING/TEST/RMS/primary"+"_"+inputfilename+"_"+date+".txt";
    String filename1= "D:/CODING/TEST/RMS/spare"+"_"+inputfilename+"_"+date+".txt";
    String filename2= "D:/CODING/TEST/RMS/energy"+"_"+inputfilename+"_"+date+".txt";
    
  //  Writer writer = new FileWriter(filename);
    // Writer writer1 = new FileWriter(filename1);
  //  Writer writer2 = new FileWriter(filename2);
    
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
  //   writer2.write("TASKSET UTILIZATION SYS_FREQ FREQ P_ACTIVE P_IDLE P_SLEEP S_ACTIVE S_IDLE S_SLEEP PRIMARY_ENERGY SPARE_ENERGY NPM\n");
 
    SysClockFreq frequency = new SysClockFreq();
    
    while ((set = reader.nextTaskset()) != null)
    {
    	boolean primaryBusy=false;
    	boolean spareBusy= true;
    	boolean deadlineMissed = false;
    	Job lastExecutedJob= null, primaryJob, backupJob;
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
    	//LIST OF FREE PROCESSORS
			Comparator<Processor> comparator = new Comparator<Processor>() {
		    	 public int compare(Processor p1, Processor p2) {
					int cmp =  (int) (p1.getId()-p2.getId());
					return cmp;
				}
			  };
			
			  PriorityQueue<Processor> freeProcList = new PriorityQueue<Processor> (comparator); //LIST OF FREE PROCESSORS

    	ArrayList<Processor> no_of_proc = new ArrayList<Processor>(); //total processor list
			for(int i = 1;i<=2;i++)  // m is number of processors
			 {
				 Processor p = new Processor(i,false); // i gives the processor id value , false means processor is free
				 freeProcList.add(p);
				 no_of_proc.add(p);
			 }
    	
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

	       // if(hyper>100000000)
	        	hyper = 35;
		
			
    	ParameterSetting ps = new ParameterSetting();
    	boolean unschedulable = false;
    	ps.setBCET(taskset, 0.5);
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
		 // npmResult = npm.schedule(taskset_copy, fault);
	//	ps.setParameterDouble(taskset);	  
    	double set_fq = frequency.SysClockF(taskset), fq = 0;
    
    	fq=Math.max(set_fq, CRITICAL_freq);
    	
    	System.out.println("frequency   " +fq);
    	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
    	
    	boolean schedulability = schedule.worstCaseResp_TDA_RMS(taskset);
    	System.out.println("on  one processor   "+schedulability+"  at fq "+fq);
   
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
    		 continue;
    	
    	 fq= Math.max(U_SUM, CRITICAL_freq);
     	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));   // set frequency
     	System.out.println("frequency   " +fq+" usum  "+U_SUM);
     	schedulability = schedule.worstCaseResp_TDA_RMS(taskset);//, fq);

     	//System.out.println("on  one processor   "+schedulability+"  at fq "+fq);
   	
     	
     	ps.setResponseTime(taskset);    
     	ps.setPromotionTime(taskset);       //SET PROMOTION TIMES

    	/*for(ITask t : taskset)
    	{
    	System.out.println("in taskset id  "+t.getId()+" wcet  "+t.getWcet()+"  bcet  "+t.getBCET()+"  acet  "+t.getACET());
    	System.out.println("slack    "+t.getSlack()+"   response  "+t.getResponseTime());
    	}*/
   	  
     	//ALLOCATION STARTED 
   	  do{      // WHILE ALL PROCESSORS HAVE SCHEDULABLE TASKSETS ON GIVEN FREQUENCIES 
     	
     		for(Processor pMin : freeProcList)
     		{
         		
     			pMin.taskset.clear();
     			pMin.setWorkload(0);
         		System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
     		}    		
     	
   	
    	
	//ALLOCATION OF PRIMARIES
    	
    	
    	
    	for(ITask t : taskset)
    	{
    		double u = Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline()))), work=1;
    		Processor minP=null;
    	//	System.out.println(" u  "+u);
    		
    		for(Processor pMin : freeProcList)
    		{
    			if(work >pMin.getWorkload())
    			{
    				work=Double.valueOf(twoDecimals.format(pMin.getWorkload()));
    				minP = pMin;
    			}
    		//	System.out.println("work   "+work+"  minP  "+minP.getId()+ "  pMin  "+pMin.getId());
        		
    		}
    		t.setPrimary(true);
    		minP.taskset.add(t);
    		minP.setWorkload(Double.valueOf(twoDecimals.format(minP.getWorkload()+u)));
    		t.setP(minP);
    		
    	}
    	
    	//ALLOCATION OF BACKUPS
    	for(ITask t : taskset)
    	{
    		double u = Double.valueOf(twoDecimals.format(((double)t.getWCET_orginal()/(double)t.getD()))), work=1;
    		ITask backup_task;
    		Processor minP=null;
    	//	System.out.println(" u backup  "+u);
    		for(Processor pMin : freeProcList)
    		{
    			if (pMin == t.getP())   // IF PRIMARY PROCESSOR CONTAINS THE TASK, ALLOCATE BACKUP ON SOME OTHER PROCESSOR
    				continue;
    			if(work >pMin.getWorkload())
    			{
    				work=Double.valueOf(twoDecimals.format(pMin.getWorkload()));
    				minP = pMin;
    			}
    		//	System.out.println("work   "+work+"  minP  "+minP.getId()+ "  pMin  "+pMin.getId());
        		
    		}
    		t.setBackupProcessor(minP);
    		backup_task = t.cloneTask_MWFD_RMS_EEPS();
    		backup_task.setPrimary(false);  //setup backup processor
    		minP.taskset.add(backup_task);
    		minP.setWorkload(Double.valueOf(twoDecimals.format(minP.getWorkload()+u)));
    		backup_task.setP(minP);
    	}
    	
    	//CHECK SCHEDULABILITY ON ALL PROCESSORS
    	
    	for(Processor pMin : freeProcList)
		{
    		
    		System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
    	/*	for(ITask t : pMin.taskset)
    			
        	{
    			System.out.println("task   "+t.getId()+"  u  "+ Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline())))
    			+"   primary  "+t.isPrimary()+"  Proc   "+t.getP().getId());
       
        	}*/
    		schedulability = schedule.worstCaseResp_TDA_RMS(pMin.taskset);
    		System.out.println("proc   "+pMin.getId()+"   schedulability   "+schedulability);
    		if(schedulability==false)
    		{
    			unschedulable= true;
    			fq=fq+0.01;
    			ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
    	    	
    		}
    		else 
    			unschedulable=false;
		}
    	System.out.println("fq  "+fq+"   unschedulable  "+unschedulable);

		for(Processor pMin : freeProcList)
		{
    		
			System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
		}   
    	}while( unschedulable == true);
    	
    	///END ALLOCATION

    	
    	fault = f.lamda_F(hyper, CRITICAL_freq, fq, 0);        //////////////FAULT////////////
		
	//	fault = f.lamda_0(10000000);
    	
    	
    	long temp=0;
		ISortedJobQueue activeJobQ = new SortedJobQueue(); // dynamic jobqueue 
		TreeSet<Job> backupQueue = new TreeSet<Job>(new Comparator<Job>() {
	          @Override
	          public int compare(Job t1, Job t2) {
	                         
	              if( t1.getPromotionTime()!= t2.getPromotionTime())
	                  return (int)( t1.getPromotionTime()- t2.getPromotionTime());
	              
	              return (int) (t1.getPeriod() - t2.getPeriod());
	          }
	      }); 
		
	
			
			Job j;//,  backupJob = null; //job
		TreeSet<Long> activationTimes = new TreeSet<Long>();
	//	TreeSet<Long> promotionTimes = new TreeSet<Long>();
	ArrayList <Long> promotionTimes = new ArrayList<Long>();
	
		long nextActivationTime=0;
		
		long executedTime=0;
		
	
		
    	// ACTIVATE ALL TASKS AT TIME 0 INITIALLY IN QUEUE  
		
		for(ITask t : taskset)  // activate all tasks at time 0
		{
					temp=0;
					j =  t.activate_MWFD_RMS_EEPS(time);  
					j.setPriority(t.getPriority());
					j.setCompletionSuccess(false);
					Processor p;
					p= j.getProc();  // get the processor on which task has been allocated
					p.primaryJobQueue.addJob(j);
				//	System.out.println("task  "+t.getId()+"  job  "+j.getJobId()+"  p  "+p.getId()+"  queue size  "+p.primaryJobQueue.size());
					//backup addition
					backupJob = j.cloneJob_MWFD_RMS_EEPS();
					backupJob.setPrimary(false);
					backupJob.setCompletionSuccess(false);
					p=j.getBackupProcessor();
					p.backupJobQueue.addJob(backupJob);
			/*		System.out.println("task  "+t.getId()+"  backup job  "+backupJob.getJobId()+" primary  "+backupJob.isPrimary()+
							"  p  "+p.getId()+"  queue size  "+p.backupJobQueue.size());
			*/		
					
					activeJobQ.addJob(j);  //////ADD TO PRIMARY QUEUE
					backupQueue.add(backupJob);   /////ADD TO SPARE  QUEUE
					while (temp<=hyper)
					{
						
						
						temp+=t.getPeriod();
						activationTimes.add(temp);
						promotionTimes.add((long) (t.getSlack()));
						promotionTimes.add((long) (t.getSlack()+temp));
					}
						
		}
		
//		System.out.println("activationTimes  "+activationTimes.size()+"  promotionTimes  "+promotionTimes.size());
		promotionTimes.sort(new Comparator <Long>() {
	          @Override
	          public int compare(Long t1, Long t2) {
	        	  if(t1!=t2)
	        		  return (int) (t1-t2);
	        	  else 
	        		  return 0;
	        	 
	          }
	          });
		/*Iterator itr = promotionTimes.iterator();
		while(itr.hasNext())
			System.out.println("promotionTimes   "+itr.next());
	  	*/
             //  writer.write("\n\nSCHEDULE\nTASK ID  JOBID  ARRIVAL  ACET WCET DEADLINE  isPreempted STARTTIME ENDTIME  \n");
              //  writer1.write("\n\nSCHEDULE\nTASK ID  JOBID  ARRIVAL Av_CET WCETor DEADLINE  isPreempted STARTTIME ENDTIME  \n");

        nextActivationTime=  activationTimes.pollFirst();
          System.out.println("nextActivationTime  "+nextActivationTime);
    	 timeToNextPromotion = promotionTimes.get(0);
    		for (Processor proc : freeProcList)
        	{
    			System.out.println("p  "+proc.getId()+"   primary   "+proc.primaryJobQueue.size()+"  backup   "+proc.backupJobQueue.size());
        	}
    	 
       
        //START SCHEDULING///////////////////////////////START SCHEDULING////////
        
        while(time<hyper)
    	{
        	System.out.println(" time  "+time);
        	
        	// BACKUP JOB CHECKING AND EXECUTION
        	for (Processor proc : freeProcList)
        	{
        		while(!proc.backupJobQueue.isEmpty() && time>=proc.backupJobQueue.first().getPromotionTime() )
        		{
        			  Job b = proc.backupJobQueue.pollFirst();
        			  System.out.println("   in backing up  time   "+time + "  p  "+proc.getId()+ " task  "+b.getTaskId()+" job "+b.getJobId());
        			  
        			  // if processor is freee and job has not completed on primary
        			  if (!proc.isBusy() && !b.isCompletionSuccess()) 
        			{
        			System.out.println("       backup           time   "+time + "  p  "+proc.getId()+ " task  "+b.getTaskId()+" job "+b.getJobId());
        		  //---------START EXECUTION 
        			proc.setCurrentJob(b);
        			proc.setProc_state(proc_state.ACTIVE);
    				proc.getCurrentJob().setStartTime(time);
    				//set end time
    				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRomainingTimeCost());  // time + wcet_original 
    				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
    				proc.setBusy(true);
        			
        			
        			break;
        		    
        			}
        			else if (proc.isBusy() && proc.getCurrentJob().isPrimary() && !b.isCompletionSuccess()) // processor is busy with main/primary  task
        			{
        				System.out.println("processor busy");
        				//preempt the  currently running primary job
        				Job current1 = proc.getCurrentJob();
        				current1.setRemainingTime(current1.getRemainingTime()-(time- current1.getStartTime()));  // total time- executed time
        				proc.primaryJobQueue.addJob(current1);
        				// now start the backup job
        				 //START EXECUTION 
        				proc.setCurrentJob(b);
            			proc.setProc_state(proc_state.ACTIVE);
        				proc.getCurrentJob().setStartTime(time);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRomainingTimeCost());  // time + wcet_original 
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
        				break;
        			}
        		}
        	}

        	//new activation
        	if( (long)time== (long)nextActivationTime) // AFTER 0 TIME JOB ACTIVAIONS
			{
	
    			if (!activationTimes.isEmpty())
    			nextActivationTime=  activationTimes.pollFirst();
    		
   		   System.out.println("nextActivationTime  "+nextActivationTime+" size  "+activationTimes.size());

    			for (ITask t : taskset) 
				{
					
					Job n = null;
					long activationTime;
					activationTime = t.getNextActivation(time-1);  //GET ACTIVATION TIME
			//		System.out.println("  activationTime  "+activationTime);
					long temp1= (long) activationTime, temp2 =(long) time;
					if (temp1==temp2)
						n= t.activate_MWFD_RMS_EEPS(time); ///	remainingTime =  (long)ACET;  ////////////////
					
					if (n!=null)
					{
						
						n.setPriority(t.getPriority());
						n.setCompletionSuccess(false);
						Processor p;
						p= n.getProc();  // get the processor on which task has been allocated
						p.primaryJobQueue.addJob(n);
						System.out.println("task  "+t.getId()+"  job  "+n.getJobId()+"  p  "+p.getId()+"  queue size  "+p.primaryJobQueue.size());
						//backup addition
						backupJob = n.cloneJob_MWFD_RMS_EEPS();
						backupJob.setPrimary(false);
						backupJob.setCompletionSuccess(false);
						p=n.getBackupProcessor();
						p.backupJobQueue.addJob(backupJob);
						System.out.println("task  "+t.getId()+"  backup job  "+backupJob.getJobId()+" primary  "+backupJob.isPrimary()+
								"  p  "+p.getId()+"  queue size  "+p.backupJobQueue.size());
						
						
						activeJobQ.addJob(n);  //////ADD TO PRIMARY QUEUE
						backupQueue.add(backupJob);   /////ADD TO SPARE  QUEUE
						
					}
				}
				
			} 
        	
    		//////////////////PREEMPTION////////////////////////
        	for (Processor proc : freeProcList)
        	{
        		if(!proc.primaryJobQueue.isEmpty()  && proc.getCurrentJob().getPeriod()>proc.primaryJobQueue.first().getPeriod())
        		{
        			Job lowP = proc.getCurrentJob() , highP = proc.primaryJobQueue.first();
        			lowP.setRemainingTime(lowP.getRemainingTime()- (time-lowP.getStartTime()));
        			proc.primaryJobQueue.addJob(lowP);
        			// start high priority
        			proc.setCurrentJob(highP);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRemainingTime());
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
        			        			
        		}
        	
        	}
        	
        	// PRIMARY JOB CHECKING AND EXECUTIOM
        	for (Processor proc : freeProcList)
        	{
        		if(!proc.primaryJobQueue.isEmpty() && proc.isBusy()==false )
        		{
        			proc.setCurrentJob( proc.primaryJobQueue.pollFirst());
        			System.out.println("task started   "+proc.getCurrentJob().getTaskId());
        			if (proc.getCurrentJob()!=null && proc.getCurrentJob().isCompletionSuccess()==false)      // if job in queue is null 
	        		{
        				System.out.println("p  "+proc.getId()+"  task  "+proc.getCurrentJob().getTaskId()+"  job   "+proc.getCurrentJob().getJobId());
        				
        				proc.setProc_state(proc_state.ACTIVE);
        				proc.getCurrentJob().setStartTime(time);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRemainingTime());
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
	        		}
        		}
        		else
        		{//---------------
        		//	proc.setIdleStartTime(time);
        		//	System.out.println("time  "+time+"   idle start   "+proc.getIdleStartTime());
        		//	proc.idleTime++;
        		}
        	}
        	
        
        	// count busy time
        	for (Processor proc : freeProcList)
        	{
        		if (proc.getProc_state()==proc_state.ACTIVE)
        		{
        			proc.activeTime++;
        			System.out.println("TIME  "+time+"  p  "+proc.getId()+" active  "+ proc.activeTime);;
        			
        		}
        		if (proc.getProc_state()==proc_state.IDLE)
        		{
        			proc.idleTime++;
        			System.out.println("TIME  "+time+"  p  "+proc.getId()+"  idle "+ proc.idleTime);;
                	
        		}
        		if (proc.getProc_state()==proc_state.SLEEP)
        		{	
        			proc.sleepTime++;
        			System.out.println("TIME  "+time+"  p  "+proc.getId()+" sleep  "+proc.sleepTime );;
                	
        		}
        	}
        	
        	
        	//at end time of any job in any processor
        	for (Processor proc : freeProcList)
        	{
        		if(time== proc.getEndTimeCurrentJob())
        		{
        			proc.setProc_state(proc_state.IDLE);
        			proc.setBusy(false);
        			proc.getCurrentJob().setCompletionSuccess(true);
        			System.out.println("  p  "+proc.getId()+"end time  "+proc.getEndTimeCurrentJob());
        			
        			
        			// delete the backup job if not started
        			Iterator<Job> itr_backup = proc.getCurrentJob().getBackupProcessor().backupJobQueue.iterator();
        			while(itr_backup.hasNext())
        			{
        				Job backup = itr_backup.next();
        				if(backup.getTaskId()==proc.getCurrentJob().getTaskId() && backup.getJobId()==proc.getCurrentJob().getJobId())
        				{
        					System.out.println(" time  "+time+"   p  "+proc.getId()+ "  backup p  " +proc.getCurrentJob().getBackupProcessor().getId()+
        							"  delete task  "+	backup.getTaskId() +"  job  "+ backup.getJobId());
        					backup.setCompletionSuccess(true);
        					break;
        				}
        			}
        	
        		}
        	}
        	
        	time++;
    		
    	}
        for (Processor proc : freeProcList)
    	{
    		
    			System.out.println("out TIME  "+time+"  p  "+proc.getId()+" active  "+ proc.activeTime);;
    		
    			System.out.println("TIME  "+time+"  p  "+proc.getId()+"  idle "+ proc.idleTime);;
            	
    			System.out.println("TIME  "+time+"  p  "+proc.getId()+" sleep  "+proc.sleepTime );;
            	
    		
    	}
  /*  	System.out.println(" spare active time "+spare.getActiveTime()+"  sleep "+spare.getSleepTime()+"  idle  "+spare.getIdleTime());
    	System.out.println("primary  active time "+primary.getActiveTime()+"  sleep "+primary.getSleepTime()+"  idle  "+primary.getIdleTime());
    */	/*Iterator<Job> itr1 = spareQueue.iterator();
    	 while (itr1.hasNext())
    	 {
    		 
    		 j = itr1.next();
    		 System.out.println("task  "+j.getTaskId()+"  job  "+j.getJobId()+"   period   "+j.getPeriod()+"   prio   " +j.getPriority()
    		 +"  start time  "+j.getActivationDate()+"  promotion "+j.getPromotionTime());
    	 }*/
    
    	double primaryEnergy, spareEnergy;
    	primaryEnergy = energyConsumed.energyActive(primary.activeTime, fq)+energyConsumed.energy_IDLE(primary.idleTime) +energyConsumed.energySLEEP(primary.sleepTime) ;
    	spareEnergy = energyConsumed.energyActive(spare.getActiveTime(), 1)+energyConsumed.energy_IDLE(spare.idleTime) +energyConsumed.energySLEEP(spare.sleepTime) ;
  /*  	
    	System.out.println("primary  active energy"+energyConsumed.energyActive(primary.activeTime, fq)+"  idle  "+energyConsumed.energy_IDLE(primary.idleTime)
    	+" sleep  "+energyConsumed.energySLEEP(primary.sleepTime));
    	System.out.println("spare  active energy "+energyConsumed.energyActive(spare.getActiveTime(), 1)+"  idle  "+energyConsumed.energy_IDLE(spare.idleTime)
    	+" sleep  "+energyConsumed.energySLEEP(spare.sleepTime));
    
    	
    	System.out.println("primaryEnergy   "+primaryEnergy +" spareEnergy  "+spareEnergy);
    */
    /*	 writer2.write(total_no_tasksets++ + " "+Double.valueOf(twoDecimals.format(U_SUM))+" "+Double.valueOf(twoDecimals.format(set_fq))+" "
    	    	    +" "+ Double.valueOf(twoDecimals.format(fq))+" "+(double)primary.activeTime +" "+(double)primary.idleTime +" "+(double)primary.sleepTime 
    	    	    +" "+(double)spare.activeTime +" "+(double)spare.idleTime +" "+(double)spare.sleepTime +" "+Double.valueOf(twoDecimals.format(primaryEnergy))+
    	    	    " "+Double.valueOf(twoDecimals.format(spareEnergy))+" "+npmResult[2] +"\n");
     */   System.out.println("   tasksets  "+total_no_tasksets);
    
    }
    
     // writer.close();
   //   writer1.close();
   //  writer2.close();
    System.out.println("success");
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
	

	