import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class inspect {


	public static void main(String[] args) {
		//Read the File
		Scanner fileInput = null;
		StringBuilder fileContent = new StringBuilder();
		try {
			fileInput = new Scanner (new File(args[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Read the File Content to a String First and Then Break the File String into Lines
		while (fileInput.hasNextLine()) fileContent.append(fileInput.nextLine() + "\n");
		String[] line = fileContent.toString().split("\n");
		//Initialize the Input and Output Array
		String[] name =line[0].split(",");
		String[][] input = new String[line.length-1][];
		String[] output = new String[line.length-1];
		//Popularize the Input and Output Array Lists with Lines
		for(int i =0; i<line.length-1;i++) {
			String[] features=line[i+1].split(",");
			input[i]=new String[features.length-1];
			for(int j=0;j<features.length-1;j++) {
				input[i][j]=features[j].trim();				
			}
			output[i]=features[features.length-1].trim();
		}
		//Standardize the Output Array
		for(int i=0;i<output.length;i++) {
			if(output[i].equals("A")||output[i].equals("democrat")||output[i].equals("yes")) {
				output[i]="+";
			}
			else output[i]="-";
		}
		//Calculate the Initial Entropy
		int[] labelCount = {0,0};
		for(String label : output) {
			if(label.equals("+")) {
				labelCount[1]++;
			}
			else {
				labelCount[0]++;
			}
		}
		double plusRatio = (double)labelCount[1]/(labelCount[0]+labelCount[1]);
		double minusRatio = (double)labelCount[0]/(labelCount[0]+labelCount[1]);
		double entropy = plusRatio*Math.log(1/plusRatio)/Math.log(2)+minusRatio*Math.log(1/minusRatio)/Math.log(2);
		double error=Math.min(plusRatio,minusRatio);
		System.out.println("entropy: "+entropy);
		System.out.println("error: "+error);
	}

}
