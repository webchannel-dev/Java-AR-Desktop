/* 
 * PROJECT: NyARToolkit Java3D utilities.
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

import java.io.File;
import java.util.Enumeration;

import javax.media.Buffer;
import javax.media.j3d.*;
import javax.vecmath.*;

import msc.ar.NyARException;
import msc.ar.jmf.utils.*;
import msc.ar.core.*;
import msc.ar.core.param.NyARParam;
import msc.ar.core.transmat.NyARTransMatResult;
import msc.ar.detector.*;
import msc.ar.core.types.*;
import msc.ar.java3d.utils.J3dNyARRaster_RGB;
import msc.ar.java3d.utils.NyARSingleMarkerBehaviorListener;




public class NyARSingleMarkerVideoBehaviorHolder implements JmfCaptureListener
{
	private NyARParam _cparam;

	private JmfCaptureDevice _capture;

	private J3dNyARRaster_RGB _nya_raster;//最大3スレッドで共有されるので、排他制御かけること。

	private NyARSingleDetectMarker _nya;

	private NyARBehavior _nya_behavior;
        

	public NyARSingleMarkerVideoBehaviorHolder(NyARParam i_cparam, float i_rate, NyARCode i_ar_code, double i_marker_width) throws NyARException
	{
		this._nya_behavior = null;
		final NyARIntSize scr_size = i_cparam.getScreenSize();
		this._cparam = i_cparam;
		JmfCaptureDeviceList devlist=new JmfCaptureDeviceList();
		this._capture=devlist.getDevice(0);
		this._capture.setCaptureFormat(scr_size.w, scr_size.h,15f);
		this._capture.setOnCapture(this);		
		this._nya_raster = new J3dNyARRaster_RGB(this._cparam,this._capture.getCaptureFormat());
		this._nya = new NyARSingleDetectMarker(this._cparam, i_ar_code, i_marker_width,this._nya_raster.getBufferType());
		this._nya_behavior = new NyARBehavior(this._nya, this._nya_raster, i_rate);
                
	}

	public Behavior getBehavior()
	{
		return this._nya_behavior;
	}

	public void setBackGround(Background i_back_ground)
	{
		
		this._nya_behavior.setRelatedBackGround(i_back_ground);
	}

	
	public void setTransformGroup(TransformGroup i_trgroup)
	{
		
		this._nya_behavior.setRelatedTransformGroup(i_trgroup);
	}

	
	public void setUpdateListener(NyARSingleMarkerBehaviorListener i_listener)
	{
		
		this._nya_behavior.setUpdateListener(i_listener);
	}

	
	public void onUpdateBuffer(Buffer i_buffer)
	{
		try {
			synchronized (this._nya_raster) {
				this._nya_raster.setBuffer(i_buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() throws NyARException
	{
		
		this._capture.start();
	}

	public void stop()
	{
		this._capture.stop();
	}
}
class NyARBehavior extends Behavior
{
	private NyARTransMatResult trans_mat_result = new NyARTransMatResult();

	private NyARSingleDetectMarker related_nya;

	private TransformGroup trgroup;

	private Background back_ground;

	private J3dNyARRaster_RGB raster;

	private WakeupCondition wakeup;

	private NyARSingleMarkerBehaviorListener listener;

        
	public void initialize()
	{
		wakeupOn(wakeup);
	}

	
	public NyARBehavior(NyARSingleDetectMarker i_related_nya, J3dNyARRaster_RGB i_related_raster, float i_rate)
	{
		super();
		wakeup = new WakeupOnElapsedTime((int) (1000 / i_rate));
		related_nya = i_related_nya;
		trgroup = null;
		raster = i_related_raster;
		back_ground = null;
		listener = null;
		this.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
	}

	public void setRelatedBackGround(Background i_back_ground)
	{
		synchronized (raster) {
			back_ground = i_back_ground;
		}
	}

	public void setRelatedTransformGroup(TransformGroup i_trgroup)
	{
		synchronized (raster) {
			trgroup = i_trgroup;
		}
	}

	public void setUpdateListener(NyARSingleMarkerBehaviorListener i_listener)
	{
		synchronized (raster) {
			listener = i_listener;
		}
	}


	public void processStimulus(Enumeration criteria)
	{
		try {
			synchronized (raster) {
				Transform3D t3d = null;
				boolean is_marker_exist = false;
				if (back_ground != null) {
					raster.renewImageComponent2D();/*DirectXモードのときの対策*/
					back_ground.setImage(raster.getImageComponent2D());
				}
				if (raster.hasBuffer()) {
					is_marker_exist = related_nya.detectMarkerLite(raster, 100);
					if (is_marker_exist)
					{
                                              
					}else{
					 
						
					}
				}
				if (listener != null) {
					listener.onUpdate(is_marker_exist, t3d);
			}
			}
			wakeupOn(wakeup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
