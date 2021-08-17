import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Sets up the content pane containing the account data.
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class ContentPane extends JPanel
{
	/**
	 * Sets up procedures when copy button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionCopy extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Copies the text field contents in the left adjacent column of the row in which the copy button was pressed.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf(e.getActionCommand());
			String text = (String)table.getValueAt(modelRow, table.getSelectedColumn()-1);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
		}
	}

	/**
	 * Sets up procedures when more button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionMore extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a popup window to display more information about account information.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf(e.getActionCommand());

			String website = (String)table.getValueAt(modelRow, 0);
			String id = (String)table.getValueAt(modelRow, 2);
			new FrameNewAccount().accountMore(website, id);
		}
	}

	/**
	 * Sets up procedures when delete button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionDelete extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Delete account information if requested.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			int reply = JOptionPane.showConfirmDialog(null, "Deleting account, are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION)
			{
				JTable table = (JTable)e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());
				FileHandling.accounts.get((String)table.getValueAt(modelRow, 0)).remove((String)table.getValueAt(modelRow, 2)); // Remove account stored in TreeMap.
				((DefaultTableModel)table.getModel()).removeRow(modelRow); // Remove account from table.
				FileHandling.write(); // Save result by writing to file.
			}
		}
	}

	private static final long serialVersionUID = 1L;
	private DefaultTableModel tableModel;
	private JTable table;

	/**
	 * Sets up table settings.
	 */
	public ContentPane()
	{
		String [] columnHeading = {"Website","Copy","Login","Copy", "Password", "Copy", "More", "Delete"};
		tableModel = new DefaultTableModel(columnHeading, 0); // Declare table model with 7 columns and 0 rows.;
		table = new JTable(tableModel); // Set JTable model to DefaultTableModel.
		JScrollPane scrollPane = new JScrollPane(table); // Allow the table to have scrollbars.
		scrollPane.setPreferredSize(new Dimension(1080, 300));
		this.add(scrollPane); // Add the JTable of DefaultTableModel model within the JScrollPane to the JPanel.
		//this.setBorder(BorderFactory.createLineBorder(Color.RED,5));
		this.setMaximumSize(new Dimension(1080, 300));

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(390); // Website
		table.getColumnModel().getColumn(1).setPreferredWidth(50); // Copy
		table.getColumnModel().getColumn(2).setPreferredWidth(210); // Login
		table.getColumnModel().getColumn(3).setPreferredWidth(50); // Copy
		table.getColumnModel().getColumn(4).setPreferredWidth(210); // Password
		table.getColumnModel().getColumn(5).setPreferredWidth(50); // Copy
		table.getColumnModel().getColumn(6).setPreferredWidth(50); // More
		table.getColumnModel().getColumn(7).setPreferredWidth(50); // Delete

		Action copy = new AbstractActionExtensionCopy();
		Action more = new AbstractActionExtensionMore();
		Action delete = new AbstractActionExtensionDelete();
		
		// Add button columns. ButtonColumn is an external program downloaded from the internet.
		new ButtonColumn(table, copy, 1);
		new ButtonColumn(table, copy, 3);
		new ButtonColumn(table, copy, 5);
		new ButtonColumn(table, more, 6);
		new ButtonColumn(table, delete, 7);

		updateTable();
	}

	/**
	 * Updates the accounts in table. updateTable() will be called again after a new account has been created from FrameNewAccount.
	 */
	public void updateTable()
	{
		tableModel.setRowCount(0); // Clear table.

		// Update table by looping through accounts TreeMap, where all account data is stored for the session.
		Iterator<Map.Entry<String, TreeMap<String, ArrayList<String>>>> it = FileHandling.accounts.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, TreeMap<String, ArrayList<String>>> me = it.next();
			String website = me.getKey();
			TreeMap<String, ArrayList<String>> data = me.getValue();
			Iterator<Map.Entry<String, ArrayList<String>>> it2 = data.entrySet().iterator();

			while(it2.hasNext())
			{
				Map.Entry<String, ArrayList<String>> entry = it2.next();
				String id = entry.getKey();
				ArrayList<String> remain = new ArrayList<>(entry.getValue());
				remain.add(0, id);
				addRow(website, remain);
			}
		}
	}

	/**
	 * Add a row to the account data table.
	 * 
	 * @param website A table row begins with a website
	 * @param contents A table row contains the other account data
	 */
	public void addRow(String website, ArrayList<String> contents)
	{
		String[] data = new String[contents.size()+1+5]; // 1 for website, 5 for buttons
		data[0] = website;
		data[2] = contents.get(0); // ID.
		data[4] = contents.get(1); // Pass
    	tableModel.addRow(data);
	}

	/**
	 * Highlight rows that are searched for by selecting the rows containing the desired information.
	 * 
	 * @param strSearch String in the text field that is searched for
	 */
	public void highlight(String strSearch)
	{
		table.clearSelection();
		for(int i=0; i < tableModel.getRowCount(); i++)
		{
			if(((String)tableModel.getValueAt(i, 0)).indexOf(strSearch) != -1 // Check website.
			|| ((String)tableModel.getValueAt(i, 2)).indexOf(strSearch) != -1 // Check login.
			|| ((String)tableModel.getValueAt(i, 4)).indexOf(strSearch) != -1) // Check password.
			{
				table.addRowSelectionInterval(i, i);
			}
		}
	}
}