package src.presentation;
import src.domain.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;


public class ImagesTest extends JPanel implements ActionListener, ImagesPlayerWatcher
{
  private static final int PWIDTH = 500;
  private static final int PHEIGHT = 400;
  private static final int GROUND = 320;

  private static final int PERIOD = 40;
  private static final int KICK_PAUSE = 25;
  private static final int BALL_SWAP = 25;

  private ImagesLoader imsLoader;
  private ImagesPlayer kickPlayer, runPlayer;

  private BufferedImage background, scoreboard, goalLeft, goalRight;

  private int counter = 0;
  private int kickWait = 0;
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

      new Timer(PERIOD, this).start();
  }


  private void initImages()
  {
      background = imsLoader.getImage("background");
      scoreboard = imsLoader.getImage("scoreboard");
      goalLeft = imsLoader.getImage("goal_left");
      goalRight = imsLoader.getImage("goal_right");

      kickPlayer = new ImagesPlayer("player1_kick", PERIOD, 0.6, false, imsLoader);
      kickPlayer.setWatcher(this);

      runPlayer = new ImagesPlayer("player2_run", PERIOD, 0.5, true, imsLoader);
  }


  public void actionPerformed(ActionEvent e)
  {
      counter++;

      kickPlayer.updateTick();
      runPlayer.updateTick();

      if (kickWait > 0){
          kickWait--;
          if (kickWait == 0){
              kickPlayer.restartAt(0);
          }
      }

      if (counter % BALL_SWAP == 0){
          fireBall = !fireBall;
      }

      repaint();
  }


  public void sequenceEnded(String imageName)
  {
      System.out.println(imageName + " sequence has ended");
      kickWait = KICK_PAUSE;
  }


  protected void paintComponent(Graphics g)
  {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      drawImage(g2d, background, 0, 0);
      drawImage(g2d, goalLeft, 0, GROUND - goalLeft.getHeight());
      drawImage(g2d, goalRight, PWIDTH - goalRight.getWidth(), GROUND - goalRight.getHeight());
      drawScoreboard(g2d);

      BufferedImage kick = kickPlayer.getCurrentImage();
      drawImage(g2d, kick, 140, GROUND - kick.getHeight());
      label(g2d, "s player1_kick  frame " + kickPlayer.getCurrentPosition(), 110, GROUND + 18);

      BufferedImage run = runPlayer.getCurrentImage();
      drawImage(g2d, run, 300, GROUND - run.getHeight());
      label(g2d, "n player2_run  image " + runPlayer.getCurrentPosition(), 275, GROUND + 18);

      BufferedImage ball = imsLoader.getImage("balls", fireBall ? "ball_fire" : "ball");
      drawImage(g2d, ball, 238, GROUND - ball.getHeight());
      label(g2d, "g balls: " + (fireBall ? "ball_fire" : "ball"), 205, GROUND + 38);

      label(g2d, "player1_kick frames:", 10, GROUND + 62);
      for (int i = 0; i < imsLoader.numImages("player1_kick"); i++){
          BufferedImage im = imsLoader.getImage("player1_kick", i);
          g2d.drawImage(im, 130 + i * 36, GROUND + 44, 32, 40, null);
      }

      label(g2d, "updates: " + counter, PWIDTH - 90, PHEIGHT - 8);
  }


  private void drawImage(Graphics2D g2d, BufferedImage im, int x, int y)
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
  {
      int x = (PWIDTH - scoreboard.getWidth()) / 2;
      int y = 8;
      drawImage(g2d, scoreboard, x, y);

      int goals1 = counter / 50 % 10;
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
  }

}
