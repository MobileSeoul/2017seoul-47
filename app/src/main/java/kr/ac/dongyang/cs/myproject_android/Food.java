package kr.ac.dongyang.cs.myproject_android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Food extends AppCompatActivity {
    static boolean menu = false;    //false일 때 기록보기, true일 때 작성하기
    public String today;
    DatePicker dp;
    ImageView ivRecord, ivRegister;
    LinearLayout liFoodRecord, liRegister, liUpdate;
    String KorStrMeal[] = {"아침","점심","저녁","간식"};

    EditText etMeal[] = new EditText[4]; //etBreakfast, etLunch, etDinner, etSnack
    String strMeal[] = {"morning", "lunch", "dinner", "snack"};
    String strKcal[] = {"mkcal", "lkcal", "dkcal", "skcal"};
    int etMealId[] = {R.id.etBreakfast, R.id.etLunch, R.id.etDinner, R.id.etSnack};
    int intKcal[] = {0, 0, 0, 0};
    EditText etSearch;
    //여기까지가 INSERT부분
    EditText etUpdateMeal[] = new EditText[4];
    TextView tvUpdateKcal[] = new TextView[4];
    int etUpdateMealId[] = {R.id.etUpdateBreakfast, R.id.etUpdateLunch, R.id.etUpdateDinner, R.id.etUpdateSnack};
    int tvUpdateKcalId[] = {R.id.tvUpdateMorningKcalories, R.id.tvUpdateLunchKcalories, R.id.tvUpdateDinnerKcalories, R.id.tvUpdateSnackKcalories};
    //여기까지가 UPDATE부분
    TextView tvKcals[] = new TextView[4];
    FoodInformList fif = new FoodInformList();
    ArrayList<FoodList> foodList = new ArrayList<FoodList>();
    ArrayList<FoodInformList> showRecords = new ArrayList<FoodInformList>();
    ListView lvFood, lvShowRecord;
    FoodListAdapter listAdapter;
    FoodInformListAdapter foodInformListAdapter;
    Button btnRegister;
    Button btnUpdate;
    TextView tvUpdateDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        ivRecord = (ImageView) findViewById(R.id.ivRecord);
        ivRegister = (ImageView) findViewById(R.id.ivFoodRegister);
        liFoodRecord = (LinearLayout) findViewById(R.id.liFoodRecord);
        liRegister = (LinearLayout) findViewById(R.id.liRegister);
        liUpdate = (LinearLayout) findViewById(R.id.liUpdate);

        for (int i = 0; i < etMealId.length; i++) {
            etMeal[i] = (EditText) findViewById(etMealId[i]);
            etUpdateMeal[i] = (EditText) findViewById(etUpdateMealId[i]);
            tvUpdateKcal[i] = (TextView) findViewById(tvUpdateKcalId[i]);
        }

        tvKcals[0] = (TextView) findViewById(R.id.tvMorningKcalories);
        tvKcals[1] = (TextView) findViewById(R.id.tvLunchKcalories);
        tvKcals[2] = (TextView) findViewById(R.id.tvDinnerKcalories);
        tvKcals[3] = (TextView) findViewById(R.id.tvSnackKcalories);

        lvShowRecord = (ListView) findViewById(R.id.lvShowRecord);

        dp = (DatePicker) findViewById(R.id.datePicker);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        tvUpdateDate = (TextView) findViewById(R.id.tvUpdateDate);

        showDBContent("food");

    }

    //옵션
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.actionDistance :
                Intent it = new Intent(getApplicationContext(), RecordSection.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionWhatIEat :
                break;
            case R.id.actionPark :
                it = new Intent(getApplicationContext(), PMapsActivity.class);
                startActivity(it);
                break;
            case R.id.actionEtc :
                it = new Intent(getApplicationContext(), Etc.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionHome:
                it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                finish();
                break;
        }
        return true;
    }

    //database에 입력하기
    public void btnRegister(View v) {
        try {
            //DatePicker를 이용하여 날짜 가져오기
            today = String.format("%04d-%02d-%02d", dp.getYear(), (dp.getMonth() + 1), dp.getDayOfMonth());
            DBConn dbconn = new DBConn(this);
            SQLiteDatabase sqlitedb = dbconn.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("writedate", today);
            for (int i = 0; i < 4; i++) {
                values.put(strMeal[i], etMeal[i].getText().toString());
                values.put(strKcal[i], intKcal[i]);

            }

            long newRowId = sqlitedb.insert("food", null, values);
            sqlitedb.close();
            dbconn.close();

            Toast.makeText(this, "입력되었습니다.", Toast.LENGTH_SHORT).show();
            for(int i = 0; i<4; i++){
                etMeal[i].setText("");
                tvKcals[i].setText("0 kcal");
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //database에서 내용 꺼내오기
        showDBContent("food");

    }//btnRegister

    //editText누르자마자 실행->Dialog
    public void Select(final View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //dialog불러오기
        for(int i = 0; i<4; i++){
            if(v.getId()==etMeal[i].getId()){
                alert.setTitle(KorStrMeal[i]);
            }
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.foodselect_dialog_item, null);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        lvFood = (ListView) view.findViewById(R.id.lvSelect);
        alert.setView(view);

        alert.setNegativeButton("NO", null);
        alert.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < etMeal.length; i++) {
                    if (v.getId() == etMealId[i]) {
                        etMeal[i].setText(etSearch.getText().toString());
                        intKcal[i] = selectDBContent(etSearch.getText().toString());
                        tvKcals[i].setText(intKcal[i] + " kcal");
                    }
                    if (v.getId() == etUpdateMealId[i]) {
                        etUpdateMeal[i].setText(etSearch.getText().toString());
                        intKcal[i] = selectDBContent(etSearch.getText().toString());
                        tvUpdateKcal[i].setText(intKcal[i] + " kcal");
                    }
                }
                /*
                switch (v.getId()){
                    case R.id.etBreakfast :
                        etBreakfast.setText(etSearch.getText().toString());
                        tvKcals[0].setText(selectDBContent(etSearch.getText().toString()) + "kcal");
                        mkcal = selectDBContent(etSearch.getText().toString());
                        break;
                    case R.id.etLunch:
                        etLunch.setText(etSearch.getText().toString());
                        tvKcals[1].setText(selectDBContent(etSearch.getText().toString()) + "kcal");
                        lkcal = selectDBContent(etSearch.getText().toString());
                        break;
                    case R.id.etDinner:
                        etBreakfast.setText(etSearch.getText().toString());
                        tvKcals[2].setText(selectDBContent(etSearch.getText().toString()) + "kcal");
                        dkcal = selectDBContent(etSearch.getText().toString());
                        break;
                    case R.id.etSnack:
                        etBreakfast.setText(etSearch.getText().toString());
                        tvKcals[3].setText(selectDBContent(etSearch.getText().toString()) + "kcal");
                        skcal = selectDBContent(etSearch.getText().toString());
                        break;
                }
                배열로 완전히 바꾸면서 줄어든 코드>_<
                */
            }
        });
        alert.show();
    }//Select

    public void showRecord(View v) {
        menu = false;
        if (!menu) {
            ivRecord.setImageResource(R.drawable.dorecord_2);
            ivRegister.setImageResource(R.drawable.record_1);
            liFoodRecord.setVisibility(View.VISIBLE);
            liRegister.setVisibility(View.GONE);
            liUpdate.setVisibility(View.GONE);
        }
    }

    public void recordFood(View v) {
        menu = true;
        if (menu) {
            ivRegister.setImageResource(R.drawable.record_2);
            ivRecord.setImageResource(R.drawable.dorecord_1);
            liFoodRecord.setVisibility(View.GONE);
            liRegister.setVisibility(View.VISIBLE);
            liUpdate.setVisibility(View.GONE);
        }
    }

    public void tvHome(View v) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }

    public void showDBContent(String table) {
        //food에서 내용 꺼내오는 메소드 : 식단관리
        foodList.clear();
        try {
            DBConn dbconn = new DBConn(this);
            SQLiteDatabase sqlitedb = dbconn.getReadableDatabase();
            Cursor cursor = sqlitedb.query(table, null, null, null, null, null, "writedate desc");
            while (cursor.moveToNext()) {
                FoodList fd = new FoodList();
                fd.writedate = cursor.getString(cursor.getColumnIndex("writedate"));
                for (int i = 0; i < fd.strMeal.length; i++) {
                    //fd.strMeal과 strMeal은 엄연히 다름!!
                    //fd.strMeal은 FoodList클래스의 변수이고 strMeal은 Food클래스의 전역변수임!
                    fd.strMeal[i] = cursor.getString(cursor.getColumnIndex(strMeal[i]));
                    fd.intKcal[i] = cursor.getInt(cursor.getColumnIndex(strKcal[i]));
                }
                foodList.add(fd);
            }
            listAdapter = new FoodListAdapter(this, foodList);
            lvShowRecord.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

            cursor.close();
            sqlitedb.close();
            dbconn.close();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public int selectDBContent(String name) {
        int kcal = 0;
        if(name.equals("")){
            return 0;
        }
        //foodInform에서 음식(칼로리, 탄수화물 양 등) 검색해오는 메소드
        try {
            DBConn dbconn = new DBConn(this);
            SQLiteDatabase sqlitedb = dbconn.getReadableDatabase();
            Cursor cursor = sqlitedb.rawQuery("select * from foodInform where name like '%" + name + "%';", null);

            //cursor로 디비에서 찾아와서 listview에다 넣는 과정(showDBContents는 기록을 보는 메소드)
            while (cursor.moveToNext()) {
                fif.name = cursor.getString(cursor.getColumnIndex("name"));
                fif.howmuch = cursor.getInt(cursor.getColumnIndex("howmuch"));
                fif.kcal = cursor.getInt(cursor.getColumnIndex("kcal"));
                kcal = fif.kcal;
                fif.carbo = cursor.getInt(cursor.getColumnIndex("carbo"));
                fif.protein = cursor.getInt(cursor.getColumnIndex("protein"));
                fif.fat = cursor.getInt(cursor.getColumnIndex("fat"));

                showRecords.add(fif);

            }
            foodInformListAdapter = new FoodInformListAdapter(this, showRecords);
            lvFood.setAdapter(foodInformListAdapter);
            foodInformListAdapter.notifyDataSetChanged();

            showRecords = null;

            cursor.close();
            sqlitedb.close();
            dbconn.close();
        } catch (Exception ex) {
        }
        return kcal;
    }

    public void lvbtnSelect(View v) {
        listAdapter.notifyDataSetChanged();
    }

    class FoodListAdapter extends BaseAdapter {
        Context context;
        ArrayList<FoodList> array;
        int sumKcal;

        public FoodListAdapter(Context c, ArrayList<FoodList> l) {
            context = c;
            array = l;
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.food_listview_item, null);
            TextView tvname = (TextView) view.findViewById(R.id.lvtvdate);
            final TextView tvKcal = (TextView) view.findViewById(R.id.lvtvTotalKcaloies);
            //식단목록관리(날짜 | 총 섭취 칼로리) : ListView부분
            tvname.setText(array.get(position).writedate + " | ");
            for (int i = 0; i < intKcal.length; i++) {
                sumKcal += foodList.get(position).intKcal[i];
            }
            tvKcal.setText("총칼로리:" + sumKcal + " kcal");
            sumKcal = 0;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //기록보기 부분에서 view 선택하면 해당 view의 데이터 나오고 update가능하게 함
                    for (int i = 0; i < etUpdateMeal.length; i++) {
                        etUpdateMeal[i].setText(array.get(position).strMeal[i]);
                        tvUpdateKcal[i].setText(selectDBContent(etUpdateMeal[i].getText().toString()) + " kcal");
                    }
                    tvUpdateDate.setText(array.get(position).writedate);

                    liUpdate.setVisibility(View.VISIBLE);
                    liRegister.setVisibility(View.GONE);
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                DBConn dbconn = new DBConn(getApplicationContext());
                                SQLiteDatabase sqlitedb = dbconn.getWritableDatabase();
                                String query = "update food set ";
                                for (int i = 0; i < etUpdateMeal.length; i++) {
                                    if(i<3){
                                        //앞의 3개는 strKcal에 ,가 있어야 해서 이렇게 짬
                                        query += strMeal[i] + "='" + etUpdateMeal[i].getText().toString() + "', "
                                                + strKcal[i] + "=" + selectDBContent(etUpdateMeal[i].getText().toString())+", ";
                                    }else{
                                        query += strMeal[i] + "='" + etUpdateMeal[i].getText().toString() + "', "
                                                + strKcal[i] + "=" + selectDBContent(etUpdateMeal[i].getText().toString());
                                    }
                                }
                                query += " where writedate='" + array.get(position).writedate + "';";
                                sqlitedb.execSQL(query);
                                dbconn.close();
                                sqlitedb.close();
                                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT);
                                Intent it = new Intent(getApplicationContext(), Food.class);
                                startActivity(it);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            });
            return view;
        }
    }

    class FoodInformListAdapter extends BaseAdapter {
        //음식정보 : 음식의 이름, 칼로리, 단백질, 지방의 양 등등...
        Context context;
        ArrayList<FoodInformList> array;

        public FoodInformListAdapter(Context c, ArrayList<FoodInformList> l) {
            context = c;
            array = l;
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater
                    = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.food_listview_item, null);
            TextView tvname = (TextView) view.findViewById(R.id.lvtvdate);
            TextView tvKcal = (TextView) view.findViewById(R.id.lvtvTotalKcaloies);
            tvname.setText(array.get(position).name);
            tvKcal.setText(array.get(position).kcal + "kcal");
            return view;
        }
    }
}

