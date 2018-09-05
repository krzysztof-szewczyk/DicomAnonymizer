package dicomAnonymizer;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import java.awt.Desktop;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.dcm4che2.data.VR;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTable;
import javax.swing.JRadioButton;

/**
 * @author Krzysztof Szewczyk
 * 
 */
public class App extends JFrame implements ActionListener, ListSelectionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane = new JPanel();
	// Panel 1 - PanelMenu
	private JPanel panelMenu = new JPanel();
	private JButton btnAnon = new JButton("Anonymize");
	private JButton btnDeanon = new JButton("Deanonymize");
	private JLabel lblInfo = new JLabel("Choose the option you want to use");
	// Panel 2 - PanelFiles
	private JPanel panelFiles = new JPanel();
	private JLabel lblFiles1 = new JLabel("Choose DICOM files");
	private DefaultListModel<String> model = new DefaultListModel<>();
	private JList<String> listFiles = new JList<>(model);
	private final JScrollPane scrollPane = new JScrollPane();
	private JButton btnAdd = new JButton("Add");
	private JButton btnAddDir = new JButton("Add dir");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnDeleteAll = new JButton("Delete All");
	private JLabel lblFiles2 = new JLabel("Select the destination directory");
	private JTextField textDestination = new JTextField();
	private JButton btnChoose = new JButton("Choose");
	private JButton btnBack1 = new JButton("Back");
	private JButton btnNext = new JButton("Next");
	// Panel 3 - PanelTags
	private JPanel panelTags = new JPanel();
	private JLabel lblTags = new JLabel("Set tags to change");
	private JButton btnSelectAll = new JButton("Select all");
	private JButton btnClearAll = new JButton("Clear all");
	private JButton btnBack2 = new JButton("Back");
	private JButton btnDone = new JButton("Done");
	private JTable tableTags;
	private final JScrollPane scrollPaneTags = new JScrollPane();
	private JRadioButton rdbtnOriginalName = new JRadioButton("   Restore the original name");
	private JRadioButton rdbtnCurrentName = new JRadioButton("   Keep the current name");
	private JRadioButton rdbtnRandomName = new JRadioButton("   Get new random name");
	private JRadioButton rdbtnOwnName = new JRadioButton("");
	private ButtonGroup rdbtnGroup = new ButtonGroup();
	private JTextField textOwnName = new JTextField();
	// DataBase
	private DBConnection db = new DBConnection();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public App() {

		// Creating dataBase
		this.db.createTable();

		// frame sets
		this.setTitle("Dicom Anonymizer");
		this.setResizable(false);
		// Icon source: http://www.clker.com/clipart-blue-brain.html
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("blue-brain-md.jpg"));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 621, 585);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new CardLayout(0, 0));

		this.contentPane.add(this.panelMenu, "name_4808464975598");
		this.panelMenu.setLayout(null);
		this.panelMenu.setVisible(true);

		this.btnAnon.setBounds(87, 259, 151, 23);
		this.panelMenu.add(this.btnAnon);

		this.btnDeanon.setBounds(369, 259, 151, 23);
		this.panelMenu.add(this.btnDeanon);

		this.lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblInfo.setBounds(172, 182, 279, 23);
		this.panelMenu.add(this.lblInfo);

		this.panelFiles.add(this.scrollPane);
		this.contentPane.add(this.panelFiles, "name_4816017391278");
		this.panelFiles.setLayout(null);
		this.panelFiles.setVisible(false);

		this.lblFiles1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblFiles1.setBounds(194, 57, 164, 23);
		this.panelFiles.add(this.lblFiles1);
		this.scrollPane.setBounds(10, 91, 486, 214);
		scrollPane.setViewportView(listFiles);
		this.listFiles.addListSelectionListener(this);

		this.btnAdd.setBounds(506, 89, 89, 23);
		this.panelFiles.add(this.btnAdd);

		this.btnAddDir.setBounds(506, 123, 89, 23);
		this.panelFiles.add(this.btnAddDir);

		this.btnDelete.setBounds(506, 248, 89, 23);
		this.panelFiles.add(this.btnDelete);
		this.btnDelete.setEnabled(false);

		this.btnDeleteAll.setBounds(506, 282, 89, 23);
		this.panelFiles.add(this.btnDeleteAll);
		this.btnDeleteAll.setEnabled(false);
		this.btnDeleteAll.addActionListener(this);

		this.lblFiles2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblFiles2.setBounds(168, 349, 252, 23);
		this.panelFiles.add(this.lblFiles2);

		this.textDestination.setBounds(10, 383, 486, 30);
		// this.textDestination.setEnabled(false);
		this.panelFiles.add(this.textDestination);
		this.textDestination.setColumns(10);

		this.btnChoose.setBounds(506, 387, 89, 23);
		this.panelFiles.add(this.btnChoose);

		this.btnBack1.setBounds(331, 485, 89, 23);
		this.panelFiles.add(this.btnBack1);

		this.btnNext.setBounds(430, 485, 89, 23);
		this.panelFiles.add(this.btnNext);

		this.contentPane.add(this.panelTags, "name_4817144419353");
		this.panelTags.setLayout(null);

		this.lblTags.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblTags.setBounds(203, -3, 148, 32);
		this.panelTags.add(this.lblTags);

		this.btnSelectAll.setBounds(89, 409, 89, 23);
		this.panelTags.add(this.btnSelectAll);

		this.btnClearAll.setBounds(396, 409, 89, 23);
		this.panelTags.add(this.btnClearAll);

		this.btnBack2.setBounds(331, 485, 89, 23);
		this.panelTags.add(this.btnBack2);

		this.btnDone.setBounds(430, 485, 89, 23);
		this.panelTags.add(this.btnDone);

		this.scrollPaneTags.setBounds(10, 40, 575, 356);
		this.panelTags.add(this.scrollPaneTags);

		this.rdbtnOriginalName.setBounds(10, 439, 203, 23);
		this.panelTags.add(this.rdbtnOriginalName);

		this.rdbtnCurrentName.setBounds(10, 491, 203, 23);
		this.panelTags.add(this.rdbtnCurrentName);

		this.rdbtnRandomName.setBounds(10, 465, 203, 23);
		this.panelTags.add(this.rdbtnRandomName);

		this.rdbtnOwnName.setBounds(10, 517, 21, 23);
		this.panelTags.add(this.rdbtnOwnName);

		this.rdbtnGroup.add(this.rdbtnCurrentName);
		this.rdbtnGroup.add(this.rdbtnOriginalName);
		this.rdbtnGroup.add(this.rdbtnRandomName);
		this.rdbtnGroup.add(this.rdbtnOwnName);

		this.textOwnName.setEnabled(false);
		this.textOwnName.setText("Set your own name");
		this.textOwnName.setBounds(37, 517, 176, 20);
		this.panelTags.add(this.textOwnName);
		this.textOwnName.setColumns(10);

		// Tags arrays
		int[] tags = DicomTags.tags;
		VR[] vr = DicomTags.vr;
		String[] info = DicomTags.info;
		// nazwy kolumn w tabeli
		String[] columnNames = { "\u2714", "No", "Tags", "VR", "Description" };
		Object[][] data = new Object[tags.length][5];
		for (int i = 0; i < tags.length; i++) {
			for (int j = 0; j < 5; j++) {
				if (j == 0)
					data[i][j] = new Boolean(false);
				if (j == 1)
					data[i][j] = i + 1;
				if (j == 2)
					data[i][j] = "(" + Integer.toHexString(tags[i]) + ")";
				if (j == 3)
					data[i][j] = vr[i];
				if (j == 4)
					data[i][j] = info[i];
			}
		}

		this.tableTags = new JTable(data, columnNames);
		this.scrollPaneTags.setViewportView(this.tableTags);
		this.tableTags.setShowVerticalLines(false);
		this.tableTags.setCellSelectionEnabled(false);
		this.tableTags.setColumnSelectionAllowed(false);
		this.tableTags.setRowSelectionAllowed(true);
		// selecting only one whole row
		this.tableTags.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// disable moving table headers
		this.tableTags.getTableHeader().setReorderingAllowed(false);
		this.tableTags.getTableHeader().setResizingAllowed(false);
		// disable editing cells
		this.tableTags.setDefaultEditor(Object.class, null);

		this.tableTags.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.tableTags.getColumnModel().getColumn(0).setPreferredWidth(25);
		this.tableTags.getColumnModel().getColumn(1).setPreferredWidth(25);
		this.tableTags.getColumnModel().getColumn(2).setPreferredWidth(100);
		this.tableTags.getColumnModel().getColumn(3).setPreferredWidth(25);
		this.tableTags.getColumnModel().getColumn(4).setPreferredWidth(380);
		// // making checkBox in the first column
		TableColumn tableColumn = tableTags.getColumnModel().getColumn(0);
		tableColumn.setCellEditor(tableTags.getDefaultEditor(Boolean.class));
		tableColumn.setCellRenderer(tableTags.getDefaultRenderer(Boolean.class));
		// alignment of subtitles
		{
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 1; i < 3; i++) {
				TableColumn tC = tableTags.getColumnModel().getColumn(i);
				tC.setCellRenderer(dtcr);
			}
		}
		// attribution to listeners
		this.btnAnon.addActionListener(this);
		this.btnDeanon.addActionListener(this);
		this.btnAdd.addActionListener(this);
		this.btnAddDir.addActionListener(this);
		this.btnDelete.addActionListener(this);
		this.btnChoose.addActionListener(this);
		this.btnBack1.addActionListener(this);
		this.btnNext.addActionListener(this);
		this.btnSelectAll.addActionListener(this);
		this.btnClearAll.addActionListener(this);
		this.btnBack2.addActionListener(this);
		this.btnDone.addActionListener(this);
		this.rdbtnOriginalName.addActionListener(this);
		this.rdbtnCurrentName.addActionListener(this);
		this.rdbtnOwnName.addActionListener(this);
		this.rdbtnRandomName.addActionListener(this);
		this.textOwnName.addKeyListener(this);

		// alignment frame
		this.setLocationRelativeTo(null);

	}

	/**
	 * function true - anonymize ; false - deanonymize
	 */
	private boolean function = true;
	/**
	 * whichName: 0 - error 1 - current name 2 - original name 3 - random name 4 -
	 * own name
	 */
	private byte whichName = 0;

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnAnon) {
			function = true;
			setVisibleAdditionalComponents(function);
			panelFiles.setVisible(true);
			panelMenu.setVisible(false);
		}

		if (e.getSource() == btnDeanon) {
			function = false;
			setVisibleAdditionalComponents(function);
			panelFiles.setVisible(true);
			panelMenu.setVisible(false);
		}

		if (e.getSource() == btnAdd) {
			FileChooser fileChooser = new FileChooser('f', function, db); // 'f' - files (dicom)
			// function = true -> files to anonimize
			// function = false -> anonimized files to deanonimize

			if (fileChooser.getDirectory() != null && fileChooser.getDirectory().length > 0) {
				String[] directory = fileChooser.getDirectory();
				for (int i = 0; i < directory.length; i++) {
					model.addElement(directory[i]);
				}
				btnDeleteAll.setEnabled(true);
			}
		}

		if (e.getSource() == btnAddDir) {
			FileChooser fileChooser = new FileChooser('d', function, db); // 'd' - dir (dicom)
			// function = true -> files to anonimize
			// function = false -> anonimized files to deanonimize
			// check if dir exists
			if (fileChooser.getDirectory() != null) {
				String[] directory = fileChooser.getDirectory();
				File folder = new File(directory[0]);
				File[] listOfFiles = folder.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".dcm");
					}
				});
				if (listOfFiles.length > 0) {
					for (int i = 0; i < listOfFiles.length; i++) {
						model.addElement(listOfFiles[i].toString());
					}
					btnDeleteAll.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null, "The directory does not contain DICOM files", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		if (e.getSource() == btnDelete) {
			if (listFiles.getSelectedIndex() != -1) {
				int[] indexes = listFiles.getSelectedIndices();
				// multiple-removing
				for (int i = indexes.length - 1; i >= 0; i--) {
					model.removeElementAt(indexes[i]);
				}
			}
			listFiles.clearSelection();
			btnDelete.setEnabled(false);
		}

		if (e.getSource() == btnDeleteAll) {
			model.removeAllElements();
			listFiles.clearSelection();
			btnDeleteAll.setEnabled(false);
			btnDelete.setEnabled(false);
		}

		if (e.getSource() == btnChoose) {
			FileChooser fileChooser = new FileChooser('d', function, db); // 'd' - directory
			if (fileChooser.getDirectory() != null) {
				String[] directory = fileChooser.getDirectory();
				textDestination.setText(directory[0]);
			}
		}

		if (e.getSource() == btnBack1) {
			panelFiles.setVisible(false);
			panelMenu.setVisible(true);
			this.model.clear();
			this.textDestination.setText(null);
		}

		if (e.getSource() == btnNext) {
			Path path = Paths.get(textDestination.getText().toString());

			if (Files.exists(path)) {
				if (model.getSize() > 0 && textDestination.getText().length() > 0) {
					panelTags.setVisible(true);
					panelFiles.setVisible(false);
				} else if (model.getSize() <= 0 && textDestination.getText().length() <= 0) {
					JOptionPane.showMessageDialog(btnNext, "Select destination directory and files.");
				} else if (model.getSize() > 0) {
					JOptionPane.showMessageDialog(btnNext, "Select destination directory.");
				} else {
					JOptionPane.showMessageDialog(btnNext, "Select files.");
				}
			} else {
				JOptionPane.showMessageDialog(btnNext, "Please select correct destination directory.");
			}
		}

		if (e.getSource() == btnSelectAll) {
			int count = tableTags.getRowCount();
			for (int i = 0; i < count; i++) {
				tableTags.setValueAt(true, i, 0);
			}
		}

		if (e.getSource() == btnClearAll) {
			int count = tableTags.getRowCount();
			for (int i = 0; i < count; i++) {
				tableTags.setValueAt(false, i, 0);
			}
		}

		if (e.getSource() == btnBack2) {
			panelFiles.setVisible(true);
			panelTags.setVisible(false);
		}

		if (e.getSource() == btnDone) {
			// tags count
			int count = tableTags.getRowCount();
			// which tags need changes
			Boolean[] bool = new Boolean[count];
			// save tags to change
			for (int i = 0; i < count; i++) {
				bool[i] = (boolean) tableTags.getValueAt(i, 0);
			}
			if (function) { // ANONIMIZATION
				// take date
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date();
				// forwarding subsequent files
				for (int i = 0; i < model.getSize(); i++) {
					System.out.println(dateFormat.format(date));
					// dicom file path
					String dicom = model.getElementAt(i).toString();
					// dicom file name (without .dcm)
					int ind = 0;
					for (int j = 0; j < dicom.length(); j++) {
						if (dicom.charAt(j) == '\\') {
							ind = j;
						}
					}
					String dicomOldName = dicom.substring((ind + 1), (dicom.length() - 4));
					// anonimization file.dcm
					new Anonymizer(model.getElementAt(i), dicomOldName, bool, textDestination.getText(), i,
							dateFormat.format(date), db);
				}

				// restore the application to the initial state
				resetApp();
				JOptionPane.showMessageDialog(null,
						"Zakoñczono anonimizacjê.\nPliki zosta³y zapisane w katalogu:\n\n" + textDestination.getText());
				// open folder with new file
				openFolder(textDestination.getText());
				// clean textBox
				this.textDestination.setText(null);

			} else if (!function && whichName > 0) { // DEANONIMIZATION
				// date
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date();
				// counter needed for JOptionPane info
				int incorrectFilesNumber = 0;
				// size of model with list of files
				int modelSize = model.getSize();
				// forwarding subsequent files
				for (int i = 0; i < modelSize; i++) {
					// deanonimization
					Deanonymizer deanon = new Deanonymizer(model.getElementAt(i), whichName,
							textOwnName.getText().toString(), bool, textDestination.getText(), dateFormat.format(date),
							i, db);
					// counting incorrect files (false id)
					if (deanon.getErrorFile() == true) {
						incorrectFilesNumber++;
					}
				}
				// restore the application to the initial state
				resetApp();
				// no irregularities
				if (incorrectFilesNumber == 0) {
					JOptionPane.showMessageDialog(null,
							"The deanonymization has been completed.\nFiles has beed saved in folder:\n\n"
									+ textDestination.getText());
					openFolder(textDestination.getText());
					// not all files are incorrect (false id)
				} else if (incorrectFilesNumber < modelSize) {

					JOptionPane.showMessageDialog(null, "Some incorrect files have been ignored", "Warning",
							JOptionPane.WARNING_MESSAGE);
					JOptionPane.showMessageDialog(null,
							"The deanonymization has been completed.\nCorrect files have been saved in folder:\n\n"
									+ textDestination.getText());
					openFolder(textDestination.getText());
					// all files are incorrect (false id)
				} else {
					JOptionPane.showMessageDialog(null, "All files are incorrect.", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				this.textDestination.setText(null);
			} else {
				JOptionPane.showMessageDialog(null, "Please choose a naming method.", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}

		// choice of naming method
		if (e.getSource() == this.rdbtnCurrentName) {
			this.textOwnName.setEnabled(false);
			this.whichName = 1;
		} else if (e.getSource() == this.rdbtnOriginalName) {
			this.textOwnName.setEnabled(false);
			this.whichName = 2;
		} else if (e.getSource() == this.rdbtnRandomName) {
			this.textOwnName.setEnabled(false);
			this.whichName = 3;
		} else if (e.getSource() == this.rdbtnOwnName) {
			this.textOwnName.setEnabled(true);
			this.textOwnName.requestFocus();
			this.textOwnName.selectAll();
			this.whichName = 4;
		}

	}

	/**
	 * Switch on Delete button
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == listFiles) {
			btnDelete.setEnabled(true);
			System.out.println("ValueChanged");
		}

	}

	/**
	 * Enables the input panel and formats components
	 */
	private void resetApp() {
		this.panelTags.setVisible(false);
		this.panelMenu.setVisible(true);
		this.model.clear();
		this.btnClearAll.doClick(0);
		// textDestination jest czyszczony osobno, po JOptionPane
		// this.textDestination.setText(null);
		this.btnDeleteAll.setEnabled(false);
		this.btnDelete.setEnabled(false);
	}

	/**
	 * // *Depending on the option choices (anonimization || deanonimization) turns
	 * on or off some components in the last panel
	 * 
	 * @param bool
	 * @return
	 */
	void setVisibleAdditionalComponents(boolean function) {
		function = !function;
		rdbtnCurrentName.setVisible(function);
		rdbtnOriginalName.setVisible(function);
		rdbtnOwnName.setVisible(function);
		rdbtnRandomName.setVisible(function);
		textOwnName.setVisible(function);

	}

	/**
	 * Open folder with new file
	 * 
	 * @param dest
	 */
	private void openFolder(String dest) {
		Desktop desktop = Desktop.getDesktop();
		File dirToOpen = null;
		try {
			dirToOpen = new File(dest);
			desktop.open(dirToOpen);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clipboard service
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		// Checking flavor on pasting data.
		System.out.println("Obs³uga wklejania.");
		if (arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_V) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			DataFlavor flavor = DataFlavor.stringFlavor;
			String schowek = ""; // empty String
			try {
				schowek = (String) clipboard.getData(flavor);
			} catch (UnsupportedFlavorException ex) {
				System.out.println("Wklejony zosta³ z³y format.");
			} catch (IOException ex) {
				System.out.println("B³¹d wejœcia/wyjœcia.");
			}
			// if String, check numbers/letters
			for (int i = 0; i < schowek.length(); i++) {
				if (arg0.getSource() == this.textOwnName) {
					if (!ifLegalSign(schowek.charAt(i))) {
						System.out.println("Wklejanie /, \\, :, *, ?, \", <, > oraz | jest na tym polu niedozwolone.");
						arg0.consume();
						break;
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	/**
	 * Checking the characters entered
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getSource() == this.textOwnName) {
			if (!ifLegalSign(arg0.getKeyChar())) {
				arg0.consume();
			}
		}
	}

	/**
	 * Checking for illegal characters
	 * 
	 * @param ch
	 *            character to check
	 * @return true or false
	 */
	boolean ifLegalSign(char ch) {
		char[] illegal = { '/', '\\', ':', '*', '?', '"', '<', '>', '|' };
		for (char i : illegal) {
			if (ch == i) {
				return false;
			}
		}
		return true;
	}
}
