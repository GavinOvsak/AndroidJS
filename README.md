AndroidJS
=========

Goal: Write native Android apps in JavaScript using the V8 Engine, indistinguishable from normal apps.

This is a very ambitious project intended to change the face of android and allow projects to have their web clients, web servers, and native apps all be written in JavaScript. By using the same language, it will be easier for people to use new platforms and re-use existing JS libraries like http://lodash.com/ on android. I'm currently looking for people to help contribute to this project! If you're interested, message me at ovsak.gavin@gmail.com.

How to use AndroidJS
==========


Make a project folder with the following structure:
```
ProjectName/
 - js/   For your javascript files
 - layout/    For your xml layout files
 - android_info.json    An object to describe your app
```
To compile the app into an apk file, run the AndroidJS.sh file from within your ProjectName folder. This will make a folder names output/ which will contain your apk file. You can email this file to any phone or upload it to the Google Play Store. 

More documentation is coming, for now have a look at the HelloWorld example in the samples folder for an example of the AndroidJS API and android_info.json formatting.

Documentation
=============

###Require modules
The AndroidJS API revolves around the "android" object which can be imported at the beginning of your base file using:
```
var android = require('android');
```

Other javascript files than your main file can included this way if they are also in your js/ folder. The base javascript file is specified in android_info.json under the "main" field.

###Printing to the Android Log
```
android.log('I'm a debug message');
```

###Making Toast Messages
Toast messages are shown to the user for either a short or long amount of time.
```
android.toast('I\'m a short message', android.toast.short);
android.toast('I\'m a long message', android.toast.long);
```

###App Structure
Android apps are devided into activities which are basically each type of screen that an app has and switches between.

```
var activity0 = new android.Activity();
activity0.onCreate(function(bundle) {
  android.toast('Hey There!');
}
android.add(activity0);
```

###Layout Files
At the beginning of each activity, it is good to set the content view to a layout xml file in the /layout folder right away. The following sets the activity to have the layout specified in /layout/page1.xml

```
android.setContentView('page1');
```

###View Retreiving
After a layout file is loaded, views in the content view can be modified after they are retrieved using the findViewByID command as follows:

```
var button = android.findViewByID('button');
```

###View Modifications
Using this view object (which inherits from android.View), many properties can be accessed such as the following:

```
button.setText('Press Me');
var text = button.getText();
button.setVisibility(android.View.INVISIBLE);  //Options include: android.View.GONE, android.View.INVISIBLE, android.View.VISIBLE
```

###View Callbacks
Views can be registered to call a given function when they are clicked using the onClick(function) command:

```
button.onClick(function(e) {
  android.toast('I\'ve been clicked!', android.toast.short);
});
```

###Subview Access
To access a view which is inside a parent view in your layout, use the findByID command on the parent view object

```
var linearLayout = android.findViewByID('group1');
var button = linearLayout.findViewByID('button');
```

#References

This couldn't have been done if it weren't for the <a href="https://github.com/namuol/jv8">jv8 project</a> which created the bridge between Java and the V8 engine on android which this project uses.

