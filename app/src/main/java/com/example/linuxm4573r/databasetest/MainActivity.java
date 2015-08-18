package com.example.linuxm4573r.databasetest;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

//Build 7_21_15
//Created by James Seelbach

public class MainActivity extends Activity {

    public static final int NUM_ITERATIONS=500;

    //Declare variables
    Button createDBButton;
    Button readDBButton;
    Button createDBButtonThreaded;
    Button readDBButtonThreaded;
    Button deleteTable;
    TextView myTextBox;
    ImageView myImageView1;
    ImageView myImageView2;
    ImageView myImageView3;
    ImageView myImageView4;
    DBAdapter myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create instantiation of the DBAdaper.  This is how we'll do our
        //Work with the database we're using in this app.
        myDB = new DBAdapter(getApplicationContext());

        //TimerTask for recording metrics.  Runs every 10 seconds.
        Log.d("DatabaseTestApp",new String("MAIN THREAD PID="+Integer.toString(android.os.Process.myPid())));
        TimerTask timerTask = new ANRMetricsRecorder(android.os.Process.myPid());
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);


        //Find buttons and attach to correct OnClickListeners
        createDBButton = (Button)findViewById(R.id.create_DB_button);
        createDBButton.setOnClickListener(new createDBOnClickHandle());

        readDBButton = (Button)findViewById(R.id.read_DB_button);
        readDBButton.setOnClickListener(new readDBOnClickHandle());

        createDBButtonThreaded = (Button)findViewById(R.id.create_DB_button_threaded);
        createDBButtonThreaded.setOnClickListener(new createDBThreadedOnClickHandle());

        readDBButtonThreaded = (Button)findViewById(R.id.read_DB_button_threaded);
        readDBButtonThreaded.setOnClickListener(new readDBThreadedOnClickHandle());

        deleteTable = (Button)findViewById(R.id.delete_table);
        deleteTable.setOnClickListener(new deleteTableOnClickHandle());

        //attach myTextBox to View so we can set text later
        myTextBox = (TextView)findViewById(R.id.myTextBox);

        //attach ImageViews to view to add pictures so we can 'scroll'
        myImageView1 = (ImageView)findViewById(R.id.myImageView1);
        myImageView2 = (ImageView)findViewById(R.id.myImageView2);
        myImageView3 = (ImageView)findViewById(R.id.myImageView3);
        myImageView4 = (ImageView)findViewById(R.id.myImageView4);


        //picture of android robot that we'll place so we have scroll room.
        Bitmap robot_picture = BitmapFactory.decodeResource(getResources(),R.drawable.robot);
        myImageView1.setImageBitmap(robot_picture);
        myImageView2.setImageBitmap(robot_picture);
        myImageView3.setImageBitmap(robot_picture);
        myImageView4.setImageBitmap(robot_picture);

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

    /**
     * Method used for writing data to the database.  Since the purpose of this method is
     * to do database activity it doesn't write anything meaningful.
     */
public void writeToDatabase()
{
    // Gets the data repository in write mode
    SQLiteDatabase db = myDB.getWritableDatabase();


    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put("key","keyname");
    values.put("value","valuename");

    //Add rows to the table specified by DatabaseEntry.java
    //We'll need to remove the for loop and instead find ways
    //to make this a more intensive operation.
    for(int i=0;i<NUM_ITERATIONS;i++) {
        //This is the statement that actually places data into the database.
        db.insert(
                DatabaseEntry.TABLE_NAME,
                null,
                values);

        //This is just staging the next key,value pair for
        //the next iteration of the for loop.
        values.put("key","keyname");
        values.put("value","valuename"+i);
    }
    //We NEED to close the database.
    //I'll look into what happens if this statement doesn't happen.
    db.close();
}
    /**
     * Method used for reading data from the database.  Since the purpose of this method is
     * to do database activity it doesn't read anything meaningful.
     *
     * @return a string containing the values found in "keyvalue" in the table specified by
     *          DatabaseEntry.java
     */
    public String readFromDatabase()
    {
        //Database is going to have lots of data so
        //A string builder is appropriate.
        StringBuilder retStr=new StringBuilder();
        // Gets the data repository in read mode
        SQLiteDatabase db = myDB.getReadableDatabase();

        //This can be literally be thought of as a cursor.
        //It points to a piece of data in the SQL table specified
        //by DatabaseEntry.java
        //We want to look at the specific key "keyname" and iterate through the values.
        //This is a standard query so making more complex queries might produce an ANR more easily.
        Cursor c = db.query(DatabaseEntry.TABLE_NAME,new String[]{"value"},"key='keyname'",null,null,null,null);
        //This is NOT a for loop but rather a valid way of going through values attached to a particular
        //key in a database table.  An example would be if you had a particular setting and wanted to
        //pull up all the values related to that setting that apply to whatever you're application
        //is doing.

        //c.getCount() returns how many values are left to read on the key we grabbed with the Cursor
        if(c.getCount()>0) {
            //This places the cursor on the first value found.  The order in this example is non-specific
            //but I think you CAN make a row in an SQL table order dependent.
            c.moveToFirst();
            //As long as we're not at the last key, read in the value.
            while(!c.isLast()) {
                retStr.append(c.getString(0));
                //Move to next value
                c.moveToNext();
            }
            retStr.append(c.getString(0));
        }
        //if c.getCount() is 0 then there are NO rows in the table.
        else
        {
            retStr.append("NO ROWS FOUND IN DATABASE!");
        }
        //Closes Cursor and database.  Again, these statements are needed but I don't know
        //what the exact consequences are.
        c.close();
        db.close();

        //This returns the data we read from "keyvalue" in the table.
        return retStr.toString();
    }

    //This handles button clicks on the "Create DB" Button.
    //It will write to the database on the main UI thread.
    private class createDBOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Create DB Button has been clicked.
            //Calling the writeToDatabase()
            //Method on the main UI thread.
            writeToDatabase();

        }
    }

    //This handles button clicks on the "Read DB" Button.
    //It will read from the database on the main UI thread.
    private class readDBOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Read DB Button has been clicked.
            //calling readFromDatabase() on the main thread.
            String output=readFromDatabase();
            myTextBox.setText(output);

        }
    }

    //This handles button clicks on the "Create DB Threaded Button.
    //It will write to the database a an asynctask.
    private class createDBThreadedOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Create DB Button has been clicked.
            //Create a new thread to do the database work.
            new WriteToDatabaseTask().execute();
        }

        //AsyncTask used to call the writeToDatabase() method.
        private class WriteToDatabaseTask extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... params) {
                writeToDatabase();
                return null;
            }

            @Override
            protected void onPostExecute(Void s)
            {
                //Let user know when we finish.
                myTextBox.setText("DATABASE CREATED!");
            }
        }
    }
    private class readDBThreadedOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Read DB Button has been clicked.
            //Create a new thread to do the database work.
            new ReadFromDatabaseTask().execute();

        }

        //AsyncTask used to read from the database.
        private class ReadFromDatabaseTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                return readFromDatabase();
            }

            @Override
            protected void onPostExecute(String s) {
                myTextBox.setText(s);
            }
        }
    }

    //This is used to delete the table.  Not a complex operation but it's convenient to have
    //for testing purposes.
    private class deleteTableOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //sql statement to delete the table based on DatabaseEntry.java
            myDB.getWritableDatabase().execSQL("DELETE FROM " + DatabaseEntry.TABLE_NAME + ";");
        }
    }
}



