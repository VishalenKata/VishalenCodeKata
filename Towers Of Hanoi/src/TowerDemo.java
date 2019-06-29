import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class TowerDemo extends JPanel implements Runnable, ActionListener{
	public static void main(String[] args) {
	      JFrame window = new JFrame("Towers Of Hanoi");
	      window.setContentPane(new TowerDemo());
	      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      window.pack();
	      window.setResizable(true);
	      window.setLocation(300,200);
	      window.setVisible(true);
	   }
	   
	   private static Color bgColor = new Color(255,255,255); 
	   private static Color bdColor = new Color(0,0,0);
	   private static Color diskColor = new Color(102,102,102);
	   private static Color mvmntColor = new Color(180,180,255);

	   private BufferedImage screen;   

	   private int status;   
	   
	   private static final int GO = 0;        
	   private static final int PAUSE = 1;    
	   private static final int STEP = 2;     
	   private static final int RESTART = 3; 

	   private int[][] towers;
	   private int[] towerSize;
	   private int moveDisks;
	   private int moveTowers;

	   private Display dsp;  

	   private JButton runBtn;  
	   private JButton nxtBtn;
	   private JButton startBtn;

	   private class Display extends JPanel {
	      protected void paintComponent(Graphics g) {
	         super.paintComponent(g);
	         int x = (getWidth() - screen.getWidth())/2;
	         int y = (getHeight() - screen.getHeight())/2;
	         g.drawImage(screen, x, y, null);
	      }
	   }

	   
	   public TowerDemo () {
	      screen = new BufferedImage(430,143,BufferedImage.TYPE_INT_RGB);
	      dsp = new Display();
	      dsp.setPreferredSize(new Dimension(430,143));
	      dsp.setBorder(BorderFactory.createLineBorder(bdColor, 2));
	      dsp.setBackground(bgColor);
	      setLayout(new BorderLayout());
	      add(dsp, BorderLayout.CENTER);
	      JPanel btnBar = new JPanel();
	      add(btnBar, BorderLayout.SOUTH);
	      btnBar.setLayout(new GridLayout(1,0));
	      runBtn = new JButton("Run");
	      runBtn.addActionListener(this);
	      btnBar.add(runBtn);
	      nxtBtn = new JButton("Next Step");
	      nxtBtn.addActionListener(this);
	      btnBar.add(nxtBtn);
	      startBtn = new JButton("Restart");
	      startBtn.addActionListener(this);
	      startBtn.setEnabled(false);
	      btnBar.add(startBtn);
	      new Thread(this).start();
	   }

	   
	   synchronized public void actionPerformed(ActionEvent evt) {
	      Object source = evt.getSource();
	      if (source == runBtn) {  
	         if (status == GO) {  
	            status = PAUSE;
	            nxtBtn.setEnabled(true);
	            runBtn.setText("Run");
	         }
	         else {  
	            status = GO;
	            nxtBtn.setEnabled(false);  
	            runBtn.setText("Pause");
	         }
	      }
	      else if (source == nxtBtn) {  
	         status = STEP;
	      }
	      else if (source == startBtn) { 
	         status = RESTART;
	      }
	      notify(); 
	   }


	   public void run() {
	      while (true) {
	         runBtn.setText("Run");
	         nxtBtn.setEnabled(true);
	         startBtn.setEnabled(false);
	         problem();  
	         status = PAUSE;
	         statusChecker(); 
	         startBtn.setEnabled(true);
	         try {
	            solveProblem(10,0,1,2);  
	         }
	         catch (IllegalStateException e) {
	               
	         }         
	      }
	   }

	   
	   synchronized private void statusChecker() {
	      while (status == PAUSE) {
	         try {
	            wait();
	         }
	         catch (InterruptedException e) {
	         }
	      }
	      
	      if (status == RESTART)
	         throw new IllegalStateException("Restart");
	      
	   }
	   
	   
	   synchronized private void problem() {
	      moveDisks= 0;
	      towers = new int[3][10];
	      for (int i = 0; i < 10; i++)
	         towers[0][i] = 10 - i;
	      towerSize = new int[3];
	      towerSize[0] = 10;
	      if (screen != null) {
	         Graphics g = screen.getGraphics();
	         drawDisplay(g);
	         g.dispose();
	      }
	      dsp.repaint();
	   }
	   
	   private void solveProblem(int disks, int from, int to, int spare) {
	      if (disks == 1)
	         movement(from,to);
	      else {
	         solveProblem(disks-1, from, spare, to);
	         movement(from,to);
	         solveProblem(disks-1, spare, to, from);
	      }
	   }


	   synchronized private void movement(int fromStack, int toStack) {
	      moveDisks = towers[fromStack][towerSize[fromStack]-1];
	      moveTowers = fromStack;
	      movementDelay(120);
	      towerSize[fromStack]--;
	      placeDisk(mvmntColor,moveDisks,moveTowers);
	      movementDelay(80);
	      placeDisk(bgColor,moveDisks,moveTowers);
	      movementDelay(80);
	      moveTowers = toStack;
	      placeDisk(mvmntColor,moveDisks,moveTowers);
	      movementDelay(80);
	      placeDisk(diskColor,moveDisks,moveTowers);
	      towers[toStack][towerSize[toStack]] = moveDisks;
	      towerSize[toStack]++;
	      moveDisks = 0;
	      if (status == STEP)
	         status = PAUSE;
	      statusChecker();
	   }

	   
	   synchronized private void movementDelay(int milliseconds) {
	      try {
	         wait(milliseconds);
	      }
	      catch (InterruptedException e) {
	      }
	   }

	   
	   private void placeDisk(Color color, int disk, int t) {
	      Graphics g = screen.getGraphics();
	      g.setColor(color);
	      g.fillRoundRect(75+140*t - 5*disk - 5, 116-12*towerSize[t], 10*disk+10, 10, 10, 10);
	      g.dispose();
	      dsp.repaint();
	   }


	   synchronized private void drawDisplay(Graphics g) {
	      g.setColor(bgColor);
	      g.fillRect(0,0,430,143);
	      g.setColor(bdColor);
	      if (towers == null)
	         return;
	      g.fillRect(10,128,130,5);
	      g.fillRect(150,128,130,5);
	      g.fillRect(290,128,130,5);
	      g.setColor(diskColor);
	      for (int t = 0; t < 3; t++) {
	         for (int i = 0; i < towerSize[t]; i++) {
	            int disk = towers[t][i];
	            g.fillRoundRect(75+140*t - 5*disk - 5, 116-12*i, 10*disk+10, 10, 10, 10);
	         }
	      }
	      if (moveDisks > 0) {
	         g.setColor(mvmntColor);
	         g.fillRoundRect(75+140*moveTowers - 5*moveDisks - 5, 116-12*towerSize[moveTowers], 
	               10*moveDisks+10, 10, 10, 10);
	      }
	   }   
}

