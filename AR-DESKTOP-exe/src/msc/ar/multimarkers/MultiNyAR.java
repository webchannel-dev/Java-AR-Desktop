package msc.ar.multimarkers;



import java.awt.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import com.sun.j3d.utils.universe.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.media.j3d.*;
import javax.vecmath.*;
import msc.ar.NyARException;
import msc.ar.java3d.utils.J3dNyARParam;
import msc.ar.sound.PlayThread;

//C:\Users\mamatha\Documents\NetBeansProjects\AR-DESKTOP\src\Data

public class MultiNyAR extends JFrame
{
  private  String PARAMS_FNM;
  private String AUDIO_FNM;
  private String MARKER1_FNM;
  private String MARKER2_FNM;
  private String PROP1_FNM;
  private String PROP2_FNM;
  private boolean cord1;
  private boolean cord2;
  private static final int PWIDTH = 320;   // size of panel
  private static final int PHEIGHT = 240; 
  
 // private static final double SHAPE_SIZE = 0.02; 

  private static final int BOUNDSIZE = 100;  // larger than world

  private J3dNyARParam cameraParams;
  private JTextArea statusTA;



  public MultiNyAR(String heading)
  {
    super(heading);
    readApplicationProperties();
    cameraParams = readCameraParams(PARAMS_FNM);

    Container cp = getContentPane();

    // create a JPanel in the center of JFrame
    JPanel p = new JPanel();
    p.setLayout( new BorderLayout() );
    p.setPreferredSize( new Dimension(PWIDTH, PHEIGHT) );
    cp.add(p, BorderLayout.CENTER);

    // put the 3D canvas inside the JPanel
    p.add(createCanvas3D(), BorderLayout.CENTER);
   
    // add status field to bottom of JFrame
    statusTA = new JTextArea(7, 10);   // updated by DetectMarkers object (see createSceneGraph())
    statusTA.setEditable(false);
    cp.add(statusTA, BorderLayout.SOUTH);

    // configure the JFrame
    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    pack();
    setVisible(true);
    //sound
    PlayThread play = new PlayThread();
    play.loadclip(this.AUDIO_FNM);
    new Thread (play).start();
  }  // end of MultiNyAR()

  public MultiNyAR(){
      super(" ");
      
  }

//read camara parameters from given file
  private J3dNyARParam readCameraParams(String fnm)
  {
    J3dNyARParam cameraParams = null;  
    try {
      cameraParams = new J3dNyARParam();
      cameraParams.loadARParamFromFile(fnm);
      cameraParams.changeScreenSize(PWIDTH, PHEIGHT);
    }
    catch(NyARException e)
    {  System.out.println("Could not read camera parameters from " + fnm);
       System.exit(1);
    }
    return cameraParams;
  }  // end of readCameraParams()



  private Canvas3D createCanvas3D()
  /* Build a 3D canvas for a Universe which contains
     the 3D scene and view 
            univ --> locale --> scene BG
                          |
                           ---> view BG  --> Canvas3D
                              (set up using camera cameraParams)
   */
  { 
    Locale locale = new Locale( new VirtualUniverse() );
    locale.addBranchGraph( createSceneGraph() );   // add the scene

    // get the preferred graphics configuration for the default screen
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    Canvas3D c3d = new Canvas3D(config);
    locale.addBranchGraph( createView(c3d) );  // add view branch

    return c3d;
  }  // end of createCanvas3D()



  private BranchGroup createSceneGraph()
  /* The scene graph:
         sceneBG 
               ---> lights
               |
               ---> bg
               |
               -----> tg1 ---> model1  
               -----> tg2 ---> model2 
               |
               ---> behavior  (controls the bg and the tg's of the models)
  */
  { 
    BranchGroup sceneBG = new BranchGroup();  
    lightScene(sceneBG);              // add lights

    Background bg = makeBackground();
    sceneBG.addChild(bg);             // add background
    

    DetectMarkers detectMarkers = new DetectMarkers(this);

  
    // the "yellow16.pat" marker uses a cow model, scaled by 0.12 units, with coords file
    
    //MarkerModel mm2 = new MarkerModel("yellow16.pat", "cow.obj", 0.15, true);
     MarkerModel mm2 = new MarkerModel(this.MARKER1_FNM, this.PROP1_FNM, 0.15, true);
    if (mm2.getMarkerInfo() != null) {
      sceneBG.addChild( mm2.getMoveTg());
      detectMarkers.addMarker(mm2);     
   
    }
    //MarkerModel mm3 = new MarkerModel("markercircle.pat","robot.3ds",0.15,false);
    MarkerModel mm3 = new MarkerModel(this.MARKER2_FNM,this.PROP2_FNM,0.15,false);
    if(mm3.getMarkerInfo()!=null){
    	sceneBG.addChild(mm3.getMoveTg());
    	detectMarkers.addMarker(mm3);
    }
    // create a NyAR multiple marker behaviour
    sceneBG.addChild( new NyARMarkersBehavior(cameraParams, bg, detectMarkers) );

    sceneBG.compile();       // optimize the sceneBG graph
    return sceneBG;
  }  // end of createSceneGraph()



  private void lightScene(BranchGroup sceneBG)
  /* One ambient light, 2 directional lights */
  {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE); 

    // Set up the ambient light
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    // Set up the directional lights
    Vector3f light1Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
       // left, down, backwards 
    Vector3f light2Direction  = new Vector3f(1.0f, -1.0f, 1.0f);
       // right, down, forwards

    DirectionalLight light1 =  new DirectionalLight(white, light1Direction);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);

    DirectionalLight light2 = new DirectionalLight(white, light2Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light2);
  }  // end of lightScene()



  private Background makeBackground()
  // the background will be the current image captured by the camera
  { 
    Background bg = new Background();
    BoundingSphere bounds = new BoundingSphere();
    bounds.setRadius(10.0);
    bg.setApplicationBounds(bounds);
    bg.setImageScaleMode(Background.SCALE_FIT_ALL);
    bg.setCapability(Background.ALLOW_IMAGE_WRITE);  // so can change image

    return bg;
  }  // end of makeBackground()



  private BranchGroup createView(Canvas3D c3d)
  // create a view graph using the camera parameters
  {
    View view = new View();
    ViewPlatform viewPlatform = new ViewPlatform();
    view.attachViewPlatform(viewPlatform);
    view.addCanvas3D(c3d);

    view.setPhysicalBody(new PhysicalBody());
    view.setPhysicalEnvironment(new PhysicalEnvironment());

    view.setCompatibilityModeEnable(true);
    view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
    view.setLeftProjection( cameraParams.getCameraTransform() );   // camera projection

    TransformGroup viewGroup = new TransformGroup();
    Transform3D viewTransform = new Transform3D();
    viewTransform.rotY(Math.PI);   // rotate 180 degrees
    viewTransform.setTranslation(new Vector3d(0.0, 0.0, 0.0));   // start at origin
    viewGroup.setTransform(viewTransform);
    viewGroup.addChild(viewPlatform);

    BranchGroup viewBG = new BranchGroup();
    viewBG.addChild(viewGroup);

    return viewBG;
  }  // end of createView()



  public void setStatus(String msg)
  // called from DetectMarkers
  {
    statusTA.setText(msg);
  }  // end of setStatus()


  private void readApplicationProperties(){
      Properties myProps = new Properties();
      FileInputStream MyInputStream;
        try {
            File f=new File("conf\\ARconf.properties");
            MyInputStream = new FileInputStream(f);
            myProps.load(MyInputStream);
            this.PARAMS_FNM = myProps.getProperty("PARAMS_FNM");
            this.AUDIO_FNM = myProps.getProperty("AUDIO_FNM");
            this.MARKER1_FNM = myProps.getProperty("MARKER1_FNM");
            this.MARKER2_FNM = myProps.getProperty("MARKER2_FNM");
            this.PROP1_FNM = myProps.getProperty("PROP1_FNM");
            this.PROP2_FNM = myProps.getProperty("PROP2_FNM");
            //this.cord1 = new BooleanmyProps.getProperty(AUDIO_FNM)
            System.out.println(this.PARAMS_FNM);
            System.out.println(this.AUDIO_FNM);
            System.out.println(this.MARKER1_FNM);
            System.out.println(this.MARKER2_FNM);
            System.out.println(this.PROP1_FNM);
            System.out.println(this.PROP2_FNM);
            } catch (FileNotFoundException ex) {
            Logger.getLogger(MultiNyAR.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException  ei){}
        
     
      
  }

  // ------------------------------------------------------------

  public static void main(String args[])
  {  new MultiNyAR("Multiple Markers");  }
    
    
} // end of MultiNyAR class
