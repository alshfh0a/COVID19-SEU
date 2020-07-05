package com.seu.covid_19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterAdapter extends ArrayAdapter<UserModel>
{
        public AdapterAdapter(Context context, ArrayList<UserModel> users)
        {
            super(context, 0, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // Get the data item for this position
            UserModel user = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate
                        (R.layout.item_view, parent, false);
            }

            // Lookup view for data population
            TextView ID_View = (TextView) convertView.findViewById(R.id.Gov_view);
            TextView Phone_View = (TextView) convertView.findViewById(R.id.Phone_view);
            TextView Status_View = (TextView) convertView.findViewById(R.id.status_view);

            // Populate the data into the template view using the data object
            ID_View.setText(user.UserGvID);
            Phone_View.setText(user.UserPhone);
            Status_View.setText(user.Risk+"");

            // Return the completed view to render on screen
            return convertView;
        }
}

