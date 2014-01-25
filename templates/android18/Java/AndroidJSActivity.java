
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jovianware.jv8.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidJSActivity extends Activity {
	
	static V8Runner v8;
	static boolean v8started = false;
	static ArrayList<Object> objects;
	static ArrayList<Object> tempObjects;
	static ArrayList<Integer> emptyTempObjectsIndices;
	static String ACTIVITY_ID;
	static Map<String, Integer> viewsAlreadySetUp;
	
	public AndroidJSActivity() {
		if (viewsAlreadySetUp == null) {
			viewsAlreadySetUp = new HashMap<String, Integer>();
		}
	};
	
	public boolean setupV8() {
		v8 = new V8Runner();
		objects = new ArrayList<Object>();
		tempObjects = new ArrayList<Object>();
		emptyTempObjectsIndices = new ArrayList<Integer>();
		defineAPI();
		try {
			v8.runJS("(program)", "var module={}; module.exports = {}; var exports = module.exports; var require=function(file){A_require(file);return module.exports;};");
		} catch (V8Exception e) {
			System.out.println(e);
		}
		return require("basic.js");
	}
	
	public boolean require(String file) {
		try {
			StringBuilder buf=new StringBuilder();
			InputStream is = this.getAssets().open(file);
		    BufferedReader in= new BufferedReader(new InputStreamReader(is));
		    String str;
		    while ((str=in.readLine()) != null) {
		      buf.append(str+"\n"); 
		    }
		    in.close();
			try {
				v8.runJS("(require)", "var module={}; module.exports = {}; var exports = module.exports;" + buf.toString());
				return true;
			} catch (V8Exception e) {
				System.out.println(e);
			}
		} catch (IOException e1) {
			System.out.println(e1);
		}
		return false;
	}
	
	public int storeTempObject(Object obj) {
		//System.out.println("Object List Size: " + tempObjects.size());
		int objID = 0;
		if (emptyTempObjectsIndices.size() > 0) {
			objID = emptyTempObjectsIndices.remove(0);
			tempObjects.set(objID, obj);
		} else {
			objID = tempObjects.size();
			tempObjects.add(obj);
		}
		return objID;
	}
	
	public Object retreiveTempObject(int objID) {
		Object retreivedObject = tempObjects.get(objID);
		tempObjects.set(objID, null);
		emptyTempObjectsIndices.add(objID);
		return retreivedObject;
	}
	
	public void defineAPI() {
		v8.map("A_toast", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			String message = ((V8String)args[0]).toString();
			String type = ((V8String)args[1]).toString();
			int length = Toast.LENGTH_SHORT;
			if (type.equals("short")) {
				length = Toast.LENGTH_SHORT;
			} else if(type.equals("long")) {
				length = Toast.LENGTH_LONG;
			}
			Toast toast = Toast.makeText(getApplicationContext(), message, length);				
			toast.show();
		    return v8.val(0);
		  }
		});
		
		v8.map("A_log", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			System.out.println(args[0].toString());
		    return v8.val(0);
		  }
		});

		v8.map("A_require", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			require(args[0].toString()+".js");
		    return v8.val(0);
		  }
		});

		v8.map("A_setContentView", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			String layoutName = ((V8String)args[0]).toString();
  		    int layoutResourceId = getApplicationContext().getResources().getIdentifier(layoutName, "layout", getApplicationContext().getPackageName());

  		    setContentView(layoutResourceId);
			return v8.val(0);
		  }
		});

		v8.map("A_setUpList", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			String listName = ((V8String)args[0]).toString();
			int objID = (int) args[1].toNumber();
			int layoutResourceId = getApplicationContext().getResources().getIdentifier(listName, "id", getApplicationContext().getPackageName());
	  		ListView lv = (ListView) findViewById(layoutResourceId);
	  		lv.setAdapter(new AndroidJSListAdapter(getApplicationContext(), layoutResourceId, v8, objects, objID));
			return v8.val(0);
		  }
		});
		
		v8.map("A_getTempChild", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			  int objID = (int) args[0].toNumber();
			  String childID = ((V8String)args[1]).toString();
			  View tempObject = (View) retreiveTempObject(objID);
			  int viewResourceId = getApplicationContext().getResources().getIdentifier(childID, "id", getApplicationContext().getPackageName());
	  		  int tempID = storeTempObject(tempObject.findViewById(viewResourceId));
	  		  return v8.val(tempID);
		  }
		});
		
		v8.map("A_makeTempObject", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			String id = ((V8String)args[0]).toString();
  		    int viewResourceId = getApplicationContext().getResources().getIdentifier(id, "id", getApplicationContext().getPackageName());
  		    Object tempObject = findViewById(viewResourceId);
  		    int tempID = storeTempObject(tempObject);
			return v8.val(tempID);
		  }
		});
		
		v8.map("A_getTempChildFromArray", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			int objID = (int) args[0].toNumber();
			int index = (int) args[1].toNumber();
			ArrayList<View> rowItems = (ArrayList<View>)objects.get(objID);
			return v8.val(storeTempObject(rowItems.get(index)));
		  }
		});
			
		v8.map("A_makeArray", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			String listName = args[0].toString();
			int objID = 0;
			if (viewsAlreadySetUp.containsKey(listName)) {
				objID = viewsAlreadySetUp.get(listName);
			} else {
				ArrayList<View> rowItems = new ArrayList<View>();
				objects.add(rowItems);
				objID = objects.size()-1;
				viewsAlreadySetUp.put(listName, objID);
			}
			return v8.val(objID);
		  }
		});
		
		v8.map("A_setOnClick", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			int viewID = (int) ((V8Number)args[0]).toNumber();
			final int functionID = (int) ((V8Number)args[1]).toNumber();
			View view = (View)retreiveTempObject(viewID);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						v8.runJS("(program)","android.runFunction("+functionID+")");
					} catch(V8Exception e) {
						System.out.println(e);
					}
				}
			});
			return v8.val(0);
		  }
		});
		
		v8.map("A_setVisibility", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			int viewID = (int) ((V8Number)args[0]).toNumber();
			String requested_visibility = ((V8String)args[1]).toString();
			View view = (View)retreiveTempObject(viewID);
			int visibility = View.VISIBLE;
			if (requested_visibility.equals("gone")) {
				visibility = View.GONE;
			} else if (requested_visibility.equals("visible")) {
				visibility = View.VISIBLE;
			} else if (requested_visibility.equals("invisible")) {
				visibility = View.INVISIBLE;
			}
			view.setVisibility(visibility);
			return v8.val(0);
		  }
		});
		
		v8.map("A_setText", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			int viewID = (int) ((V8Number)args[0]).toNumber();
			String newText = ((V8String)args[1]).toString();
			View view = (View)retreiveTempObject(viewID);
			if (view instanceof TextView) {
				TextView tv = (TextView)view;
				tv.setText(newText);
			}
			return v8.val(0);
		  }
		});
			
		v8.map("A_getText", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			int viewID = (int) ((V8Number)args[0]).toNumber();
			View view = (View)retreiveTempObject(viewID);
			if (view instanceof TextView) {
				TextView tv = (TextView)view;
				return v8.val(tv.getText().toString());
			}
			return v8.val("");
		  }
		});
			
		v8.map("A_getStringExtra", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			Intent i = getIntent();
			String key = ((V8String)args[0]).toString();
  		    if (i.hasExtra(key)) {
  		    	return v8.val(i.getStringExtra(key));
  		    } else { 
  				return v8.val("");
  		    }
		  }
		});
		
		//Move intents to tempObject model?
		v8.map("A_makeIntent", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			  String className = ((V8String)args[0]).toString();
			  int objectID = -1;
			  try {
				  Intent newIntent = new Intent(getApplicationContext(), Class.forName(getApplicationContext().getPackageName() + "." + className));
				  objects.add(newIntent);
				  objectID = objects.size() - 1;
			  } catch (ClassNotFoundException e) {}
			  return v8.val(objectID);
		  }
		});
		
		v8.map("A_intentPutExtraString", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			  int objectID = (int) ((V8Number)args[0]).toNumber();
			  String name = ((V8String)args[1]).toString();
			  String value = ((V8String)args[2]).toString();
			  Object obj = objects.get(objectID);
			  if (obj instanceof Intent) {
				  Intent intent = (Intent)obj;
				  intent.putExtra(name, value);
				  return v8.val(0);
			  } else {
				  return v8.val(-1);
			  }
		  }
		});

		v8.map("A_startActivity", new V8MappableMethod() {
		  @Override
		  public V8Value methodToRun(V8Value[] args) {
			  int objectID = (int) ((V8Number)args[0]).toNumber();
			  Object obj = objects.get(objectID);
			  if (obj instanceof Intent) {
				  Intent intent = (Intent)obj;
				  startActivity(intent);
				  return v8.val(0);
			  } else {
				  return v8.val(-1);
			  }
		  }
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!v8started) {
			v8started = setupV8(); 
		} else {
			defineAPI();
		}
		
		if (v8started) {
			try {
				v8.runJS("(program)", "android.get(" + ACTIVITY_ID + ").call('onCreate')");
			} catch (V8Exception e) {
				System.out.println("error");
			}
		}
	}
}
