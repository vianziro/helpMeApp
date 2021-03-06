package com.helpme.android.helpmesender;
/*
    Programmer: Vitaly Simonovich
    ID: 309398311
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.LoginFilter;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vitaly on 05/16/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "helpme";

    //Table Names
    private static final String TABLE_VICTIM = "victim";
    private static final String TABLE_GPS = "gps";

    //victim column names
    private static final String VICTIM_ID = "id";
    private static final String VICTIM_NAME = "name";
    private static final String VICTIM_PASSWORD = "password";
    private static final String VICTIM_PIN = "pin";
    private static final String VICTIM_GPS = "gps";
    private static final String VICTIM_VIDEO = "video";
    private static final String VICTIM_PHOTO = "photo";
    private static final String VICTIM_MIC = "mic";
    private static final String VICTIM_PHONE = "phone";

    //gps column names
    private static final String GPS_DATETIME = "date";
    private static final String GPS_LATITUTE = "latitute";
    private static final String GPS_LONGITUDE = "longitude";
    private static final String GPS_VICTIMID = "victimID";
    private static final String GPS_SENDED = "sended";


    //Table Create statements
    //victim table
    private static final String CREATE_TABLE_VICTIM = "CREATE TABLE "
            + TABLE_VICTIM + "(" + VICTIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + VICTIM_NAME + " TEXT,"
            + VICTIM_PASSWORD + " TEXT, " + VICTIM_PIN + " INTEGER, "
            + VICTIM_GPS + " INTEGER, " + VICTIM_VIDEO + " INTEGER, " + VICTIM_PHOTO +" INTEGER, " + VICTIM_MIC + " INTEGER, "
            + VICTIM_PHONE + " INTEGER" + ")";

    private static final String CREATE_TABLE_GPS = "CREATE TABLE "
            + TABLE_GPS + "(" + GPS_DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
            + GPS_LATITUTE + " DOUBLE ," + GPS_LONGITUDE + " DOUBLE ," + GPS_VICTIMID + " INTEGER NOT NULL, "
            + GPS_SENDED +" INTEGER" + ", PRIMARY KEY ( "+GPS_DATETIME+", "+GPS_VICTIMID+"))";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VICTIM);
        db.execSQL(CREATE_TABLE_GPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VICTIM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);
        onCreate(db);
    }

    public int createVictim(Victim victim){
        SQLiteDatabase db = this.getWritableDatabase();
        int id = 0;
        Log.e("TABLE", CREATE_TABLE_VICTIM);
        ContentValues values = new ContentValues();
        //values.put(VICTIM_ID,victim.getId());
        values.put(VICTIM_NAME,victim.getName());
        values.put(VICTIM_PASSWORD,victim.getPassword());
        values.put(VICTIM_PIN, victim.getPinCode());
        values.put(VICTIM_GPS, victim.getGps());
        values.put(VICTIM_VIDEO, victim.getVideo());
        values.put(VICTIM_PHOTO, victim.getPhoto());
        values.put(VICTIM_MIC, victim.getMic());
        values.put(VICTIM_PHONE, victim.getPhone());

        db.insert(TABLE_VICTIM, null, values);

        String query = "SELECT * FROM " + TABLE_VICTIM ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) c.moveToLast();
        Log.e("ID", c.getColumnIndex(VICTIM_ID) + "");
        return c.getInt(c.getColumnIndex(VICTIM_ID));


    }

    public Victim getVictim(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Victim v = new Victim();

        String query = "SELECT * FROM " + TABLE_VICTIM +" WHERE " + VICTIM_ID +" = "+id;

        Cursor c = db.rawQuery(query, null);

        if (c != null) c.moveToFirst();

        v.setId(c.getInt(c.getColumnIndex(VICTIM_ID)));
        v.setName(c.getString(c.getColumnIndex(VICTIM_NAME)));
        v.setPassword(c.getString(c.getColumnIndex(VICTIM_PASSWORD)));
        v.setPinCode(c.getInt(c.getColumnIndex(VICTIM_PIN)));

        return v;
    }

    //Checks if victim exists
    public boolean checkVictimUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_VICTIM +" WHERE " + VICTIM_NAME +" = '"+username+"'";
        Cursor c = db.rawQuery(query,null);
        Log.e("CourserUsername", "" + c.getCount());
        if(c.getCount() > 0 ) return true;
        else return false;
    }

    public int checkVictimCredentials(String username,String password,int pin){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT "+VICTIM_ID+" FROM " + TABLE_VICTIM +" WHERE " + VICTIM_NAME +" = '"+username +"' AND "
                +VICTIM_PASSWORD + " = '" + password + "' AND " + VICTIM_PIN + " = " + pin;
        Cursor c = db.rawQuery(query, null);
        if (c != null) c.moveToFirst();
        if(c.getCount() > 0 ) return c.getInt(c.getColumnIndex(VICTIM_ID));
        else return 0;

    }

    public void updateVictimSensors(HashMap<String,Integer> mpSensor,int victimID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE "+TABLE_VICTIM + " SET ";

        HashMap<String, Integer> map = mpSensor;
        for (Map.Entry<String, Integer> entry : mpSensor.entrySet()) {
            query += entry.getKey() +" = "+entry.getValue()+", ";
        }
        query = query.substring(0,query.length()-2);
        query += " WHERE "+VICTIM_ID + " = " + victimID;

        db.execSQL(query);


    }
    public  HashMap<String, Integer> getUserSensor(int id){
        HashMap<String, Integer> temp = new HashMap<String, Integer>();
        SQLiteDatabase db = this.getReadableDatabase();

/*        String query = "SELECT "+VICTIM_GPS+", "+VICTIM_VIDEO+", "+VICTIM_PHOTO+", "+VICTIM_MIC+", "+VICTIM_PHONE +" FROM "
                + TABLE_VICTIM +" WHERE " + VICTIM_ID +" = "+id;*/
                String query = "SELECT * FROM " + TABLE_VICTIM +" WHERE " + VICTIM_ID +" = "+id;
        Cursor c = db.rawQuery(query,null);
        if (c != null) c.moveToFirst();
        if(c.getCount() > 0){
            temp.put(VICTIM_GPS, c.getInt(c.getColumnIndex(VICTIM_GPS)));
            temp.put(VICTIM_VIDEO,c.getInt(c.getColumnIndex(VICTIM_VIDEO)));
            temp.put(VICTIM_PHOTO,c.getInt(c.getColumnIndex(VICTIM_PHOTO)));
            temp.put(VICTIM_MIC,c.getInt(c.getColumnIndex(VICTIM_MIC)));
            temp.put(VICTIM_PHONE,c.getInt(c.getColumnIndex(VICTIM_PHONE)));
        }


        return temp;
    }

    public void updatePin(int victimID,int pin){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_VICTIM + " SET " +VICTIM_PIN + " = " +pin + " WHERE " + VICTIM_ID + " = "+victimID;
        db.execSQL(query);
    }
    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen()) db.close();
    }

    public void addGPSCor(int victimID,double lan,double lon,String gpsStamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GPS_VICTIMID, victimID);
        values.put(GPS_LATITUTE, lan);
        values.put(GPS_LONGITUDE, lon);
        values.put(GPS_DATETIME, gpsStamp);
        values.put(GPS_SENDED, 0);
        // insert the row
        long id = db.insert(TABLE_GPS, null, values);

    }

    public void getGPSCor(int victimID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_GPS+" WHERE "+GPS_VICTIMID+ " = "+victimID +" ORDER BY "+ GPS_DATETIME;
        Cursor c = db.rawQuery(query,null);

        if (c.moveToFirst()) {
            do {

                Log.e("VICTIM",c.getInt(c.getColumnIndex(GPS_VICTIMID))+"|"
                        +Timestamp.valueOf(c.getString(c.getColumnIndex(GPS_DATETIME)))+"|"
                        +c.getDouble(c.getColumnIndex(GPS_LATITUTE))+"|"
                        +c.getDouble(c.getColumnIndex(GPS_LONGITUDE))+"|"
                        +c.getInt(c.getColumnIndex(GPS_SENDED)));
            } while (c.moveToNext());
        }

    }

   public void setGpsSended(int victimID,String gpsStamp){
       SQLiteDatabase db = this.getReadableDatabase();
       String query = "UPDATE " + TABLE_GPS + " SET " +GPS_SENDED +"= 1 WHERE " + GPS_VICTIMID + " = "+victimID +
               " AND "+GPS_DATETIME +" = '"+gpsStamp+"'";
       db.execSQL(query);
   }

    public int getNumUnsended(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_GPS+" WHERE "+GPS_SENDED+ " = 0";
        Cursor c = db.rawQuery(query, null);
        int unsended = c.getCount();
        c.close();
        return unsended;

    }

    public void removeAllGPS() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE  FROM "+TABLE_GPS;
        db.execSQL(query);
    }

}
