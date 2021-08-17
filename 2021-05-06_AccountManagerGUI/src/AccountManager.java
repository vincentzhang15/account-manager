import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Graphical user interface Account Manager program to manage accounts.
 * Project started 10-April-2021
 * 
 * @author Vincent Zhang
 * @since 06-May-2021
 */
public class AccountManager extends JFrame
{
	private static final int WINDOW_WIDTH = 1100;
	private static final int WINDOW_HEIGHT = 700;

	private static AccountManager frame;
	private static ToolbarPane toolbarPane;
	public static TemplatePane templatePane;
	public static ContentPane contentPane;
	public static FileHandling file;
	
	/**
	 * Start program. Ensure data folder exists.
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args)
	{
		File pathData = new File("AccountManagerGUIData");
		if (!pathData.exists())
			pathData.mkdirs();

		frame = new AccountManager();
		frame.run();
    }
	
	/**
	 * Initializations.
	 */
	public AccountManager()
	{
		super("Account Manager");
		file = new FileHandling();
		templatePane = new TemplatePane();
		toolbarPane = new ToolbarPane();
		contentPane = new ContentPane();

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}	

	/**
	 * Add components to the main frame.
	 */
	private void run()
	{
		JPanel dashboard = new JPanel();
		dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.PAGE_AXIS));
		dashboard.add(templatePane);
		dashboard.add(toolbarPane);
		dashboard.add(contentPane);
		frame.add(dashboard);
	}
}