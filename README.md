Android: First Chapter
======================

5.3.2011

Recently I needed to get back into Java development, and wanted to pick up a new project to refresh my memory and my Java skills, and to hopefully also learn something new and have some fun while I was at it. After some thought I decided to get myself an [Android](http://www.android.com/) phone, and start playing around with the [development tools](http://developer.android.com/index.html) Google provides for it.

After going through some of the developer documentation and a little poking around, I decided to start my first project: a really simple [Panoramio](http://www.panoramio.com/) image viewer. The idea was to make an app that would use the phone’s location services to get the phone’s current location and use that to fetch a list of photos from nearby using the [Panoramio REST API](http://www.panoramio.com/api/data/api.html). It was easy enough to start with, and included the two services of the device I am most interested in: location and internet.

I won’t go into the details of the app here, but I’ll mention some things I liked or didn’t like about the Android platform and development tools, and mention some issues I had and how I solved them.

Tools and Documentation
-----------------------

The tools Google provides for Android developers are very good. (Disclaimer: Before this the only experience I had with mobile development was a single project using [Maemo](http://maemo.org/) some years ago. I have no way to compare the Android tools to the tools provided by any of the other current mobile platforms, like iOS or WP7.) I didn’t come across anything I would have needed from the standard Java libraries that was missing from Android, and the APIs included by Google feel intuitive and easy to use for the most part. The UI components include a good collection of views and widgets to build different UI designs and are easy to use. (More on the UI stuff later.)

The documentation for the APIs and the UI components is comprehensive and thorough. The developer website includes an [API reference](http://developer.android.com/reference/packages.html) and more [in depth guides](http://developer.android.com/guide/index.html) on many of the most common topics as well as [sample code](http://developer.android.com/resources/browser.html?tag=sample).

The [Android SDK](http://developer.android.com/sdk/index.html) integrates well with [Eclipse](http://www.eclipse.org/) and provides tools to help with developing, testing and publishing apps. Also included is an Android emulator that can be used to test your app on different Android versions, and to see how it looks and behaves in a “real” device. The only problem I had with the emulator was that it was very slow, and pretty quickly I gave up testing in the emulator and just used my phone for testing.

Activity Lifecycle
------------------

The overarching model Android applications have to fit into is the [activity lifecycle](http://developer.android.com/images/activity_lifecycle.png). In most cases applications are built as activities, and the activity lifecycle determines when and how an application is started and stopped, and the different states it can be in. The activity lifecycle also provides the necessary hooks where the application will place it’s own business logic.

When starting development it isn’t strictly necessary to have a deep understanding of the lifecycle, but pretty soon after a simple Hello World you’ll likely start running into the lifecycle and will need to understand how it works and how to make it work for you. For me that point came when I needed to start a new Activity to display a single image.

UI
--

The [UI for Android apps](http://developer.android.com/guide/topics/ui/index.html) is defined using XML layout files that describe the layout of the views and widgets that compose the app’s user interface. There’s nothing new about this, and it should feel familiar and easy to get used to for most developers. The platform includes a good amount of both views and widgets to get started, and if the ones provided aren’t enough, you can make your own.

In my app I only needed some very simple views: a view to display a grid of images and a view to display a single image, and the views provided by the platform worked fine for that. The only time I ran into any trouble with the UI was when I needed to be able to start a new thread to communicate with the network, and return the results to the UI thread. There are several ways to start new threads for asynchronous actions. I chose to use an [AsyncTask](http://developer.android.com/resources/articles/painless-threading.html). Even though the AsyncTask is documented pretty well, and the examples on the Android Developers site explain it nicely, it took me a little while to wrap my head around how I was supposed to use it.

Location Services
-----------------

The Android platform provides [tools for accessing the current location of the device](http://developer.android.com/guide/topics/location/index.html) provided by the locationing technologies present in the device. An app can either use the location API to fetch the current location of the device from one of the location providers, or the app can register to get updates from the providers as the device’s location changes.

The API seems simple enough, but I wasn’t able to get the updates to work the way I wanted to. (I kept getting updates too often, and couldn’t get them to happen less frequently.) In the end I realized a better fit for my application was to fetch the location when prompted to by the user anyway, and I gave up on getting the automatic updates to work.

Network Communication
---------------------

Just like the location services, the network communication on the Android platform is made easy for the developer, and there is no need to know how the device is connected to the network. It is simple to open up an HTTP connection to a server and fetch the data you need using a simple HttpClient class.

Before I settled on HttpClient, I had found out there are several different ways to do the HTTP communication. First I tried using a HttpURLConnection class, which didn’t work all the time, and sometimes some of the images I was trying to download just didn’t download for no apparent reason. After some googling I found [a solution](http://stackoverflow.com/questions/4414839/bitmapfactory-decodestream-returns-null-without-exception/4416821#4416821), which uses the HttpClient class. Using that all my image downloads have worked fine.

Conclusions
-----------

I found the Android platform with the tools for Eclipse provided a pleasant development environment and tool set for developing applications for Android devices. It is really easy to get into Android development, and at least with a simple app like mine the tools seemed to make things easy and not get in my way at all. The documentation is good, and there is plenty of sample code provided by Google and found elsewhere online to help a new developer get started.

Development was fast. Much faster than I had anticipated. The only speed bumps I ran into were mostly caused by my habit of just diving into writing code without really reading any of the documentation beforehand. Had I spent more time reading and planning before starting to build my app, I probably could have breezed through the entire development with very little problems.


