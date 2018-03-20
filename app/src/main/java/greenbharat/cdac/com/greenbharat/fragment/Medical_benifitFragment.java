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
public class Medical_benifitFragment extends Fragment {
    TextView tree_details;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_medical__benifit, null, false);
        tree_details = (TextView) v.findViewById(R.id.tree_details);
        tree_details.setText(getActivity().getIntent().getStringExtra("treeDetails"));
        return v;
    }
}
