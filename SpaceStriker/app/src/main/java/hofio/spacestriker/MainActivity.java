package hofio.spacestriker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    MyGameView mGameView;
    public static boolean gameplay=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mGameView=(MyGameView)findViewById(R.id.mGameView);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP)
        {//ACTION_UP을 같이 써줘야 함, 안쓰면 두 번 동작, 누를 때, 땔 때
            gameplay=false;
            AlertDialog.Builder gameesc=new AlertDialog.Builder(MainActivity.this);
            gameesc.setCancelable(false);
            gameesc.setMessage("초기화면으로 돌아가시겠습니까?");
            gameesc.setNegativeButton("아니오",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    GameResume();
                }
            });
            gameesc.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    android.os.Process.killProcess(android.os.Process.myPid());//완전 중료
                }
            });
            gameesc.show();
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0,1,0,"Sound Off");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 1:
                if(StartGame.soundon==true)
                {
                    StartGame.soundon=false;
                    item.setTitle("Sound On");
                }
                else
                {
                    StartGame.soundon=true;
                    item.setTitle("Sound Off");
                }
                StartGame.pref=getSharedPreferences("Preference",0);//프리퍼런스
                SharedPreferences.Editor edit=StartGame.pref.edit();//프리퍼런스.에딧
                edit.putBoolean("SoundKey", StartGame.soundon);//프리퍼런스 저장
                edit.commit();//저지르다- 저장
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    public static void GameResume()
    {
        gameplay=true;
        synchronized(MyGameView.mThread)
        {
            MyGameView.mThread.notify();
        }
    }
}
