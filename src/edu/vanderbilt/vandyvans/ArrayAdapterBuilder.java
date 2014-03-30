package edu.vanderbilt.vandyvans;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A Convenience class that provide a fluent interface in building an
 * ArrayAdapter. As it stand, ArrayAdapter<T> has six constructors
 * (https://github
 * .com/android/platform_frameworks_base/blob/master/core/java/android
 * /widget/ArrayAdapter.java)
 * 
 * @author athran
 * 
 * @param <T>
 */
public class ArrayAdapterBuilder<T> {

    /**
     * Initialize the Builder by supplying an array of objects. The type of the
     * objects in this array will determine the type of the ArrayAdapter.
     * 
     * @param objects
     * @return
     */
    public static <U> ArrayAdapterBuilder<U> fromCollection(U[] objects) {
        ArrayAdapterBuilder<U> b = new ArrayAdapterBuilder<U>();
        b.mObjects = Arrays.asList(objects);
        return b;
    }
    
    /**
     * Initialize the Builder by supplying a List of objects. The type of the
     * objects in this List will determine the type of the ArrayAdapter.
     * 
     * @param objects
     * @return
     */
    public static <U> ArrayAdapterBuilder<U> fromCollection(List<U> objects) {
        ArrayAdapterBuilder<U> b = new ArrayAdapterBuilder<U>();
        b.mObjects = objects;
        return b;
    }
    
    /**
     * Supply the Context object. This is mandatory. The `build()` method will
     * throw an Exception if this is not set.
     * 
     * @param ctx
     * @return
     */
    public ArrayAdapterBuilder<T> withContext(Context ctx) {
        mCtx = ctx;
        return this;
    }

    /**
     * Supply the layout resource to be used when inflating the view. This is
     * mandatory. The `build()` method will throw an Exception if this is not
     * set.
     * 
     * @param layoutResource
     * @return
     */
    public ArrayAdapterBuilder<T> withResource(int layoutResource) {
        mLayoutResource = layoutResource;
        return this;
    }
    
    /**
     * Supply the id of the TextView in which the object's individual label
     * should be displayed. This is optional. Refer to the original ArrayAdapter
     * documentation for what happen when this is not set.
     * 
     * @param tvId
     * @return
     */
    public ArrayAdapterBuilder<T> withTextViewId(int tvId) {
        mTextViewId = tvId;
        return this;
    }

    /**
     * Supply the function ( T -> String ). This is optional. The object's
     * `toString()` will be used if this is not set.
     * 
     * @param stringer
     * @return
     */
    public ArrayAdapterBuilder<T> withStringer(ToString<T> stringer) {
        mStringer = stringer;
        return this;
    }
    
    /**
     * Build the ArrayAdapter.
     * 
     * @return
     */
    public ArrayAdapter<T> build() {
        if (mCtx == null || mLayoutResource == -1)
            throw new IllegalStateException("You must supply a Context object.");
        
        if (mObjects == null)
            mObjects = new LinkedList<T>();
        
        MyArrayAdapter<T> a;
        
        if (mTextViewId == -1) {
            a =  new MyArrayAdapter<T>(mCtx, mLayoutResource, mObjects);
        } else {
            a =  new MyArrayAdapter<T>(mCtx, mLayoutResource, mTextViewId, mObjects);
        }
        
        a.setStringer(mStringer);
        return a;
    }

    /**
     * A (T -> String) function in case you want a different behaviour as the
     * object's `toString()` method.
     * 
     * @author athran
     * 
     * @param <U>
     */
    public interface ToString<U> {
        String apply(U object);
    }
    
    private Context     mCtx            = null;
    private int         mLayoutResource = -1;
    private int         mTextViewId     = -1;
    private List<T>     mObjects;
    private ToString<T> mStringer;
    
    private ArrayAdapterBuilder() {}

    private static class MyArrayAdapter<V> extends ArrayAdapter<V> {

        ToString<V> mStringer;
        LayoutInflater mInflater;
        int mFieldId;
        int mResource;
        
        public MyArrayAdapter(Context context, int resource) {
            super(context, resource);
            mResource = resource;
        }
        
        public MyArrayAdapter(
                Context ctx, 
                int     resource,
                List<V> objects) 
        {
            super(ctx, resource, objects);
            mResource = resource;
        }
        
        public MyArrayAdapter(
                Context context, 
                int     resource,
                int     textViewResourceId,
                List<V> objects) 
        {
            super(context, resource, textViewResourceId, objects);
            mFieldId = textViewResourceId;
            mResource = resource;
        }

        public void setStringer(ToString<V> stringer) {
            mStringer = stringer;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent, mResource);
        }
        
        private View createViewFromResource(int       position,
                                            View      convertView,
                                            ViewGroup parent,
                                            int       resource) {
            View view;
            TextView text;

            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if (convertView == null) {
                view = mInflater.inflate(resource, parent, false);
            } else {
                view = convertView;
            }

            try {
                if (mFieldId == 0) {
                    //  If no custom field is assigned, assume the whole resource is a TextView
                    text = (TextView) view;
                } else {
                    //  Otherwise, find the TextView field within the layout
                    text = (TextView) view.findViewById(mFieldId);
                }
            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }

            V item = getItem(position);
            if (item instanceof CharSequence) {
                text.setText((CharSequence)item);
            } else {
                if (mStringer == null)
                    text.setText(item.toString());
                else
                    text.setText(mStringer.apply(item));
            }

            return view;
        }
        
    }
    
}
