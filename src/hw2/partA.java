package hw2;
import java.util.*;
import java.io.*;
import java.math.*;

public class partA {

	public static void main(String[] args) throws IOException {
		// 1
		int input =(int)Math.pow(2, 9);
		System.out.println(input);
		// 2
		double c = Math.pow(2,input);
		System.out.println(155);
		// 3
		int h =(int)Math.pow(3, 9)+1;
		System.out.println(h);
		// 4
		System.out.println((int)Math.pow(3, 10)+1);
		// 5
		System.out.println((int)Math.pow(3, 9)*4/3+1);
		// 6
		// Read Data from Training DataSet
		// Create the 2-d ArrayList
		List<String[]> train = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new FileReader("9Cat-Train.labeled"));
		try {
		    String line = br.readLine();

		    while (line != null) {
		        String[] token = line.split("\\s+");
		        String[] feature = new String[10];
		        for(int i =0;i<10;i++) {
		        	feature[i]=token[2*i+1];
		        }
		        train.add(feature);
		        line = br.readLine();
		    }
		} 
		finally {
		    br.close();
		}
		
		//Peform FIND-S
		String[] hypo = {"!","!","!","!","!","!","!","!","!"};
		PrintWriter w = new PrintWriter("partA6.txt","UTF-8");
		for(int i = 0; i<train.size();i++) {
			if(train.get(i)[9].equals("Yes")) {
				for(int j =0;j<9;j++) {
					if (hypo[j].equals("!")) {
						hypo[j]=train.get(i)[j];
					}
					else if(hypo[j].equals("?")) {
						continue;
					}
					else if(hypo[j].equals(train.get(i)[j])) {
						continue;
					}
					else {
						hypo[j]="?";
					}
				}
			}
			if((i+1)%20==0) {
				for(int k=0;k <8;k++) {
					w.print(hypo[k]+'\t');
				}
				w.println(hypo[8]);										
			}
		}
		w.close();
		
		// 7
		// Read Data from Test DataSet
		List<String[]> test = new ArrayList<String[]>();
		BufferedReader br1 = new BufferedReader(new FileReader("9Cat-Dev.labeled"));
		try {
		    String line = br1.readLine();

		    while (line != null) {
		        String[] token = line.split("\\s+");
		        String[] feature = new String[10];
		        for(int i =0;i<10;i++) {
		        	feature[i]=token[2*i+1];
		        }
		        test.add(feature);
		        line = br1.readLine();
		    }
		} 
		finally {
		    br1.close();
		}
					
		// Make Prediction on Test
		String[] Prediction = new String[test.size()];
		int misCount =0;
		for(int i =0;i<test.size();i++) {
			Prediction[i]="Yes";
			for(int j=0;j<=8;j++) {
				if (hypo[j].equals("!")){
					Prediction[i]="No";
					break;
				}
				else if (hypo[j].equals("?")) {
					continue;
				}
				else if(!hypo[j].equals(test.get(i)[j])) {
					Prediction[i]="No";
					break;
				}
			}
			if(!Prediction[i].equals(test.get(i)[9])) {
				misCount++;
			}
		}
		System.out.println((double)misCount/test.size());

		// 8
		// Read Data from Input DataSet
		List<String[]> in = new ArrayList<String[]>();
		BufferedReader br2 = new BufferedReader(new FileReader(args[0]));
		try {
		    String line = br2.readLine();

		    while (line != null) {
		        String[] token = line.split("\\s+");
		        String[] feature = new String[9];
		        for(int i =0;i<9;i++) {
		        	feature[i]=token[2*i+1];
		        }
		        in.add(feature);
		        line = br2.readLine();
		    }
		} 
		finally {
		    br2.close();
		}
		
		// Make Prediction on Input
		String[] PredictionIn = new String[in.size()];
		for(int i =0;i<in.size();i++) {
			PredictionIn[i]="Yes";
			for(int j=0;j<=8;j++) {
				if (hypo[j].equals("!")){
					PredictionIn[i]="No";
					break;
				}
				else if (hypo[j].equals("?")) {
					continue;
				}
				else if(!hypo[j].equals(in.get(i)[j])) {
					PredictionIn[i]="No";
					break;
				}
			}
			System.out.println(PredictionIn[i]);
		}


	}

}
