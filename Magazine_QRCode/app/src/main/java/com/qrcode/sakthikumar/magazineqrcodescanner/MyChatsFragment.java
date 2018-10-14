package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView;
    SearchView searchView;
    TextView noContentTextView;
    ArrayList<User> myChatsList, filteredList;
    private static MyChatAdapter adapter;
    private ProgressDialog dialog;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    String searchText = "";


    private OnFragmentInteractionListener mListener;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_chats, container, false);
        listView = (ListView) view.findViewById(R.id.mychats_listview);
        searchView = (SearchView) view.findViewById(R.id.my_chat_searchView);
        noContentTextView = (TextView) view.findViewById(R.id.my_chat_no_content_textview);

        myChatsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new MyChatAdapter(filteredList, getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        dialog = new ProgressDialog(getActivity());
        getConversationListCall();

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("receiver_user_id", myChatsList.get(position).id);
//                intent.putExtra("title", myChatsList.get(position).name);
//                startActivity(intent);
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        return view;
    }

    private void filter(String query) {
        query = query.toLowerCase();
        searchText = query;

        filteredList.clear();
        for (User model : myChatsList) {
            final String text = model.name.toLowerCase();
            if (text.contains(query)) {
                filteredList.add(model);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stopTimer();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showProgressDialog() {
        dialog.setMessage("loading...");
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer() {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        stopTimer();
                        getConversationListCall();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 5000, 5000);
    }

    // SERVICE API CALL
    public void getConversationListCall() {
        if (getActivity() == null) { return; }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        noContentTextView.setVisibility(myChatsList.isEmpty() ? View.VISIBLE : View.GONE);
        noContentTextView.setText("Loading...");

        StringRequest request = new StringRequest(Request.Method.POST, Constants.ALL_CONVERSATIONS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());
                        noContentTextView.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        UserResponse userResponse = gson.fromJson(response.toString(), UserResponse.class);
                        if (userResponse.conversationList.isEmpty()) {
                            noContentTextView.setVisibility(View.VISIBLE);
                            noContentTextView.setText(userResponse.message);
                        } else {
                            if (searchText.isEmpty()) {
                                myChatsList.clear();
                                filteredList.clear();
                                myChatsList.addAll(userResponse.conversationList);
                                filteredList.addAll(userResponse.conversationList);
                                adapter.notifyDataSetChanged();
                            } else {
                                myChatsList.clear();
                                myChatsList.addAll(userResponse.conversationList);
                            }
                        }

                        startTimer();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                        noContentTextView.setVisibility(myChatsList.isEmpty() ? View.VISIBLE : View.GONE);
                        noContentTextView.setText("No chat rooms available.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("login_user_id", PrefManager.getInstance(getActivity().getApplicationContext()).getUserId());
                return params;
            }
        };

        requestQueue.add(request);
    }
}
