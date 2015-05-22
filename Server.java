import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
	private final int NTHERADS = 100;
	private Executor exec = Executors.newFixedThreadPool(NTHERADS);
	private LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<String>();
	private LinkedList<PrintWriter> clients = new LinkedList<PrintWriter>();
	private Object lock = new Object();

	public void run() {
		new Thread() {
			public void run() {
				handleMessages();
			}
		}.start();
		
		new Thread(){
			public void run(){
				try {
					ServerSocket socket = new ServerSocket(13042);
					while (true) {
						final Socket connection = socket.accept();
						synchronized (lock) {
							PrintWriter toClient = new PrintWriter(
									connection.getOutputStream());
							clients.add(toClient);
						}
						Runnable task = new Runnable() {
							public void run() {
								hadleRequest(connection);
							}
						};
						exec.execute(task);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void handleMessages() {
		try {
			while (true) {
				String message = messages.take();
				synchronized (lock) {
					for (PrintWriter p : clients) {
						p.println(message);
						p.flush();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void hadleRequest(Socket connection) {
		try {
			Scanner scan = new Scanner(connection.getInputStream());
			while (scan.hasNext()) {
				String line = scan.nextLine();
				messages.put(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Server().run();
	}

}
