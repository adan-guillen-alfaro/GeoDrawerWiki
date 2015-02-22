package com.example.adan.geodrawerwiki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;;
import android.widget.TextView;

/**
 * Created by Adan on 23/1/15.
 */
public class drawerListAdapter extends BaseAdapter {

    Context mContext = null;

    drawerListAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (View)inflater.inflate(R.layout.drawer_option_item, null);
        }

        if (convertView != null)
        {
            CheckBox check = (CheckBox)convertView.findViewById(R.id.option_check);
            TextView text = (TextView)convertView.findViewById(R.id.option_desc);

            switch (position)
            {
                case 0:
                    check.setText("Wikipedia locations");
                    text.setText("Obtains data from Wikipedia page");

                    //TODO: Parametrico
                    check.setChecked(true);
                    check.setClickable(false);
                    break;
                case 1:
                    check.setText("Google locations");
                    text.setText("Obtains data from Google pages");

                    //TODO: Parametrico
                    check.setChecked(false);
                    check.setClickable(false);
                    break;
                default:
                    break;
            }
        }

        return  convertView;
    }

}
