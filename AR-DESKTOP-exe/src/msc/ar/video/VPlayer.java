/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ar.video;
import javax.swing.*;
  import javax.media.*;
  import java.awt.*;
  import java.awt.event.*;
  import java.net.*;
  import java.io.*;
/**
 *
 * @author mamatha
 */

    
   //sample video code. 

public class VPlayer extends JFrame {
  
    Player player;
    Component center;
    Component south;
  
    public VPlayer() {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      JButton button = new JButton("Select File");
      ActionListener listener = 
          new ActionListener() {
        public void actionPerformed(
            ActionEvent event) {
          JFileChooser chooser = 
            new JFileChooser(".");
          int status = 
            chooser.showOpenDialog(VPlayer.this);
          if (status == 
              JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
              load(file);
            } catch (Exception e) {
              System.err.println("Try again: " + e);
            }
          }
        }
      };
      button.addActionListener(listener);
      getContentPane().add(button, 
        BorderLayout.NORTH);
      pack();
      show();
    }
  
    public void load(final File file) 
        throws Exception {
      URL url = file.toURL();
      final Container contentPane = 
        getContentPane();
      if (player != null) {
        player.stop();
      }
      player = Manager.createPlayer(url);
      ControllerListener listener = 
          new ControllerAdapter() {
        public void realizeComplete(
            RealizeCompleteEvent event) {
          Component vc = 
            player.getVisualComponent();
          if (vc != null) {
            contentPane.add(vc, 
              BorderLayout.CENTER);
            center = vc;
          } else {
            if (center != null) {
              contentPane.remove(center);
              contentPane.validate();
            }
          }
          Component cpc = 
            player.getControlPanelComponent();
          if (cpc != null) {
            contentPane.add(cpc, 
              BorderLayout.SOUTH);
            south = cpc;
          } else {
            if (south != null) {
              contentPane.remove(south);
              contentPane.validate();
            }
          }
          pack();
          setTitle(file.getName());
        }
      };
     

 player.addControllerListener(listener);
      player.start();
    }
  

   public static void main(String args[]){
      VPlayer n= new VPlayer();
    }
  }