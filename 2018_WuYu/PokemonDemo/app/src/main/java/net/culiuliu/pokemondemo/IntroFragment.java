package net.culiuliu.pokemondemo;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends Fragment{

    private View view;


    public IntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_intro, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void set_select(String character_name) {

        TextView title = view.findViewById(R.id.detai_title);
        title.setText(character_name);
        int intro_id = getResources().getIdentifier(character_name, "raw", getActivity().getPackageName());
        TextView detail = view.findViewById(R.id.detail_txt);

        Scanner scanner = new Scanner(getResources().openRawResource(intro_id));
        String line = "";
        while (scanner.hasNext()) {
            line += scanner.nextLine();
        }
        detail.setText(line);

    }
}
