package msc.ar.multimarkers;

import java.util.Enumeration;
import javax.media.Buffer;

import javax.media.j3d.*;
import javax.vecmath.*;

import msc.ar.NyARException;
import msc.ar.core.param.NyARParam;
import msc.ar.core.types.NyARIntSize;
import msc.ar.java3d.utils.J3dNyARRaster_RGB;
import msc.ar.jmf.utils.JmfCaptureDevice;
import msc.ar.jmf.utils.JmfCaptureDeviceList;
import msc.ar.jmf.utils.JmfCaptureListener;




public class NyARMarkersBehavior extends Behavior
                       implements JmfCaptureListener
{
  private final double FPS = 30.0;  // so executes about 30 times/sec


  private Background bg = null;
  private DetectMarkers detectMarkers;   // the detector for the markers

  private WakeupCondition wakeup;

  private JmfCaptureDevice captureDev;   // captures the camera image
  private J3dNyARRaster_RGB rasterRGB;   // the camera image



  public NyARMarkersBehavior(NyARParam params, Background bg, DetectMarkers ms)
  {
    super();
    this.bg = bg;
    detectMarkers = ms;

    wakeup = new WakeupOnElapsedTime((int)(1000.0/FPS));
    setSchedulingBounds( new BoundingSphere(new Point3d(), 100.0) );

    initCaptureDevice(params);
  }  // end of NyARMarkersBehavior()



  private void initCaptureDevice(NyARParam params)
  {
    NyARIntSize screenSize = params.getScreenSize();

    try {
      JmfCaptureDeviceList devlist = new JmfCaptureDeviceList();  // get devices
      captureDev = devlist.getDevice(0);   // use the first
      captureDev.setCaptureFormat(screenSize.w, screenSize.h, 15.0f);
      captureDev.setOnCapture(this);

      rasterRGB = new J3dNyARRaster_RGB(params, captureDev.getCaptureFormat());  // create raster 

      detectMarkers.createDetector(params, rasterRGB);   // initialise detector

      captureDev.start(); 
    }
    catch(NyARException e)
    {  System.out.println(e);
       System.exit(1);
    }
  }  // end of initCaptureDevice()



  public void initialize()
  {  wakeupOn(wakeup);  }



  public void processStimulus(Enumeration criteria)
  /* use the detector to update the models on the markers */
  {
    try {
      synchronized (rasterRGB) {
        if (bg != null) {
          rasterRGB.renewImageComponent2D(); 
          bg.setImage(rasterRGB.getImageComponent2D());  // refresh background
        }
      }
      detectMarkers.updateModels(rasterRGB);
      wakeupOn(wakeup);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }  // end of processStimulus()



  public void onUpdateBuffer(Buffer buf)
  // triggered by JmfCaptureListener event
  {
    try {
      synchronized (rasterRGB) {
        rasterRGB.setBuffer(buf);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }  // end of onUpdateBuffer()


/*
  public void stop()
  {  captureDev.stop();  }
*/
}  // end of NyARMarkersBehavior class
