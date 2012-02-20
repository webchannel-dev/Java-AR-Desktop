AR-DESKTOP
-----------------
Augmented Reality application

About AR-Desktop
---------------
* Sample applications that illustrates the marker based AR operations.
* 2 different samples are included which demonstrate how to load a video& audio  or to load a 3d file when a marker is detected in the input video that is captured


Libraries used
--------------
NyARtoolkit,JMF1.2 ,java3d, portfolio


Configuration
---------------
To customise the prop files ,video and audio used, edit 
	conf\ARVideoconf.properties
           &
	conf\ARconf.properties

To run
---------
Run the batch files run3d & runvideo


Note
----
JMF needs to be installed in the system. It will creat a .properties file in the JMF installation directory. copy the same to libs/.


Making exewrapper for the jar
-----------------------------

Using launch4j, the AR-DESKTOP.jar is wrapped into a windows executable. The ar.xml serves as the configuaration file for launch4j.
The generated exe is configured to use the embedded jre. (subdirectory jre of current folder). 
The exe can be further tuned by choosing the exact libraries need to be linked with the exe, omitting the irrelevent ones. 

Installer using nsis
--------------------
The wrapped exe is packaged with nsis. The installer has the executable, resource & configuration files and the embedded jre.
