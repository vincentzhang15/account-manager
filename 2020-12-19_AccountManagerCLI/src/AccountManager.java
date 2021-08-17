import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;    

/**
 * Command-line interface of account manager program to manage accounts. 
 * Project started 19-December-2020
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class AccountManager
{
	TreeMap<String, ArrayList<String>> formats; // Link, Remaining Data.
	TreeMap<String, ArrayList<String>> accounts; // Date and time of Entry, Data.

	/**
	 * Start program.
	 * 
	 * @param args Command=line arguments
	 * @throws IOException Throw IOException
	 */
	public static void main (String [] args) throws IOException
	{
		AccountManager am = new AccountManager();
		am.run();
	}

	/**
	 * Obtain data from files.
	 * 
	 * @throws IOException
	 */
	void init() throws IOException
	{
		formats = new TreeMap<String, ArrayList<String>>();
		accounts = new TreeMap<String, ArrayList<String>>();

		BufferedReader brFormats = Files.newBufferedReader(Paths.get("../data/Format.txt"));
		BufferedReader brAccounts = Files.newBufferedReader(Paths.get("../data/AccountsData.txt"));

		String line; String[] data;
		while ((line = brFormats.readLine()) != null && (data = line.split("\\|")) != null) formats.put(data[0], new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(data, 1, data.length)))); // Website, Fields.
		while ((line = brAccounts.readLine()) != null && (data = line.split("\\|")) != null) accounts.put(data[0], new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(data, 1, data.length)))); // Website, Fields.

		brFormats.close();
		brAccounts.close();
	}

	/**
	 * Program control, contains menu.
	 * 
	 * @throws IOException
	 */
	void run() throws IOException
	{
		init();
		while(true)
		{
			System.out.println("1: Add Account\n2: Add Format\n3: Print\n0: Quit");
			BufferedReader brTask = new BufferedReader(new InputStreamReader(System.in));
			int choice = Integer.parseInt(brTask.readLine());
			switch(choice)
			{
				case 1:
					addAccount();
					accountsDataBackup();
					break;
				case 2:
					addFormat();
					formatBackup();
					break;
				case 3:
					print();
					outputBackup();
					break;
				default:
					System.exit(0);
			}
		}
	}

	/**
	 * Helper method to get the current date and time and return formatted String.
	 * 
	 * @return Formatted date and time
	 */
	String dateTime() {return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());}

	/**
	 * Backup output result.
	 * 
	 * @throws IOException
	 */
	void outputBackup() throws IOException {Files.copy(Paths.get("../output/Output.txt"), Paths.get("../backup/OutputBackup/" + dateTime() + ".txt"));}

	/**
	 * Backup accounts.
	 * 
	 * @throws IOException
	 */
	void accountsDataBackup() throws IOException {Files.copy(Paths.get("../data/AccountsData.txt"), Paths.get("../backup/AccountsDataBackup/" + dateTime() + ".txt"));}

	/**
	 * Backup formats.
	 * 
	 * @throws IOException
	 */
	void formatBackup() throws IOException {Files.copy(Paths.get("../data/Format.txt"), Paths.get("../backup/FormatBackup/" + dateTime() + ".txt"));}
	
	/**
	 * Add new account information.
	 * 
	 * @throws IOException
	 */
	void addAccount() throws IOException
	{
		// Get data from user.
		{
			System.out.println("What is link?");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String link = br.readLine();
			if(!formats.containsKey(link)) {System.out.println("Format does not exist, please add format first."); return;}
	
			ArrayList<String> accountValue = new ArrayList<>(Collections.singletonList(link));
			for(String format : formats.get(link))
			{
				System.out.println(format);
				accountValue.add(br.readLine());
			}
			accounts.put(dateTime(), accountValue);
		}

		// Write to file.
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("../data/AccountsData.txt"));  
			for(Map.Entry<String, ArrayList<String>> entry : accounts.entrySet())
			{
				bw.write(entry.getKey());
				for(String s : entry.getValue())
					bw.write("|" + s);
				bw.write("\n");
			}
			bw.close();
		}
	}

	/**
	 * Add new format information.
	 * 
	 * @throws IOException
	 */
	void addFormat() throws IOException
	{
		// Get data from user.
		{
			System.out.println("Add format information, start with link.");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String link = br.readLine();
			if(formats.containsKey(link)) {System.out.println("Format Exists, cannot override."); return;}
	
			System.out.println("-1: to quit");
			ArrayList<String> value = new ArrayList<String>();
			String line;
			while(!(line = br.readLine()).equals("-1"))
				value.add(line);
			formats.put(link, value);
		}

		// Write to file.
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("../data/Format.txt"));  
			for(Map.Entry<String, ArrayList<String>> entry : formats.entrySet())
			{
				bw.write(entry.getKey());
				for(String s : entry.getValue())
					bw.write("|" + s);
				bw.write("\n");
			}
			bw.close();
		}
	}
	
	/**
	 * Helper method for print() to print out data and write to file.
	 * 
	 * @param line
	 * @return Parameter string
	 * @throws IOException
	 */
	String output(String line) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter("../output/Output.txt", true));
		bw.write(line+"\n");
		System.out.println(line);
		bw.close();
		return line;
	}
	/**
	 * Standard output and write all account information.
	 * 
	 * @throws IOException
	 */
	void print() throws IOException
	{
		BufferedWriter clear = new BufferedWriter(new FileWriter("../output/Output.txt")); clear.close(); // Clear file, need to append later.
		for(Map.Entry<String, ArrayList<String>> account : accounts.entrySet()) // Loop through accounts.
		{
			output(account.getKey()); // Date and time of creation.
			ArrayList<String> accInfo = account.getValue(); // Account information.
			ArrayList<String> format = formats.get(output(accInfo.get(0))); // Format based on link.

			for(int i = 1; i < accInfo.size(); i++)
				output((format.get(i-1) + ((format.get(i-1).length() >= 8)?("\t"):("\t\t"))) + accInfo.get(i));
			output("");
		}
	}
}