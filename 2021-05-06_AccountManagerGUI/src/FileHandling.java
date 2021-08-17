import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class FileHandling
{
    // Key: Website, Value: login, password, date of creation, status on date, purpose, etc.
    public static TreeMap<String, ArrayList<String>> templates; // Website, headings
    public static TreeMap<String, TreeMap<String, ArrayList<String>>> accounts; // Website, id(user), fields

    /**
     * Get data from files immediately after program start.
     */
    public FileHandling()
    {
        read();
    }

    /**
     * Read data from files.
     */
    public static void read()
    {
        // Reset stored session data when (re-)reading.
        templates = new TreeMap<>();
        accounts = new TreeMap<>();

        // Read files.
        try
        {
            String tmpLine;
            BufferedReader br1 = new BufferedReader(new FileReader("AccountManagerGUIData/AccountTemplates.txt"));
            while((tmpLine = br1.readLine()) != null)
            {
                Queue<String> data = new ArrayDeque<>(Arrays.asList(tmpLine.split("\\^")));
                templates.put(data.poll(), new ArrayList<>(data));
            }
            br1.close();

            BufferedReader br2 = new BufferedReader(new FileReader("AccountManagerGUIData/AccountData.txt"));
            while((tmpLine = br2.readLine()) != null)
            {
                List<String> result = Arrays.asList(tmpLine.split("\\^"));
                Queue<String> data = new ArrayDeque<>(result);
                String website = data.poll();
                TreeMap<String, ArrayList<String>> store = accounts.containsKey(website)?accounts.get(website):new TreeMap<>();
                store.put(data.poll(), new ArrayList<>(data));
                accounts.put(website, store);
            }
            br2.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Write data to files.
     */
    public static void write()
    {
        try
        {
            // Write to templates.
            BufferedWriter bw1 = new BufferedWriter(new FileWriter("AccountManagerGUIData/AccountTemplates.txt", false));
            Iterator<Map.Entry<String, ArrayList<String>>> it1 = templates.entrySet().iterator();
            while(it1.hasNext()) // Loop through templates.
            {
                Map.Entry<String, ArrayList<String>> me = it1.next();
                bw1.write(me.getKey() + "^"); // Website.
                for(String s : me.getValue()) // Template headings.
                    bw1.write(s + "^");
                bw1.write("\n");
            }
            bw1.close();

            // Write to accounts.
            BufferedWriter bw2 = new BufferedWriter(new FileWriter("AccountManagerGUIData/AccountData.txt", false));
            Iterator<Map.Entry<String, TreeMap<String, ArrayList<String>>>> websites = accounts.entrySet().iterator();
            
            while(websites.hasNext()) // Loop through websites.
            {
                Map.Entry<String, TreeMap<String, ArrayList<String>>> entry = websites.next();
                String website = entry.getKey();
                TreeMap<String, ArrayList<String>> info = accounts.get(website); // Id, fields.

                Iterator<Map.Entry<String, ArrayList<String>>> acc = info.entrySet().iterator();
                while(acc.hasNext()) // Loop through account.
                {
                    Map.Entry<String, ArrayList<String>> me2 = acc.next();
                    String id = me2.getKey(); // ID.
                    ArrayList<String> remain = me2.getValue(); // Fields.

                    bw2.write(website + "^");
                    bw2.write(id + "^");
                    for(int j = 0; j < remain.size(); j++)
                        bw2.write(remain.get(j)+"^");
                    bw2.write("\n");
                }
            }
            bw2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}