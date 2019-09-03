package net.culiuliu.pokemondemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class GridFragment extends Fragment implements View.OnClickListener{


    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_grid, container, false);

        view.findViewById(R.id.charmander_btn).setOnClickListener(this);
        view.findViewById(R.id.blas_btn).setOnClickListener(this);
        view.findViewById(R.id.bul_btn).setOnClickListener(this);
        view.findViewById(R.id.cha_btn).setOnClickListener(this);
        view.findViewById(R.id.ivysaur_btn).setOnClickListener(this);
        view.findViewById(R.id.charmeleon_btn).setOnClickListener(this);
        view.findViewById(R.id.wartortle_btn).setOnClickListener(this);
        view.findViewById(R.id.squirtle_btn).setOnClickListener(this);
        view.findViewById(R.id.venusaur_btn).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity activity = (MainActivity) getActivity();
        activity.btn_clicked(v);
    }
}
