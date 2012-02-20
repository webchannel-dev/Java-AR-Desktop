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
package msc.ar.java3d.sample;

import java.awt.BorderLayout;
import javax.media.j3d.*;

import com.sun.j3d.utils.universe.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.vecmath.*;

import msc.ar.core.*;
import msc.ar.java3d.utils.*;

import com.sun.j3d.utils.geometry.ColorCube;
import java.io.File;
import javax.swing.*;
  import javax.media.*;
  import java.awt.*;
  import java.awt.event.*;
  import java.net.*;
  import java.io.*;


public class NyARJava3D extends JFrame implements NyARSingleMarkerBehaviorListener
{
	private static final long serialVersionUID = -8472866262481865377L;
        
	private final String CARCODE_FILE = "src/Data/blue16.pat";

	private final String PARAM_FILE = "src/Data/camera_para.dat";


	private NyARSingleMarkerBehaviorHolder nya_behavior;

	private J3dNyARParam ar_param;

	
	private Canvas3D canvas;

	private Locale locale;

	private VirtualUniverse universe;
        Player player;
        Component center;
        Component south;
     
       
        
	public static void main(String[] args)
	{
           
		try {
			NyARJava3D frame = new NyARJava3D();

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
		
	}

	public void startCapture() throws Exception
	{
		nya_behavior.start();
	}

	public NyARJava3D() throws Exception
	{
		super("Java3D Example NyARToolkit");

		//NyARToolkitの準備
		NyARCode ar_code = new NyARCode(16, 16);
		ar_code.loadARPattFromFile(CARCODE_FILE);
		ar_param = new J3dNyARParam();
		ar_param.loadARParamFromFile(PARAM_FILE);
		ar_param.changeScreenSize(320, 240);

		//locale
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

		//バックグラウンドの作成
		Background background = new Background();
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(10.0);
		background.setApplicationBounds(bounds);
		background.setImageScaleMode(Background.SCALE_FIT_ALL);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		BranchGroup root = new BranchGroup();
		root.addChild(background);

		//TransformGroupで囲ったシーングラフの作成
		TransformGroup transform = new TransformGroup();
		transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transform.addChild(createSceneGraph());
		root.addChild(transform);

		//NyARToolkitのBehavio
		nya_behavior = new NyARSingleMarkerBehaviorHolder(ar_param, 30f, ar_code, 0.08);
		//Behavior
		nya_behavior.setTransformGroup(transform);
		nya_behavior.setBackGround(background);

		
		root.addChild(nya_behavior.getBehavior());
		nya_behavior.setUpdateListener(this);

		
		locale.addBranchGraph(root);

		
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
	}

	
        
	private Node createSceneGraph()
	{
		TransformGroup tg = new TransformGroup();
		Transform3D mt = new Transform3D();
		mt.setTranslation(new Vector3d(0.00, 0.0, 20 * 0.001));
		// 大きさ 40mmの色付き立方体を、Z軸上で20mm動かして配置）
		tg.setTransform(mt);
		tg.addChild(new ColorCube(20 * 0.001));
		return tg;
	}
        
   
}
