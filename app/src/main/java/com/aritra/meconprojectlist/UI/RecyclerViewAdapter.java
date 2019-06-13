package com.aritra.meconprojectlist.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.aritra.meconprojectlist.Activities.DetailsActivity;
import com.aritra.meconprojectlist.Data.DatabaseHandler;
import com.aritra.meconprojectlist.Model.Project;
import com.aritra.meconprojectlist.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
   private Context context;
    private List<Project> projectItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Project> projectItems) {
        this.context = context;
        this.projectItems = projectItems;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Project project = projectItems.get(position);

        holder.projectClientName.setText(project.getClientName());
        holder.projectStatus.setText("Status: " + project.getStatus());
        holder.dateAdded.setText("Added On: " + project.getDateItemAdded());

    }

    @Override
    public int getItemCount() {
        return projectItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView projectClientName;
        public TextView projectStatus;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;


        public ViewHolder(View view, Context ctx) {
            super(view);

            context = ctx;

            projectClientName = view.findViewById(R.id.projectClient);
            projectStatus = view.findViewById(R.id.projectStatus);
            dateAdded = view.findViewById(R.id.dateAdded);

            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen/ DetailsActivity
                    int position = getAdapterPosition();

                    Project project = projectItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id", project.getId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.editButton:
                    int position = getAdapterPosition();
                    Project project = projectItems.get(position);
                    editItem(project, project.getId());
                    break;

                case R.id.deleteButton:
                     position = getAdapterPosition();
                     project = projectItems.get(position);
                    deleteItem(project.getId());
                    break;
            }
        }

        public void deleteItem(final int id) {

            //create an AlertDialog
            alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Sure with that request?");
            alertDialogBuilder.setMessage("This action cannot be undone!");
            alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    //delete item
                    db.deleteProject(id);
                    projectItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialogInterface.dismiss();
                }
            });

            dialog = alertDialogBuilder.create();
            dialog.show();
        }


        public void editItem(final Project project, final Integer id) {

            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            final EditText clientName = view.findViewById(R.id.popup_clientName);
            final EditText tenure = view.findViewById(R.id.popup_tenure);
            final EditText projectCost = view.findViewById(R.id.popup_totalCost);
            final TextView title = view.findViewById(R.id.popupTitle);
            final CheckBox cbStatus = view.findViewById(R.id.cbStatus);

            title.setText("Edit Project");
            Button saveButton = view.findViewById(R.id.saveButton);

            DatabaseHandler databaseHandler = new DatabaseHandler(context);
            final Project project1 = databaseHandler.getProjectByID(id);
            clientName.setText(project1.getClientName());
            tenure.setText(project1.getTenure());
            projectCost.setText(project1.getTotalCost());

            if(project1.getStatus().equals("ACTIVE"))
                cbStatus.setChecked(true);
            else
                cbStatus.setChecked(false);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = new DatabaseHandler(context);
                    //Update item
                    project.setEmployeeName(project1.getEmployeeName());
                    project.setClientName(projectClientName.getText().toString());
                    project.setTenure(tenure.getText().toString());
                    project.setTotalCost(projectCost.getText().toString());
                    project.setStatus(cbStatus.isChecked()? "ACTIVE" : "CANCELLED");

                    if (!projectClientName.getText().toString().isEmpty()
                            && !tenure.getText().toString().isEmpty()
                            && !projectCost.getText().toString().isEmpty()) {
                        db.updateProject(project);
                        notifyItemChanged(getAdapterPosition(),project);
                    }else {
                        Snackbar.make(view, "Fields cannot be Empty!", Snackbar.LENGTH_LONG).show();
                    }

                    dialog.dismiss();

                }
            });

        }
    }

}
