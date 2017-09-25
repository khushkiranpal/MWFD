/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskGeneration;

/**
 *
 * @author KIRAN
 */
public class JobId {
          private  final long taskid ;
           private final int jobid ;
            JobId(long l, int jobid)
            {
               //  System.out.println("in jobid     "+jobid+"job id "+ taskid+" task id");
                this.jobid= jobid;
                this.taskid = l;
            }
           public long getTaskId() {
		return taskid;
	}
           public int getJobId() {
		return jobid;
	}
    
}
