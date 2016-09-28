# About

PolyDrop is a game created by Matt Broussard, Josh Kelle, and Dallas Kelle for use with the [Leap Motion Controller](https://www.leapmotion.com/). It was conceived at the first annual Leap Motion + Compare Metrics hackathon (February 21-23, 2014) at Capital Factory in Austin, TX. After being selected as a finalist, work continued culminating in its selection as the grand prize winner at Compare Metrics' "Think Tank | Drink Tank" SXSW happy hour event two weeks later. Work continues on the game to ready it for submission to the Leap Motion Airspace store.

## Authors

* [Matt Broussard](http://mattb.name/) ([email](http://scr.im/mbroussard))
* [Josh Kelle](http://joshkelle.com/) ([email](http://scr.im/jkelle))
* Dallas Kelle ([email](http://scr.im/dkelle))

## Disclaimer

This code is unfinished and still being actively modified. We make no guarantees as to the quality or stability of it at this time. There is an effort ongoing to clean things up in preparation to submit to Airspace.

We are open to feedback via the [Issues](https://github.com/mattbroussard/polydrop-game/issues) page. In the future, we will also accept pull requests but for now are keeping the project under our own control.

## License

This code is covered by [GPL v3](https://github.com/mattbroussard/polydrop-game/blob/master/LICENSE.txt).

# Developer Information

## Missing files

The following files (required to build/run successfully) are missing for licensing reasons:

* Leap Motion SDK (available [here](https://developer.leapmotion.com/downloads))
  * lib/Leap.dll
  * lib/LeapJava.dll
  * lib/LeapJava.jar
  * lib/libLeap.dylib
  * lib/libLeapJava.dylib
* jBox2D Library (available [here](http://www.jbox2d.org/))
  * lib/jbox2d-library-2.2.1.1.jar

The following must be present to generate the Mac bundle:

* AppBundler (available [here](https://java.net/projects/appbundler))
  * buildtools/appbundler-1.0.jar

The following must be present to generate the Windows bundle:

* Launch4j (available [here](http://launch4j.sourceforge.net/))
  * buildtools/launch4j/*

## Building

This project uses [Apache Ant](http://ant.apache.org/) 1.9.1 or later for builds.

* To remove previously created build artifacts: `ant clean`
* To build: `ant`
* To run test build: `ant run`
* To run test build in windowed mode: `ant windowed`
* To generate JAR: `ant jar`
* To generate Mac bundle: `ant mac` or `ant mac-jre` to include JRE (specify JRE location with `-Djavahome=[location]` if foreign platform)
* To generate Windows bundle: `ant win` or `ant win-jre` to include JRE (specify JRE location with `-Djavahome=[location]` if foreign platform)
* To generate Airspace files, `ant airspace` (Mac-only since requires `hdiutil`)
