package TEST;

import java.util.ArrayList;

import platform.Fault;

public class TestGen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Fault f = new Fault();
		//fault = f.lamda_F(hyper, CRITICAL_freq, fq, 1);  
		ArrayList<Integer> fault = f.lamda_F(100000, .50, .70, 2);  
		/* UUniFastDiscardTaskSetGen genTask ;
	     ITaskGenerator gen = new TaskGenerator();
		 genTask = new UUniFastDiscardTaskSetGen(gen, 15, .15, 0,100);
		 ITask[] tasks = genTask.generate();
*/
	}

}
