package src.presentation;
import src.domain.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;


public class ImagesTest extends JPanel implements ActionListener, ImagesPlayerWatcher
{
  private static final int PWIDTH = 500;     // same size as background.jpg
  private static final int PHEIGHT = 400;
  private static final int GROUND = 320;     // y of the line the players stand on

  private static final int PERIOD = 40;      // ms between updates (25 per second)
  private static final int KICK_PAUSE = 25;  // updates to wait after a kick (1 sec)
  private static final int BALL_SWAP = 25;   // updates between ball images (1 sec)

  private ImagesLoader imsLoader;
  private ImagesPlayer kickPlayer, runPlayer;

  private BufferedImage background, scoreboard, goalLeft, goalRight;

  private int counter = 0;        // number of updates so far
  private int kickWait = 0;       // updates left before the kick restarts
  private boolean fireBall = false;

  private Font labelFont, scoreFont;
  private FontMetrics scoreMetrics;

  public ImagesTest()
  {
      setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

      imsLoader = new ImagesLoader("imsInfo.txt");
      initImages();

      labelFont = new Font("SansSerif", Font.BOLD, 11);
      scoreFont = new Font("SansSerif", Font.BOLD, 22);
      scoreMetrics = getFontMetrics(scoreFont);

      new Timer(PERIOD, this).start();   // start updating and repainting
  } // end of ImagesTest()


  private void initImages()
  // get the single images and create the players for the sequences
  {
      background = imsLoader.getImage("background");
      scoreboard = imsLoader.getImage("scoreboard");
      goalLeft = imsLoader.getImage("goal_left");
      goalRight = imsLoader.getImage("goal_right");

      // the kick plays once (false) in 0.6 secs; we are told when it ends
      kickPlayer = new ImagesPlayer("player1_kick", PERIOD, 0.6, false, imsLoader);
      kickPlayer.setWatcher(this);

      // the run repeats (true), a full cycle every 0.5 secs
      runPlayer = new ImagesPlayer("player2_run", PERIOD, 0.5, true, imsLoader);
  } // end of initImages()


  public void actionPerformed(ActionEvent e)
  // called by the Timer every PERIOD ms: update everything, then repaint
  {
      counter++;

      kickPlayer.updateTick();
      runPlayer.updateTick();

      if (kickWait > 0){         // waiting after a kick
          kickWait--;
          if (kickWait == 0){
              kickPlayer.restartAt(0);
          }
      }

      if (counter % BALL_SWAP == 0){
          fireBall = !fireBall;
      }

      repaint();
  } // end of actionPerformed()


  public void sequenceEnded(String imageName)
  // called by kickPlayer when the kick reaches its last image
  {
      System.out.println(imageName + " sequence has ended");
      kickWait = KICK_PAUSE;     // stay on the last image for a while
  }


  protected void paintComponent(Graphics g)
  {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // o: single images
      drawImage(g2d, background, 0, 0);
      drawImage(g2d, goalLeft, 0, GROUND - goalLeft.getHeight());
      drawImage(g2d, goalRight, PWIDTH - goalRight.getWidth(), GROUND - goalRight.getHeight());
      drawScoreboard(g2d);

      // s: the kick strip, played by kickPlayer
      BufferedImage kick = kickPlayer.getCurrentImage();
      drawImage(g2d, kick, 140, GROUND - kick.getHeight());
      label(g2d, "s player1_kick  frame " + kickPlayer.getCurrentPosition(), 110, GROUND + 18);

      // n: the numbered run images, played by runPlayer
      BufferedImage run = runPlayer.getCurrentImage();
      drawImage(g2d, run, 300, GROUND - run.getHeight());
      label(g2d, "n player2_run  image " + runPlayer.getCurrentPosition(), 275, GROUND + 18);

      // g: one image of the balls group, chosen by its file prefix
      BufferedImage ball = imsLoader.getImage("balls", fireBall ? "ball_fire" : "ball");
      drawImage(g2d, ball, 238, GROUND - ball.getHeight());
      label(g2d, "g balls: " + (fireBall ? "ball_fire" : "ball"), 205, GROUND + 38);

      // the whole strip, frame by frame, to compare with the animation
      label(g2d, "player1_kick frames:", 10, GROUND + 62);
      for (int i = 0; i < imsLoader.numImages("player1_kick"); i++){
          BufferedImage im = imsLoader.getImage("player1_kick", i);
          g2d.drawImage(im, 130 + i * 36, GROUND + 44, 32, 40, null);   // drawn at half size
      }

      label(g2d, "updates: " + counter, PWIDTH - 90, PHEIGHT - 8);
  } // end of paintComponent()


  private void drawImage(Graphics2D g2d, BufferedImage im, int x, int y)
  // draw im, or a red box if it could not be loaded
  {
      if (im == null){
          g2d.setColor(Color.red);
          g2d.drawRect(x, y, 20, 20);
      }
      else{
          g2d.drawImage(im, x, y, null);
      }
  }

  private void drawScoreboard(Graphics2D g2d)
  // the scoreboard image with a score centred in each half
  {
      int x = (PWIDTH - scoreboard.getWidth()) / 2;
      int y = 8;
      drawImage(g2d, scoreboard, x, y);

      int goals1 = counter / 50 % 10;     // fake scores that change over time
      int goals2 = counter / 75 % 10;
      int half = scoreboard.getWidth() / 2;
      int textY = y + (scoreboard.getHeight() - scoreMetrics.getHeight()) / 2
                    + scoreMetrics.getAscent();

      g2d.setFont(scoreFont);
      g2d.setColor(Color.white);
      centre(g2d, "" + goals1, x, half, textY);
      centre(g2d, "" + goals2, x + half, half, textY);
  }

  private void centre(Graphics2D g2d, String s, int x, int width, int y)
  // draw s centred horizontally in the area that starts at x
  {  g2d.drawString(s, x + (width - scoreMetrics.stringWidth(s)) / 2, y);  }

  private void label(Graphics2D g2d, String s, int x, int y)
  {
      g2d.setFont(labelFont);
      g2d.setColor(Color.white);
      g2d.drawString(s, x, y);
  }


  public static void main(String[] args)
  {
      ImagesTest ttPanel = new ImagesTest();

      JFrame app = new JFrame("Images Test");
      app.getContentPane().add(ttPanel, BorderLayout.CENTER);
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      app.pack();
      app.setResizable(false);
      app.setVisible(true);
  } // end of main()

} // end of ImagesTest class
