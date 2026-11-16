package src.domain;
import java.awt.image.BufferedImage;

public class ImagesPlayer
{
  private String imName;
  private boolean isRepeating;
  private boolean ticking;
  private ImagesLoader imsLoader;

  private int animPeriod;
  private long animTotalTime;

  private int showPeriod;
  private double seqDuration;

  private int numImages;
  private int imPosition;

  private ImagesPlayerWatcher watcher = null;

  public ImagesPlayer(String nm, int ap, double d, boolean isr, ImagesLoader il)
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
  }


  public void updateTick()
  {
      if (!ticking){
          return;
      }

      long seqTime = (long) (1000 * seqDuration);
      animTotalTime += animPeriod;

      if (animTotalTime >= seqTime){
          if (isRepeating){
              animTotalTime %= seqTime;
          }
          else{
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
      if (imPosition >= numImages){
          imPosition = numImages - 1;
      }
  }


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
  {  ticking = false;  }

  public boolean isStopped()
  {  return !ticking;  }

  public boolean atSequenceEnd()
  {  return (imPosition == numImages - 1) && !isRepeating && !ticking;  }

  public void restartAt(int imPosn)
  {
      if (numImages != 0){
          if ((imPosn < 0) || (imPosn > numImages - 1)){
              System.out.println("Out of range restart, starting at 0");
              imPosn = 0;
          }
          imPosition = imPosn;
          animTotalTime = (long) imPosition * showPeriod;
          ticking = true;
      }
  }

  public void resume()
  {
      if (numImages != 0){
          ticking = true;
      }
  }
}
