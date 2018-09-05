package dicomAnonymizer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;;


/**
 * @author Krzysztof Szewczyk
 */
public class FileChooser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFileChooser chooser = new JFileChooser();
	private String[] directory = null;

	/**
	 * 
	 * @param ch
	 *            :'f' - files or 'd' - directory
	 * @param b
	 *            : false - deanon or true - anon
	 * @param db
	 *            : DBConnection
	 */
	public FileChooser(char ch, boolean b, DBConnection db) {
		switch (ch) {
		// choose only FILES
		case 'f':
			this.chooser.setCurrentDirectory(new java.io.File("."));
			this.chooser.setDialogTitle("Select the destination directory");
			this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			this.chooser.setMultiSelectionEnabled(true);
			//delete the folder creation option
			disableCreateNewFolder(chooser);
			// Removal of options "All files"
			this.chooser.setAcceptAllFileFilterUsed(false);
			// Possible format in FileChooser is ".dcm"
			this.chooser.addChoosableFileFilter(new FileNameExtensionFilter("Dicom", "dcm"));
			//Possible formats are received all file names in the database
			String[] dicomNames = db.getDicomNames();
			// Only "anonimised" files are selected in the loop
			if (!b) {
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return ".dcm";
					}
			
					@Override
					public boolean accept(File file) {
						// always accept directories
						if (file.isDirectory())
							return true;
						for (int i = 0; i < dicomNames.length; i++) {
							System.out.println(file.getName());
							System.out.println(dicomNames[i]);
							if (file.getName().equals(dicomNames[i])) {
								return true;
							}
						}
						return false;
					}
				});
			}
			
			//Getting path
			if (this.chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				int i = this.chooser.getSelectedFiles().length;
				int j = 0;
				directory = new String[i];
				for (File dir : this.chooser.getSelectedFiles()) {
					directory[j] = dir + "";
					j++;
				}
				System.out.println("getCurrentDirectory(): " + this.chooser.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + directory);
			} else {
				System.out.println("No Selection ");
			}
			break;
			
		// only folders are able to select
		case 'd':
			this.chooser.setCurrentDirectory(new java.io.File("."));
			this.chooser.setDialogTitle("Select the destination directory");
			this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// disable the "All files" option.
			this.chooser.setAcceptAllFileFilterUsed(false);
			if (this.chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.directory = new String[] { this.chooser.getSelectedFile() + "" };
				System.out.println("getCurrentDirectory(): " + this.chooser.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + directory);
			} else {
				System.out.println("No Selection ");
			}
			break;
		}
	}

	/**
	 * 
	 * @returnReturns the one-dimensional array of paths
	 */
	public String[] getDirectory() {
		return this.directory;
	}

	/**
	 * @param c JFileChooser
	 * @return
	 */
	public static boolean disableCreateNewFolder(Container c) {
		Component[] comps = c.getComponents();
		boolean gotIt = false;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JButton) {
				JButton b = (JButton) comps[i];
				String ttText = b.getToolTipText();
				if (ttText != null && ttText.equals("Create New Folder")) {
					b.setEnabled(false);
				}
				gotIt = true;
			}
			if (comps[i] instanceof Container) {
				gotIt = disableCreateNewFolder((Container) comps[i]);
			}

			if (gotIt) {
				break;
			}
		}

		return gotIt;
	}

}
