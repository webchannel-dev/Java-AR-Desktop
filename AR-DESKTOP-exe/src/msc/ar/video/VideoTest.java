/* 
 * PROJECT: NyARToolkit Java3d sample program.
 * --------------------------------------------------------------------------------
 * The MIT License
 * Copyright (c) 2008 nyatla
 * airmail(at)ebony.plala.or.jp
 * http://nyatla.jp/nyartoolkit/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package msc.ar.video;

import java.awt.BorderLayout;
import javax.media.j3d.*;

import com.sun.j3d.utils.universe.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.vecmath.*;

import msc.ar.core.*;
import msc.ar.java3d.utils.*;

import java.io.File;
import javax.swing.*;
  import javax.media.*;
  import java.awt.*;
  import java.awt.event.*;
  import java.net.*;
  import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import msc.ar.sound.PlayThread;



//Plays vedio on detection on marker and audio as a seperate thread.

public class VideoTest extends JFrame implements NyARSingleMarkerBehaviorListener
{
	private static final long serialVersionUID = -8472866262481865377L;
        
	private final String CARCODE_FILE = "src/Data/blue16.pat";

	private final String PARAM_FILE = "src/Data/camera_para.dat";

	private NyARSingleMarkerVideoBehaviorHolder nya_behavior;

	private J3dNyARParam ar_param;

	private Canvas3D canvas;

	private Locale locale;

	private VirtualUniverse universe;
        private String AUDIO_FNM;
        private String MARKER_FNM;
        private String VIDEO_FNM;
        private String PARAMS_FNM;
        
        Player player;
        Component center;
        Component south;
        File fvideo ;
        PlayThread play = new PlayThread();
       
         private void readApplicationProperties(){
        Properties myProps = new Properties();
        FileInputStream MyInputStream;
        try {
            File f=new File("conf\\ARVideoconf.properties");
            MyInputStream = new FileInputStream(f);
            myProps.load(MyInputStream);
            this.PARAMS_FNM = myProps.getProperty("PARAMS_FNM");
            this.AUDIO_FNM = myProps.getProperty("AUDIO_FNM");
            this.MARKER_FNM = myProps.getProperty("MARKER_FNM");
            this.VIDEO_FNM = myProps.getProperty("VIDEO_FNM");
            } catch (FileNotFoundException ex) {
            Logger.getLogger(VideoTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException  ei){}
        
     
      
  }
	public static void main(String[] args)
	{
           
		try {
			VideoTest frame = new VideoTest();
                        frame.readApplicationProperties();
                        frame.initialise();
                        frame.fvideo = new File(frame.VIDEO_FNM);
			frame.setVisible(true);
			Insets ins = frame.getInsets();
			frame.setSize(320 + ins.left + ins.right, 240 + ins.top + ins.bottom);
			frame.startCapture();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onUpdate(boolean i_is_marker_exist, Transform3D i_transform3d)
	{
		// Upon marker detection paly audio and video. Stop the search for the marker
           if(i_is_marker_exist==true)
           {
              // System.out.println("marker ");
            try{
              play.loadclip(this.AUDIO_FNM);
              new Thread (play).start();
              playVedio(fvideo);
              
              nya_behavior.stop();
             // System.exit(0);
                 
            }catch(Exception e){e.printStackTrace();}
           }
	}

	public void startCapture() throws Exception
	{
		nya_behavior.start();
	}

	public VideoTest()
	{
            super("Video test");
        }
        public void initialise() throws Exception{
		
		NyARCode ar_code = new NyARCode(16, 16);
		ar_code.loadARPattFromFile(this.MARKER_FNM);  //load pattern
		ar_param = new J3dNyARParam();
		ar_param.loadARParamFromFile(this.PARAMS_FNM);  //load camera parameters
		ar_param.changeScreenSize(320, 240);

                JPanel p = new JPanel();
                p.setLayout( new BorderLayout() );
		universe = new VirtualUniverse();
		locale = new Locale(universe);
		canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		View view = new View();
		ViewPlatform viewPlatform = new ViewPlatform();
		view.attachViewPlatform(viewPlatform);
		view.addCanvas3D(canvas);
		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(new PhysicalEnvironment());

	
		Transform3D camera_3d = ar_param.getCameraTransform();
		view.setCompatibilityModeEnable(true);
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		view.setLeftProjection(camera_3d);

	
		TransformGroup viewGroup = new TransformGroup();
		Transform3D viewTransform = new Transform3D();
		viewTransform.rotY(Math.PI);
		viewTransform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
		viewGroup.setTransform(viewTransform);
		viewGroup.addChild(viewPlatform);
		BranchGroup viewRoot = new BranchGroup();
		viewRoot.addChild(viewGroup);
		locale.addBranchGraph(viewRoot);

		Background background = new Background();
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(10.0);
		background.setApplicationBounds(bounds);
		background.setImageScaleMode(Background.SCALE_FIT_ALL);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		BranchGroup root = new BranchGroup();
		root.addChild(background);
	
		nya_behavior = new NyARSingleMarkerVideoBehaviorHolder(ar_param, 30f, ar_code, 0.08);
		nya_behavior.setBackGround(background);

		root.addChild(nya_behavior.getBehavior());
		nya_behavior.setUpdateListener(this);

		locale.addBranchGraph(root);
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
	}

        
 //Play video using palyer calss in JMF       
        private void playVedio(final File file) throws Exception{

        URL url = file.toURL();
        final Container contentPane = getContentPane();
        //System.out.println(contentPane.getName());
        if (player != null) {
             player.stop();
        }
        player = Manager.createPlayer(url);
        ControllerListener listener = new ControllerAdapter() {
        public void realizeComplete(RealizeCompleteEvent event) {
                   Component vc =  player.getVisualComponent();
                    if (vc != null) {
                            contentPane.add(vc,BorderLayout.SOUTH);
                            center = vc;
                    } else {
                    if (center != null) {
                        contentPane.remove(center);
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
}
