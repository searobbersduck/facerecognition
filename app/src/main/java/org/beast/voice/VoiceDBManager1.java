package org.beast.voice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weidong.zhang on 2016/5/18.
 */
public class VoiceDBManager1 {

    private VoiceDBHelper1 helper;
    private SQLiteDatabase db;

    public VoiceDBManager1(Context context) {
        helper = new VoiceDBHelper1(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * @param persons
     */
    public void add(List<VoicePerson> persons) {
        db.beginTransaction();  //开始事务
        try {
            for (VoicePerson person : persons) {
                db.execSQL("INSERT INTO person VALUES(null, ?, ?)", new Object[]{person.mName, person.mID});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void add(VoicePerson person) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO person VALUES(null, ?, ?)", new Object[]{person.mName, person.mID});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     * @param person
     */
//    public void updateAge(Person person) {
//        ContentValues cv = new ContentValues();
//        cv.put("age", person.age);
//        db.update("person", cv, "name = ?", new String[]{person.name});
//    }

    /**
     * delete old person
     * @param person
     */
//    public void deleteOldPerson(Person person) {
//        db.delete("person", "age >= ?", new String[]{String.valueOf(person.age)});
//    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<VoicePerson> query() {
        ArrayList<VoicePerson> persons = new ArrayList<VoicePerson>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            VoicePerson person = new VoicePerson();
            person.mName = c.getString(c.getColumnIndex("name"));
            person.mID = c.getString(c.getColumnIndex("uid"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM person", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
