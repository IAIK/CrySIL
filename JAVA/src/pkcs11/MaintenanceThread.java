package pkcs11;

public class MaintenanceThread extends Thread implements Runnable {

	private boolean run = true;
	
	@Override
	public void start(){
		run = true;
		this.setName("MaintenanceThread");
		super.start();
	}

	@Override
	public void run() {
		while (run) {
			doMaintenance();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.yield();
			}
		}
	}

	public void stopMaintenanceThread() {
		run = false;
	}
	
	
	public void doMaintenance(){
		JAVApkcs11Interface.tick();
		
	}

}
