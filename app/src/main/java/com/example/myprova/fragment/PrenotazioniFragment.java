package com.example.myprova.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myprova.R;

import com.example.myprova.fragment.DAO.Ripetizioni;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PrenotazioniFragment extends Fragment {
    private ArrayAdapter<String> spinnerAdapter;
    ArrayList<Ripetizioni> prenSel = new ArrayList<>();
    boolean option = false;
    int idPren;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fresh();
        return inflater.inflate(R.layout.fragment_prenotazioni, container, false);
    }

    public void fresh(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("action", "INIT");
        params.put("username", Connection.username);
        params.put("role", Connection.isAdmin);
        //params.put("caseMobile", "mobile");


        client.post(Connection.URL + "PrenotazioniServlet", params, new JsonHttpResponseHandler() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray goPren = response.getJSONArray(0);
                    JSONArray goSvol = response.getJSONArray(1);
                    JSONArray goDisd = response.getJSONArray(2);
                    final ArrayList<Ripetizioni> prenAtt = new ArrayList<>();
                    final ArrayList<Ripetizioni> prenEff = new ArrayList<>();
                    final ArrayList<Ripetizioni> prenDis = new ArrayList<>();
                    final ArrayList<String> titleListAtt = new ArrayList<>();
                    final ArrayList<String> titleListEff = new ArrayList<>();
                    final ArrayList<String> titleListDis = new ArrayList<>();

                    String days[]={"Lunedì","Martedì","Mercoledì","giovedì", "Venerdì"};
                    String hours[]={"14:00","15:00","16:00","17:00"};

                    for(int i=0;i<goPren.length();i++){
                        JSONObject json= goPren.getJSONObject(i);
                        prenAtt.add(new Ripetizioni((int)json.get("id_rip"),json.get("stato").toString(),json.get("giorno").toString(),(int)json.get("ora_i"),(int)json.get("ora_f"),(int)json.get("id_corso"),(int)json.get("id_docente"),json.get("username").toString()));
                        titleListAtt.add((int)json.get("id_corso") + ", " + days[(int)json.get("giorno")] +" alle ore " + hours[(int)json.get("ora_i")]  );
                    }
                    for(int i=0;i<goSvol.length();i++){
                        JSONObject json= goSvol.getJSONObject(i);
                       prenEff.add(new Ripetizioni((int)json.get("id_rip"),json.get("stato").toString(),json.get("giorno").toString(),(int)json.get("ora_i"),(int)json.get("ora_f"),(int)json.get("id_corso"),(int)json.get("id_docente"),json.get("username").toString()));
                        titleListEff.add((int)json.get("id_corso") + ", " + days[(int)json.get("giorno")] +" alle ore " + hours[(int)json.get("ora_i")]  );
                    }
                    for(int i=0;i<goDisd.length();i++){
                        JSONObject json= goDisd.getJSONObject(i);
                       prenDis.add(new Ripetizioni((int)json.get("id_rip"),json.get("stato").toString(),json.get("giorno").toString(),(int)json.get("ora_i"),(int)json.get("ora_f"),(int)json.get("id_corso"),(int)json.get("id_docente"),json.get("username").toString()));
                        titleListDis.add((int)json.get("id_corso") + ", " + days[(int)json.get("giorno")] +" alle ore " + hours[(int)json.get("ora_i")]  );
                    }


                    //----------------------------------------//
                    spinnerAdapter=new ArrayAdapter<String>(getContext().getApplicationContext(),android.R.layout.simple_list_item_1);
                    spinnerAdapter.add("prenotato");
                    spinnerAdapter.add("svolto");
                    spinnerAdapter.add("disdetto");
                    final Spinner sp=(Spinner) getView().findViewById(R.id.spinner);
                    sp.setAdapter(spinnerAdapter);
                    final View line = getView().findViewById(R.id.line1);
                    GradientDrawable drawable = (GradientDrawable)line.getBackground();
                    drawable.setStroke(3, Color.GREEN); // set stroke width and stroke color
                    //sp.setBackgroundColor(Color.GREEN);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ListView listaCorrente =  (ListView) getView().findViewById(R.id.activeListView);
                            ArrayAdapter<String> adapter  = new ArrayAdapter<String> (getContext().getApplicationContext(),
                                    android.R.layout.simple_list_item_1, titleListAtt);
                            TextView tv = (TextView)getView().findViewById(R.id.txt_empty);

                            if(prenAtt.size() == 0){
                                //Lista vuota
                                tv.setText("Non ci sono ancora prenotazioni attive!");
                            }else{
                                tv.setText("");
                            }
                            GradientDrawable drawable = (GradientDrawable)line.getBackground();

                            switch (position){
                                case 0:
                                    listaCorrente = (ListView) getView().findViewById(R.id.activeListView);
                                    adapter  = new ArrayAdapter<String> (getContext().getApplicationContext(),
                                            android.R.layout.simple_list_item_1, titleListAtt);
                                    prenSel = prenAtt;
                                    option = true;
                                    //idPren = position;
                                    // sp.setBackgroundColor(Color.GREEN);
                                    if(prenAtt.size() == 0){
                                        //Lista vuota
                                        tv.setText("Non ci sono ancora prenotazioni attive!");
                                    }else{
                                        tv.setText("");
                                    }
                                    drawable.setStroke(3, Color.GREEN);
                                    break;
                                case 1:
                                    listaCorrente = (ListView) getView().findViewById(R.id.activeListView);
                                    adapter  = new ArrayAdapter<String> (getContext().getApplicationContext(),android.R.layout.simple_list_item_1, titleListEff);
                                    prenSel = prenEff;
                                    option = false;
                                    if(prenEff.size() == 0){
                                        //Lista vuota
                                        tv.setText("Non ci sono ancora prenotazioni effettuate!");
                                    }else{
                                        tv.setText("");
                                    }
                                    //sp.setBackgroundColor(Color.BLUE);
                                    drawable.setStroke(3, Color.BLUE);
                                    break;
                                case 2:
                                    listaCorrente = (ListView) getView().findViewById(R.id.activeListView);
                                    adapter  = new ArrayAdapter<String> (getContext().getApplicationContext(),android.R.layout.simple_list_item_1, titleListDis);
                                    prenSel = prenDis;
                                    option = false;
                                    if(prenDis.size() == 0){
                                        //Lista vuota
                                        tv.setText("Non ci sono ancora prenotazioni disdette!");
                                    }else{
                                        tv.setText("");
                                    }
                                    //sp.setBackgroundColor(Color.RED);
                                    drawable.setStroke(3, Color.RED);
                                    break;
                            }

                            listaCorrente.setAdapter(adapter);

                            listaCorrente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(true);
                                    builder.setTitle(prenSel.get(pos).getId_corso());
                                    builder.setMessage("Docente: " + prenSel.get(pos).getId_docente());
                                    if(option){
                                        idPren = prenSel.get(pos).getId_rip();
                                        builder.setPositiveButton("Effettua", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                cambiaStato("effettuata", idPren);
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton("Disdici", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                cambiaStato("disdetta", idPren);
                                                dialog.dismiss();
                                            }

                                        });}
                                    builder.show();
                                }
                            });
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("failure",""+ statusCode+""+ errorResponse);
            }
        });
    }
    public void cambiaStato(final String stato,  int idPrenotazione){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("action", "STATO");
        params.put("docente", Connection.docente);
        params.put("ora", Connection.hours);
        params.put("giorno", Connection.days);
        params.put("stato", stato);
      //  params.put("id", idPrenotazione);
        params.put("caseMobile", "mobile");
        //Log.d("IDPREN :" ,Integer.toString(idPren));

        client.post(Connection.URL + "PrenotazioniServlet", params, new JsonHttpResponseHandler() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);Toast.makeText(getContext().getApplicationContext(), "Prenotazione " + stato + "!", Toast.LENGTH_SHORT).show();
                fresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("failure", ""+ statusCode+""+ errorResponse);
            }
        });
    }
}