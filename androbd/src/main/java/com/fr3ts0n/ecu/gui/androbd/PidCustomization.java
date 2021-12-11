package com.fr3ts0n.ecu.gui.androbd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataPv;

/**
 * Customisation of OBD data item display
 * - Allow customisation of:
 *   - Display color
 *   - Display value range (MIN/MAX)
 * - per data item
 *
 * - Store customized values to preferences
 * - Re-use preference values at further runs
 */
public class PidCustomization
    extends Activity
{
    /** Data item to be customized */
    static EcuDataItem item;
    /**
     * Color selection
     */
    ColorAdapter colorAdapter;
    Spinner spColor;
    Integer dispColor;

    /**
     * Range selection
     */
    TextView tvMin, tvMax, tvUnits;
    /** SeekBars for customizing range
     * @implNote
     * Since all SeekBars operate on integer values only,
     * but final display range is defined in physical (float) values, I decided to use raw values
     * with item defined limits within SeekBars, but always display and return
     * physical item representation (using item specific conversion)
     */
    SeekBar sbMin, sbMax;
    /** Display range MIN/MAX values */
    Number dispMin, dispMax;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pid_customization);

        // Color selection
        colorAdapter = new ColorAdapter(this, android.R.layout.simple_spinner_item);
        spColor = findViewById(R.id.pid_color);
        spColor.setAdapter(colorAdapter);
        spColor.setOnItemSelectedListener(colorSelected);

        // Range selection
        tvMin = findViewById(R.id.range_min);
        tvMax = findViewById(R.id.range_max);
        tvUnits = findViewById(R.id.range_units);
        sbMin = findViewById(R.id.sb_min);
        sbMax = findViewById(R.id.sb_max);

        sbMin.setOnSeekBarChangeListener(sbChanged);
        sbMax.setOnSeekBarChangeListener(sbChanged);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        TextView tv = findViewById(R.id.data_item);
        tv.setText((String)item.pv.get(EcuDataPv.FID_DESCRIPT));

        // Color selection
        Integer color = ColorAdapter.getItemColor(item.pv);
        if(color != null)
        {
            int pos = colorAdapter.getPosition(color);
            spColor.setSelection(pos);
        }

        // Display range
        TextView tvUnits = findViewById(R.id.range_units);
        tvUnits.setText((String)item.pv.get(EcuDataPv.FID_UNITS));

        long minValue = item.rawMin();
        long maxValue = item.rawMax();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            sbMin.setMin((int) minValue);
            sbMax.setMin((int) minValue);
        }
        sbMin.setMax((int) maxValue);
        sbMax.setMax((int) maxValue);

        // Indicate current display range
        dispMin = (Number) item.pv.get(EcuDataPv.FID_MIN);
        if(dispMin == null) dispMin = item.physMin();
        sbMin.setProgress((int) item.rawVal(dispMin));

        dispMax = (Number) item.pv.get(EcuDataPv.FID_MAX);
        if(dispMax == null) dispMax = item.physMax();
        sbMax.setProgress((int) item.rawVal(dispMax));
    }

    @Override
    protected void onPause()
    {
        SharedPreferences.Editor ed = MainActivity.prefs.edit();
        String mnemonic = (String)item.pv.get(EcuDataPv.FID_MNEMONIC);
        // Save Color selection
        String prefName = mnemonic.concat("/").concat(EcuDataPv.FID_COLOR);
        ed.putInt(prefName, dispColor);

        // Save range selection
        prefName = mnemonic.concat("/").concat(EcuDataPv.FID_MIN);
        ed.putFloat(prefName, dispMin.floatValue());
        prefName = mnemonic.concat("/").concat(EcuDataPv.FID_MAX);
        ed.putFloat(prefName, dispMax.floatValue());

        ed.apply();

        super.onPause();
    }

    /**
     * Listener for Color selection
     */
    AdapterView.OnItemSelectedListener colorSelected = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            dispColor = colorAdapter.getItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };

    /**
     * Listener for SeekBar changes
     */
    SeekBar.OnSeekBarChangeListener sbChanged = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            String format = (String) item.pv.get(EcuDataPv.FID_FORMAT);
            if(seekBar == sbMin)
            {
                dispMin = item.physVal(progress);
                tvMin.setText(String.format(format, dispMin));
            }
            else
            {
                dispMax = item.physVal(progress);
                tvMax.setText(String.format(format, dispMax));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {

        }
    };
}