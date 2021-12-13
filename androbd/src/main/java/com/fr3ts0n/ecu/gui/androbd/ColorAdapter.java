package com.fr3ts0n.ecu.gui.androbd;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuDataPv;

/**
 * Adapter to handle display colors of data items
 * - automatic color selection based on item id/position
 * - support for customization
 */
public class ColorAdapter
    extends ArrayAdapter<Integer>
{
    /**
     * List of colors to be used for series
     */
    static final Integer[] colors =
    {
        0xFFF44336, // RED 500
        0xFFE91E63, // PINK 500
        0xFFFF2C93, // LIGHT PINK 500
        0xFF9C27B0, // PURPLE 500
        0xFF673AB7, // DEEP PURPLE 500
        0xFF3F51B5, // INDIGO 500
        0xFF2196F3, // BLUE 500
        0xFF03A9F4, // LIGHT BLUE 500
        0xFF00BCD4, // CYAN 500
        0xFF009688, // TEAL 500
        0xFF4CAF50, // GREEN 500
        0xFF8BC34A, // LIGHT GREEN 500
        0xFFCDDC39, // LIME 500
        0xFFFFEB3B, // YELLOW 500
        0xFFFFC107, // AMBER 500
        0xFFFF9800, // ORANGE 500
        0xFF795548, // BROWN 500
        0xFF607D8B, // BLUE GREY 500
        0xFF9E9E9E, // GREY 500
    };

    public ColorAdapter(Context context, int resource)
    {
        super(context, resource, colors);
    }

    /**
     * get color for an ID number preferrably unique pid number
     * this is to get persistent coloring/lining for each id
     * @param id id to get color for
     * @return color for given ID
     */
    public static int getItemColor(int id)
    {
        return colors[id % colors.length];
    }

    /**
     * Get color for a specified EcuDataItem
     * - includes customisation settings
     *
     * @param currPv current EcuDataPv
     * @return Display color for given EcuDataPv
     */
    public static int getItemColor(EcuDataPv currPv)
    {
        Integer pidColor = (Integer)currPv.get(EcuDataPv.FID_COLOR);
        if(pidColor == null)
        {
            int pid = currPv.getAsInt(EcuDataPv.FID_PID);
            pidColor = ColorAdapter.getItemColor(pid);
        }
        return pidColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView view = (TextView)super.getView(position, convertView, parent);
        view.setBackgroundColor(getItemColor(position));
        view.setText("");

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        TextView view = (TextView)super.getDropDownView(position, convertView, parent);
        view.setBackgroundColor(getItemColor(position));
        view.setText("");

        return view;
    }
}
