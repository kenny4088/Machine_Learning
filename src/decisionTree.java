import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class decisionTree {
	static String[][] trainInput;
	static String[] trainOutput;
	static String[][] testInput;
	static String[] testOutput;
	static node root;
	static String[] colName;


	static void main(String[] args) {
		//Read Dataset
		readFile(args);
		//Replace values in the Output Array with "+" and "-"
		trainOutput=standardizeOutput(trainOutput);
		testOutput=standardizeOutput(testOutput);
		//Transfer Data to Arraylists for Future Use
		ArrayList<ArrayList<String>> trI = toArrayList(trainInput);
		ArrayList<ArrayList<String>> teI = toArrayList(testInput);
		ArrayList<String> trO=toArrayList(trainOutput);
		ArrayList<String> teO=toArrayList(testOutput);
		root= new node();
		root.parent=root;
		root.value="";
		root.field=-1;
		root.count=countOutput(trO);
		System.out.println("["+root.count[1]+"+/"+root.count[0]+"-]");
		treeConstruct( root, trI, trO);
		treePrint(root,0);
		double trainError =calculateError(treePredict(root, trI), trO);
		double testError=calculateError(treePredict(root, teI), teO);
		System.out.println("error(train): "+trainError);
		System.out.println("error(test): "+testError);

	}
	static void readFile(String[] fileName) {
		//Read the Training File
		Scanner fileInput = null;
		StringBuilder fileContent = new StringBuilder();
		try {
			fileInput = new Scanner (new File(fileName[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Read the File Content to a String First and Then Break the File String into Lines
		while (fileInput.hasNextLine()) fileContent.append(fileInput.nextLine() + "\n");
		String[] line = fileContent.toString().split("\n");
		//Initialize the Input and Output Array
		colName =line[0].split(",");
		trainInput = new String[line.length-1][];
		trainOutput = new String[line.length-1];
		//Popularize the Input and Output Array Lists with Lines
		for(int i =0; i<line.length-1;i++) {
			String[] features=line[i+1].split(",");
			trainInput[i]=new String[features.length-1];
			for(int j=0;j<features.length-1;j++) {
				trainInput[i][j]=features[j].trim();				
			}
			trainOutput[i]=features[features.length-1].trim();
		}
		//Read the Test File
		StringBuilder testFileContent = new StringBuilder();
		try {
			fileInput = new Scanner (new File(fileName[1]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Read the File Content to a String First and Then Break the File String into Lines
		while (fileInput.hasNextLine()) testFileContent.append(fileInput.nextLine() + "\n");
		String[] line1 = testFileContent.toString().split("\n");
		//Initialize the Input and Output Array
		testInput = new String[line1.length-1][];
		testOutput = new String[line1.length-1];
		//Popularize the Input and Output Array Lists with Lines
		for(int i =0; i<line1.length-1;i++) {
			String[] features=line1[i+1].split(",");
			testInput[i]=new String[features.length-1];
			for(int j=0;j<features.length-1;j++) {
				testInput[i][j]=features[j].trim();				
			}
			testOutput[i]=features[features.length-1].trim();
		}
		
	}
	static String[] standardizeOutput(String[] output) {
		//Standardize the Output Array
		for(int i=0;i<output.length;i++) {
			if(output[i].equals("A")||output[i].equals("democrat")||output[i].equals("yes")) {
				output[i]="+";
			}
			else output[i]="-";
		}
		return output;		
	}
	static ArrayList<String> toArrayList(String[] array){
		ArrayList<String> al = new ArrayList<>();
		for(String s:array) {
			al.add(s);
		}
		return al;
	}
	static ArrayList<ArrayList<String>> toArrayList(String[][] array){
		ArrayList<ArrayList<String>> al = new ArrayList<>();
		for(int i=0;i<array[0].length;i++) {
			ArrayList<String> col = new ArrayList<>();
			for(int j=0;j<array.length;j++) {
				col.add(array[j][i]);
			}
			al.add(col);
		}
		return al;
	}
	static void treeConstruct(node currentNode,ArrayList<ArrayList<String>> input,ArrayList<String> output) {
		//Return if Depth is Larger Than 2
		if(!currentNode.equals(root)&&!currentNode.parent.equals(root)) {
			return;
		}
		double maxI =0.1;// to Store the Maximum Mutual Information
		int nodeCandidate=-1; // to Store the Index of the Node Candidate
		int[] labelCount = countOutput(output);
		double plusRatio = (double)labelCount[1]/(labelCount[0]+labelCount[1]);
		double minusRatio = (double)labelCount[0]/(labelCount[0]+labelCount[1]);
		double currentEntropy = plusRatio*Math.log(1/plusRatio)/Math.log(2)+minusRatio*Math.log(1/minusRatio)/Math.log(2);
		//Calculate the Mutual Information between Each Input Column and the Output. 
		//Pick the Column with Maximum Mutual Information
		for(int i=0;i<input.size();i++) {
			if(i==currentNode.field) {
				continue;
			}
			double conditionalEntropy=conditionalEntropy(input.get(i), output);			
			double mutualInforamtion=currentEntropy-conditionalEntropy;
			if(mutualInforamtion>=maxI) {
				maxI=mutualInforamtion;
				nodeCandidate=i;
			}
		}
		//Return if Mutual Information is Less Than 0.1
		if(nodeCandidate==-1) {
			return;
		}
		//Get Distinc Values whithin the Column
		Set<String> distinctValues = new HashSet<String>(input.get(nodeCandidate));
		Object[] tempArr=distinctValues.toArray();
		//Build Two Nodes Based on two Values of the Selected Column
		node node1 = new node();
		node1.field=nodeCandidate;
		node1.value=tempArr[0].toString();
		node1.parent=currentNode;
		node1.count=countOutput(output,node1,input);
		node node2 = new node();
		node2.field=nodeCandidate;
		node2.value=tempArr[1].toString();
		node2.parent=currentNode;
		node2.count=countOutput(output,node2,input);
		currentNode.children=new ArrayList<>();
		currentNode.children.add(node1);
		currentNode.children.add(node2);
		treeConstruct(node1, selectSubInput(input, node1), selectSubOutput(input, node1, output));
		treeConstruct(node2, selectSubInput(input, node2), selectSubOutput(input, node2, output));

		
		
	}
	static void treePrint(node root,int depth) {
		//Return if the Leaf has been Reached
		if(root.children==null) {
			return;
		}
		//Access to the Children Nodes
		node child1 = root.children.get(0);
		node child2 = root.children.get(1);
		//Left Branch
		//Print with the Format of Root if the Depth is 0
		if(depth==0) {
			System.out.println(colName[child1.field]+" = "+child1.value+": ["+child1.count[1]+"+/"+child1.count[0]+"-]");
		}
		else {
			//Construct the Format for the Branch According to Depth
			String format="|";
			for(int i=0;i<depth;i++) {
				format=format+" ";
			}
			System.out.println(format+colName[child1.field]+" = "+child1.value+": ["+child1.count[1]+"+/"+child1.count[0]+"-]");
		}
		//Recursion
		treePrint(child1, depth+1);
		//Right Branch
		//Print with the Format of Root if the Depth is 0
		if(depth==0) {
			System.out.println(colName[child2.field]+" = "+child2.value+": ["+child2.count[1]+"+/"+child2.count[0]+"-]");
		}
		else {
			//Construct the Format for the Branch According to Depth
			String format="|";
			for(int i=0;i<depth;i++) {
				format=format+" ";
			}
			System.out.println(format+colName[child2.field]+" = "+child2.value+": ["+child2.count[1]+"+/"+child2.count[0]+"-]");
		}
		//Recursion
		treePrint(child2, depth+1);
	}
	static ArrayList<String> treePredict(node root,ArrayList<ArrayList<String>> input) {
		//Initialize a List to Store Predictions
		ArrayList<String> prediction = new ArrayList<>();
		//Travel on the Tree According to Field Value
		for(int i =0; i<input.get(0).size();i++) {
			node currentNode = root;
			while(currentNode.children!=null) {
				if(input.get(currentNode.children.get(0).field).get(i).equals(currentNode.children.get(0).value)) {
					currentNode=currentNode.children.get(0);
				}
				else {
					currentNode=currentNode.children.get(1);
				}
			}
			//Make Prediction
			if(currentNode.count[0]>currentNode.count[1]) {
				prediction.add("-");
			}
			else {
				prediction.add("+");
			}
		}
		return prediction;
	}
	static double calculateError(ArrayList<String> prediction, ArrayList<String> output) {
		double errorRate=0;
		int errorCount=0;
		for(int i=0;i<output.size();i++) {
			if(!prediction.get(i).equals(output.get(i))) {
				errorCount++;
			}
		}
		errorRate=(double)errorCount/output.size();
		return errorRate;
	}
	static double conditionalEntropy(ArrayList<String> input, ArrayList<String> output) {
		String value1 = input.get(0); // Set the First Observation Value as Default
		double[][] count = {{0,0},{0,0}}; //2-D Array to Store the Counts for 4 Cases
		//Popularize the Count Array
		for(int i=0;i<input.size();i++) {
			if (input.get(i).equals(value1)) {
				if(output.get(i).equals("-")) {
					count[0][0]++;
				}
				else count[0][1]++;
			}
			else {
				if(output.get(i).equals("-")) {
					count[1][0]++;
				}
				else count[1][1]++;
			}
		}
		double countValue1=count[0][0]+count[0][1];
		double countValue2=count[1][0]+count[1][1];
		//Calculating the Entropy
		double entropy = 
				(countValue1/input.size())*
				((count[0][0]/(countValue1==0?1:countValue1))*
						Math.log((countValue1==0?1:countValue1)/(count[0][0]==0?1:count[0][0]))/Math.log(2)+
				 (count[0][1]/(countValue1==0?1:countValue1))*
				 		Math.log((countValue1==0?1:countValue1)/(count[0][1]==0?1:count[0][1]))/Math.log(2))
				+
				(countValue2/input.size())*
				((count[1][0]/(countValue2==0?1:countValue2))*
						Math.log((countValue2==0?1:countValue2)/(count[1][0]==0?1:count[1][0]))/Math.log(2)+
				 (count[1][1]/(countValue2==0?1:countValue2))*
				 		Math.log((countValue2==0?1:countValue2)/(count[1][1]==0?1:count[1][1]))/Math.log(2));
		return entropy;
	}
	
	static int[] countOutput(ArrayList<String> output) {
		int[] count = {0,0};
		for(int i=0;i<output.size();i++) {
			if(output.get(i).equals("+")) {
				count[1]++;
			}
			else count[0]++;
		}
		return count;
	}
	static int[] countOutput(ArrayList<String> output,node n, ArrayList<ArrayList<String>> input) {
		int[] count = {0,0};
		for(int i=0;i<output.size();i++) {
			if(input.get(n.field).get(i).equals(n.value)) {
				if(output.get(i).equals("+")) {
					count[1]++;
				}
				else count[0]++;
			}
		}
		return count;
	}
	static class node{
		private int field;
		private String value;
		private int[] count;
		private node parent;
		private ArrayList<node> children;

	}
	static ArrayList<ArrayList<String>> selectSubInput(ArrayList<ArrayList<String>> input, node n){
		ArrayList<ArrayList<String>>  subInput = new ArrayList<>();
		ArrayList<Integer> mark = new ArrayList<>();
		for(int i=0;i<input.get(n.field).size();i++) {
			if(input.get(n.field).get(i).equals(n.value)) {
				mark.add(i);
			}
		}
		for(int i=0;i<input.size();i++) {
			ArrayList<String> col = new ArrayList<>();
			for(int j=0;j<mark.size();j++) {
				col.add(input.get(i).get(mark.get(j)));	
			}
			subInput.add(col);
		}
		return subInput;
	}
	static ArrayList<String> selectSubOutput(ArrayList<ArrayList<String>> input, node n,ArrayList<String> output){
		ArrayList<String>  subOutput = new ArrayList<>();
		ArrayList<Integer> mark = new ArrayList<>();
		for(int i=0;i<input.get(n.field).size();i++) {
			if(input.get(n.field).get(i).equals(n.value)) {
				mark.add(i);
			}
		}
		for(int i=0;i<mark.size();i++) {
			subOutput.add(output.get(mark.get(i)));
		}
		return subOutput;
	}
}
