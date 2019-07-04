package hpc_catpcha;

import java.util.List;

import javax.swing.JProgressBar;

public class pbHandler extends Thread{
	private Boolean life;
	private float prog;
	private javax.swing.JProgressBar pb;
	
	public pbHandler(Object in) {
		this.pb = (JProgressBar) in;
		this.life = true;
		this.prog = 0;
	}
	
	public void run() {
		while(life) {
			List<String> total = HpcAttack.getWordList();
			Integer progress = HpcAttack.getCurrentWord();
			if(total != null && !total.isEmpty()) {
				prog = progress * 100 / total.size();
			}
			pb.setValue((int) prog);
			pb.repaint();
			try {
				Thread.sleep(125);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(prog == 100 || !HpcAttack.isFound()) {
				life = false;
			}
		}
	}
}
