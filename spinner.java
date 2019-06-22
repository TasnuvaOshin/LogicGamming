package com.example.hp.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.myapplication.Util.UtilWork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    ArrayList<String> list, rsm_list, dsm_list, pso_list, team_list, region_list, short_rsm, final_img_list;
    ArrayAdapter<String> adapter, rsm_adapter, dsm_adapter, pso_adapter, team_adapter, region_adapter, rsm_short_adapter;
    public Spinner spinner, rsm_spinner, dsm_spinner, pso_spinner, team_spinner, region_spinner, short_rsm_spinner;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private EditText editText, editText2;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int rsm = 0;
    LinearLayout linearLayout;
    private TextView gm_name, rsm_name, dsm_name, pso_name, team_name, region_name;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gm_name = findViewById(R.id.tv_gm_name);
        rsm_name = findViewById(R.id.tv_rsm_name);
        dsm_name = findViewById(R.id.tv_dsm_name);
        pso_name = findViewById(R.id.tv_pso_name);
        team_name = findViewById(R.id.tv_team_name);
        region_name = findViewById(R.id.tv_region_name);
        editText = findViewById(R.id.from_date);
        editText2 = findViewById(R.id.to_date);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Processing");
        linearLayout = findViewById(R.id.root);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(dayOfMonth);
                        editText.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(dayOfMonth);
                        editText2.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("data");

        list = new ArrayList<String>();
        rsm_list = new ArrayList<String>();
        dsm_list = new ArrayList<String>();
        pso_list = new ArrayList<String>();
        team_list = new ArrayList<String>();
        region_list = new ArrayList<String>();
        short_rsm = new ArrayList<String>();
        final_img_list = new ArrayList<String>();

        spinner = findViewById(R.id.spinner);
        region_spinner = findViewById(R.id.spinner_region);
        rsm_spinner = findViewById(R.id.spinner_rsm);
        dsm_spinner = findViewById(R.id.spinner_dsm);
        pso_spinner = findViewById(R.id.spinner_pso);
        team_spinner = findViewById(R.id.spinner_team);

        CallApi();


//Gm TO Rsm
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("sm", "THIS " + parent.getItemAtPosition(position));
                CallRsm(String.valueOf(parent.getItemAtPosition(position)));
                gm_name.setText(String.valueOf(parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("***********", "THIS ");
            }
        });


//RSM to DSM
        rsm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("rsm", "THIS " + adapterView.getItemAtPosition(i));
                CallDsm(String.valueOf(adapterView.getItemAtPosition(i)));
                rsm_name.setText(String.valueOf(adapterView.getItemAtPosition(i)));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//DSM to PSO
        dsm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("sm", "THIS " + adapterView.getItemAtPosition(i));
                CallPso(String.valueOf(adapterView.getItemAtPosition(i)));
                dsm_name.setText(String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pso_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pso_name.setText(String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        team_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                team_name.setText(String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                region_name.setText(String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void CallPso(String s) {
        pso_list.clear();
        JsonFetchPso jsonFetchPso = new JsonFetchPso();
        jsonFetchPso.execute("http://red-canvas.com/orcefapp/pso_name.php?id=" + s);
    }

    private void CallDsm(String s) {
        dsm_list.clear();
        JsonFetchDsm jsonFetchDsm = new JsonFetchDsm();
        jsonFetchDsm.execute("http://red-canvas.com/orcefapp/dsm_name.php?id=" + s);

    }


    private void CallRsm(String itemAtPosition) {
        short_rsm.clear();
        JsonFetchShortRsm jsonFetchShortRsm = new JsonFetchShortRsm();
        jsonFetchShortRsm.execute("http://red-canvas.com/orcefapp/rsm_name.php?id=" + itemAtPosition);

    }


    private void CallApi() {
        JsonFetchRegion jsonFetchRegion = new JsonFetchRegion();
        JsonFetchTeam jsonFetchTeam = new JsonFetchTeam();
        JsonFetch jsonFetch = new JsonFetch();
        jsonFetchRegion.execute("http://red-canvas.com/orcefapp/region_name.php");
        jsonFetchTeam.execute("http://red-canvas.com/orcefapp/team_name.php");
        jsonFetch.execute("http://red-canvas.com/orcefapp/gm_name.php");


    }

    //Button CLick to get data
    public void GetAllImage(View view) {
        progressDialog.show();
        String sm = String.valueOf(gm_name.getText());
        String rsm = String.valueOf(rsm_name.getText());
        String dsm = String.valueOf(dsm_name.getText());
        String pso = String.valueOf(pso_name.getText());
        String team = String.valueOf(team_name.getText());
        String region = String.valueOf(region_name.getText());
        String f_date = String.valueOf(editText.getText());
        String t_date = String.valueOf(editText2.getText());
        if (TextUtils.isEmpty(editText.getText())) {
            progressDialog.dismiss();
            UtilWork.NotifyUser(linearLayout, "Enter the Date's Correctly");
            editText.requestFocus();
        }

        if (TextUtils.isEmpty(editText2.getText())) {
            progressDialog.dismiss();
            UtilWork.NotifyUser(linearLayout, "Enter the Date's Correctly");
            editText2.requestFocus();
        } else {
            FinalImageFetching finalImageFetching = new FinalImageFetching();
            finalImageFetching.execute("http://red-canvas.com/orcefapp/Get_all_img.php?f_date=" + f_date + "&t_date=" + t_date);
            progressDialog.dismiss();
        }

    }

    public class FinalImageFetching extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //todo
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("img");
                    Log.d("img", name);
                    // arrayList.add(new JsonDataList(name));
                    final_img_list.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class JsonFetchShortRsm extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, short_rsm);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            rsm_spinner.setAdapter(spinnerArrayAdapter);
            spinnerArrayAdapter.notifyDataSetChanged();
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("rsm");
                    Log.d("short", name);
                    // arrayList.add(new JsonDataList(name));
                    if (!name.equals("")) {
                        //Push names into the array


// repeated additions:
                        if (!short_rsm.contains(name)) {
                            short_rsm.add(name);
                        }

                    } else {
                        short_rsm.clear();
                    }

                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class JsonFetchRegion extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter<String> spinnerArrayAdapterRegion = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, region_list);
            spinnerArrayAdapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            region_spinner.setAdapter(spinnerArrayAdapterRegion);
            spinnerArrayAdapterRegion.notifyDataSetChanged();


        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("region");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    region_list.add(name);

                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class JsonFetchTeam extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter<String> spinnerArrayAdapterTeam = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, team_list);
            spinnerArrayAdapterTeam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            team_spinner.setAdapter(spinnerArrayAdapterTeam);
            spinnerArrayAdapterTeam.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("team");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    team_list.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class JsonFetchPso extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, pso_list);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            pso_spinner.setAdapter(spinnerArrayAdapter);
            spinnerArrayAdapter.notifyDataSetChanged();
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    pso_list.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class JsonFetchDsm extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dsm_list);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            dsm_spinner.setAdapter(spinnerArrayAdapter);
            spinnerArrayAdapter.notifyDataSetChanged();
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("dsm");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    dsm_list.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

//    public class JsonFetchRsm extends AsyncTask<String, String, String> {
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            // rsm_adapter.notifyDataSetChanged();
//        }
//
//        HttpURLConnection httpURLConnection = null;
//        String mainfile;
//        BufferedReader bufferedReader = null;
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String link = strings[0];
//            try {
//                URL url = new URL(link);
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.connect();
//
//                InputStream inputStream = httpURLConnection.getInputStream();
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                StringBuffer stringBuffer = new StringBuffer();
//                String line = " ";
//                while ((line = bufferedReader.readLine()) != null) {
//
//                    stringBuffer.append(line);
//
//
//                }
//
//                mainfile = stringBuffer.toString();
//
//
//                JSONArray parent = new JSONArray(mainfile);
//                int i = 0;
//                while (i <= parent.length()) {
//
//                    JSONObject child = parent.getJSONObject(i);
//
//                    String name = child.getString("rsm");
//                    Log.d("name", name);
//                    // arrayList.add(new JsonDataList(name));
//                    rsm_list.add(name);
//                    i++;
//                }
//
//
//                return mainfile;
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//
//    }


    public class JsonFetch extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner.setAdapter(spinnerArrayAdapter);
            spinnerArrayAdapter.notifyDataSetChanged();
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("sm");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    list.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }


}

