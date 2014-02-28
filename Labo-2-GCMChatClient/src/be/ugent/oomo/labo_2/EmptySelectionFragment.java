package be.ugent.oomo.labo_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link EmptySelectionFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class EmptySelectionFragment extends Fragment {

	public EmptySelectionFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_empty_selection, container, false);
	}

}
