
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jovianware.jv8.V8Exception;
import com.jovianware.jv8.V8Runner;

public class AndroidJSListAdapter extends ArrayAdapter<Object> {
  private final Context context;
  private final int listViewId;
  private final int listID;
  private ArrayList<Object> objects;
  private V8Runner v8;

  public AndroidJSListAdapter(Context context, int id, V8Runner v8, ArrayList<Object> objects, int listID) {
    super(context, id, new String[] {});
    this.listViewId = id;
    this.context = context;
    this.objects = objects;
    this.v8 = v8;
    this.listID = listID;
  }
  
  @Override
  public int getCount(){
    int count = 0;
    try {
      count = (int) v8.runJS("(program)", "A_getListLength(" + listID +")").toNumber();
  } catch (V8Exception e) {
    System.out.println("error");
  }
    return count;
  }

  /*@Override
  public boolean isEnabled(int position) {
    return true;
  }*/
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    String layoutName ="";
    try {
    layoutName = v8.runJS("(program)", "A_getLayoutName(" + listID + "," + position+")").toString();
  } catch (V8Exception e) {
    System.out.println("error");
  }
    int layoutResourceId = context.getResources().getIdentifier(layoutName, "layout", context.getPackageName());

    View rowView = inflater.inflate(layoutResourceId, parent, false);
    ArrayList<View> rowItems = (ArrayList<View>)objects.get(listID);
    rowItems.add(position, rowView);
//    int objID = objects.size() - 1;
    try {
    v8.runJS("(program)", "A_listItemMade("+listID + "," + position + ");");
  } catch (V8Exception e) {
    System.out.println("error");
  }
    return rowView;
  }
} 
