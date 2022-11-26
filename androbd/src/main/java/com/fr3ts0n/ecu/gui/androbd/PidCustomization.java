package com.fr3ts0n.ecu.gui.androbd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    TextView tvMin, tvMax, tvUnits, tvUpdate;
    /** SeekBars for customizing range
     * @implNote
     * Since all SeekBars operate on integer values only,
     * but final display range is defined in physical (float) values, I decided to use raw values
     * with item defined limits within SeekBars, but always display and return
     * physical item representation (using item specific conversion)
     */
    SeekBar sbMin, sbMax;
    /** Seek bar to set expected update period */
    SeekBar sbUpdatePeriod;
    /** Display range MIN/MAX values */
    Number dispMin, dispMax;
    /** expected PID update period [ms]*/
    long updatePeriod;

    Button btnCancel, btnOk, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pid_customization);

        btnOk       = findViewById(R.id.btnOk);
        btnCancel   = findViewById(R.id.btnCancel);
        btnReset    = findViewById(R.id.btnReset);

        btnOk.setOnClickListener(okClicked);
        btnCancel.setOnClickListener(cancelClicked);
        btnReset.setOnClickListener(resetClicked);

        // Color selection
        colorAdapter = new ColorAdapter(this, android.R.layout.simple_spinner_dropdown_item);
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

        tvUpdate = findViewById(R.id.txt_update_period);
        sbUpdatePeriod = findViewById(R.id.sb_update_period);
        sbUpdatePeriod.setOnSeekBarChangeListener(sbUpdateChanged);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        TextView tv = findViewById(R.id.data_item);
        tv.setText((String)item.pv.get(EcuDataPv.FID_DESCRIPT));

        // Color selection
        Integer color = ColorAdapter.getItemColor(item.pv);
        int pos = colorAdapter.getPosition(color);
        spColor.setSelection(pos);

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

        updatePeriod = item.updatePeriod_ms;
        sbUpdatePeriod.setProgress((int)(updatePeriod / 1000));
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
     * OK Button handler
     */
    View.OnClickListener okClicked = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
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

            prefName = mnemonic.concat("/").concat(EcuDataPv.FID_UPDT_PERIOD);
            ed.putLong(prefName, updatePeriod);

            ed.apply();

            finish();
        }
    };

    /**
     * CANCEL Button handler
     */
    View.OnClickListener cancelClicked = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            finish();
        }
    };

    /**
     * RESET Button handler
     */
    View.OnClickListener resetClicked = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            SharedPreferences.Editor ed = MainActivity.prefs.edit();

            String mnemonic = (String)item.pv.get(EcuDataPv.FID_MNEMONIC);
            // Reset Color selection
            String prefName = mnemonic.concat("/").concat(EcuDataPv.FID_COLOR);
            ed.remove(prefName);
            item.pv.remove(EcuDataPv.FID_COLOR);

            // Reset range selection
            prefName = mnemonic.concat("/").concat(EcuDataPv.FID_MIN);
            ed.remove(prefName);
            item.pv.remove(EcuDataPv.FID_MIN);

            prefName = mnemonic.concat("/").concat(EcuDataPv.FID_MAX);
            ed.remove(prefName);
            item.pv.remove(EcuDataPv.FID_MAX);

            ed.apply();

            finish();
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
            try
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
            catch(Exception ex)
            {
                // ignore
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    /**
     * Listener for SeekBar UpdatePeriod changes
     */
    SeekBar.OnSeekBarChangeListener sbUpdateChanged = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            updatePeriod = (long)(progress * 1000);
            tvUpdate.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };
}