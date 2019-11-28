package server;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Font;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	protected ServerGUI guiReference = this;
	protected Server server;
	protected String IP ;
	protected JPanel contentPane;
	protected JTextArea textArea;
    protected JLabel lblIndirizzoIpServer;
    protected  JButton Button;
	
    
    public ServerGUI() throws UnknownHostException {
    	
    	setBounds(100, 100, 512, 497);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		InetAddress inetAddress = InetAddress.getLocalHost();
        String iA=inetAddress.getHostAddress();
		lblIndirizzoIpServer = new JLabel(iA);
		lblIndirizzoIpServer.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblIndirizzoIpServer.setBounds(22, 10, 172, 14);
		contentPane.add(lblIndirizzoIpServer);
		
		textArea = new JTextArea(160, 250);
		textArea.setBounds(20, 50, 470, 400);
		contentPane.add(textArea);
		textArea.setEditable(false);
		server  = new Server(guiReference);
    }
		
    public void Update(String msg) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				textArea.append(msg+"\n");
			}
	   });
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
    