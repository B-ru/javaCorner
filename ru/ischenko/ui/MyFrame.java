package ru.ischenko.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class MyFrame  extends JFrame{
	private final static String DEFAULT_COMPRESSION	= "1";
	private final static String STATUS_IDLE 		= "Ready";
	private final static String STATUS_DONE 		= "Backup finished";
	private final static String STATUS_ERROR 		= "!Error ";
	private final static String STATUS_WORKING 		= "Backup in progress...";
	private 			 String scriptPath 			= "";
	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public void loadScriptPath() throws FileNotFoundException, IOException {
		String scrPattern = "^script=(.*)$";
		Pattern pattern = Pattern.compile(scrPattern);
		try (BufferedReader r = new BufferedReader ( new FileReader("Backup_FE.conf") )) {
			r.lines().forEach(line -> {
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						setScriptPath(matcher.group(1));
					}
				}
			);
		}
	}
	private static final long serialVersionUID = 2201590917822874574L;
	private JLabel			gLabel1, gLabel2, gLabel3, gStatusLabel;
	private JTextField		gObject, gDestination, gCompression;
	private JButton			gGo, gQuit;
	private JPanel			gStatusPan;
	private JProgressBar	gProgBar;
	private JFileChooser 	fc = new JFileChooser();
	///////////////////////////////
	public void switchControls(boolean value) {
		gObject		.setEnabled(value);
		gDestination.setEnabled(value);
		gCompression.setEnabled(value);
		gGo			.setEnabled(value);
		gQuit		.setEnabled(value);		
	}
	public void switchStatus(boolean value) {
		gStatusLabel.setText(!value?STATUS_IDLE:STATUS_WORKING);
	}
	public void setStatus(String string) {
		gStatusLabel.setText(string);
	}
	///////////////////////////////
	SwingWorker<Integer,Void> worker = new SwingWorker<Integer, Void>(){
		@Override
		public Integer doInBackground() throws Exception {
			// TODO: add pre-launch check for all parameters
			Process p = null;
			ProcessBuilder pB = new ProcessBuilder(getScriptPath(), gCompression.getText(), gObject.getText()+"/", gDestination.getText()+"/");
			try {
				p = pB.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			};
			while (p.isAlive()) {
				TimeUnit.MILLISECONDS.sleep(500);
			}
			if (p.exitValue() == 0) setStatus(STATUS_DONE);
			else setStatus(STATUS_ERROR+p.exitValue());
			switchControls(true);
			gProgBar.setIndeterminate(false);
			return p.exitValue();
		}
	};
	///////////////////////////////
		
	public MyFrame() {
		super		("Backup frontend");
		try {
			this.loadScriptPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this		.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this		.setLayout(null);
		this		.setSize(400, 140);
		this		.setResizable(false);
		///////////////////////////////
		gLabel1 	= new JLabel("Object:");
		gLabel2 	= new JLabel("Destination:");
		gLabel3 	= new JLabel("Compression:");
		gStatusLabel= new JLabel(STATUS_IDLE);
		gObject		= new JTextField();
		gDestination= new JTextField();
		gCompression= new JTextField();
		gStatusPan 	= new JPanel();
		gProgBar	= new JProgressBar();
		gGo			= new JButton("Backup");
		gQuit		= new JButton("Quit");
		///////////////////////////////
		gLabel1		.setBounds(  5,  0, 370, 15);
		gLabel2		.setBounds(  5, 30, 200, 20);
		gLabel3		.setBounds(280, 30, 100, 20);
		gStatusLabel.setBounds(	 5,  3, 250, 15);
		gObject		.setBounds( 15, 16, 360, 16);
		gDestination.setBounds( 15, 47, 338, 16);
		gCompression.setBounds(354, 47,  22, 16);
		gStatusPan	.setBounds(-1, this.getHeight()-50, this.getWidth(), 20);
		gProgBar	.setBounds(251,  2, 139, 18);
		gGo			.setBounds( 15, 70, 100, 15);
		gQuit		.setBounds(276,	70,	100, 15);
		///////////////////////////////
		gObject		.setText(null);
		gDestination.setText(null);
		gCompression.setText(DEFAULT_COMPRESSION);
		gStatusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gProgBar	.setVisible(true);
		gProgBar	.setIndeterminate(false);
		gProgBar	.setBorder(null);
		gStatusPan	.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		fc			.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc			.setDialogTitle("Select directory");
		fc			.setFileFilter(null);
		///////////////////////////////
		gStatusPan	.setLayout(null);
		gStatusPan	.add(gStatusLabel);
		gStatusPan	.add(gProgBar);
		///////////////////////////////
		//action handling VVV//////////		
		///////////////////////////////
		gObject		.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					gStatusLabel.setText("Select directory need to backup");
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						gObject.setText(fc.getSelectedFile().toString());
					}
					e.consume();
					switchStatus(false);
				}		
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		///////////////////////////////		
		gDestination.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					gStatusLabel.setText("Select directory backup to");
					int returnVal = fc.showSaveDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						gDestination.setText(fc.getSelectedFile().toString());
					}
					e.consume();
					switchStatus(false);
				}		
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		///////////////////////////////
		gGo			.addActionListener(new ActionListener(){
	     	public void actionPerformed (ActionEvent ae) {
				gProgBar.setIndeterminate(true);
				switchStatus(true);
				switchControls(false);
				worker.execute();
	     	}		
	    });
		///////////////////////////////
		gQuit	.addActionListener(event -> System.exit(0));
		///////////////////////////////
		this	.add(gStatusPan);
		this	.add(gLabel1);
		this	.add(gLabel2);
		this	.add(gLabel3);
		this	.add(gObject);
		this	.add(gDestination);
		this	.add(gCompression);
		this	.add(gGo);
		this	.add(gQuit);		
		this	.setVisible(true);
	}
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MyFrame f = new MyFrame();
	}
}

