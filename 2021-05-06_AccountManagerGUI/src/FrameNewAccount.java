import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Sets up the main pop-up frame.
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class FrameNewAccount
{
    private JFrame frame;
    private ArrayList<JTextField[]> textFieldContent = new ArrayList<JTextField[]>(); // Contains the row-pair in pop-up frame.
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 15);

    /**
     * Setup frame settings.
     */
    public FrameNewAccount()
    {
        frame = new JFrame("Add Account");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 1000));
        frame.setLayout(new GridLayout(0, 2));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
    
    /**
     * Called when "use" button is pressed in templates table. Pre-fills pop-up frame for user to fill to add a new account.
     * 
     * @param website The website the account is added for
     * @param displayInfo All template data excluding the website
     */
    public void displayFromTemplate(String website, ArrayList<String> displayInfo) // ArrayList doesn't include "Website" in template.
    {
        // Header button row.
        {
            // Add row button.
            JButton newInfo = new JButton("Add Row");
            newInfo.setFont(DEFAULT_FONT);
            newInfo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JOptionPane.showMessageDialog(null, "Cannot change format of existing template.");
                }
            });
            frame.add(newInfo);
    
            // Save button.
            JButton save = new JButton("Save and Close");
            save.setFont(DEFAULT_FONT);
            save.addActionListener(ae->save());
            frame.add(save);
        }

        // Display form content from stored template. Template is not editable.
        {
            addRow("Website", website, false, false);
            for(String heading : displayInfo)
            {
                if(heading.equals("Date of Creation"))
                    addRow("Date of Creation", DateTimeFormatter.ofPattern("uuuu/MM/dd").format(LocalDate.now()), false, true);
                else
                    addRow(heading, null, false, true);
            }
        }
    }

    /**
     * Called when add button from toolbar is pressed. Creates the pop-up form.
     */
    public void displayGUI()
    {
        // Header button row.
        {
            JButton newInfo = new JButton("Add Row");
            newInfo.setFont(DEFAULT_FONT);
            newInfo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    addRow(null, null, true, true);
                    frame.revalidate();
                    frame.repaint();
                }
            });
            frame.add(newInfo);
    
            JButton save = new JButton("Save and Close");
            save.setFont(DEFAULT_FONT);
            save.addActionListener(ae->save());
            frame.add(save);
        }

        // Default form format.
        {
            addRow("Website", null, false, true);
            addRow("Login", null, false, true);
            addRow("Password", null, false, true);
            addRow("Date of Creation", DateTimeFormatter.ofPattern("uuuu/MM/dd").format(LocalDate.now()), false, true);
            for(int i = 0; i < 6; i++)
                addRow(null, null, true, true);
        }
    }

    /**
     * Add a row to the pop-up form.
     * 
     * @param left The string that should be displayed in the left text field
     * @param right Text in the right text field
     * @param canEditLeft Set edit state of left text field
     * @param canEditRight Set edit state of right text field
     */
    private void addRow(String left, String right, boolean canEditLeft, boolean canEditRight)
    {
        JTextField [] entry = new JTextField[2];
        entry[0] = new JTextField(left); entry[0].setFont(DEFAULT_FONT); frame.add(entry[0]); entry[0].setEditable(canEditLeft);
        entry[1] = new JTextField(right); entry[1].setFont(DEFAULT_FONT); frame.add(entry[1]); entry[1].setEditable(canEditRight);
        textFieldContent.add(entry); // Add every row to the collection of rows.
    }

    /**
     * Called from contentPane when more button is pressed.
     * 
     * @param website Website of the row the more button was clicked
     * @param id Login
     */
    public void accountMore(String website, String id)
    {
        ArrayList<String> template = FileHandling.templates.get(website);
        ArrayList<String> account = FileHandling.accounts.get(website).get(id);
        addRow("Website", website, false, false);
        addRow("Login", id, false, false);
        for(int i = 0; i < template.size()-1; i++)
            addRow(template.get(i+1), account.get(i), false, false); // Display account data with form template.
    }
    /**
     * Called from templatePane when more button is pressed.
     * 
     * @param website Website link
     */
    public void templateMore(String website)
    {
        ArrayList<String> template = FileHandling.templates.get(website);
        addRow("Website", website, false, false);
        addRow("Login", "", false, false);
        for(int i = 0; i < template.size()-1; i++)
            addRow(template.get(i+1), "", false, false);
    }
    /**
     * Called from templatePane when edit button is pressed. Allow the template fields to be editable.
     * 
     * @param website Website URL
     */
    public void templateEdit(String website)
    {
        // Header button row.
        {
            JButton newInfo = new JButton("Add Row");
            newInfo.setFont(DEFAULT_FONT);
            newInfo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    addRow(null, null, true, true);
                    frame.revalidate();
                    frame.repaint();
                }
            });
            frame.add(newInfo);
    
            JButton save = new JButton("Save and Close");
            save.setFont(DEFAULT_FONT);
            save.addActionListener(ae->save());
            frame.add(save);
        }

        // Form content.
        {
            addRow("Website", website, false, true);
            addRow("Login", "", true, true);
            ArrayList<String> template = FileHandling.templates.get(website);
            for(int i = 0; i < template.size()-1; i++)
                addRow(template.get(i+1), "", true, true);
        }
    }

    /**
     * Popup message when there is a saving error. save()'s helper method.
     */
    private void errorSave()
    {
        JOptionPane.showMessageDialog(null, "Error when saving: Website and Login cannot be empty.");
    }
    /**
     * Update FileHandling templates and accounts.
     */
    public void save()
    {
        // An account is incomplete without a website and a login.
        String website = textFieldContent.get(0)[1].getText(); if(website.length()==0 || website==null){errorSave(); return;}
        String id = textFieldContent.get(1)[1].getText(); if(id.length()==0 || id==null){errorSave(); return;}

        // Check if template exist. Use template format if template already exists. Data will be cropped to fit the existing template format.
        if(FileHandling.templates.containsKey(website))
        {
            // Right column data.
            ArrayList<String> account = new ArrayList<>();
            TreeMap<String, ArrayList<String>> temp = FileHandling.accounts.get(website);
            if(temp == null || temp.size() == 0) // Contains template but no accounts.
            {
                temp = new TreeMap<>();
                for(int i = 2; i < FileHandling.templates.get(website).size()+1; i++)
                {
                    String s = textFieldContent.get(i)[1].getText();
                    account.add(s.length()==0 || s==null ? " " : s);
                }
                ArrayList<String> template = new ArrayList<>();
                for(int i = 1; i < textFieldContent.size(); i++)
                {
                    String heading = textFieldContent.get(i)[0].getText();
                    if(heading.length() == 0)
                        continue;
                    template.add(heading);
                }
                FileHandling.templates.put(website, template); // Allow template override because no account exists.
            }
            else
            {
                for(int i = 2; i < FileHandling.templates.get(website).size()+1; i++)
                {
                    String s = textFieldContent.get(i)[1].getText();
                    account.add(s.length()==0 || s==null ? " " : s);
                }
            }
            temp.put(id, account);
            FileHandling.accounts.put(website, temp); // Add account.
        }
        else // New template.
        {
            // Add template to templates.
            Queue<Integer> q = new LinkedList<>();
            ArrayList<String> template = new ArrayList<>();
            for(int i = 1; i < textFieldContent.size(); i++)
            {
                String heading = textFieldContent.get(i)[0].getText();
                if(heading.length() == 0)
                    continue;

                template.add(heading);
                if(i != 1) // Already have ID don't need to add.
                    q.add(i);
            }
            FileHandling.templates.put(website, template);
            
            // Right column data.
            ArrayList<String> account = new ArrayList<>();
            while(!q.isEmpty())
            {
                int i = q.poll();
                String content = textFieldContent.get(i)[1].getText();
                account.add(content);
            }
            TreeMap<String, ArrayList<String>> temp = new TreeMap<>();
            temp.put(id, account);
            
            FileHandling.accounts.put(website, temp);
        }
        FileHandling.write(); // Export to file.
        AccountManager.contentPane.updateTable();
        AccountManager.templatePane.updateTable();
        frame.dispose();
    }
}