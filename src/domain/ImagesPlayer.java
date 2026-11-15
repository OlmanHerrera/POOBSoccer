package src.domain;
import java.awt.image.BufferedImage;

public class ImagesPlayer
{
  private String imName;          // name of the images in ImagesLoader
  private boolean isRepeating;    // start again after the last image?
  private boolean ticking;        // is the animation running?
  private ImagesLoader imsLoader;

  private int animPeriod;         // game loop period, in ms
  private long animTotalTime;     // animation time elapsed, in ms

  private int showPeriod;         // how long each image is shown, in ms
  private double seqDuration;     // total time of the sequence, in secs

  private int numImages;
  private int imPosition;         // position of the current image

  private ImagesPlayerWatcher watcher = null;

  public ImagesPlayer(String nm, int ap, double d, boolean isr, ImagesLoader il)
  /* nm: name of the images, ap: game loop period in ms,
     d: duration of the whole sequence in seconds,
     isr: repeat the sequence, il: the loader with the images */
  {
      imName = nm;
      animPeriod = ap;
      seqDuration = d;
      isRepeating = isr;
      imsLoader = il;

      animTotalTime = 0L;

      if (seqDuration < 0.5){
          System.out.println("Warning: minimum sequence duration is 0.5 sec.");
          seqDuration = 0.5;
      }

      if (!imsLoader.isLoaded(imName)){
          System.out.println(imName + " is not known by ImagesLoader");
          numImages = 0;
          imPosition = -1;
          ticking = false;
      }
      else{
          numImages = imsLoader.numImages(imName);
          imPosition = 0;
          ticking = true;
          showPeriod = (int) (1000 * seqDuration / numImages);
      }
  } // end of ImagesPlayer()


  public void updateTick()
  // called by the game loop once per update
  {
      if (!ticking){
          return;
      }

      long seqTime = (long) (1000 * seqDuration);   // in ms
      animTotalTime += animPeriod;

      if (animTotalTime >= seqTime){      // reached the end of the sequence
          if (isRepeating){
              animTotalTime %= seqTime;   // wrap around to the start
          }
          else{                           // stay on the last image
              animTotalTime = seqTime - 1;
              imPosition = numImages - 1;
              ticking = false;
              if (watcher != null){
                  watcher.sequenceEnded(imName);
              }
              return;
          }
      }

      imPosition = (int) (animTotalTime / showPeriod);
      if (imPosition >= numImages){       // rounding in showPeriod
          imPosition = numImages - 1;
      }
  } // end of updateTick()


  public BufferedImage getCurrentImage()
  {
      if (numImages != 0){
          return imsLoader.getImage(imName, imPosition);
      }
      return null;
  }

  public int getCurrentPosition()
  {  return imPosition;  }

  public void setWatcher(ImagesPlayerWatcher w)
  {  watcher = w;  }

  public void stop()
  // stop at the current image
  {  ticking = false;  }

  public boolean isStopped()
  {  return !ticking;  }

  public boolean atSequenceEnd()
  // only true for a non-repeating sequence that has finished
  {  return (imPosition == numImages - 1) && !isRepeating && !ticking;  }

  public void restartAt(int imPosn)
  // start showing the images again from position imPosn
  {
      if (numImages != 0){
          if ((imPosn < 0) || (imPosn > numImages - 1)){
              System.out.println("Out of range restart, starting at 0");
              imPosn = 0;
          }
          imPosition = imPosn;
          animTotalTime = (long) imPosition * showPeriod;   // so updateTick() continues from there
          ticking = true;
      }
  }

  public void resume()
  // continue from the current image
  {
      if (numImages != 0){
          ticking = true;
      }
  }
} // end of ImagesPlayer class
