package hw2;
import java.util.*;
import java.io.*;
import java.math.*;

public class partB {

	public static void main(String[] args) throws IOException {
		// 1
		int input = (int)Math.pow(2, 4);
		System.out.println(input);
		// 2
		int c = (int)Math.pow(2, input);
		System.out.println(c);
		// 3
		// Read Data from the Training DataSet
		List<String[]> train = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new FileReader("4Cat-Train.labeled"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        String[] token = line.split("\\s+");
		        String[] feature = new String[5];
		        for(int i =0;i<5;i++) {
		        	feature[i]=token[2*i+1];
		        }
		        train.add(feature);
		        line = br.readLine();
		    }
		} 
		finally {
		    br.close();
		}
		// Transfer String Data to Numeric Data
		int[][] D = new int[train.size()][5];
		for(int i=0;i<train.size();i++) {
			D[i][0]=(train.get(i)[0].equals("Young")?1:0);
			D[i][1]=(train.get(i)[1].equals("1")?1:0);
			D[i][2]=(train.get(i)[2].equals("Southampton")?1:0);
			D[i][3]=(train.get(i)[3].equals("Male")?1:0);
			D[i][4]=(train.get(i)[4].equals("Yes")?1:0);
		}
		
		// Build up the Initial List of Hypothesis
		int[][][] fullList = new int[c][input][5];
		List<int[][]> versionSpace = new ArrayList<int[][]>();
		for(int i=0;i<c;i++) {
			for(int j=0;j<input;j++) {
				for(int k=0;k<4;k++) {
					if ((j/(input/(int)Math.pow(2, k+1)))%2==0) {
						fullList[i][j][k]=0;
					}
					else {
						fullList[i][j][k]=1;
					}
				}
				if ((i/(c/(int)Math.pow(2, j+1)))%2==0) {
					fullList[i][j][4]=0;
				}
				else {
					fullList[i][j][4]=1;
				}								
			}		
		}
		
		// Eliminate Hypothesis through Training Data
		int VS =0;
		for(int i=0;i<c;i++) {
			boolean test = true;
			for(int k=0;k<train.size();k++) {
				for(int j=0;j<input;j++) {
					boolean match = true;
					for(int h=0;h<4;h++) {
						if(fullList[i][j][h]!=D[k][h]) {
							match = false;
						}
					}
					if(match) {
						if(fullList[i][j][4]!=D[k][4]) {
							test=false;
						}
					}
				}				
			}
			if(test) {
				versionSpace.add(fullList[i]);
				VS++;
			}						
		}
		System.out.println(VS);
		
		// 4
		// Read Data from the Testing DataSet
		List<String[]> test = new ArrayList<String[]>();
		BufferedReader br1 = new BufferedReader(new FileReader(args[0]));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br1.readLine();

		    while (line != null) {
		        String[] token = line.split("\\s+");
		        String[] feature = new String[5];
		        for(int i =0;i<5;i++) {
		        	feature[i]=token[2*i+1];
		        }
		        test.add(feature);
		        line = br1.readLine();
		    }
		} 
		finally {
		    br1.close();
		}
		// Transfer String Data to Numeric Data
		int[][] T = new int[test.size()][5];
		for(int i=0;i<test.size();i++) {
			T[i][0]=(test.get(i)[0].equals("Young")?1:0);
			T[i][1]=(test.get(i)[1].equals("1")?1:0);
			T[i][2]=(test.get(i)[2].equals("Southampton")?1:0);
			T[i][3]=(test.get(i)[3].equals("Male")?1:0);
			T[i][4]=(test.get(i)[4].equals("Yes")?1:0);
		}
		
		// Test Hypothesis with testing data
		int[][] vote = new int[test.size()][2];
		for(int i=0;i<test.size();i++) {
			vote[i][0]=0;
			vote[i][1]=0;
			for(int j=0;j<VS;j++) {
				boolean pass=false;
				for(int k=0;k<input;k++) {
					boolean match1 = true;
					for(int h=0;h<4;h++) {
						if(versionSpace.get(j)[k][h]!=T[i][h]) {
							match1=false;
						}
					}
					if(match1) {
						if(versionSpace.get(j)[k][4]==1) {
							pass=true;
						}
					}
				}
				if(pass) {
					vote[i][0]++;
					
				}
				else {
					vote[i][1]++;
				}
			}
		System.out.println(vote[i][0]+" "+vote[i][1]);
		}
	}
}

				


