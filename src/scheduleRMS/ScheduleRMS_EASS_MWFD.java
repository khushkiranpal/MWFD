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
import java.util.Random;
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
			public static final  long hyperperiod_factor= 10;	
			public static final   double  CRITICAL_TIME=  1.5*hyperperiod_factor;///1500;  //
			public static final   double  CRITICAL_freq= 0.50;//0.42;   //
		
			public static final int d = 2;
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
	String inputfilename= "test1";
    FileTaskReaderTxt reader = new FileTaskReaderTxt("D:/CODING/TASKSETS/uunifast/"+inputfilename+".txt"); // read taskset from file
    DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
    Calendar cal = Calendar.getInstance();
    String date = dateFormat.format(cal.getTime());
  String filename= "D:/CODING/TEST/EESP/allocation"+"_"+inputfilename+"_"+date+".txt";
  String filename4= "D:/CODING/TEST/EESP/tasksProcWise"+"_"+inputfilename+"_"+date+".txt";
  	String filename1= "D:/CODING/TEST/EESP/processorwise"+"_"+inputfilename+"_"+date+".txt";
    String filename2= "D:/CODING/TEST/EESP/energyMWFD"+"_"+inputfilename+"_"+date+".txt";
    String filename3= "D:/CODING/TEST/EESP/tasksMWFD"+"_"+inputfilename+"_"+date+".txt";
    
 //   Writer writer_allocation = new FileWriter(filename);
 //  Writer writer_schedule = new FileWriter(filename1);
    Writer writer_energy = new FileWriter(filename2);
    Writer writer_tasks = new FileWriter(filename3);
 //   Writer writer_taskProcWise = new FileWriter(filename4);
    DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 1 decimal points
    DecimalFormat fourDecimals = new DecimalFormat("#.###");
    Energy energyConsumed = new Energy();
    Job[] current= new Job[2], spare_current = new Job[2];  // FOR SAVING THE NEWLY INTIAlIZED JOB  FROM JOBQUEUE SO THAT IT 
	// IS VISIBLE OUTSIDE THE BLOCK
    
  ITask task;
    ITask[] set = null;
    double U_SUM;
    int m =2;// no. of processors
    // final   long  CRITICAL_TIME= 4;
      
    // IDLE SLOTS QUEUE
    IdleSlot slot = new IdleSlot(); // idle slot
    List <IdleSlot> slots = new ArrayList<IdleSlot>();
    int total_no_tasksets=1;
    writer_energy.write("TASKSET UTILIZATION SYS_FREQ FREQ TOTAL_ENERGY \n");
    writer_tasks.write("fullBackupsExecuted partialBackupsExecuted fullBackupsCancelled"
    		+ "	 cancelledPrimariesFull   cancelledPrimariesPartial  fullPrimariesExecuted noOfFaults");
   // writer_taskProcWise.write("proc primary  backup  total");
 
    SysClockFreq frequency = new SysClockFreq();
    		///////////////////////////////////////ScheduleRMS_EASS//////////
    		ScheduleRMS_EASS test = new ScheduleRMS_EASS();
    			test.schedule(inputfilename,hyperperiod_factor, d,CRITICAL_TIME,CRITICAL_freq);
    
    while ((set = reader.nextTaskset()) != null)
    {
    	long fullBackupsExecuted=0;
    	long partialBackupsExecuted=0;
    	long fullBackupsCancelled=0;
    	long cancelledPrimariesFull=0;
    	long cancelledPrimariesPartial=0;
    	long fullPrimariesExecuted=0;
    
    	 
    	long noOfFaults=0;
    	double energyTotal=0;
    	boolean deadlineMissed = false;
    	Job lastExecutedJob= null, primaryJob, backupJob;
        ProcessorState proc_state = null;
        
    	  int id = 0;  // idle slot id 
    	 long time=0 ;
    	     long timeToNextPromotion=0, spareActiveTime = 0;
			long timeToNextArrival=0;
    	     long endTime = 0; // endtime of job
			long spareEndTime=0;
    	     long idle = 0;  // idle time counter for processor idle slots
    	     SchedulabilityCheck schedule = new SchedulabilityCheck();
    	
    	 Processor primary = new Processor();
    	 Processor spare = new Processor();
    	 
    	 spare.setBusy(false);
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
			for(int i = 1;i<=m;i++)  // m is number of processors
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
	     //////////////   	hyper = hyperperiod;
		
			
    	ParameterSetting ps = new ParameterSetting();
    	boolean unschedulable = false;
    	ps.setBCET(taskset, 0.5);
    	ps.setACET(taskset);
    	
    	
		
    	double set_fq = frequency.SysClockF(taskset), fq = 0;
    
    	fq=Math.max(set_fq, CRITICAL_freq);
    	
    	System.out.println("frequency   " +fq);
    	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
    	
    	boolean schedulability = schedule.worstCaseResp_TDA_RMS(taskset);
  //  	System.out.println("on  one processor   "+schedulability+"  at fq "+fq);
   
    	while(schedulability==false)
       {
    	
    	
         fq=fq+0.01;

  //	   System.out.println("while  frequency   " +fq);
     	ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
        
    	   schedulability = schedule.worstCaseResp_TDA_RMS(taskset);   
    	   System.out.println(schedulability);
        
    	   if (fq>1)
    	   {
    	//	   System.out.println("breaking   "+fq);
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
    	}
   	  */
     
    	
      	
      	//ALLOCATION STARTED 
    	//writer_allocation.write("Proc TASK U WCET PERIOD IS_PRIMARY BACKUP_PR PRIMARY_PR");
    	
        
		// SORT IN DECREASING ORDER OF UTILIZATION FOR MFWD wcet according to frequency
		
		Comparator<ITask> c = new Comparator<ITask>() {
	    	 public int compare(ITask p1, ITask p2) {
	    		 int cmp;
		//	System.out.println("t1 "+p1.getId()+"  u1 "+Double.valueOf(fourDecimals.format(((double)p1.getWcet()/(double)p1.getDeadline()))));
		//	System.out.println("t2 "+p2.getId()+"  u2 "+Double.valueOf(fourDecimals.format(((double)p2.getWcet()/(double)p2.getDeadline()))));

	    		 double temp =  ( (Double.valueOf(twoDecimals.format(((double)p2.getWcet()/(double)p2.getDeadline()))))
						-(Double.valueOf(twoDecimals.format(((double)p1.getWcet()/(double)p1.getDeadline()))))); // backup????? wcet uti??
			//	System.out.println("temp   "+temp);
				if(temp>0)
					cmp = 1;
				else
					cmp=-1;
			//	System.out.println(" cmp  "+cmp);
	    		 return cmp;
			}
		  };
		taskset.sort(c);
		
		for(ITask t : taskset)
    	{
		//	System.out.println("task  "+t.getId()+" u "+ (Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline())))) );
    	}
   	  do{      // WHILE ALL PROCESSORS HAVE SCHEDULABLE TASKSETS ON GIVEN FREQUENCIES 
     	
     		for(Processor pMin : freeProcList)
     		{
         		
     			pMin.taskset.clear();
     			pMin.setWorkload(0);
         	//	System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
     		}    		
     	
   	
    	
	//ALLOCATION OF PRIMARIES
    	
     		//writer_allocation.write("\nPRIMARY ");
    	
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
    		t.setFrequency(fq);
    		minP.taskset.add(t);
    		minP.setWorkload(Double.valueOf(twoDecimals.format(minP.getWorkload()+u)));
    		t.setP(minP);
    		t.setPrimaryProcessor(minP);
    	//writer_allocation.write("\n"+minP.getId()+" "+t.getId()+" "+u+" "+t.getWcet()+" "+t.getPeriod());
    		
    	}
    
    	
    	
    	//ALLOCATION OF BACKUPS
    	//writer_allocation.write("\nBACKUPS ");
    	// SORT IN DECREASING ORDER OF UTILIZATION FOR MFWD wcet_original
		
    			Comparator<ITask> c1 = new Comparator<ITask>() {
    		    	 public int compare(ITask p1, ITask p2) {
    		    		 int cmp;
    			//	System.out.println("t1 "+p1.getId()+"  u1 "+Double.valueOf(fourDecimals.format(((double)p1.getWcet()/(double)p1.getDeadline()))));
    			//	System.out.println("t2 "+p2.getId()+"  u2 "+Double.valueOf(fourDecimals.format(((double)p2.getWcet()/(double)p2.getDeadline()))));

    		    		 double temp =  ( (Double.valueOf(twoDecimals.format(((double)p2.getWCET_orginal()/(double)p2.getDeadline()))))
    							-(Double.valueOf(twoDecimals.format(((double)p1.getWCET_orginal()/(double)p1.getDeadline()))))); // backup????? wcet uti??
    				//	System.out.println("temp   "+temp);
    					if(temp>0)
    						cmp = 1;
    					else
    						cmp=-1;
    				//	System.out.println(" cmp  "+cmp);
    		    		 return cmp;
    				}
    			  };
    			taskset.sort(c1);
    	
    	for(ITask t : taskset)
    	{
    		double u = Double.valueOf(twoDecimals.format(((double)t.getWCET_orginal()/(double)t.getD()))), work=1;
    		ITask backup_task;
    		Processor minP=null;
  //  		System.out.println("t  "+t.getId()+" u backup  "+u);
    		for(Processor pMin : freeProcList)
    		{
    			if (pMin == t.getP())   // IF PRIMARY PROCESSOR CONTAINS THE TASK, ALLOCATE BACKUP ON SOME OTHER PROCESSOR
    				continue;
    			if(work >pMin.getWorkload())
    			{
    				work=Double.valueOf(twoDecimals.format(pMin.getWorkload()));
    				minP = pMin;
    			}
    //			System.out.println("work   "+work+"  minP  "+minP.getId()+ "  pMin  "+pMin.getId());
        		
    		}
    		t.setBackupProcessor(minP);
    		backup_task = t.cloneTask_MWFD_RMS_EEPS();
    		backup_task.setPrimary(false);  //setup backup processor
    		backup_task.setFrequency(1);
    		backup_task.setBackupProcessor(minP);
    		backup_task.setPrimaryProcessor(t.getP());
    		
    		minP.taskset.add(backup_task);
    		minP.setWorkload(Double.valueOf(twoDecimals.format(minP.getWorkload()+u)));
    		backup_task.setP(minP);
    		
   		//writer_allocation.write("\n"+minP.getId()+" "+t.getId()+" "+u+" "+t.getWcet()+" "+t.getPeriod());
     	   
    	}
    	
    	//CHECK SCHEDULABILITY ON ALL PROCESSORS
    	
    	for(Processor pMin : freeProcList)
		{
    		
    	//	System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
    	/*	for(ITask t : pMin.taskset)
    			
        	{
    			System.out.println("task   "+t.getId()+"  u  "+ Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline())))
    			+"   primary  "+t.isPrimary()+"  Proc   "+t.getP().getId()+" prom ");
    			
       
        	}*/
    		
    		schedulability = schedule.worstCaseResp_TDA_RMS(pMin.taskset);
    	//	System.out.println("proc   "+pMin.getId()+"   schedulability   "+schedulability);
    		if(schedulability==false)
    		{
    			unschedulable= true;
    			fq=fq+0.01;
    			ps.set_freq(taskset,Double.valueOf(twoDecimals.format(fq)));
    	    	
    		}
    		else 
    			unschedulable=false;
		}
   // 	System.out.println("fq  "+fq+"   unschedulable  "+unschedulable);

		for(Processor pMin : freeProcList)
		{
    		
	//		System.out.println("processor   "+pMin.getId()+"   size  "+pMin.taskset.size()+"  w  "+pMin.getWorkload());
		}   
    	}while( unschedulable == true);
    	
    	///END ALLOCATION
   	  
      //////////////FAULT///////////////////////////
   	fault = f.lamda_F(hyper, CRITICAL_freq, fq, d);        //////////////FAULT////////////
		
	/*for(Processor p : freeProcList)
	{
//		System.out.println("FAULT  P "+p.getId());
		fault = f.lamda_F(hyper*hyperperiod_factor, CRITICAL_freq, fq, d);
		p.setFault(fault);
		for(int fa: fault)
		{
		//	System.out.println(" p "+p.getId()+"   fault  "+ fa);
		}
		
		
	}*/

 
   	/*for(Processor pMin : freeProcList)
	{
   		//writer_allocation.write("\n\nprocessor   "+pMin.getId()+"\t frequency   "+fq+"\n");
		for(ITask t : pMin.taskset)
			
    	{
			writer_allocation.write(pMin.getId()+" "+t.getId()+" "+ Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline())))
			+" "+t.getWCET_orginal()+" "+t.getPeriod()+" "+
			" "+t.isPrimary()+	" "+t.getBackupProcessor().getId()+" "+t.getPrimaryProcessor().getId()+"\n");
			
			System.out.println("task   "+t.getId()+"  u  "+ Double.valueOf(twoDecimals.format(((double)t.getWcet()/(double)t.getDeadline())))
			+"   primary  "+t.isPrimary()+"  Proc   "+t.getP().getId()+	"   backup p  "+t.getBackupProcessor().getId()+
			"   primary  "+t.getPrimaryProcessor().getId());
   
    	}
		writer_allocation.write("\n"+"FAULT  \t\t");
		for(int fa: pMin.getFault())
		{
			writer_allocation.write(fa+"\t\t");
		}
		writer_allocation.write("\nU "+Double.valueOf(twoDecimals.format((SystemMetric.utilisation(pMin.taskset)))));
	}*/
   	  
   	  

    	
    	
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
		
		taskset = queue.getSortedSet();
		
    	// ACTIVATE ALL TASKS AT TIME 0 INITIALLY IN QUEUE  
		
		for(ITask t : taskset)  // activate all tasks at time 0
		{
					temp=0;
					j =  t.activate_MWFD_RMS_EEPS(time);  
		//		System.out.println("t "+t.getId()+"j  "+j.getJobId());
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
					while (temp<=hyper*hyperperiod_factor)
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
	/*	 writer_schedule.write("\nP_ID TASKID FREQ WCET ACET BCET DEADLINE P/B \n");
	     
		for(Processor p : freeProcList)
		{
			for(ITask t :p.taskset)
			{
				writer_schedule.write("\n"+p.getId()+" "+t.getId()+" "+t.getFrequency()+" "+t.getWcet()+" "+t.getACET() 
				+" "+t.getBCET()+" "+t.getDeadline()+" "+t.isPrimary()
				);
			}
		}*/
		
     //     writer_schedule.write("\nP_ID jobno. TASKID  JOBID PR/BK FREQ WCET DEADLINE  isPreempted STARTTIME ENDTIME FAULTY idleS idleE idleLen \n");
        nextActivationTime=  activationTimes.pollFirst();
          // System.out.println("nextActivationTime  "+nextActivationTime);
    	 timeToNextPromotion = promotionTimes.get(0);
    		for (Processor proc : freeProcList)
        	{
    			// System.out.println("p  "+proc.getId()+"   primary   "+proc.primaryJobQueue.size()+"  backup   "+proc.backupJobQueue.size());
        	}
    	 
       
        //START SCHEDULING///////////////////////////////START SCHEDULING///////////////////
        
        while(time<hyper*hyperperiod_factor)
    	{
        	    		
        //	System.out.println(" time  "+time);
        	
        	// BACKUP JOB CHECKING AND EXECUTION
        	for (Processor proc : freeProcList)
        	{
        		 /* System.out.println("    BACKUP JOB CHECKING AND EXECUTION  "+time + "  p  "+proc.getId()+ " task  "+
        				  proc.backupJobQueue.first().getTaskId()+" job "+ proc.backupJobQueue.first().getJobId());
        		  */
        		while(!proc.backupJobQueue.isEmpty() && time>=proc.backupJobQueue.first().getPromotionTime() )
        		{
        			  Job b = proc.backupJobQueue.pollFirst();
        			  // System.out.println("    BACKUP JOB CHECKING AND EXECUTION  "+time + "  p  "+proc.getId()+ " task  "+b.getTaskId()+" job "+b.getJobId());
        			  
        			  // if processor is freee and job has not completed on primary
        			  if (!proc.isBusy() && (!b.isCompletionSuccess() || b.isFaulty())) 
        			{
        			// System.out.println("     BACKUP JOB CHECKING AND EXECUTION      time   "+time + "  p  "+proc.getId()+ " task  "+b.getTaskId()+" job "+b.getJobId());
        		  //---------START EXECUTION 
        			
        			proc.setIdleEndTime(time); // IF PROCESSOR WAS FREE , END IDLE SLOT

					// RECORD THE SLOT LENGTH
					if (proc.getIdleSlotLength()>0)
					{
			//		writer.write("\n\t\t\t\t\t\t\t"+processor.getId()+"\t\t\t\t\t"+processor.getIdleStartTime()+"\t"+time+" \t"+processor.getIdleSlotLength());
			//		writer_schedule.write("\n"+proc.getId()+" "+proc.getIdleStartTime()+" "+time+" "+proc.getIdleSlotLength()+" idleend");
						proc.setIdleSlotLength(0); // REINITIALIZE THE IDLE LENGTH
					}
    				
    				
        			
        			proc.setCurrentJob(b);
        			
        			if(!proc.getCurrentJob().isPreempted)
						proc.setNoOfBackJobs(proc.getNoOfBackJobs()+1);
        			proc.setProc_state(proc_state.ACTIVE);
    				proc.getCurrentJob().setStartTime(time);
    				//set end time
    				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRomainingTimeCost());  // time + wcet_original 
    				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
    				proc.setBusy(true);
    		/*		writer_schedule.write("\nb"+proc.getId()+" "+proc.getNoOfBackJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
    						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
    						+" "+	proc.getCurrentJob().getRomainingTimeCost()+" "+	proc.getCurrentJob().getDeadline()
    						+" "+	proc.getCurrentJob().isPreempted+" "+time+" ");
        	*/		
        			break;
        		    
        			}
        			else if (proc.isBusy() && proc.getCurrentJob().isPrimary() && 
        					(!b.isCompletionSuccess() || b.isFaulty())) // processor is busy with main/primary  task
        			{
        				// System.out.println("processor busy");
        				
        				//preempt the  currently running primary job
        				Job current1 = proc.getCurrentJob();
        				current1.setRemainingTime(current1.getRemainingTime()-(time- current1.getStartTime()));  // total time- executed time
        			    current1.isPreempted=true;
        		/*		writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfPriJobs()+" "+current1.getTaskId()+" "+current1.getJobId()+" "+
        						current1.isPrimary()+" "+Double.valueOf(twoDecimals.format(	current1.getFrequency()))
        						+" "+	current1.getRemainingTime()+" "+	current1.getDeadline()
        						+" "+	current1.isPreempted+" "+current1.getStartTime()+" ");
        			    writer_schedule.write("\t "+time+" preemptbybackup");
        		*/		proc.primaryJobQueue.addJob(current1);
        				// now start the backup job
        				 //START EXECUTION 
        				proc.setCurrentJob(b);
        				if(!proc.getCurrentJob().isPreempted)
    						proc.setNoOfBackJobs(proc.getNoOfBackJobs()+1);
            			proc.setProc_state(proc_state.ACTIVE);
        				proc.getCurrentJob().setStartTime(time);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRomainingTimeCost());  // time + wcet_original 
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
        			/*	writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfBackJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
        						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
        						+" "+	proc.getCurrentJob().getRomainingTimeCost()+" "+	proc.getCurrentJob().getDeadline()
        						+" "+	proc.getCurrentJob().isPreempted+" "+time+" ");
            		*/	
        				
        				break;
        			}
        		}
        	}

        	//new activation
        	if( (long)time== (long)nextActivationTime) // AFTER 0 TIME JOB ACTIVAIONS
			{
	
    			if (!activationTimes.isEmpty())
    			nextActivationTime=  activationTimes.pollFirst();
    		
   		   // System.out.println("//new activation  nextActivationTime  "+nextActivationTime+" size  "+activationTimes.size());

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
						// System.out.println("//new activation activated   task  "+t.getId()+"   time   "+time+"  job  "+n.getJobId()+"  p  "+p.getId()+"  primaryJobQueue queue size  "+p.primaryJobQueue.size());
						//backup addition
						backupJob = n.cloneJob_MWFD_RMS_EEPS();
						backupJob.setPrimary(false);
						backupJob.setCompletionSuccess(false);
						backupJob.setFrequency(1);
						p=n.getBackupProcessor();
						p.backupJobQueue.addJob(backupJob);
						// System.out.println("//new activation  task  "+t.getId()+"  backup job  "+backupJob.getJobId()+" primary  "+backupJob.isPrimary()+
					//			"  p  "+p.getId()+"  backupJobQueue queue size  "+p.backupJobQueue.size());
						
						
						activeJobQ.addJob(n);  //////ADD TO PRIMARY QUEUE
						backupQueue.add(backupJob);   /////ADD TO SPARE  QUEUE
						
					}
				}
				
			} 
        	
    		//////////////////PREEMPTION////////////////////////
        	for (Processor proc : freeProcList)
        	{
        		//preempt primary by primary
        		if(time>0 && !proc.primaryJobQueue.isEmpty() && proc.getCurrentJob().isPrimary() &&
        				!proc.getCurrentJob().isCompletionSuccess() &&  
        				proc.getCurrentJob().getPeriod()>proc.primaryJobQueue.first().getPeriod())
        		{
        		// System.out.println("  //PREEMPTION/   ");	
        			Job lowP = proc.getCurrentJob() , highP = proc.primaryJobQueue.first();
        		    
        			lowP.setRemainingTime(lowP.getRemainingTime()- (time-lowP.getStartTime()));
        			proc.primaryJobQueue.addJob(lowP);
        			lowP.isPreempted=true;
        	/*		writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfPriJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
    						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
    						+" "+	proc.getCurrentJob().getRemainingTime()+" "+	proc.getCurrentJob().getDeadline()
    						+" "+	proc.getCurrentJob().isPreempted+" "+proc.getCurrentJob().getStartTime());
        			
        			writer_schedule.write("\t "+time +"  preempted ");
        	*/		// start high priority
        			proc.setCurrentJob(highP);
        			
        			if(!proc.getCurrentJob().isPreempted)
						proc.setNoOfPriJobs(proc.getNoOfPriJobs()+1);
        			proc.getCurrentJob().setStartTime(time);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRemainingTime());
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
        			        			
        		}
        		// preempt backup job by backup
        		if(time>0 && !proc.backupJobQueue.isEmpty() && !proc.getCurrentJob().isPrimary() &&
        				!proc.getCurrentJob().isCompletionSuccess() &&  
        				proc.getCurrentJob().getPeriod()>proc.backupJobQueue.first().getPeriod()
        				&& (proc.backupJobQueue.first().isFaulty() || !proc.backupJobQueue.first().isCompletionSuccess()) )
        		{
        		//pending-----------------------------------------------------------
        		}
        	}
        	
        	// PRIMARY JOB CHECKING AND EXECUTIOM
        	for (Processor proc : freeProcList)
        	{
        		if(!proc.primaryJobQueue.isEmpty() && proc.isBusy()==false )
        		{
        			/*Job firstJob = proc.primaryJobQueue.pollFirst();
        			System.out.println("p  "+proc.getId()+"  firstJob  "+firstJob.getTaskId()+" job  "+firstJob.getJobId());
        			while(firstJob.isCompletionSuccess())
        				{
        				firstJob = proc.primaryJobQueue.pollFirst();
        				if(firstJob== null  )
        					break;
        				System.out.println("p  "+proc.getId()+"  firstJob  "+firstJob.getTaskId()+" job  "+firstJob.getJobId());
        	        	
        				}*/
        		
        			proc.setCurrentJob( proc.primaryJobQueue.pollFirst());
        			
        			// System.out.println("// PRIMARY JOB CHECKING AND EXECUTIOM   task started   "+proc.getCurrentJob().getTaskId() +"  proc.getCurrentJob().isCompletionSuccess() "+proc.getCurrentJob().isCompletionSuccess());
        			if (proc.getCurrentJob()!=null && proc.getCurrentJob().isCompletionSuccess()==false)      // if job in queue is null 
	        		{
        				// System.out.println("time   "+time+"   p  "+proc.getId()+"  task  "+proc.getCurrentJob().getTaskId()+"  job   "+proc.getCurrentJob().getJobId());
        				
        				proc.setIdleEndTime(time); // IF PROCESSOR WAS FREE , END IDLE SLOT

						// RECORD THE SLOT LENGTH
						if (proc.getIdleSlotLength()>0)
						{
						//	writer.write("\n\t\t\t\t\t\t\t"+processor.getId()+"\t\t\t\t\t"+processor.getIdleStartTime()+"\t"+time+" \t"+processor.getIdleSlotLength());
			/*				writer_schedule.write("\n"+proc.getId()+" "+proc.getIdleStartTime()+" "+time+
									" "+proc.getIdleSlotLength()+" idleend");
			*/				proc.setIdleSlotLength(0); // REINITIALIZE THE IDLE LENGTH
						}
        				
        				if(!proc.getCurrentJob().isPreempted)
						proc.setNoOfPriJobs(proc.getNoOfPriJobs()+1);
        				
						proc.setProc_state(proc_state.ACTIVE);
        				proc.getCurrentJob().setStartTime(time);
        				//set end time
        				proc.getCurrentJob().setEndTime(time+proc.getCurrentJob().getRemainingTime());
        				proc.setEndTimeCurrentJob(proc.getCurrentJob().getEndTime()-1);
        				proc.setBusy(true);
        			/*	writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfPriJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
        						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
        						+" "+	proc.getCurrentJob().getRemainingTime()+" "+	proc.getCurrentJob().getDeadline()
        						+" "+	proc.getCurrentJob().isPreempted+" "+time+" ");
            		*/	
        				
	        		}
        		}
        		else if (proc.primaryJobQueue.isEmpty() && proc.isBusy()==false )
        		{//---------------
        		
        	
        			// System.out.println("  p  " +proc.getId()+"  proc.getIdleSlotLength()  "+proc.getIdleSlotLength());
        			if (proc.getIdleSlotLength()==0)
					{
					// System.out.println("idle slot started");
        	//			writer_schedule.write("\n"+proc.getId()+ " "+time+" idlestart");
						proc.setIdleSlotLength(proc.getIdleSlotLength()+1);// INCREMENT THE  LENGTH OF IDLE SLOT FROM 0 TO 1
						proc.setIdleStartTime(time);
					}
					else
						proc.setIdleSlotLength(proc.getIdleSlotLength()+1); // INCREMENT THE  LENGTH OF IDLE SLOT 
				
        			
        		////////////	writer1.write("\n setIdleStartTime "+time);
        	//		proc.setIdleStartTime(time);
        		if (proc.getTimeToNextArrival()>CRITICAL_TIME)
        			proc.setProc_state(proc_state.SLEEP);
        		else
        			proc.setProc_state(proc_state.IDLE);
        		// System.out.println("// PRIMARY JOB CHECKING AND EXECUTIOM ELSE PART   p  "+proc.getId()+"   timeToNextArrival   "+proc.getTimeToNextArrival()+"   Proc_state  "+proc.getProc_state());
        		}
        	}
        	
        
        	// count busy time
        	for (Processor proc : freeProcList)
        	{
        		if (proc.getProc_state()==proc_state.ACTIVE)
        		{
        			proc.activeTime++;
        		//	 System.out.println("TIME  "+time+"  p  "+proc.getId()+" active  "+ proc.activeTime);
        			
        		}
        		if (proc.getProc_state()==proc_state.IDLE)
        		{
        			proc.idleTime++;
        		//	 System.out.println("TIME  "+time+"  p  "+proc.getId()+"  idle "+ proc.idleTime);;
                	
        		}
        		if (proc.getProc_state()==proc_state.SLEEP)
        		{	
        			proc.sleepTime++;
        		//	 System.out.println("TIME  "+time+"  p  "+proc.getId()+" sleep  "+proc.sleepTime );;
                	
        		}
        	}
        	
       		/////////////////////////////FAULT INDUCTION///////////////////////
        	//	if(time == 			11000)
        		//{
        	Random rand = new Random();
        		
        	
        	if ( fault.size()>0 )
    		{
		//	System.out.println("out fault time  "+time+"  task  "+lastExecutedJob.getTaskId()+" job  "+lastExecutedJob.getJobId());

    		if(time==fault.get(0)*hyperperiod_factor)
    		
    			{
    			int tempPr= 1+rand.nextInt(m), count = m;
    			 for ( Processor p : freeProcList)
    			 {
    				 if (p.getId()==tempPr )
    				 {
    					 if (p.getProc_state()==proc_state.ACTIVE && p.getCurrentJob().isPrimary())
         				{	
         			 System.out.println("                               fault time  "+time);//+"                task  "+
         			//	p.getCurrentJob().getTaskId()+" job  "+p.getCurrentJob().getJobId());
         				
         				p.getCurrentJob().setCompletionSuccess(false);
         				p.getCurrentJob().setFaulty(true);
         				noOfFaults++;
         				 Iterator<Job> spareItr = p.getCurrentJob().getBackupProcessor().backupJobQueue.iterator();
             			 while(spareItr.hasNext())
             			 {
             				 Job temp1;
             				 temp1  = spareItr.next();
             				 // System.out.println("primaary pending  task  "+temp1.getTaskId());
             		    		 
             				 if(temp1.getTaskId()== p.getCurrentJob().getTaskId() && temp1.getJobId()== p.getCurrentJob().getJobId())
             				 {
             					 temp1.setFaulty(true);
             					
             					 // System.out.println("time    "+time+" primaary pending task  "+temp1.getTaskId()+"  spare"+temp1.getTaskId() );
             				    break;
             				 }
             			 }
         				
         				}
    					 /*else
    					 {
    						 tempPr= 1+rand.nextInt(m);
    						 count--;
    						 if(count==0)
    							 break;
    					 }*/
    				 }
    			 }
    				fault.remove(0);
    			}
    		}
       /* for ( Processor p : freeProcList)
        {
        	// System.out.println("FAULT PROCESSOR  "+p.getId());
        	if ( p.fault.size()>0 )
        		{
    			// System.out.println("out fault time  "+time+"  task  "+p.getCurrentJob().getTaskId()+" job  "+p.getCurrentJob().getJobId());
    		//	if(time == 			11)
    			if(time==p.fault.get(0))
        		
        			{
    				// System.out.println("    fault time  "+time+"     task  " +"  primary  "+p.getCurrentJob().isPrimary()
        		//	+p.getCurrentJob().getTaskId()+" job  "+p.getCurrentJob().getJobId());
        				if (p.getProc_state()==proc_state.ACTIVE && p.getCurrentJob().isPrimary())
        				{	
        			 System.out.println("                               fault time  "+time);//+"                task  "+
        			//	p.getCurrentJob().getTaskId()+" job  "+p.getCurrentJob().getJobId());
        				
        				p.getCurrentJob().setCompletionSuccess(false);
        				p.getCurrentJob().setFaulty(true);
        				noOfFaults++;
        				 Iterator<Job> spareItr = p.getCurrentJob().getBackupProcessor().backupJobQueue.iterator();
            			 while(spareItr.hasNext())
            			 {
            				 Job temp1;
            				 temp1  = spareItr.next();
            				 // System.out.println("primaary pending  task  "+temp1.getTaskId());
            		    		 
            				 if(temp1.getTaskId()== p.getCurrentJob().getTaskId() && temp1.getJobId()== p.getCurrentJob().getJobId())
            				 {
            					 temp1.setFaulty(true);
            					
            					 // System.out.println("time    "+time+" primaary pending task  "+temp1.getTaskId()+"  spare"+temp1.getTaskId() );
            				    break;
            				 }
            			 }
        				
        				}
        				// System.out.println(" fault time for removing "+p.fault.get(0));
        				p.fault.remove(0);
        			}
        	}
        }
        	
        	*/
        	// CHECK DEADLINE MISS
        	for (Processor proc : freeProcList)
        	{
        	
				// System.out.println("// CHECK DEADLINE MISS   time  "+time);//+ "  job id  "+j1.getJobId()+  "   task id  " + j1.getTaskId() +"  deadline  "+j1.getAbsoluteDeadline());
				if (!proc.getCurrentJob().isCompletionSuccess() && proc.getCurrentJob().getAbsoluteDeadline()<time) // IF TIME IS MORE THAN THE DEADLINE, ITS A MISSING DEADLINE
				{
					// System.out.println("deadline missed  task id "+proc.getCurrentJob().getTaskId()+"job id " + proc.getCurrentJob().getJobId()+"  deadline time  "+"\t"+proc.getCurrentJob().getActivationDate()+proc.getCurrentJob().getAbsoluteDeadline()+"  time "+time);
									
					/*writer_schedule.write("\ndeadline missed  task id "+proc.getCurrentJob().getTaskId()+"  deadline time  "+proc.getCurrentJob().getAbsoluteDeadline()+"  time "+time);
					writer_schedule.write("\n "+time+"\t"+"\t"+proc.getCurrentJob().getTaskId()+"\t"+proc.getCurrentJob().getJobId()+"\t"+proc.getCurrentJob().getActivationDate()+
					"\t"+proc.getCurrentJob().getRemainingTime()+"\t"+proc.getCurrentJob().getAbsoluteDeadline()+"\t"+proc.getCurrentJob().getProc().getId()+
					"\t"+proc.getCurrentJob().getStartTime()+"\t"+proc.getCurrentJob().getEndTime()+"\t"+proc.getCurrentJob().NoOfPreemption);
				*/	deadlineMissed= true;
				
				
			}
        	}
        	
        	
        	//at end time of any job in any processor
        	for (Processor proc : freeProcList)
        	{
        		if(time== proc.getEndTimeCurrentJob() && proc.isBusy())
        		{
        			proc.setProc_state(proc_state.IDLE);
        			proc.setBusy(false);
        			proc.getCurrentJob().setCompletionSuccess(true);
        			proc.setActiveEnergy(energyConsumed.energyActive((time-proc.getCurrentJob().getStartTime()+1), proc.getCurrentJob().getFrequency()));
        			// System.out.println(" //at end time of any job    p  "+proc.getId()+"   end time  "+proc.getEndTimeCurrentJob()
        	//		+"  primary   "+proc.getCurrentJob().isPrimary());
        			if(proc.getCurrentJob().isPrimary())
        			{
        				
        		    	fullPrimariesExecuted++;
        		/*			writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfPriJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
    						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
    						+" "+	proc.getCurrentJob().getRemainingTime()+" "+	proc.getCurrentJob().getDeadline()
    						+" "+	proc.getCurrentJob().isPreempted+" "+proc.getCurrentJob().getStartTime()+" ");
        			writer_schedule.write(""+proc.getCurrentJob().getEndTime()+" "+proc.getCurrentJob().isFaulty() );
        		*/	}
        			else
        			{
        				
        				fullBackupsExecuted++;
        		//    	System.out.println("time  "+time  +"  proc  "+proc.getId()+"  fullBackupsExecuted   "+fullBackupsExecuted);
        							
        	/*			 writer_schedule.write("\n"+proc.getId()+" "+proc.getNoOfBackJobs()+" "+proc.getCurrentJob().getTaskId()+" "+proc.getCurrentJob().getJobId()+" "+
        						proc.getCurrentJob().isPrimary()+" "+Double.valueOf(twoDecimals.format(	proc.getCurrentJob().getFrequency()))
        						+" "+	proc.getCurrentJob().getRomainingTimeCost()+" "+	proc.getCurrentJob().getDeadline()
        						+" "+	proc.getCurrentJob().isPreempted+" "+proc.getCurrentJob().getStartTime()+" ");
            			writer_schedule.write(""+proc.getCurrentJob().getEndTime()+" "+proc.getCurrentJob().isFaulty() );
        	*/		}
        			if(proc.getCurrentJob().isPrimary() && !proc.getCurrentJob().isFaulty())
        			{
        			// delete the backup job if not started
        				// System.out.println("//at end time of any job    delete the backup job if not started ");
        			Iterator<Job> itr_backup = proc.getCurrentJob().getBackupProcessor().backupJobQueue.iterator();
        			while(itr_backup.hasNext())
        			{
        				Job backup = itr_backup.next();
        				if(!backup.isFaulty() && backup.getTaskId()==proc.getCurrentJob().getTaskId() && backup.getJobId()==proc.getCurrentJob().getJobId())
        				{
        					// System.out.println(" time  "+time+"   p  "+proc.getId()+ "  backup p  " +proc.getCurrentJob().getBackupProcessor().getId()+
        				//			"  delete task  "+	backup.getTaskId() +"  job  "+ backup.getJobId());
        					backup.setCompletionSuccess(true);
        					proc.getCurrentJob().getBackupProcessor().backupJobQueue.remove(backup);
        					
        					if(backup.isPreempted==true)
        						partialBackupsExecuted++;
        					else
        			    	 fullBackupsCancelled++;
        			    
        					break;
        				}
        			}
        			
        			//delete the backup job if running
        			
        			Job onPrimary, onBackup;
        			onPrimary = proc.getCurrentJob();
        			onBackup=onPrimary.getBackupProcessor().getCurrentJob();
        			if(!onBackup.isCompletionSuccess() && onBackup.getTaskId()==onPrimary.getTaskId()
        					&& onBackup.getJobId()==onPrimary.getJobId())
        			{
        		
        		    	 partialBackupsExecuted++;
        		    	
        				
        				// System.out.println("//at end time of any job  //delete the backup job if running");
        				onPrimary.getBackupProcessor().setBusy(false);
        				onBackup.setCompletionSuccess(true);
        				proc.setActiveEnergy(energyConsumed.energyActive((time-onBackup.getStartTime()), onBackup.getFrequency()));
        			/*	writer_schedule.write("\n//deletethebackup"+proc.getCurrentJob().getBackupProcessor().getId()+" "+proc.getNoOfBackJobs()+
        						" "+onBackup.getTaskId()+" "+onBackup.getJobId()+" "+
        						onBackup.isPrimary()+" "+Double.valueOf(twoDecimals.format(	onBackup.getFrequency()))
        						+" "+	onBackup.getRemainingTime()+" "+onBackup.getDeadline()
        						+" "+	onBackup.isPreempted+" "+onBackup.getStartTime()+" ");
        				writer_schedule.write(""+(time+1));
        		*/		
        			}
        			
        			}  // end if(proc.getCurrentJob().isPrimary())
        			else if (!proc.getCurrentJob().isPrimary()) //backup has completed
        			{
        				// delete the primary job if not started
        				// System.out.println("delete the primary job if not started");
        				Iterator<Job> itr_primary = proc.getCurrentJob().getPrimaryProcessor().primaryJobQueue.iterator();
        				while(itr_primary.hasNext())
            			{
            				Job primaryTask = itr_primary.next();
            				if(primaryTask.getTaskId()==proc.getCurrentJob().getTaskId() && primaryTask.getJobId()==proc.getCurrentJob().getJobId())
            				{
            					// System.out.println(" time  "+time+"   p  "+proc.getId()+ "  primary p  " +proc.getCurrentJob().getPrimaryProcessor().getId()+
            		//					"  delete task  "+	primaryTask.getTaskId() +"  job  "+ primaryTask.getJobId());
            					primaryTask.setCompletionSuccess(true);
            					if(primaryTask.isPreempted==true)
            						cancelledPrimariesPartial++;
            					else
            						cancelledPrimariesFull++;
            					proc.getCurrentJob().getPrimaryProcessor().primaryJobQueue.remove(primaryTask);
            					
            					            					
            					break;
            				}
            			}
        				
        				//delete the primary job if running
        				// System.out.println("delete the primary job if running");
            			Job onPrimary, onBackup;
            			onBackup = proc.getCurrentJob();
            			onPrimary=onBackup.getPrimaryProcessor().getCurrentJob();
            			if(!onPrimary.isCompletionSuccess() && onBackup.getTaskId()==onPrimary.getTaskId() && onBackup.getJobId()==onPrimary.getJobId())
            			{
            				
            		    	 cancelledPrimariesPartial++;
            		    	
            		    	 onPrimary.getPrimaryProcessor().setBusy(false);
            				onPrimary.setCompletionSuccess(true);
            				proc.setActiveEnergy(energyConsumed.energyActive((time-onPrimary.getStartTime()), onPrimary.getFrequency()));
            			/*	writer_schedule.write("\ndeletetheprimary"+proc.getId()+" "+proc.getNoOfPriJobs()+" "+onPrimary.getTaskId()+" "+onPrimary.getJobId()+" "+
            						onPrimary.isPrimary()+" "+Double.valueOf(twoDecimals.format(	onPrimary.getFrequency()))
            						+" "+	onPrimary.getRemainingTime()+" "+	onPrimary.getDeadline()
            						+" "+	onPrimary.isPreempted+" "+onPrimary.getStartTime()+" ");
            				writer_schedule.write(""+(time+1));
            	*/		}
        			}
        		//	System.out.println("p  "+proc.getId());
        			proc.setNextActivationTime(time);
            		proc.setTimeToNextArrival( proc.getNextActivationTime()-time-1);
        		}
        	}
        
        	
        	
        	time++;
        	if (deadlineMissed)
	    		break;
        
    	}
        for (Processor proc : freeProcList)
    	{
        	proc.setIdleEnergy(energyConsumed.energy_IDLE(proc.idleTime));
        	proc.setSleepEnergy(energyConsumed.energySLEEP(proc.sleepTime));
        	proc.setEnergy_consumed(proc.getActiveEnergy()+proc.getIdleEnergy()+proc.getSleepEnergy());
    	/*		System.out.println("out TIME  "+time+"  p  "+proc.getId()+" active  "+ proc.activeTime+"  energy  "+proc.getActiveEnergy());
    		
    			System.out.println("TIME  "+time+"  p  "+proc.getId()+"  idle "+ proc.idleTime+" energy  "+proc.getIdleEnergy());
            	
    			System.out.println("TIME  "+time+"  p  "+proc.getId()+" sleep  "+proc.sleepTime +"   energy   "+proc.getSleepEnergy());
            	System.out.println("total energy  "+proc.getEnergy_consumed());
            	*/
    		energyTotal+= proc.getEnergy_consumed();
    	}
     /*   for(Processor p : freeProcList)
        {
        writer_taskProcWise.write("\n "+p.getId()+" "+p.getNoOfPriJobs()+" "+p.getNoOfBackJobs()+
        		" "+(p.getNoOfPriJobs()+p.getNoOfBackJobs()));
        }*/
        writer_tasks.write("\n"+fullBackupsExecuted +" "+partialBackupsExecuted +" "+fullBackupsCancelled+" "
        + cancelledPrimariesFull +" "+  cancelledPrimariesPartial +" "+ fullPrimariesExecuted +" "+noOfFaults);
    	
    	 writer_energy.write(total_no_tasksets++ + " "+Double.valueOf(twoDecimals.format(U_SUM))+" "+Double.valueOf(twoDecimals.format(set_fq))+" "
    	    	    +" "+ Double.valueOf(twoDecimals.format(fq))+" " +Double.valueOf(twoDecimals.format(energyTotal))+"\n");
        System.out.println("   tasksets  "+total_no_tasksets+" energy  "+energyTotal);
    
    }
    
/*   writer_allocation.close();
  writer_schedule.close();
 */   writer_energy.close();
    writer_tasks.close();
  //  writer_taskProcWise.close();
    System.out.println("success ScheduleRMS_EASS_MWFD");
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
	

	