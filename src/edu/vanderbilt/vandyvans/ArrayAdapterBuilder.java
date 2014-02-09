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

public class ArrayAdapterBuilder<T> {

    public static <U> ArrayAdapterBuilder<U> fromCollection(U[] objects) {
        ArrayAdapterBuilder<U> b = new ArrayAdapterBuilder<U>();
        b.mObjects = Arrays.asList(objects);
        return b;
    }
    
    public static <U> ArrayAdapterBuilder<U> fromCollection(List<U> objects) {
        ArrayAdapterBuilder<U> b = new ArrayAdapterBuilder<U>();
        b.mObjects = objects;
        return b;
    }
    
    public ArrayAdapterBuilder<T> withContext(Context ctx) {
        mCtx = ctx;
        return this;
    }

    public ArrayAdapterBuilder<T> withResource(int layoutResource) {
        mLayoutResource = layoutResource;
        return this;
    }
    
    public ArrayAdapterBuilder<T> withTextViewId(int tvId) {
        mTextViewId = tvId;
        return this;
    }
    
    public ArrayAdapterBuilder<T> withStringer(ToString<T> stringer) {
        mStringer = stringer;
        return this;
    }
    
    public ArrayAdapter<T> build() {
        if (mCtx == null)
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
    
    public interface ToString<U> {
        String apply(U object);
    }
    
    private Context mCtx;
    private int mLayoutResource;
    private int mTextViewId = -1;
    private List<T> mObjects;
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
                int resource,
                List<V> objects) 
        {
            super(ctx, resource, objects);
            mResource = resource;
        }
        
        public MyArrayAdapter(
                Context context, 
                int resource, 
                int textViewResourceId, 
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
        
        private View createViewFromResource(int position, View convertView, ViewGroup parent,
                int resource) {
            View view;
            TextView text;

            mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
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
