package com.aritra.meconprojectlist.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aritra.meconprojectlist.Data.DatabaseHandler;
import com.aritra.meconprojectlist.Model.Project;
import com.aritra.meconprojectlist.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class DetailsActivity extends AppCompatActivity {
    private TextView clientName;
    private TextView status;
    private TextView projectCost;
    private TextView projectCollections;
    private TextView projectDue;
    private TextView startDate;
    private TextView endDate;
    private TextView elapsedDays;
    private TextView remainingDays;
    private ProgressBar projectProgress;
    private int projectID;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    private Calendar cal;
    private String employee;
    private DatabaseHandler db;
    private Project project;
    private Long daysElapsed;
    private Date start, end;
    private Long longTenure;
    private Float completionPercentage, collections, due;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkUIElements();

        Bundle bundle = getIntent().getExtras();

        if ( bundle != null ) {
            projectID = bundle.getInt("id");
        }
        db = new DatabaseHandler(getApplicationContext());
        project =  db.getProjectByID(projectID);

        setUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();
        switch (id){
            case R.id.menuDetailsEdit:
                //TODO : EDIT ITEM not working as intended
                editItem(project, projectID);
                break;
            case R.id.menuDetailsDelete:
                //TODO : More Polishing Required
                deleteProject(projectID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void linkUIElements(){

        clientName = findViewById(R.id.tvClient);
        status = findViewById(R.id.tvStatus);
        projectCost = findViewById(R.id.tvTotalCost);
        projectCollections = findViewById(R.id.tvCollected);
        projectDue = findViewById(R.id.tvDue);
        projectProgress = findViewById(R.id.progressBar);
        startDate = findViewById(R.id.tvStartDate);
        endDate = findViewById(R.id.tvEndDate);
        elapsedDays = findViewById(R.id.tvElapsed);
        remainingDays = findViewById(R.id.tvRemaining);
    }

    public void setUI(){
        clientName.setText("Client: " + project.getClientName());
        status.setText("Status: " + project.getStatus());
        projectCost.setText("Project Cost: " + project.getTotalCost() + " crores");
        startDate.setText("Start Date: " + project.getDateItemAdded());

        DateFormat dateFormat = DateFormat.getDateInstance();
        String now = dateFormat.format(new Date().getTime());
        try {
            start = dateFormat.parse(project.getDateItemAdded());
            end = dateFormat.parse(now);
            cal = Calendar.getInstance();
            cal.setTime(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.add(Calendar.DATE, Integer.parseInt(project.getTenure()));
        Date DateEnd = cal.getTime();

        daysElapsed = getDifferenceBWDates(start, end);
        elapsedDays.setText("Elapsed Days: " + String.valueOf(daysElapsed));
        endDate.setText("End Date: " + dateFormat.format(DateEnd));
        longTenure = Long.parseLong(project.getTenure());
        remainingDays.setText("Days Remaining: " + String.valueOf(longTenure - daysElapsed));

        completionPercentage = Float.valueOf(daysElapsed.toString()) / Float.valueOf(longTenure.toString()) ;
        collections = completionPercentage * Long.parseLong(project.getTotalCost());

        DecimalFormat df = new DecimalFormat("##.##");
        projectCollections.setText("Amount Collected: " + String.valueOf(df.format(collections)) + " crores");
        due = Float.parseFloat(project.getTotalCost()) - collections;
        projectDue.setText("Amount Due: " + String.valueOf(df.format(due)) + " crores");

        projectProgress.setProgress(Math.round(completionPercentage * 100));
        TextView progressHeader = findViewById(R.id.tvHeaderProgress);
        progressHeader.setText("Progress (" + String.valueOf(Math.round(completionPercentage * 100) + "%)"));
    }

    public Long getDifferenceBWDates(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        return elapsedDays;
    }

    public void editItem(final Project project, final Integer id) {

        alertDialogBuilder = new AlertDialog.Builder(DetailsActivity.this);

        inflater = LayoutInflater.from(getApplicationContext());
        final View view = inflater.inflate(R.layout.popup, null);

        final EditText clientName = view.findViewById(R.id.popup_clientName);
        final EditText tenure = view.findViewById(R.id.popup_tenure);
        final EditText projectCost = view.findViewById(R.id.popup_totalCost);
        final TextView title = view.findViewById(R.id.popupTitle);
        final CheckBox cbStatus = view.findViewById(R.id.cbStatus);

        title.setText("Edit Project");
        Button saveButton = view.findViewById(R.id.saveButton);

//        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
//        final Project project1 = db.getProjectByID(id);
        clientName.setText(project.getClientName());
        tenure.setText(project.getTenure());
        projectCost.setText(project.getTotalCost());

        if(project.getStatus().equals("ACTIVE"))
            cbStatus.setChecked(true);
        else
            cbStatus.setChecked(false);

        alertDialogBuilder.setView(view);
        dialog = alertDialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                final Project projectTemp = new Project();

//                Project project2 = databaseHandler.getProjectByID(projectID);
                //Update item
                projectTemp.setId(projectID);
                projectTemp.setEmployeeName(project.getEmployeeName());
                projectTemp.setClientName(clientName.getText().toString());
                projectTemp.setTenure(tenure.getText().toString());
                projectTemp.setTotalCost(projectCost.getText().toString());
                projectTemp.setStatus(cbStatus.isChecked()? "ACTIVE" : "CANCELLED");

                if (!clientName.getText().toString().isEmpty()
                        && !tenure.getText().toString().isEmpty()
                        && !projectCost.getText().toString().isEmpty()) {
                    databaseHandler.updateProject(projectTemp);
//                    notifyItemChanged(getAdapterPosition(),project);
                }else {
                    Snackbar.make(view, "Fields cannot be Empty!", Snackbar.LENGTH_LONG).show();
                }

                dialog.dismiss();
                Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
                intent.putExtra("id", projectID);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    public void deleteProject(final Integer ID){
        AlertDialog.Builder dialogueBuilder = new AlertDialog.Builder(DetailsActivity.this);
        dialogueBuilder.setTitle("Sure with that request?");
        dialogueBuilder.setMessage("This action cannot be undone");
        dialogueBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogueBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.deleteProject(ID);
                dialogInterface.dismiss();
                Intent intent = new Intent(DetailsActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog alertDialogue = dialogueBuilder.create();
        alertDialogue.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailsActivity.this, ListActivity.class));
        super.onBackPressed();
    }
}
