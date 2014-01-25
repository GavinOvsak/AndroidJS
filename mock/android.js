//Should try and make this automatically. Could be tough

android = {};
android.Toast = {};
android.toast = function(message, length) {
};
android.toast.short = 'short';
android.toast.long = 'long';
android.setContentView = function(layout_name) {
};

module.exports = android;

android.log = function(message) {
};

android.functions = [];

android.storeFunction = function(f) {
	android.functions.push(f);
	return android.functions.length - 1;
};

android.runFunction = function(id) {
	android.functions[id]();
};

android.Activity = function() {
	this.onCreate_f = function(){};
	this.onCreate = function(f) {
		this.onCreate_f = f;
	};
	this.call = function(name) {
		if (name == 'onCreate') {
			this.onCreate_f();
		}
	};
};

android.Intent = function(className) {
	this.objectID = A_makeIntent(className);
	this.putExtra = function(name, value) {
		return this;
	};
	this.startActivity = function() {
	};
};

android.View = function(id, isInObjects) {
	this.id = id;
	this.isInObjects = isInObjects;
	this.onClick = function(f) {
	};
	this.setText = function(newText) {
	};
	this.getText = function() {
		return "";
	};
	this.setVisibility = function(visibility) {
	};
	this.findViewById = function(name) {
		return this;
	}
};

android.View.GONE = 'gone';
android.View.VISIBLE = 'visible';
android.View.INVISIBLE = 'invisible';

android.makeIntent = function(className) {
	return new android.Intent(className);
};

android.getStringExtra = function(key) {
	return "";
}

android.findViewById = function(viewName) {
	var id = 0;
	return new android.View(id, false);
};

android.activities = [];

android.add = function(activity) {
	android.activities.push(activity);
	numActivities = android.activities.length;
};

android.get = function(activityID) {
	return android.activities[activityID];
};

android.lists = {};

android.List = function(id) {
	this.id = id;
	this.itemLayoutName = "";
	this.length = 0;
	this.render_function = function(view) {};
	this.onViewRender = function(f) {
		this.render_function = f;
	}
};

android.setUpList = function(listName, itemName, length) {
	var listID = android.findViewById(listName).id;
	var list = new android.List(listID);
	list.itemLayoutName = itemName;
	list.length = length;
	android.lists[listID] = list;
	return list;
}

var A_getLayoutName = function(listID, position) {
	return android.lists[listID].itemLayoutName;
};

var A_getListLength = function(listID) {
	return android.lists[listID].length;
};

var A_listItemMade = function(listID, position, objID) {
	var list = android.lists[listID];
	var view = new android.View(objID, true);
	list.render_function(view, position);
};

module.exports = android;