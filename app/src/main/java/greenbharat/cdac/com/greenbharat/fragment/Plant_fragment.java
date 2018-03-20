package greenbharat.cdac.com.greenbharat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import greenbharat.cdac.com.greenbharat.R;

/**
 * Created by CDAC on 8/30/2016.
 */
public class Plant_fragment extends Fragment {

    TextView soil_type, max_height_textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plant_fragment, null, false);
        soil_type = (TextView) v.findViewById(R.id.soil_type);
        soil_type.setText(getActivity().getIntent().getStringExtra("soilType"));
        max_height_textView = (TextView) v.findViewById(R.id.max_height_textView);
        max_height_textView.setText(getActivity().getIntent().getStringExtra("maxHeight"));
        return v;
    }

}
