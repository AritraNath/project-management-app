package com.aritra.meconprojectlist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aritra.meconprojectlist.Model.Project;
import com.aritra.meconprojectlist.Util.Constants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJECT_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_EMPLOYEE_NAME+ " TEXT,"
                + Constants.KEY_CLIENT_NAME + " TEXT,"
                + Constants.KEY_TENURE + " TEXT,"
                + Constants.KEY_TOTAL_COST + " TEXT,"
                + Constants.KEY_STATUS + " TEXT,"
                + Constants.KEY_DATE_ADDED + " LONG);";

        db.execSQL(CREATE_PROJECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);

    }

    /**
     *  CRUD OPERATIONS: Create, Read, Update, Delete Methods
     */

    //Add Grocery
    public void addProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_EMPLOYEE_NAME, project.getEmployeeName());
        values.put(Constants.KEY_CLIENT_NAME, project.getClientName());
        values.put(Constants.KEY_TENURE, project.getTenure());
        values.put(Constants.KEY_TOTAL_COST, project.getTotalCost());
        values.put(Constants.KEY_STATUS, project.getStatus());
        values.put(Constants.KEY_DATE_ADDED, java.lang.System.currentTimeMillis());

        //Insert the row
        db.insert(Constants.TABLE_NAME, null, values);
    }

    //Get a Project
    public Project getProjectByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID,
             Constants.KEY_EMPLOYEE_NAME, Constants.KEY_CLIENT_NAME, Constants.KEY_TENURE,
                Constants.KEY_TOTAL_COST, Constants.KEY_STATUS, Constants.KEY_DATE_ADDED},
                Constants.KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

            Project project = new Project();
            assert cursor != null;
            project.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            project.setEmployeeName(cursor.getString(cursor.getColumnIndex(Constants.KEY_EMPLOYEE_NAME)));
            project.setClientName(cursor.getString(cursor.getColumnIndex(Constants.KEY_CLIENT_NAME)));
            project.setTenure(cursor.getString(cursor.getColumnIndex(Constants.KEY_TENURE)));
            project.setTotalCost(cursor.getString(cursor.getColumnIndex(Constants.KEY_TOTAL_COST)));
            project.setStatus(cursor.getString(cursor.getColumnIndex(Constants.KEY_STATUS)));

            //converting timestamp to something readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))
            .getTime());

            project.setDateItemAdded(formattedDate);
            cursor.close();
        return project;
    }

    //Get all Groceries
    public List<Project> getAllProjectsByEmployee(String employee) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID,
                Constants.KEY_EMPLOYEE_NAME, Constants.KEY_CLIENT_NAME, Constants.KEY_TENURE,
                Constants.KEY_TOTAL_COST, Constants.KEY_STATUS, Constants.KEY_DATE_ADDED},
                Constants.KEY_EMPLOYEE_NAME + "=?", new String[] {employee},
                null, null, Constants.KEY_DATE_ADDED +
                " DESC");

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project();
                project.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                project.setEmployeeName(cursor.getString(cursor.getColumnIndex(Constants.KEY_EMPLOYEE_NAME)));
                project.setClientName(cursor.getString(cursor.getColumnIndex(Constants.KEY_CLIENT_NAME)));
                project.setTenure(cursor.getString(cursor.getColumnIndex(Constants.KEY_TENURE)));
                project.setTotalCost(cursor.getString(cursor.getColumnIndex(Constants.KEY_TOTAL_COST)));
                project.setStatus(cursor.getString(cursor.getColumnIndex(Constants.KEY_STATUS)));

                //converting timestamp to something readable
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))
                        .getTime());

                project.setDateItemAdded(formattedDate);

                // Add to the projectList
                projectList.add(project);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return projectList;
    }

    //Updated Project
    public void updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_EMPLOYEE_NAME, project.getEmployeeName());
        values.put(Constants.KEY_CLIENT_NAME, project.getClientName());
        values.put(Constants.KEY_TENURE, project.getTenure());
        values.put(Constants.KEY_TOTAL_COST, project.getTotalCost());
        values.put(Constants.KEY_STATUS, project.getStatus());
        values.put(Constants.KEY_DATE_ADDED, java.lang.System.currentTimeMillis());

        //update row
        db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?",
                new String[] { String.valueOf(project.getId())} );
    }

    //Delete Project
    public void deleteProject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[] {String.valueOf(id)});

        db.close();

    }

    //Get count
    public int getProjectsCountByEmployee(String employee) {
        SQLiteDatabase db = this.getReadableDatabase();

        Integer count;
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID,
                        Constants.KEY_EMPLOYEE_NAME, Constants.KEY_CLIENT_NAME, Constants.KEY_TENURE,
                        Constants.KEY_TOTAL_COST, Constants.KEY_STATUS, Constants.KEY_DATE_ADDED},
                Constants.KEY_EMPLOYEE_NAME + "=?", new String[] {employee},
                null, null, null);

        cursor.moveToFirst();
        count = cursor.getCount();
        cursor.close();
        return count;
    }
}
