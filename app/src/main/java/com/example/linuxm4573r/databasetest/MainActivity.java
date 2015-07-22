package com.example.linuxm4573r.databasetest;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//Build 7_20_15
//Created by James Seelbach

public class MainActivity extends Activity {

    Button createDBButton;
    Button readDBButton;
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


        //Create the 'create DB' button.
        createDBButton = (Button)findViewById(R.id.create_DB_button);
        createDBButton.setOnClickListener(new createDBOnClickHandle());

        readDBButton = (Button)findViewById(R.id.read_DB_button);
        readDBButton.setOnClickListener(new readDBOnClickHandle());

        //attach myTextBox to View
        myTextBox = (TextView)findViewById(R.id.myTextBox);

        //attach ImageViews to view
        myImageView1 = (ImageView)findViewById(R.id.myImageView1);
        myImageView2 = (ImageView)findViewById(R.id.myImageView2);
        myImageView3 = (ImageView)findViewById(R.id.myImageView3);
        myImageView4 = (ImageView)findViewById(R.id.myImageView4);


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


    private class createDBOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Create DB Button has been clicked.

            // Gets the data repository in write mode
            SQLiteDatabase db = myDB.getWritableDatabase();


            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("key","keyname");
            values.put("value","valuename");

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            for(int i=0;i<5000;i++) {
                newRowId = db.insert(
                        DatabaseEntry.TABLE_NAME,
                        null,
                        values);
                values.put("key","keyname");
                values.put("value","valuename"+i);
            }

            db.close();
        }
    }
    private class readDBOnClickHandle implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //Read DB Button has been clicked.

            // Gets the data repository in read mode
            SQLiteDatabase db = myDB.getReadableDatabase();

            Cursor c = db.query(DatabaseEntry.TABLE_NAME,new String[]{"value"},"key='keyname'",null,null,null,null);
            if(c.getCount()>0) {
                c.moveToFirst();
                while(!c.isLast()) {
                    myTextBox.setText(c.getString(0));
                    c.moveToNext();
                }
            }
            else
            {
                myTextBox.setText("NO ROWS FOUND IN DATABASE!");
            }
            c.close();
            db.close();
        }
    }
}



