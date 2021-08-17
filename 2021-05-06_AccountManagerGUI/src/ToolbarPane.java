import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Sets up the toolbar.
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class ToolbarPane extends JPanel
{
    /**
     * Set settings for the pannel.
     */
    public ToolbarPane()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setMaximumSize(new Dimension(1080, 43));
        
        addContent();
    }

    /**
     * Add the buttons and search field.
     */
    public void addContent()
    {
        // Create add button to add an account with a new format.
        {
            JButton buttonAdd = new JButton("Add");
            buttonAdd.setPreferredSize(new Dimension(60, 30));
            buttonAdd.addActionListener(e -> SwingUtilities.invokeLater(() -> new FrameNewAccount().displayGUI()));
            add(buttonAdd);
        }

        // Create search bar to highlight all rows in accounts and templates with match.
        {
            // Textfield for searching.
            JTextField searchField = new JTextField();
            searchField.setPreferredSize(new Dimension(240, 30));
            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed (KeyEvent e) {
                    Optional.of(e.getKeyCode()==KeyEvent.VK_ENTER).ifPresent((p) -> highlight(searchField.getText())); // Check every key.
                    // if(e.getKeyCode()==KeyEvent.VK_ENTER) highlight(searchField.getText()); // Check after press "Enter".
                }
            });
            add(searchField);

            // Search button.
            JButton btnSearch = new JButton("Search");
            btnSearch.setPreferredSize(new Dimension(80, 30));
            btnSearch.addActionListener(e -> highlight(searchField.getText()));
            add(btnSearch);
        }
    }

    /**
     * When call methods to highlight rows in templates and accounts.
     * 
     * @param strSearch The String in the text field
     */
    private void highlight(String strSearch)
    {
        AccountManager.contentPane.highlight(strSearch);
        AccountManager.templatePane.highlight(strSearch);
    }
}