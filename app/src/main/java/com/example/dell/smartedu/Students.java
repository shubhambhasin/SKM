package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class Students extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addStudentButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView studentList;
    Notification_bar noti_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_student = getIntent();
        final String id = from_student.getStringExtra("id");
        addStudentButton = (Button)findViewById(R.id.addButton);
        studentList = (ListView) findViewById(R.id.studentList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(Students.this, "id class selected is = " +id, Toast.LENGTH_LONG).show();

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId", id);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(Students.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    int rollnumber=u.getInt("rollNumber");
                                    String name = u.getString("name");
                                    name= String.valueOf(rollnumber) + ". " + name;
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                        studentQuery.whereEqualTo("name", item);
                                        studentQuery.whereEqualTo("class", classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject u = (ParseObject) studentListRet.get(0);
                                                    String id = u.getObjectId();
                                                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                    Intent to_student_info = new Intent(Students.this, StudentInfo.class);
                                                    to_student_info.putExtra("id", id);
                                                    startActivity(to_student_info);
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });


                            } else {
                                Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();



        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Students.this, NewStudent.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });




    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Students.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Students.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        if(position==0)
        {
            /*Intent i = new Intent(MainActivity.this,CurrentOrder.class);
            startActivity(i);*/
        }

        if(position==2)
        {
            //  Intent i = new Intent(MainActivity.this,HomeSlider.class);
            //startActivity(i);
        }

        if(position==8)
        {
            Intent i = new Intent(Students.this,ChooseRole.class);
            startActivity(i);
        }
        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(Students.this, login.class);
            startActivity(i);
        }
    }
}
