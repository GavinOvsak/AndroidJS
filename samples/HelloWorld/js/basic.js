var android = require('android');
var _ = require('lodash');

_.map([1, 2, 3], function(n) { android.log(n * 3); });

var activity0 = new android.Activity();
var activity1 = new android.Activity();
var activity2 = new android.Activity();

android.log("Hey Out There!");
 
activity0.onCreate(function(bundle) {
	android.setContentView('helloworld');
	android.log('Page 1'); 
	var button = android.findViewByID('toaster');
	var label = android.findViewByID('text');
	button.onClick(function(e) {
		label.setText('Hey Sabrina!');
		button.setVisibility(android.View.GONE);
		/*android.toast('Hello World!', android.toast.short);
		android.makeIntent('Activity2').
			putExtra('Name', 'Gavin').
			startActivity();*/
	});
});

activity1.onCreate(function(bundle) {
	android.log(android.getStringExtra('Name'));
	android.setContentView('page2');
});

activity2.onCreate(function(bundle) {
	android.setContentView('listpage');
	
	var list = android.setUpList('list', 'listitem', 2);
	list.onViewRender(function(view, position){
		view.onClick(function(e){
			android.toast('Ive Been Clicked! ' + position, android.toast.short);
		});
		var secondLine = view.findViewByID('secondLine');
		secondLine.setText('Position: ' + position);
	});
});

android.add(activity0);
android.add(activity1);
android.add(activity2);
