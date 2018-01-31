package kr.ac.dongyang.cs.myproject_android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kmm on 2017-05-18.
 */

public class DBConn extends SQLiteOpenHelper {

    public DBConn(Context context) {
        super(context, "MYDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //food는 사용자가 식단을 입력한 db
        //foodInform은 DB에 들어가있는 음식들의 칼로리 등을 나타냄
        //personalInform은 날짜별로 사용자가 걸은 거리, 몸무게를 나타냄
        //myInform은 날짜별로 kg과 걸음수를 나타냄
        db.execSQL("create table food(writedate char(10) primary key, morning text, mkcal int, lunch text, lkcal int," +
                " dinner text, dkcal int, snack text, skcal int);");
        db.execSQL("create table foodInform(name char(15) primary key, howmuch int, kcal int not null, carbo int, protein int, fat int);");
        db.execSQL("create table personalInform(writedate char(10) primary key, weight int, walk int);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
