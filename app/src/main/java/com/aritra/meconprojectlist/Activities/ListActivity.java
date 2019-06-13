package com.aritra.meconprojectlist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.aritra.meconprojectlist.Data.DatabaseHandler;
import com.aritra.meconprojectlist.Model.Project;
import com.aritra.meconprojectlist.R;
import com.aritra.meconprojectlist.UI.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Project> projectList;
    private List<Project> projectlistItems;
    private DatabaseHandler db;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    private EditText projectClient;
    private EditText projectTenure;
    private EditText projectCost;
    private CheckBox cbStatus;
    private Button saveButton;

    private String[] employee = {"Aritra", "Arijit"};
    private String finalEmployee = employee[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });

        db = new DatabaseHandler(this);
        recyclerView = findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectList = new ArrayList<>();
        projectlistItems = new ArrayList<>();

        // Get items from database
        projectList = db.getAllProjectsByEmployee(finalEmployee);

        for (Project proj : projectList) {
            Project project = new Project();
//            project.setEmployeeName(proj.getEmployeeName());
            project.setClientName(proj.getClientName());
            project.setStatus(proj.getStatus());
            project.setId(proj.getId());
            project.setDateItemAdded(proj.getDateItemAdded());

            projectlistItems.add(project);

        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, projectlistItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
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

        // Log.d("Item Added ID:", String.valueOf(db.getGroceriesCount()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                //start a new activity
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();
            }
        }, 1200); //  1 second.
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        startActivity(new Intent(this, ListActivity.class));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
