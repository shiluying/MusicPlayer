package com.shiluying.musicplayer;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shiluying.musicplayer.Music.MusicContent.MusicItem;

import java.util.ArrayList;


public class ContentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private static final String ARG_PARAM1 = "MusicId";
    private static final String ARG_PARAM2 = "MusicName";
    private ArrayList<MusicItem> musicName=new ArrayList<MusicItem>();
    private OnContentFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContentFragment newInstance(int columnCount) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ArrayList<Integer> listid = getArguments().getIntegerArrayList(ARG_PARAM1);
            ArrayList<String> listname = getArguments().getStringArrayList(ARG_PARAM2);
            musicName.clear();
            for(int i=0;i<listid.size();i++){
                MusicItem musicItem=new MusicItem(listid.get(i),listname.get(i));
                musicName.add(musicItem);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ContentRecyclerViewAdapter(musicName, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContentFragmentInteractionListener) {
            mListener = (OnContentFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnContentFragmentInteractionListener {

        void OnContentFragmentInteractionListener(MusicItem mItem);
    }
}
