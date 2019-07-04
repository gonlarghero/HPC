package hpc_catpcha;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.UIManager;
import javax.swing.JProgressBar;

public class window {

	private JFrame frame;
	private JTextField textField;
	private JProgressBar progressBar;
	JButton startButton;
	private pbHandler pbHnd;
		
	public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window window = new window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Controller c = Controller.getInstance();
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnRefreshButton = new JButton("");
		btnRefreshButton.setBackground(UIManager.getColor("Button.background"));
		Image refreshIco = new ImageIcon(this.getClass().getResource("/Refresh-icon.png")).getImage();
		Image newimg = refreshIco.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ; 
		btnRefreshButton.setIcon(new ImageIcon(newimg));
		btnRefreshButton.setBounds(33, 106, 25, 25);
		frame.getContentPane().add(btnRefreshButton);
		
		JLabel label = new JLabel("");
		label.setBackground(Color.WHITE);
		label.setBounds(33, 23, 196, 112);
		frame.getContentPane().add(label);
		
		btnRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				label.setIcon(new ImageIcon(c.getRandomCaptcha()));
			}
		});
		
		textField = new JTextField();
		textField.setBounds(32, 146, 118, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
				
		JButton buttonGenerate = new JButton("Generar");
		buttonGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textField.getText()!= null && textField.getText().trim() != "") {
					label.setIcon(new ImageIcon(c.getCaptchaFromString(textField.getText()))); 
				}
			}
		});
		buttonGenerate.setBounds(156, 145, 89, 23);
		frame.getContentPane().add(buttonGenerate);
		
		progressBar = new JProgressBar();		
		progressBar.setBounds(33, 224, 212, 14);
		frame.getContentPane().add(progressBar);
		
		
		startButton = new JButton("ATACAR");
		startButton.setBackground(new Color(250, 128, 114));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = null;			

				startButton.setEnabled(false);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        
				pbHnd = new pbHandler(progressBar);
				pbHnd.start();			
				try {
					result = c.attack(1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Toolkit.getDefaultToolkit().beep();
	            startButton.setEnabled(true);
	            frame.setCursor(null);
				JOptionPane.showMessageDialog(null, result, "Resultado", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		startButton.setBounds(33, 177, 97, 36);
		frame.getContentPane().add(startButton);
		
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}
