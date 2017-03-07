package nyc.c4q.helenchan.makinghistory;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.anwarshahriar.calligrapher.Calligrapher;
import nyc.c4q.helenchan.makinghistory.leigh.AddConentActivity;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initViews();
        setListeners();
        setFontType();
    }

    private void initViews(){
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
    }

    private void setListeners(){
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    private void setFontType() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "ArimaMadurai-Bold.ttf", true);
        calligrapher.setFont(findViewById(R.id.bottom_nav_view), "Raleway-Regular.ttf");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case R.id.suggestedIcon:
               Intent suggestedIntent = new Intent(getBaseContext(), PointOfInterestActivity.class);
               startActivity(suggestedIntent);
               break;
           case R.id.createIcon:
               Intent createIntent = new Intent(getBaseContext(), AddConentActivity.class);
               startActivity(createIntent);
               break;
           case R.id.exploreIcon:
               Intent exploreIntent = new Intent(getBaseContext(), ExploreMoreActivity.class);
               startActivity(exploreIntent);
       }
        return true;
    }
}