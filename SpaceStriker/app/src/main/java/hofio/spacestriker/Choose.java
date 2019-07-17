package hofio.spacestriker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Choose extends Activity {

    /** Called when the activity is first created. */
    private ImageView bluechk,blueberrychk,yellowchk;
    private TextView bestpoint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.choose);


        // TODO Auto-generated method stub
        ImageView blue=(ImageView)findViewById(R.id.imageblue);
        ImageView blueberry=(ImageView)findViewById(R.id.imageblueberry);
        ImageView yellow=(ImageView)findViewById(R.id.imageyellow);
        ImageView backto=(ImageView)findViewById(R.id.imagebackto);
        ImageView go=(ImageView)findViewById(R.id.imagego);

        bestpoint=(TextView)findViewById(R.id.bestPoint);
        bluechk=(ImageView)findViewById(R.id.bluechk);
        blueberrychk=(ImageView)findViewById(R.id.blueberrychk);
        yellowchk=(ImageView)findViewById(R.id.yellowchk);
        blue.setOnClickListener(l);blueberry.setOnClickListener(l);
        yellow.setOnClickListener(l);backto.setOnClickListener(l);
        go.setOnClickListener(l);
        bestpoint.setOnClickListener(l);
        bestpoint.setText("BestPoint:"+StartGame.point2);
        if(StartGame.soundon==true)
        {
            StartGame.mbacksound.start();
        }
    }
    ImageView.OnClickListener l=new ImageView.OnClickListener()
    {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(StartGame.soundon==true)
            {
                StartGame.mclick.start();
            }
            if(v.getId()==R.id.imagego)
            {
                if(MyGameView.select!=4)
                {
                    startActivity(new Intent(Choose.this,MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplication(), "Choose plz", Toast.LENGTH_SHORT).show();
                }
            }
            if(v.getId()==R.id.imagebackto)
            {
                if(StartGame.soundon==true)
                {
                    StartGame.mbacksound.pause();
                }
                finish();
            }
            if(v.getId()==R.id.imageblue)
            {
                MyGameView.select=2;
                bluechk.setVisibility(0);
                blueberrychk.setVisibility(8);
                yellowchk.setVisibility(8);
            }
            if(v.getId()==R.id.imageblueberry)
            {
                MyGameView.select=0;
                bluechk.setVisibility(8);
                blueberrychk.setVisibility(0);
                yellowchk.setVisibility(8);
            }
            if(v.getId()==R.id.imageyellow)
            {
                MyGameView.select=1;
                bluechk.setVisibility(8);
                blueberrychk.setVisibility(8);
                yellowchk.setVisibility(0);
            }
            if(v.getId()==R.id.bestPoint)
            {
                AlertDialog.Builder pointinit=new AlertDialog.Builder(Choose.this);
                pointinit.setCancelable(false);
                pointinit.setMessage("기록을 지우시겠습니까?");
                pointinit.setNegativeButton("아니오",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                pointinit.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        SharedPreferences.Editor edit=StartGame.pref.edit();//프리퍼런스.에딧
                        StartGame.point2=0;
                        edit.putInt("BestPoint", StartGame.point2);//프리퍼런스 저장
                        edit.commit();
                        bestpoint.setText("BestPoint:"+StartGame.point2);
                    }
                });
                pointinit.show();
            }
        }
    };
    protected void onPause() {
        // TODO Auto-generated method stub
        if(StartGame.soundon==true)
        {
            StartGame.mbacksound.pause();
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if(StartGame.soundon==true)
        {
            StartGame.mbacksound.start();
        }
        super.onResume();
    }
}
