package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyVoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyVoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyVoiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView listView;
    ArrayList<User> myVoicesList;
    private static MyVoicesAdapter adapter;
    private ProgressDialog dialog;
    TextView noContentTextView;

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyVoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyVoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyVoiceFragment newInstance(String param1, String param2) {
        MyVoiceFragment fragment = new MyVoiceFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_voice, container, false);

        listView = (ListView) view.findViewById(R.id.myvoices_listview);
        noContentTextView = (TextView) view.findViewById(R.id.my_voice_no_content_textview);
        myVoicesList = new ArrayList<>();
        adapter = new MyVoicesAdapter(myVoicesList, getActivity().getApplicationContext());
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("receiver_user_id", myVoicesList.get(position).id);
//                intent.putExtra("title", myVoicesList.get(position).name);
//                startActivity(intent);
//            }
//        });
        dialog = new ProgressDialog(getActivity());
        getFriendsListCall();
        return view;
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

    public void showProgressDialog() {
        dialog.setMessage("loading...");
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
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
                        getFriendsListCall();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 5000, 5000);
    }

    // SERVICE API CALL
    public void getFriendsListCall() {
        if (getActivity() == null) {  return; }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        noContentTextView.setVisibility(myVoicesList.isEmpty() ? View.VISIBLE : View.GONE);
        noContentTextView.setText("Loading...");

        StringRequest request = new StringRequest(Request.Method.POST, Constants.ALL_FRIENDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());
                        noContentTextView.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        UserResponse userResponse = gson.fromJson(response.toString(), UserResponse.class);
                        if (userResponse.friendsList.isEmpty()) {
                            noContentTextView.setVisibility(View.VISIBLE);
                            noContentTextView.setText(userResponse.message);
                        } else {
                            myVoicesList.clear();
                            myVoicesList.addAll(userResponse.friendsList);
                            adapter.notifyDataSetChanged();
                        }

                        startTimer();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                        noContentTextView.setVisibility(myVoicesList.isEmpty() ? View.VISIBLE : View.GONE);
                        noContentTextView.setText("No chat rooms available.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", PrefManager.getInstance(getActivity().getApplicationContext()).getUserId());
                return params;
            }
        };

        requestQueue.add(request);
    }
}
