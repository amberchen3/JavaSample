import java.awt.EventQueue;


public class Main {
	
	public static void main(String[] args) {
		new Server().run();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Client();
			}
		});
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Client();
			}
		});

	}

}
