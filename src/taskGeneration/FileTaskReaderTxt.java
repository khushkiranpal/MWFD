/**
 * 
 */
package taskGeneration;

import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author kiran
 *
 */
public class FileTaskReaderTxt {
	
		private final Scanner scanner;
		private String[] lastline = null;
		
		
		/**
		 * 
		 * @param source
		 * @return 
		 * @throws FileNotFoundException
		 */
		public FileTaskReaderTxt(String source) throws FileNotFoundException {
			this(new File(source));
		}

		/**
		 * 
		 * @param source
		 * @return 
		 */
		public FileTaskReaderTxt(InputStream source) {
			scanner = new Scanner(source);
		}

		/**
		 * 
		 * @param source
		 * @return 
		 * @throws FileNotFoundException
		 */
		public FileTaskReaderTxt(File source) throws FileNotFoundException {
			scanner = new Scanner(source);
		}

		
		public ITask[] nextTaskset() {
			ArrayList<ITask> tasks = new ArrayList<ITask>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				lastline = line.split(" ");
				//System.out.println("lastline  "+ lastline.length);
				if (lastline.length == 6)  // if begining of new taskset
				{
					if (!tasks.isEmpty()) //if taskset is not empty return it in the form of array 
					{
						ITask[] tab = new ITask[tasks.size()];  // create an array named tab of size equal to size of arrayList
						return tasks.toArray(tab);   // return the taskset read in the form of array ITask[]
					}
					tasks.clear();  // get ready for reading next taskset
				} 
				else if (lastline.length > 6)
				{
					float u=0;
					long C = 0, P= 0, D= 0;
					long Id = 0;
					for (int i = 0; i < lastline.length; i++) 
					{
						String param = lastline[i].toLowerCase();
						if("id=".equals(param)) {
							Id = Integer.parseInt(lastline[i + 1]);
							//System.out.println(Id);
						}
						else if("p=".equals(param)) {
							P = Long.parseLong(lastline[i + 1]);
							//System.out.println(P);
						}
						else if("d=".equals(param)) {
							D = Long.parseLong(lastline[i + 1]);
							//System.out.println(D);

						}
						else if("c=".equals(param)) {
							C = Long.parseLong(lastline[i + 1]);
							//System.out.println(C);
						}
						else if("u".equals(param)) {
							u= Float.parseFloat(lastline[i + 1]);
							//System.out.println(C);
						}
						
						}
					ITask task = new Task (0,Id,C,P,D,1,u);
					tasks.add(task);
				}
				
				else if (lastline.length < 6)
				{
					for (int i = 0; i < lastline.length; i++) 
					{
						String param = lastline[i].toLowerCase();
						if("processors".equals(param)) {
							System.out.println("no of processors  "+Integer.parseInt(lastline[i + 1]));
							//System.out.println(Id);
						}
					}
				}
			}
			if (!tasks.isEmpty()) {
				ITask[] tab = new ITask[tasks.size()];
				return tasks.toArray(tab);
			}
			return null;
		}

		
		/**
		 * 
		 */
		public ArrayList<ITask[]> readAll() {
		    int count =1;
			ArrayList<ITask[]> sets = new ArrayList<ITask[]>();
			ITask[] set = null;
			while ((set = nextTaskset()) != null) {
				
				System.out.println("fileTaskReader taskset count  "+count++);
				sets.add(set);
			}
			return sets;
		}
		

		/**
		 * 
		 */
		public void close() {
			scanner.close();
		}

	}


