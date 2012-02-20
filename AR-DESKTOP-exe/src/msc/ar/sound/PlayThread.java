/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ar.sound;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 *
 * @author mamatha
 */

//Play audio  in .wav .au format countinuously in a seperate thread

public class PlayThread implements Runnable{
    DataLine.Info info;
    Clip clip;
    AudioInputStream audioInputStream;
    public void loadclip(String filename){
        try{
        audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
        info = new DataLine.Info(Clip.class, audioInputStream.getFormat()); 
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(audioInputStream);
        clip.loop(clip.LOOP_CONTINUOUSLY ); //loop 
        }catch(Exception e){e.printStackTrace();}
    }
    public void run(){
        clip.start();
     }
    
}
