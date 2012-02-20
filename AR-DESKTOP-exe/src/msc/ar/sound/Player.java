/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ar.sound;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mamatha
 */
public class Player implements Runnable{
    InputStream waveStream;
    int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb      
    AudioInputStream audioInputStream = null;
    
    public Player(){
        
    }
    
    public static void main(String args[]){
      // Player.play("src\\models\\test.wav");
      //Player.play("C:\\Users\\mamatha\\Documents\\NetBeansProjects\\AR-DESKTOP\\src\\models\\piano.au");
      //new Player().playwav("C:\\Users\\mamatha\\Documents\\NetBeansProjects\\AR-DESKTOP\\src\\models\\piano.wav");
    }
    
    public void loadSoundfile(String fname){
        String filename="C:\\Users\\mamatha\\Documents\\NetBeansProjects\\AR-DESKTOP\\src\\models\\piano.wav";
        
        try {
             waveStream =new FileInputStream(filename);
	     audioInputStream = AudioSystem.getAudioInputStream(waveStream);
	} catch (UnsupportedAudioFileException e1) {
	 e1.printStackTrace();
	} catch (IOException e2) {
	    e2.printStackTrace();
	}
    }
    
    public void run(){
        
        
        
        AudioFormat audioFormat = audioInputStream.getFormat();
	Info info = new Info(SourceDataLine.class, audioFormat);
 
	// opens the audio channel
	SourceDataLine dataLine = null;
	try {
	    dataLine = (SourceDataLine) AudioSystem.getLine(info);
	    dataLine.open(audioFormat, EXTERNAL_BUFFER_SIZE);
	} catch (LineUnavailableException e1) {
	    e1.printStackTrace();
	}
 
	// Starts the music :P
	dataLine.start();
       
	int readBytes = 0;
	byte[] audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];
 
	try {
	    while (readBytes != -1) {
		readBytes = audioInputStream.read(audioBuffer, 0,
			audioBuffer.length);
		if (readBytes >= 0){
		    dataLine.write(audioBuffer, 0, readBytes);
		}
	    }
	} catch (IOException e1) {
	   e1.printStackTrace();
	} finally {
	    // plays what's left and and closes the audioChannel
	    dataLine.drain();
	    dataLine.close();
	}
    }
  // public                     
}

class PlayClipThread extends Thread
{
    private Clip clip = null;
    private boolean loop = false;
    public PlayClipThread(Clip clip)
    {
        this.clip = clip;
    }

    public void run()
    {
        if (clip != null)
        {
            stopSound();

            // give the thread that is playing the clip a chance to
            // stop the clip before we restart it
            try
            {
                Thread.sleep(1);
            }
            catch(InterruptedException e)
            {
                // don't do anything if the thread was interrupted
            }

            playSound();
        }
    }

    public void setLooping(boolean loop)
    {
        this.loop = loop;
    }

    public void stopSound()
    {
        clip.stop();
    }

    public void playSound()
    {
        clip.setFramePosition(0);
        if (loop)
        {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else
        {
            clip.start();
        }
    }
}