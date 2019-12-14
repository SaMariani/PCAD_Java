package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;



import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import interfacce.ClientInt;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	protected ClientGUI guiReference = this;
	protected ClientInt client;
	protected String IP ;
	protected JPanel contentPane;
	protected JLabel messageField;
	protected JTextField IpField;
    	protected JLabel lblIndirizzoIpServer;
    	protected JButton IpButton;
    	protected JLabel lblsearch; //scritta "RICERCA"
    	protected JButton srcButton; //bottone per cercare
    	protected JTextField srcField; //casella per inserire testo da cercare
    	protected JLabel lblLocation; //scritta "POSIZIONE"
    	protected JButton locationButton; //bottone per inserire la location
    	protected JTextField locationField; //casella testo posizione
    	protected JTextArea textArea; //area di testo sulla quale stampare 
    	protected JScrollPane scrollPane; //scroll
    	protected JButton mswButton; //bottone per stampa parole più frequenti
    	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ClientGUI() {
	
		setBounds(100, 100, 512, 497);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblIndirizzoIpServer = new JLabel("INDIRIZZO IP SERVER");
		lblIndirizzoIpServer.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblIndirizzoIpServer.setBounds(22, 10, 172, 14);
		contentPane.add(lblIndirizzoIpServer);
		
		IpField = new JTextField();
		IpField.setBounds(48, 30, 146, 20);
		contentPane.add(IpField);
		IpField.setColumns(10);
		IpField.setText("localhost");
		IpButton = new JButton("Inserisci");
		IpButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent arg0) {
				String ip = IpField.getText();
				IP = ip ;
				client = new Client(guiReference ,IP);
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						JOptionPane.showMessageDialog (guiReference,"INDIRIZZO IP INSERITO");
	                }
	                         
				});
           }
		});
		IpButton.setBounds(220, 30, 95, 22);
		contentPane.add(IpButton);
		
		messageField = new JLabel("");
		messageField.setBounds(48, 50, 400, 45);
		contentPane.add(messageField);
				
		JLabel textLabel = new JLabel("Testo:");
		textLabel.setBounds(48, 170, 106, 16);
		contentPane.add(textLabel);
				
		//creazione casella per inserire il testo
		srcField = new JTextField();
		srcField.setBounds(48, 190, 100, 25);
		contentPane.add(srcField);
		//srcField.setColumns(10);
				
		//creazione bottone di ricerca
		srcButton = new JButton("Cerca");
		srcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkIp()) return;
				String words = srcField.getText();
				String loc = locationField.getText();
				(new MyWorker(guiReference, "research", client,loc,words)).execute();
			}
		});
		srcButton.setBounds(60, 230, 75, 25);
		contentPane.add(srcButton);
				
		//creazione label posizione
		lblLocation = new JLabel("RICERCA");
		lblLocation.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblLocation.setBounds(22, 100, 172, 14);
		contentPane.add(lblLocation);
						
		JLabel locLabel = new JLabel("Location:");
		locLabel.setBounds(48, 120, 106, 16);
		contentPane.add(locLabel);
				
		//creazione casella per inserire il testo
		locationField = new JTextField();
		locationField.setBounds(48, 140, 100, 25);
		contentPane.add(locationField);
		
		//creazione bottone per stampa parole più frequenti
		mswButton = new JButton("Parole più cercate");
		mswButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkIp()) return;
				(new MyWorker(guiReference, "mostSearchedW", client,null,null)).execute();
			}
		});
		mswButton.setBounds(250, 165, 200, 25);
		contentPane.add(mswButton);
				
		//creazione area di testo con scrollbar
		textArea = new JTextArea(160, 250);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(20, 300, 470, 150);
		contentPane.add(scrollPane);
		setLocationRelativeTo(null);
				
	}
			
	public boolean Update(String msg) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				textArea.append(msg+"\n");
			}
		});
		return true;
	}
	
	private boolean checkIp(){
		if (IP == null || IP.equals("")){
			JOptionPane.showMessageDialog (guiReference,"INDIRIZZO IP MANCANTE");
			return false;
		}
		return true;
	}
		
}