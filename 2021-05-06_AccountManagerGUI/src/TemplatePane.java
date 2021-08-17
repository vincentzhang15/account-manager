import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Sets up the template pane containing the account templates.
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class TemplatePane extends JPanel
{
	/**
	 * Sets up procedures when delete is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
    private final class AbstractActionExtensionDelete extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * If the template is not currently in use, delete the template.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf( e.getActionCommand() );
			String website = (String)table.getValueAt(modelRow, 0);

			if(FileHandling.accounts.get(website) != null && FileHandling.accounts.get(website).size() != 0)
			{
				JOptionPane.showMessageDialog(null, "Template currently in use. Cannot delete.");
				return;
			}

			int reply = JOptionPane.showConfirmDialog(null, "Deleting template, are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION)
			{
				FileHandling.templates.remove(website);
				((DefaultTableModel)table.getModel()).removeRow(modelRow);
				FileHandling.write();
			}
		}
	}

	/**
	 * Sets up procedures when edit button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionEdit extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * If template is not in use, allow the template to be edited by adding a new account based on the existing template.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf( e.getActionCommand() );
			String website = (String)table.getValueAt(modelRow, 0);

			if(FileHandling.accounts.get(website) != null && FileHandling.accounts.get(website).size() != 0)
			{
				JOptionPane.showMessageDialog(null, "Template currently in use. Cannot edit.");
				return;
			}

			new FrameNewAccount().templateEdit(website);
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
		 * Create a pop-up window with the full account information displayed.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf(e.getActionCommand());

			String website = (String)table.getValueAt(modelRow, 0);
			new FrameNewAccount().templateMore(website);
		}
	}

	/**
	 * Sets up procedures when copy button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionCopy extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Copy the website of the row in which the copy button was pressed.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf( e.getActionCommand() );
			String text = (String)table.getValueAt(modelRow, table.getSelectedColumn()-2);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
		}
	}

	/**
	 * Sets up procedures when use button is pressed.
	 * 
	 * @author Vincent Zhang
	 * @since 06-May-2021
	 */
	private final class AbstractActionExtensionUse extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a pop-up window with a pre-filled template of an account type.
		 * 
		 * @param e Event
		 */
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf( e.getActionCommand() );
			String website = (String)table.getValueAt(modelRow, 0);
			new FrameNewAccount().displayFromTemplate(website, FileHandling.templates.get(website));
		}
	}

	private DefaultTableModel tableModel;
	private JTable table;

	/**
	 * Set up table settings.
	 */
    public TemplatePane()
    {
        String[] columnHeading = {"Website","Use", "Copy", "More", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columnHeading, 0);
        table = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1080, 300));
        this.add(scrollPane); // Add the JTable of DefaultTableModel model within the JScrollPane to the JPanel.
		this.setMaximumSize(new Dimension(1080, 300));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(812); // Website
        table.getColumnModel().getColumn(1).setPreferredWidth(50); // Use
        table.getColumnModel().getColumn(2).setPreferredWidth(50); // Copy
        table.getColumnModel().getColumn(3).setPreferredWidth(50); // More
        table.getColumnModel().getColumn(4).setPreferredWidth(50); // Edit
        table.getColumnModel().getColumn(5).setPreferredWidth(50); // Delete
                
		Action copy = new AbstractActionExtensionCopy();
		Action use = new AbstractActionExtensionUse();
		Action more = new AbstractActionExtensionMore();
		Action edit = new AbstractActionExtensionEdit();
		Action delete = new AbstractActionExtensionDelete();

		// Add button columns. ButtonColumn.java was downloaded from the internet.
        new ButtonColumn(table, use, 1);
        new ButtonColumn(table, copy, 2);
        new ButtonColumn(table, more, 3);
        new ButtonColumn(table, edit, 4);
        new ButtonColumn(table, delete, 5);

		updateTable();
	}
	
	/**
	 * Updates the templates table. updateTable() is called when a new account is created with a new template.
	 * 
	 */
	public void updateTable()
	{
		tableModel.setRowCount(0); // Clear table.

		// Add row.
		Iterator<Map.Entry<String, ArrayList<String>>> it = FileHandling.templates.entrySet().iterator();
		while(it.hasNext())
			tableModel.addRow(new String[]{it.next().getKey()});
	}

	/**
	 * Highlights the table rows where the search string matches any substring of the website.
	 * 
	 * @param strSearch The String in the text field that is searched for.
	 */
	public void highlight(String strSearch)
	{
		table.clearSelection();
		for(int i=0; i < tableModel.getRowCount(); i++)
			if(((String)tableModel.getValueAt(i, 0)).indexOf(strSearch) != -1)
				table.addRowSelectionInterval(i, i);
	}
}