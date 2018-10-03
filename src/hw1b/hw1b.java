package hw1b;
import java.io.*;
import java.util.ArrayList;

public class hw1b {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			ArrayList<String> text = new ArrayList<String>();
			String line;
			int i =-1;
			while ((line=br.readLine())!=null) {
				text.add(line);
				i++;
			}
			while (i>-1) {
				System.out.println(text.get(i));
				i--;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
