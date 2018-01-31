package kr.ac.dongyang.cs.myproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent WalkService = new Intent(this, WalkService.class);
        startService(WalkService);
    }

    //옵션
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDistance:
                Intent it = new Intent(getApplicationContext(), RecordSection.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionWhatIEat:
                it = new Intent(getApplicationContext(), Food.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionPark:
                it = new Intent(getApplicationContext(), PMapsActivity.class);
                startActivity(it);
                break;
            case R.id.actionEtc:
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

}

