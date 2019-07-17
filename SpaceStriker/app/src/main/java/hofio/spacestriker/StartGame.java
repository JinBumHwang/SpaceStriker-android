package hofio.spacestriker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class StartGame extends Activity {
    /** Called when the activity is first created. */
    static boolean soundon=false;
    static MediaPlayer mbacksound,mclick,mdeadem,mdeadmy,mdeadboss,mfanfare,mbossentry,mlose,mskill;
    static SharedPreferences pref;
    static int point2;
    Button btnStart,btnOpn,btnExit,btnSound;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.startgame);
        // TODO Auto-generated method stub
        FrameLayout framelayout=(FrameLayout)findViewById(R.id.FrameLayout1);
        framelayout.setBackgroundResource(R.drawable.back);

        btnStart=(Button)findViewById(R.id.btnStart);
        btnOpn=(Button)findViewById(R.id.btnOpn);
        btnExit=(Button)findViewById(R.id.btnExit);
        btnSound=(Button)findViewById(R.id.btnSound);
        btnStart.setBackgroundResource(R.drawable.start);
        btnOpn.setBackgroundResource(R.drawable.option);
        btnExit.setBackgroundResource(R.drawable.exit);
        btnStart.setOnClickListener(l);
        btnOpn.setOnClickListener(l);
        btnExit.setOnClickListener(l);
        btnSound.setOnClickListener(l);
        MyGameView.select=4;

        pref=getSharedPreferences("Preference", 0);
        soundon=pref.getBoolean("SoundKey",true);
        point2=pref.getInt("BestPoint",0);
        if(soundon)
        {
            btnSound.setBackgroundResource(R.drawable.soundon);
        }
        else
        {
            btnSound.setBackgroundResource(R.drawable.soundoff);
        }

        mclick=MediaPlayer.create(this, R.raw.click);
        mbacksound=MediaPlayer.create(this,R.raw.music);
        mdeadem=MediaPlayer.create(this, R.raw.deadem);
        mdeadboss=MediaPlayer.create(this, R.raw.deadboss);
        mdeadmy=MediaPlayer.create(this, R.raw.deadmy);
        mfanfare=MediaPlayer.create(this, R.raw.fanfare);
        mbossentry=MediaPlayer.create(this, R.raw.bossentry);
        mlose=MediaPlayer.create(this, R.raw.lose);
        mskill=MediaPlayer.create(this, R.raw.skill);
        mbacksound.setVolume(0.2f, 0.2f);
        mdeadem.setVolume(0.7f,0.7f);
    }
    Button.OnClickListener l=new Button.OnClickListener()
    {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(soundon)
            {
                mclick.start();
            }
            if(v.getId()==R.id.btnStart)
            {
                startActivity(new Intent(StartGame.this,Choose.class));
            }
            if(v.getId()==R.id.btnSound)
            {
                if(soundon==true)
                {
                    soundon=false;
                    btnSound.setBackgroundResource(R.drawable.soundoff);
                    mbacksound.pause();
                }
                else
                {
                    soundon=true;
                    btnSound.setBackgroundResource(R.drawable.soundon);
                    mbacksound.start();
                }
                pref=getSharedPreferences("Preference",0);//프리퍼런스
                SharedPreferences.Editor edit=pref.edit();//프리퍼런스.에딧
                edit.putBoolean("SoundKey", soundon);//프리퍼런스 저장
                edit.commit();//저지르다- 저장
            }
            if(v.getId()==R.id.btnOpn)//Help
            {
                AlertDialog.Builder help=new AlertDialog.Builder(StartGame.this);
                help.setCancelable(false);
                help.setMessage("게임화면의 좌측상단에 \n초록색 원을 소모하여," +
                        "\n필살기를 사용합니다." +
                        "\n두 손가락으로 터치\n하시면 사용됩니다." +
                        "\n(Touch with two fingers)"+
                        "\n초록색원은\n흰색 게이지가 \n다 차면 생성됩니다.");
                help.setNegativeButton("Close",null);
                help.show();
            }
            if(v.getId()==R.id.btnExit)
            {
                onDestroy();
                android.os.Process.killProcess(android.os.Process.myPid());//완전 중료
            }
        }
    };
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if(soundon)
        {
            mbacksound.stop();
            mbacksound.release();
        }
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(soundon)
        {
            mbacksound.pause();
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if(soundon)
        {
            mbacksound.start();
        }
        super.onResume();
    }
}
