package com.aritra.meconprojectlist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.aritra.meconprojectlist.Data.DatabaseHandler;
import com.aritra.meconprojectlist.Model.Project;
import com.aritra.meconprojectlist.R;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private EditText projectClient;
    private EditText projectTenure;
    private EditText projectCost;
    private CheckBox cbStatus;
    private Button saveButton;
    private DatabaseHandler db;

    private String[] employee = {"Aritra", "Arijit"};
    private String finalEmployee = employee[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = new DatabaseHandler(this);

        byPassActivity();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createPopupDialog() {

        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        projectClient = view.findViewById(R.id.popup_clientName);
        projectTenure = view.findViewById(R.id.popup_tenure);
        projectCost = view.findViewById(R.id.popup_totalCost);
        cbStatus = view.findViewById(R.id.cbStatus);
        saveButton = view.findViewById(R.id.saveButton);

        dialogBuilder.setView(view);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!projectClient.getText().toString().isEmpty()
                        && !projectTenure.getText().toString().isEmpty()
                        && !projectCost.getText().toString().isEmpty()) {
                    saveProjectToDB(v);
                }
            }
        });
    }

    private void saveProjectToDB(View v) {

        Project project = new Project();

        String newProjectEmployee = finalEmployee;
        String newProjectClient = projectClient.getText().toString();
        String newProjectTenure = projectTenure.getText().toString();
        String newProjectCost = projectCost.getText().toString();
        String newProjectStatus = cbStatus.isChecked() ? "ACTIVE": "CANCELLED";

        project.setEmployeeName(newProjectEmployee);
        project.setClientName(newProjectClient);
        project.setTenure(newProjectTenure);
        project.setTotalCost(newProjectCost);
        project.setStatus(newProjectStatus);

        //Save to DB
        db.addProject(project);

        Snackbar.make(v, "Project added to Database!", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                //start a new activity
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        }, 1200); //  1 second.

    }


    public void byPassActivity() {
        //Checks if database is empty; if not, then we just
        //go to ListActivity and show all added items

        if (db.getProjectsCountByEmployee(finalEmployee) > 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            finish();
        }

    }
}
