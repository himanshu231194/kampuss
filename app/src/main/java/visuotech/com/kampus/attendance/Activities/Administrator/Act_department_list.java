package visuotech.com.kampus.attendance.Activities.Administrator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import visuotech.com.kampus.attendance.Adapter.Ad_cou_dept;
import visuotech.com.kampus.attendance.Adapter.Ad_course;
import visuotech.com.kampus.attendance.Adapter.Ad_department;
import visuotech.com.kampus.attendance.MarshMallowPermission;
import visuotech.com.kampus.attendance.Model.Course;
import visuotech.com.kampus.attendance.Model.Department;
import visuotech.com.kampus.attendance.R;
import visuotech.com.kampus.attendance.SessionParam;
import visuotech.com.kampus.attendance.retrofit.BaseRequest;
import visuotech.com.kampus.attendance.retrofit.RequestReciever;

public class Act_department_list extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Ad_cou_dept adapter;
    Ad_department adapter2;
    RecyclerView rv_list;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressbar;
    Spinner spinner_course;

    Context context;
    Activity activity;
    SessionParam sessionParam;
    MarshMallowPermission marshMallowPermission;
    private BaseRequest baseRequest;
    private SearchableSpinner mSearchableSpinner;
    EditText inputSearch;

    ArrayList<Department> department_list;
    ArrayList<String>department_name_list=new ArrayList<>();

    ArrayList<Course> course_list;
    ArrayList<String>course_name_list=new ArrayList<>();

    ArrayList<Course> course_list2;
    ArrayList<String>course_name_list2=new ArrayList<>();

    ImageView iv_add;
    Dialog mDialog;
    TextView tv_alert;
    String dept_name,course,cour_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_department_list);

        //-------------------------toolbar------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Department List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//-------------------------classes------------------------------------------
        context = this;
        activity = this;
        sessionParam = new SessionParam(getApplicationContext());
        marshMallowPermission = new MarshMallowPermission(activity);
//        adapter2=new Ad_department(department_list,context);

//-------------------------recyclerview------------------------------------------
        rv_list=findViewById(R.id.rv_list);
        progressbar=findViewById(R.id.progressbar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setItemAnimator(new DefaultItemAnimator());

        adapter2=new Ad_department(department_list,context);
        inputSearch = findViewById(R.id.inputSearch);
        iv_add =  findViewById(R.id.iv_add);


        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog=new Dialog(context);

                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  //without extar space of title
                mDialog.setContentView(R.layout.add_department);
                mDialog.setCanceledOnTouchOutside(false);
                //dialog layout

                ImageView iv_cancel_dialog;
                Button btn_save;

                final EditText et_name;
//
                iv_cancel_dialog=mDialog.findViewById(R.id.iv_cancel_dialog);
                tv_alert=mDialog.findViewById(R.id.tv_alert);
                spinner_course=mDialog.findViewById(R.id.spinner_course);
                ApigetCourseList();

                et_name= mDialog.findViewById(R.id.et_name);
                btn_save= mDialog.findViewById(R.id.btn_save);
                spinner_course.setOnItemSelectedListener(Act_department_list.this);



                iv_cancel_dialog.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                        ApigetCourse();


                    }
                });
                btn_save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (et_name.getText().toString().isEmpty()) {
                            tv_alert.setVisibility(View.VISIBLE);
                            tv_alert.setTextColor(getResources().getColor(R.color.DarkRed));
                            tv_alert.setText("please enter course name!!");
                        } else{
                            dept_name=et_name.getText().toString();
                            apiAdddepartment();
                            et_name.setText("");
                        }


                    }
                });
                mDialog.show();
            }
        });


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        ApigetCourse();
        ApigetDepartment();

    }

    public void apiAdddepartment() {
        baseRequest = new BaseRequest();
        baseRequest.setBaseRequestListner(new RequestReciever() {
            @Override
            public void onSuccess(int requestCode, String Json, Object object) {

                tv_alert.setVisibility(View.VISIBLE);
                tv_alert.setTextColor(getResources().getColor(R.color.Green));
                tv_alert.setText("course saved sucessfully!!");

            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkFailure(int requestCode, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            }
        });
        RequestBody dept_name_ = RequestBody.create(MediaType.parse("text/plain"), dept_name);
        RequestBody org_id_ = RequestBody.create(MediaType.parse("text/plain"), sessionParam.org_id);
        RequestBody cour_id_ = RequestBody.create(MediaType.parse("text/plain"), cour_id);


        baseRequest.callAPIDept(1,"http://collectorexpress.in/",dept_name_,org_id_,cour_id_);

    }


    private void ApigetCourse(){
        baseRequest = new BaseRequest();
        baseRequest.setBaseRequestListner(new RequestReciever() {
            @Override
            public void onSuccess(int requestCode, String Json, Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(Json);
                    JSONArray jsonArray=jsonObject.optJSONArray("user");

                    course_list=baseRequest.getDataList(jsonArray,Course.class);
                    for (int i=0;i<course_list.size();i++){
                        course_name_list.add(course_list.get(i).getCourse_name());
                    }

                    adapter=new Ad_cou_dept(course_list,context);
                    rv_list.setAdapter(adapter);




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {

            }
            @Override
            public void onNetworkFailure(int requestCode, String message) {

            }
        });
        String remainingUrl2="/Kampus/Api2.php?apicall=course_list&organization_id="+sessionParam.org_id;
        baseRequest.callAPIGETData(1, remainingUrl2);
    }

    private void ApigetCourseList(){
        baseRequest = new BaseRequest();
        baseRequest.setBaseRequestListner(new RequestReciever() {
            @Override
            public void onSuccess(int requestCode, String Json, Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(Json);
                    JSONArray jsonArray=jsonObject.optJSONArray("user");

                    course_list2=baseRequest.getDataList(jsonArray,Course.class);
                    for (int i=0;i<course_list2.size();i++){
                        course_name_list2.add(course_list2.get(i).getCourse_name());
                    }

                    ArrayAdapter adapter_course = new ArrayAdapter(context,android.R.layout.simple_spinner_item,course_name_list2);
                    adapter_course.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_course.setAdapter(adapter_course);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {

            }
            @Override
            public void onNetworkFailure(int requestCode, String message) {

            }
        });
        String remainingUrl2="/Kampus/Api2.php?apicall=course_list&organization_id="+sessionParam.org_id;
        baseRequest.callAPIGETData(1, remainingUrl2);
    }



    private void ApigetDepartment(){
        baseRequest = new BaseRequest();
        baseRequest.setBaseRequestListner(new RequestReciever() {
            @Override
            public void onSuccess(int requestCode, String Json, Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(Json);
                    JSONArray jsonArray=jsonObject.optJSONArray("user");

                    department_list=baseRequest.getDataList(jsonArray,Department.class);
                    for (int i=0;i<department_list.size();i++){
                        department_name_list.add(department_list.get(i).getDepartment_name());
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {

            }
            @Override
            public void onNetworkFailure(int requestCode, String message) {

            }
        });
        String remainingUrl2="/Kampus/Api2.php?apicall=department_list&organization_id="+sessionParam.org_id;
        baseRequest.callAPIGETData(1, remainingUrl2);
    }






    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Department> dept_list2 = new ArrayList<>();

        //looping through existing elements
        for (int i=0;i<department_list.size();i++){
            if (department_list.get(i).getDepartment_name().toLowerCase().contains(text.toLowerCase())){
                Department department=new Department();
                department.setDepartment_name(department_list.get(i).getDepartment_name());
                dept_list2.add(department);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter2.filterList(dept_list2);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()){
            case R.id.spinner_course :
                course=course_list2.get(i).getCourse_name();
                cour_id =course_list2.get(i).getCourse_id();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(Act_department_list.this, Administrator_Act_home.class);
        startActivity(i);
        finish();
        return true;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Act_department_list.this, Administrator_Act_home.class);
        startActivity(i);
        finish();
    }


}
