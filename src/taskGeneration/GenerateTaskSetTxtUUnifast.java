/**
 * GENERATE TASK SET FOR UNIPROCESSOR 
 */
package taskGeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * @author kiran
 *
 */
public class GenerateTaskSetTxtUUnifast {
	public static Random random = new Random();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 //INPUT NUMBER OF PROCESSORS
    	Scanner input  = new Scanner (System.in);
   //   	System.out.print("Enter no. of processors\t");
    	int m=1 ; //input.nextInt();
    	System.out.print("Enter no. of tasksets\t");
    	double taskUti = 0.1; // or 0.05
    	int TOTAL_NUM_TASKSETS= input.nextInt(); // NUMBER OF TASKSETS
    //	System.out.print("Enter no. of MAX PERIOD\t");
    	long MAX_PERIOD=100;//100000   //input.nextLong(); // MAX PERIOD
    //	System.out.println(" FOR IMPLICIT , Press 0  OR \n    FOR CONSTRAINED , Press -1");
    	int deadlineModel = 0;   //input.nextInt();
    	System.out.println("Enter utilization");
    	double Utotal=input.nextDouble();//0.15;//
    	 //	System.out.print("Enter no. of tasks in taskset\t");
    	int n= (int)(Utotal/taskUti) ;   //input.nextInt(); // NUMBER OF TASKS    15;  //
    	System.out.println("n   "+n);
   //	 System.out.println("Enter 1 for UUnifast  and 2 for Sporadic");
    	int tasksetType = 1;   //input.nextInt();
        // FILE NAME SETTING
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String filename;
        DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 2 decimal points

        if (deadlineModel==0)
        {
         filename = "D:/CODING/TASKSETS/uunifast/IMPLICIT_TOT_SETS_"+TOTAL_NUM_TASKSETS+"_n_"+n+"_MAX_P_"+MAX_PERIOD+"_Utotal_"+Utotal+"_"+date+".txt";
        }
        else
        {
        	filename = "D:/CODING/TASKSETS/uunifast/CONSTRAINED_TOT_SETS_"+TOTAL_NUM_TASKSETS+"_n_"+n+"_MAX_P_"+MAX_PERIOD+"_Utotal_"+Utotal+"_"+date+".txt";

        }
    	
        ITaskGenerator gen = new TaskGenerator();
        UUniFastDiscardTaskSetGen genTask ;
        ITask[] tasks;
      
        Writer taskwrite = new FileWriter(filename);
        long noOfTasksets=1;
        
        Random random1 = new Random();
       // taskwrite.write("Id\tC\tD\tP\n");
      
       
        switch (tasksetType)
        {
        case 1:
        	for (int nbTasks = n; nbTasks <= n; nbTasks++)
     	  	{
        		
        		for (int count =1;count<=TOTAL_NUM_TASKSETS;count++)
        		{
        			/*Utotal += 0.01; //((double) nextInt(15, 85)/(double) 100); // random1.nextDouble(); //10
        			if (Double.valueOf(twoDecimals.format(Utotal))==0.85)
        				Utotal=0.15;*/
        		System.out.println(" count   TOTAL_NUM_TASKSETS  "+count);
        			genTask = new UUniFastDiscardTaskSetGen(gen, nbTasks, Utotal, deadlineModel,MAX_PERIOD);

     			  tasks = genTask.generate();
     			 ArrayList<ITask> taskSet = new  ArrayList<ITask>(Arrays.asList(tasks));
     			long hyper = SystemMetric.hyperPeriod(taskSet);
     			
     			if(hyper >1000000)
   			 {
   				count--;
   				System.out.println(" count  "+count+" hyper large ");//+"  TASKSETNO. "+noOfTasksets--);
   				continue;
   		
   			 } 
     			boolean schedulable = worstCaseResp_TDA_RMS(taskSet);
     			
     			if (schedulable)
     			{
     				
     				taskwrite.write("TASKSETNO. "+noOfTasksets++ +" nbTasks "+nbTasks+" Utilization "+ Double.valueOf(twoDecimals.format(SystemMetric.utilisation(tasks)))+"\n");
     			}
     			else
     			{
     				count--;
     				System.out.println("un schedulable  "+schedulable+"  TASKSETNO. "+noOfTasksets);
     				continue;
     			}
     		//	taskwrite.write("TASKSETNO. "+noOfTasksets++ +" nbTasks "+nbTasks+" Utilization "+ Double.valueOf(twoDecimals.format(SystemMetric.utilisation(tasks)))+"\n");
     			
     			//  System.out.println("\n nbTasks   "+ nbTasks);
     			 
     				 for (ITask task : tasks)
                 {
     				taskwrite.write("Id= "+task.getId()+" C= "+(long)task.getWcet()+
     	        			  " D= "+task.getDeadline()+" P= "+task.getPeriod()+" u= "+Double.valueOf(twoDecimals.format(((double)task.getWcet()/(double)task.getPeriod())))+"\n"); 
                 }
     		  }
     	  	}
        	break;
        case 2:
        	
        	System.out.println("sporadic ");
        	Vector<Double> utilization = new Vector<Double>();
        	//UtilizationDistribution utilizationModel= EXPONENTIAL_DISTRIBUTION;
        	//ExponentialDistribution exp = new ExponentialDistribution(parameter);
        	Double sample;
        		
        	
        	
        	ITask task;
        	int taskcount=0;
        	ArrayList<ITask> taskset = new ArrayList<ITask>();
        	ArrayList <ArrayList<ITask>> tasksets = new ArrayList<ArrayList<ITask>>();
    		double parameter=.1;
    		while (parameter<1)
    		{
    		//	taskwrite.write("\n parameter "+ Double.valueOf(twoDecimals.format(parameter)));
    			ExponentialDistribution exp = new ExponentialDistribution(parameter);
    			
    			int nbTasks= 2;
            	for (int i=1; i<=TOTAL_NUM_TASKSETS;i++)
            	{
            		utilization.clear();
            		for (int j = 1; j<=nbTasks; j++)
            		{
            		//	System.out.println("nbtasks   "+nbTasks);
            			sample = exp.sample();
            		//	System.out.println("j  =  "+j+"sampel "+sample);
            			if (sample<1)
            				
            				utilization.addElement( Double.valueOf(twoDecimals.format(sample)));  
            			else 
            				j--;
            		 
            		}
            		if (!taskset.isEmpty())
            			taskset.clear();
            		java.util.Iterator<Double> itr = utilization.iterator();
            		while (itr.hasNext())
            		{
            			
            	        parameter=  Double.valueOf(twoDecimals.format(parameter));
            			System.out.println("parameter   "+parameter);
            	//		System.out.println("utilization      "+utilization);
            	     
            	        	task = gen.generateSporadic(-1,itr.next(), parameter, -1);// generate sporadic taskset
                			taskset.add(task);
            	     
            		}
            		 if (!checkSchedulability(taskset))
            		 {
            			 taskset.clear();
            			 nbTasks = 2;
            			 i--;
            		 }
            		 else
            		 {
            			 tasksets.add(taskset);
            			 taskwrite.write("TASKSETNO. "+ taskcount++ +" nbTasks "+taskset.size()+
            					 " Utilization "+ Double.valueOf(twoDecimals.format(SystemMetric.utilisation(taskset)))+"\n");
            			 java.util.Iterator<ITask> itr1 = taskset.iterator();
            			 while (itr1.hasNext())
            			 {
            				 task = itr1.next();
            				 taskwrite.write("Id= "+task.getId()+" C= "+task.getWcet()+
            	        			  " D= "+task.getDeadline()+" P= "+task.getPeriod()+"\n");
            			 }
            			 nbTasks++;
            		//	 System.out.println("nbTasks    "+nbTasks);
            			 
            		 }
            	}
            	
            	parameter = parameter + .2;
    		}
        	
     /*   	 java.util.Iterator<ArrayList<ITask>> itr =  tasksets.iterator();
        	 ArrayList<ITask> taskset1;
        	 long tasksetnb = 0; 
    		 while (itr.hasNext())
    		 {
    			 taskset1 = itr.next();
    			 taskwrite.write("TASKSETNO. "+ ++tasksetnb +" nbTasks "+taskset1.size()+
    					 " Utilization "+SystemMetric.utilisation(taskset1)+"\n");
    			 java.util.Iterator<ITask> itr1 = taskset1.iterator();
    			 while (itr1.hasNext())
    			 {
    				 task = itr1.next();
    				 taskwrite.write("Id= "+task.getId()+" C= "+task.getWcet()+
    	        			  " D= "+task.getDeadline()+" P= "+task.getPeriod()+"\n");
    			 }
        
    		 }
    		 */
     	
        }
        
        taskwrite.close();
        
      //  FileTaskSetWriter write = new FileTaskSetWriter (filename);
        //     write.write(taskset, "taskset");      
                     /*       for (ITask t : taskset)
                            {
                               //t.activate(time++);
                                System.out.println("id   "+t.getId() + " wcet   "+t.getWcet()+
                                        "  deadline  = "+t.getDeadline()+" Priority== "+t.getPriority()
                                +"  period =  "+ t.getPeriod()+ " arrival   "+ t.getArrival());
                             //   t.activate(time++);
                            } */
                                    
        System.out.println("done");
	}
	
	public static  int nextInt(int from, int to) {
		return from + random.nextInt(to - from + 1);
                }
	
	public static boolean worstCaseResp_TDA_RMS( ArrayList<ITask> taskSet) {
   //   ArrayList<ITask> taskSet = new  ArrayList<ITask>(Arrays.asList(tasks));
     // taskSet.addAll(tasks);
   
      
      taskSet.sort( new Comparator<ITask>() {
          @Override
          public int compare(ITask t1, ITask t2) {
                         
              if( t1.getPeriod()!= t2.getPeriod())
                  return (int)( t1.getPeriod() - t2.getPeriod());
              
              return (int) (t1.getId() - t2.getId());
          }
      });
     
		for(ITask t:taskSet)
        {
	//		System.out.println("task i "+t.getId()+" wcet  "+t.getWcet());
            double w=t.getWcet(),w1=w-1;
            while(w != w1)
            {
                w1 = w;
                w =t.getWcet();
                for(int i=0; taskSet.get(i) != t; i++)
                {
                	w += (int) (Math.ceil((double) w1/taskSet.get(i).getPeriod())*taskSet.get(i).getWcet());
          //      	 System.out.println("task j "+taskSet.get(i).getId()+"response time  "+w);
                }
            }
            if( w > t.getDeadline())
                return false;
     //     System.out.println("response time  "+w);
        }
        return true;
    }
	
	public static boolean checkSchedulability(ArrayList<ITask> taskset)
	{
		long T;
		long temp;
		long[] tmp = new long[2];
		long[] deadline = new long [taskset.size()];
		long[] period = new long [taskset.size()];
		long[] exec = new long [taskset.size()];
		int count=0; 
		
		//UTILIZATION
	    Double c = SystemMetric.utilisation(taskset);
	    
	    if (c>1)
	    {
	    //	System.out.println("Utilization       "+c);
	    	return false;
	    }
	    // LCM(P1,P2...PN)
        long hyper = SystemMetric.hyperPeriod(taskset);  // HYPER PERIOD
    //    System.out.println("hyper  "+hyper);
        
        //MAX DEADLINE
        long max = 0;
        
        for (ITask t : taskset)
       {
       	
        	if(max<t.getDeadline())
       		max=t.getDeadline();
       		
       }
       
       long M = (hyper + max);
       if (c==1)
       {
         // System.out.println("c==1");
    	   T = M;
       }
       else
       {
    	   for (ITask t : taskset)
           {
       		 max =0;
       		 temp = (t.getPeriod()- t.getDeadline());
       		 if (temp>max)
       		 max=temp;
       		 
       		
           }
       
       		temp = (long)(c/(1-c))*max;
       		T = Math.min(M,temp);
       }
       
       for (ITask t : taskset)
       {
    	   //MAKE A DEADLINE AND EXEC. TIME ARRAY FOR FUTURE USE
      		deadline[count]=t.getDeadline();
           exec[count] = t.getWCET_orginal();
           period[count] = t.getPeriod();
           count++;
       }
       long H = 0;
          
       
       for (long t = 1; t<=T ; t++)
       {
       	for (int i =1; i<=taskset.size(); i++)
       	{
       		//System.out.println("in check period[i-1]"+period[i-1]);
       		if ((t>=deadline[i-1]) && (t==(t-deadline[i-1])/period[i-1]))
       		{
       		H=H+exec[i-1];
          	System.out.println("inside H=    "+H+"t=    "+t);

       		}
       	}
      //	System.out.println("outside H=    "+H+"t=    "+t);
       	if (H>t)
       	{
       		System.out.println("not feasible");
       		return false;

       	}
       }
       System.out.println("feasible");
       return true;
	}

}
