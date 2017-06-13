package com.example.user.bakingtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by user on 6/12/2017.
 */

public class SingleRecepieFragment extends Fragment {



    ListView listView;
ProgressBar bar;
    boolean from_tab;
    String type;
    ArrayList<String> steps_list;
    ArrayList<String> Ingredeint_list;
    TextView textView;
    StepSelected stepSelected;
    String videoURL;
    String description;
    String thumbnail;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.single_recepie_fragment,container,false);
        type=getArguments().getString("type");
        bar=(ProgressBar) v.findViewById(R.id.bar);
        from_tab=getArguments().getBoolean("fromtab");

        steps_list=new ArrayList<>();
        Ingredeint_list=new ArrayList<>();
        listView=(ListView) v.findViewById(R.id.step_list);
        make_network_call(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
if(from_tab==true){
    stepSelected.onstepselected(position);
}
else{
    Toast.makeText(getActivity(),"inelse " +position,Toast.LENGTH_LONG).show();
    makeNetworkcall2(position);


}

            }});
        textView=(TextView) v.findViewById(R.id.ingre_textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(),IngredientActivity.class);
                intent.putExtra("id",type);
                startActivity(intent);
            }
        });



        return v;
    }
    public void make_network_call(Context context) {
        bar.setVisibility(View.VISIBLE);

        final RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, NetworkUtils.RECIPIE_API, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                bar.setVisibility(View.INVISIBLE);
                try {
                    JSONArray array=new JSONArray(response);
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        String id=String.valueOf(object.getLong("id"));
                        if(type.equals(id)){
                            JSONArray steps=object.getJSONArray("steps");
                            JSONArray ingre=object.getJSONArray("ingredients");
                            for(int j=0;j<steps.length();j++){
                                JSONObject object1=steps.getJSONObject(j);
                                String des=object1.getString("shortDescription");
steps_list.add(des);
                            }
                            listView.setAdapter(new StepsAdapter(getActivity(),steps_list));

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                queue.stop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();

                queue.stop();
            }
        });
        queue.add(request);
    }
    public  void makeNetworkcall2(final int position){

        bar
                .setVisibility(View.VISIBLE);
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET, NetworkUtils.RECIPIE_API, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                bar.setVisibility(View.INVISIBLE);
                try {
                    JSONArray array=new JSONArray(response);
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        String id=String.valueOf(object.getLong("id"));
                        if(type.equals(id)){
                            Toast.makeText(getActivity(),"got " +type,Toast.LENGTH_LONG).show();

                            JSONArray steps=object.getJSONArray("steps");
                            //JSONArray ingre=object.getJSONArray("ingredients");
                            for(int j=0;j<steps.length();j++){
                                if(j==position) {
                                    Toast.makeText(getActivity(),"gotkk " +position +j,Toast.LENGTH_LONG).show();

                                    JSONObject object1=steps.getJSONObject(position);
                                    videoURL=object1.getString("videoURL");
                                    description=object1.getString("description");
                                    thumbnail=object.getString("thumbnailURL");
                                }
                            }



                            }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(getActivity(),StepDetailActivity.class);

                intent.putExtra("videoURL",videoURL);
                intent.putExtra("description",description);
                intent.putExtra("thumb",thumbnail);
                startActivity(intent);

                queue.stop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();

                queue.stop();
            }
        });
        queue.add(request);

    }












    public interface StepSelected{
        void onstepselected(int position);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        stepSelected=(StepSelected)(activity);
    }
}