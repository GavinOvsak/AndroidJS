android = {};
android.Toast = {};
android.toast = function(message, length) {
	A_toast(message, length);
};
android.toast.short = 'short';
android.toast.long = 'long';
android.setContentView = function(layout_name) {
	A_setContentView(layout_name);
};

android.log = function(message) {
	A_log(message);
};

android.functions = [];
android.undefindedFIDs = [];

android.storeFunction = function(f) {
	var fID = 0;
	if (android.undefindedFIDs.length > 0) {
		fID = android.undefindedFIDs.pop();
		android.functions[fID] = f;
	} else {
		fID = android.functions.length;
		android.functions.push(f);
	}
	return fID;
};

android.runFunction = function(fID) {
	android.functions[fID]();
};

android.deleteFunction = function(fID) {
	android.functions[fID] = undefined;
	android.undefindedFIDs.push(fID);
};

android.replaceFunction = function(f, fID) {
	android.functions[fID] = f;
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
		A_intentPutExtraString(this.objectID, name, value);
		return this;
	};
	this.startActivity = function() {
		A_startActivity(this.objectID);
	};
};

android.ViewParent = {};
android.ViewParent.getTempChild = function(id) {
	var tID = A_makeTempObject(id); //id is an R.id.___
	return tID;
};

android.View = function(id, parent) {
	this.id = id;
	this.parent = parent;
	this.onClick_fID;
	this.getTempObjID = function() {
		return this.parent.getTempChild(this.id);
	};

	this.onClick = function(f) {
		if (this.onClick_fID == undefined) {
			this.onClick_fID = android.storeFunction(f);
		} else {
			android.replaceFunction(f, this.onClick_fID);
		}
		A_setOnClick(this.getTempObjID(), this.onClick_fID);
	};
	this.setText = function(newText) {
		A_setText(this.getTempObjID(), newText);
	};
	this.getText = function() {
		return A_getText(this.getTempObjID());
	};
	this.setVisibility = function(visibility) {
		A_setVisibility(this.getTempObjID(), visibility);
	};
	
	this.getTempChild = function(id) {
		return A_getTempChild(this.getTempObjID(), id);
	};
	
	this.findViewByID = function(name) {
		return new android.View(name, this);
	};
};
android.View.GONE = 'gone';
android.View.VISIBLE = 'visible';
android.View.INVISIBLE = 'invisible';

android.makeIntent = function(className) {
	return new android.Intent(className);
};

android.getStringExtra = function(key) {
	return A_getStringExtra(key);
};

android.findViewByID = function(viewName) {
	return new android.View(viewName, android.ViewParent);
};

android.activities = [];

android.add = function(activity) {
	android.activities.push(activity);
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
	};
	this.getTempChild = function(id) {
		return A_getTempChildFromArray(this.id, id);
	};
	this.getItemByIndex = function(index) {
		return new android.View(index, this);
	};
};

android.setUpList = function(listName, itemName, length) {
	var arrayID = A_makeArray(listName);
	if (android.lists[arrayID] != undefined) {
		var list = android.lists[arrayID];
	} else {
		var list = new android.List(arrayID);
		list.itemLayoutName = itemName;
		list.length = length;
		android.lists[arrayID] = list; 
	}
	A_setUpList(listName, arrayID); //Needs to be last so adapter doesn't happen yet
	return list;
};

var A_getLayoutName = function(listID, position) {
	return android.lists[listID].itemLayoutName;
};

var A_getListLength = function(listID) {
	return android.lists[listID].length;
};

var A_listItemMade = function(listID, position) {
	var list = android.lists[listID];
	var view = list.getItemByIndex(position);
	list.render_function(view, position);
};

module.exports = android;