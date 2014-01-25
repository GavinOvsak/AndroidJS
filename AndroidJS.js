//AndroidJS Compiler

//__dirname
var fs = require('fs-extra');
var vm = require('vm');
console.log(process.argv[2]);
var file = process.argv[2] + '/android_info.json';

var numActivities = 0;

var data = fs.readFileSync(file, 'utf8');
data = JSON.parse(data);

var filename = process.argv[2] + '/js/' + data.main;

var sandbox = {
	require: function(file){
		var module = {};
		module.exports = {};
		var exports = module.exports;
		var fullfile = '';
		if (file == 'android') {
			fullfile = __dirname + '/mock/android.js';
		} else {
			fullfile = process.argv[2] + '/js/' + file + '.js';
		}
		var func = fs.readFileSync(fullfile, 'utf8');
		eval(func);
		return module.exports;
	},
    console: console,
    __filename: filename,
    numActivities: numActivities
};
vm.runInNewContext(fs.readFileSync(filename, 'utf8'), sandbox, 'AndroidJS.vm');

var preText = 'package ' + data.package + ';\n';
var filesToPackage = ['AndroidJSActivity.java', 'AndroidJSListAdapter.java'];
filesToPackage.map(function(file) {
	var postText = fs.readFileSync(__dirname + '/templates/android18/Java/' + file);
	var newFileName = process.argv[2] + '/output/AndroidJS/src/' + data.package.replace(/\./g,'/') + '/' + file;
	fs.createFileSync(newFileName);
	fs.writeFileSync(newFileName, preText + postText);
});

var manifest = '<manifest xmlns:android="http://schemas.android.com/apk/res/android"\n' +
'    package="' + data.package + '"\n' +
'    android:versionCode="1"\n' +
'    android:versionName="1.0">\n' +
'    <uses-sdk\n' +
'        android:minSdkVersion="8"\n' +
'        android:targetSdkVersion="18" />\n' +
'    <application\n' +
'        android:allowBackup="true"\n' +
'        android:icon="@drawable/ic_launcher"\n' +
'        android:label="@string/app_name"\n' +
'        android:theme="@style/AppTheme" >\n';

for(var i=0; i < numActivities; i++) {
	manifest += 
'        <activity android:name="Activity'+i+'">\n';
	if (i == 0) {
		manifest += 
'            <intent-filter>\n' +
'                <action android:name="android.intent.action.MAIN" />\n' +
'                <category android:name="android.intent.category.LAUNCHER" />\n' +
'            </intent-filter>\n';
	}
	manifest += 
'        </activity>\n';

	var activityContent = 
		'package '+data.package+';\n' +
		'\n' +
		'public class Activity'+i+' extends AndroidJSActivity {\n' +
		'	\n' +
		'	public Activity'+i+'() {\n' +
		'		super();\n' +
		'		ACTIVITY_ID = "'+i+'";\n' +
		'	}\n' +
		'}';

	fs.writeFileSync(process.argv[2] + '/output/AndroidJS/src/' + data.package.replace(/\./g,'/') + '/Activity' + i + '.java', activityContent);
}
manifest += '   </application>\n' +
'</manifest>\n';

fs.writeFileSync(process.argv[2] + '/output/AndroidJS/AndroidManifest.xml', manifest);
