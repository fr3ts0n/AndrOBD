package com.fr3ts0n.ecu.gui.androbd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.fr3ts0n.ecu.EcuDataPv;

public class PidCustomization extends Activity
{
    static EcuDataPv pv;
    ColorAdapter colorAdapter;
    Spinner spColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pid_customization);

        colorAdapter = new ColorAdapter(this, android.R.layout.simple_spinner_item);

        spColor = findViewById(R.id.pid_color);
        spColor.setAdapter(colorAdapter);
        spColor.setOnItemSelectedListener(colorSelected);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Integer color = (Integer)pv.get(EcuDataPv.FID_COLOR);
        if(color != null)
        {
            int pos = colorAdapter.getPosition(color);
            spColor.setSelection(pos);
        }
    }

    AdapterView.OnItemSelectedListener colorSelected = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Integer color = colorAdapter.getItem(position);
            // SET field of PID display
            pv.put(EcuDataPv.FID_COLOR, color);
            // SET preference value
            String mnemonic = (String)pv.get(EcuDataPv.FID_MNEMONIC);
            String prefName = mnemonic.concat("/").concat(EcuDataPv.FID_COLOR);
            MainActivity.prefs.edit().putInt(prefName, color).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };
}