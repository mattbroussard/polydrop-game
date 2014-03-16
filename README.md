# About

PolyDrop is a game created by Matt Broussard, Josh Kelle, and Dallas Kelle for use with the [Leap Motion Controller](https://www.leapmotion.com/). It was conceived at the first annual Leap Motion + Compare Metrics hackathon (February 21-23, 2014) at Capital Factory in Austin, TX. After being selected as a finalist, work continued culminating in its selection as the grand prize winner at Compare Metrics' "Think Tank | Drink Tank" SXSW happy hour event two weeks later. Work continues on the game to ready it for submission to the Leap Motion Airspace store.

# Authors

* [Matt Broussard](http://mattb.name/) ([email](http://scr.im/mbroussard))
* Josh Kelle ([email](http://scr.im/jkelle))
* Dallas Kelle ([email](http://scr.im/dkelle))

# Missing files

The following files are missing for licensing reasons:

* Leap Motion SDK (available [here](https://developer.leapmotion.com/downloads))
  * lib/Leap.dll
  * lib/LeapJava.dll
  * lib/LeapJava.jar
  * lib/libLeap.dylib
  * lib/libLeapJava.dylib
* jBox2D Library (available [here](http://www.jbox2d.org/))
  * lib/jbox2d-library-2.2.1.1.jar

The following must be present to generate the Mac bundles:

* AppBundler (available [here](https://java.net/projects/appbundler))
  * buildtools/appbundler-1.0.jar

The following must be present to generate the Windows bundles:

* To be determined.

# Building

This project uses [Apache Ant](http://ant.apache.org/) 1.9.1 or later for builds.

* To remove previously created build artifacts: `ant clean`
* To build: `ant`
* To run test build: `ant run`
* To run test build in windowed mode: `ant windowed`
* To generate distrib/PolyDrop.jar: `ant jar`
* To generate Mac bundle: `ant mac` or `ant mac-jre` to include JRE