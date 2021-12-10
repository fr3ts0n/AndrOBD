package com.fr3ts0n.ecu.gui.androbd;

import android.content.Context;
import android.graphics.Color;
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
        Color.LTGRAY,
        Color.DKGRAY,
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.GRAY,
        Color.CYAN,
        Color.parseColor("#FF000080"), // navy
        Color.YELLOW,
        Color.parseColor("#FF00FFFF"), // aqua
        Color.parseColor("#FFFF00FF"), // fuchsia
        Color.parseColor("#FF800000"), // maroon
        Color.parseColor("#FF00FF00"), // lime
        Color.MAGENTA,
        Color.parseColor("#FF808000"), // olive
        Color.parseColor("#FF800080"), // purple
        Color.parseColor("#FFC0C0C0"), // silver
        Color.parseColor("#FF008080"), // teal
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
