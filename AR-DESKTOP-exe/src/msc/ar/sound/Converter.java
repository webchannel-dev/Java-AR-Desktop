/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ar.sound;
import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 *
 * @author mamatha
 */

//Convert unsupported mono wav format to stereo PCM signed

public class Converter {
     public static void main(String args[]){
         
     }
    public static void convert(String inputfile, String outputfile){
        File soundFile = new File(inputfile);
        File fileout = new File(outputfile);
         try{
          AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
          AudioFormat format = ais.getFormat();
          System.out.println(format.toString());
          if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    format.getSampleSizeInBits() * 2,
                    format.getChannels(),
                    format.getFrameSize() * 2,
                    format.getFrameRate(),
                    true);
         }
        AudioFileFormat.Type targettype = AudioFileFormat.Type.WAVE;
        AudioInputStream targetaudiostream = AudioSystem.getAudioInputStream(format, ais);
        AudioSystem.write(targetaudiostream, targettype, fileout);
        System.out.println("PCM encoded file generated successfully.");
        ais.close();
        }catch(Exception e){e.printStackTrace();}
    }
}
