package scheduleRMS;

import java.util.ArrayList;
import java.util.Comparator;

import taskGeneration.ITask;

public class SchedulabilityCheck {
	
	public  boolean worstCaseResp_TDA_RMS( ArrayList<ITask> taskSet) {
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
		//			System.out.println("task i "+t.getId()+" wcet  "+t.getWcet());
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
		  //        System.out.println("response time  "+w);
		        }
		        return true;
		    }

}
