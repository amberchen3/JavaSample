import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame {
	private JTextArea chatBox;
	private Scanner scan;
	private PrintWriter toClient;

	Client() {
		int width = 500;
		int height = 500;
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat Room");
		setLocation(300, 200);
		setVisible(true);

		// initial
		JPanel connectArea = new JPanel();
		add(connectArea, BorderLayout.NORTH);
		connectArea.setLayout(new GridLayout(2, 3));
		final JTextField nameText = new JTextField("monster"
				+ new Random().nextInt(100));
		connectArea.add(nameText);
		connectArea.add(new JPanel());
		connectArea.add(new JPanel());

		// client
		final JTextField serverAddress = new JTextField("localhost");
		connectArea.add(serverAddress);
		final JTextField serverPort = new JTextField("13042");
		connectArea.add(serverPort);
		JButton asClient = new JButton("As Client");
		connectArea.add(asClient);
		asClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String host = serverAddress.getText();
				int port = Integer.parseInt(serverPort.getText());
				try {
					Socket socket = new Socket(host, port);
					scan = new Scanner(socket.getInputStream());
					toClient = new PrintWriter(socket.getOutputStream());
					new Thread() {
						public void run() {
							while (scan.hasNext()) {
								String message = scan.nextLine();
								chatBox.append(message + "\n");
							}
						}
					}.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// chat box
		chatBox = new JTextArea();
		add(chatBox, BorderLayout.CENTER);

		// message
		JPanel messageArea = new JPanel();
		add(messageArea, BorderLayout.SOUTH);
		messageArea.setLayout(new GridLayout(1, 3));
		final JTextField message = new JTextField("...");
		messageArea.add(message);
		JButton send = new JButton("Send");
		messageArea.add(send);
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toClient == null){
					chatBox.append("have not initialed connection yet, click \"As Client\"\n");
					return;
				}
				toClient.println(nameText.getText() + ": " + message.getText());
				toClient.flush();
			}
		});
	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Client();
			}
		});
	}

}
