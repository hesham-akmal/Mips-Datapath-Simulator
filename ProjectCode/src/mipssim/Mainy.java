package mipssim;

public class Mainy {

	public static Registerss Registers = new Registerss();
	public static Frame frame1 = new Frame();
	public static boolean start = false;
	public static boolean RunONEline = false;
	public static int Speed = 1000;

	public static void main(String[] args) {

		frame1.setVisible(true);

		Thread thread1 = new Thread() {
			public void run() {

				while (true) {

					try {
						Thread.sleep(Speed + 50); // pause for x milliseconds
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}

					// Register values update
					frame1.updateRegPnls();
					// Memory contents update
					Programy.prtMemWo0sTXT();

				}
			}
		};
		Thread thread2 = new Thread() {
			public void run() {

				Registers.returnReg("$sp").value = 4000; // set $sp to
															// first place
															// in memory

				while (true) {

					try {// every 1 second, Run a line of code
						Thread.sleep(Speed + 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (start) { // when pressed run, start = true
						Programy.RunALine();
					}
					if (RunONEline) {
						Programy.RunALine();
						RunONEline = false;
					}

				}
			}
		};

		thread1.start();
		thread2.start();

	}

}
