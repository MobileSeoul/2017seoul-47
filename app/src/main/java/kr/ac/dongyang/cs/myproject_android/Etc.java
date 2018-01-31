package kr.ac.dongyang.cs.myproject_android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Etc extends AppCompatActivity {
    LinearLayout liMyInform, liFoodKcalRegister;
    DatePicker dpEtc;
    EditText etKg, etTall;
    EditText etFoodInform[] = new EditText[5];
    ListView lvPersonalInform;
    String strFoodInform[] = new String[5];
    int etId[] = {R.id.etFoodName, R.id.etFoodKcal, R.id.etFoodCarbo, R.id.etFoodProtein, R.id.etFoodFat};
    String dbColumn[] = {"name","kcal","carbo","protein","fat"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        liMyInform = (LinearLayout) findViewById(R.id.liMyInform);
        liFoodKcalRegister = (LinearLayout) findViewById(R.id.liFoodKcalRegister);
        dpEtc = (DatePicker) findViewById(R.id.dpEtc);
        etKg = (EditText) findViewById(R.id.etKg);
        etTall = (EditText) findViewById(R.id.etTall);
        for(int i = 0; i<strFoodInform.length;i++){
            etFoodInform[i]=(EditText) findViewById(etId[i]);
        }
        lvPersonalInform = (ListView) findViewById(R.id.lvPersonalInform);

    }
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
                it = new Intent(getApplicationContext(), Food.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionPark :
                it = new Intent(getApplicationContext(), PMapsActivity.class);
                startActivity(it);
                break;
            case R.id.actionEtc :
                break;
            case R.id.actionHome:
                it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                finish();
                break;
        }
        return true;
    }

    public void btnMyInform(View v){
        liMyInform.setVisibility(View.VISIBLE);
        liFoodKcalRegister.setVisibility(View.GONE);
    }

    public void btnFoodKcal(View v){
        liMyInform.setVisibility(View.GONE);
        liFoodKcalRegister.setVisibility(View.VISIBLE);
    }

    public void btnPersonalInformRegister(View v){

    }

    public void btnFoodInformRegister(View v){
        try{
            DBConn dbconn = new DBConn(this);
            SQLiteDatabase sqlitedb = dbconn.getWritableDatabase();

            ContentValues values = new ContentValues();
            for(int i = 0; i<etFoodInform.length; i++){
                strFoodInform[i] = etFoodInform[i].getText().toString();

                values.put(dbColumn[i], strFoodInform[i]);
            }
            long newRowId = sqlitedb.insert("foodInform", null, values);
            sqlitedb.close();
            dbconn.close();

            Toast.makeText(getApplicationContext(), "입력되었습니다.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
        }
    }

}
