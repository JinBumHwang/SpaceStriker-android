package hofio.spacestriker;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MyGameView extends SurfaceView implements Callback {
    static GameThread mThread;
    SurfaceHolder mHolder;
    Context mContext;//필수구문1

    boolean canrun=true;

    boolean continues=false;

//    final byte MAX=60;//maxstage
    public static byte select;//영웅 선택
    public byte heart,superb,level,heartbonus;
    //영웅 목숨,슈퍼샷,스테이지,하트보너스
    boolean supershot=false;
    int gauge,unbeatable,bonus;//게이지,무적,보너스
    int rx,ry;//단위
    int width,height,count;//가로,세로,시간
    static int point;// 점수
    int x1,y1,x2,y2;//터치이벤트
    boolean touchsec=false;//터치이벤트
    Bitmap[] mback;
    Bitmap back,imgheart,imgbar,imgcontinues;//배경전체,목숨이미지,게이지,계속할껴?
    Hero mhero;//영웅클래스
    MyList<Shot> mshot;//영웅총클래스
    MyList<SuperShot> msupershot;//영웅슈퍼총클래스
    MyList<Empty> mempty;//적클래스
    MyList<EmShot> memshot;//적총클래스
    MyList<Item> mItem;//아이템
    MyList<Ex> mex;//폭발클래스
    Paint whitepaint,greenpaint,redpaint,blackpaint;//페인트색깔
    Matrix matrix;//매트릭스
    //static/
    int bluex;
    int bluetwo;
    int lucky;

    AudioManager audioManager=(AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);

     byte[] map=new byte[18];
     byte mapcount;//몬스터 배치
    private Random ran;

    public MyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);

        mHolder=holder;
        mContext=context;
        //홀더와 콘테스트 보존...(이거 때매 3일??)
        mThread=new GameThread(holder,context);//필수구문2

        heart=2;
        superb=2;
        //hof

        Init();
        Makestage();
        setFocusable(true);//필수구문3
    }

    @SuppressWarnings("deprecation")
    private void Init()//display, new
    {
        ran=new Random();

        Display display=((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width=display.getWidth();
        height=display.getHeight();
        rx=width/80;ry=height/80;
        mback=new Bitmap[height/2];

        matrix=new Matrix();
        whitepaint=new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(17*ry/10);
        greenpaint=new Paint();
        greenpaint.setColor(Color.GREEN);
        greenpaint.setTextSize(20*ry/10);
        redpaint=new Paint();
        redpaint.setColor(Color.argb(170, 250, 85, 0));
        blackpaint=new Paint();
        blackpaint.setColor(Color.argb(150, 0, 0, 0));

        mhero=new Hero(width/2,height-50);//new

        Shot[] tShot=new Shot[16];
        for(int i=0;i<tShot.length;i++)
            tShot[i]=new Shot();
        mshot=new MyList<>(tShot);

        SuperShot[] tSuperShot=new SuperShot[128];
        for(int i=0;i<tSuperShot.length;i++)
            tSuperShot[i]=new SuperShot();
        msupershot=new MyList<>(tSuperShot);

        Empty[] tEmpty=new Empty[64];
        for(int i=0;i<tEmpty.length;i++)
            tEmpty[i]=new Empty();
        mempty=new MyList<>(tEmpty);

        EmShot[] tEmShot =new EmShot[128];
        for(int i = 0; i< tEmShot.length; i++)
            tEmShot[i]=new EmShot();
        memshot=new MyList<>(tEmShot);

        Item[] tItem=new Item[64];
        for(int i=0;i<tItem.length;i++)
            tItem[i]=new Item();
        mItem =new MyList<>(tItem);

        Ex[] tEx=new Ex[256];
        for(int i=0;i<tEx.length;i++)
            tEx[i]=new Ex();
        mex=new MyList<>(tEx);

        for(byte a = 0;a<map.length;a++)
        {
            map[a]=a;
        }
        for(byte a=0;a<map.length;a++)
        {
            int b=ran.nextInt(map.length);
            byte temp=map[a];
            map[a]=map[b];
            map[b]=temp;
        }
    }//init/

    private void Makestage()//bitmapfactory`
    {
        back=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.back);
        back=Bitmap.createScaledBitmap(back, width, height, false);
        imgbar=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gauge);
            imgheart=Bitmap.createScaledBitmap(mhero.imghero,mhero.imghero.getWidth()/4,mhero.imghero.getWidth()/4,false);
        for(int i=0;i<mback.length;i++)
        {
            mback[i]=Bitmap.createBitmap(back,0,i*height/mback.length,width,height/mback.length);
        }
        imgcontinues=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.continues);
        imgcontinues=Bitmap.createScaledBitmap(imgcontinues, 428, 167, false);
    }//Makestage/
    private void DrawAll(Canvas canvas)//draw'
    //배경,영웅,총,적,슈퍼총,적총,폭발,점수,게이지,필살기,목숨
    {
        for(int i=0;i<mback.length;i++)
        {
            int j=(mback.length-count%mback.length);
            canvas.drawBitmap(mback[(j+i)%mback.length],0, i*height/mback.length, null);
        }
        if(unbeatable>0)
        {
            canvas.drawCircle(mhero.x, mhero.y, mhero.cx, redpaint);
        }
        canvas.drawBitmap(mhero.imghero, mhero.x-mhero.cx, mhero.y-mhero.cy,null);
        for(int i=0;i<mshot.size();i++)
        {
            Shot tmp=mshot.get(i);
            canvas.drawBitmap(tmp.imgshot,tmp.x-tmp.cx,tmp.y-tmp.cy,null);
        }
        for(int i=0;i<mempty.size();i++)
        {
            Empty tmp=mempty.get(i);
            canvas.drawBitmap(tmp.imgempty,tmp.x-tmp.cx,tmp.y-tmp.cy,null);
        }
        for(int i=0;i<msupershot.size();i++)
        {
            SuperShot tmp=msupershot.get(i);
            if(select==1)
            {
                    canvas.drawRect(mhero.x-mhero.cx/2,-mhero.cy,mhero.x+mhero.cx/2,mhero.y,tmp.supershotpaint);
            }
            else
            {
                canvas.drawBitmap(tmp.imgsupershot,tmp.x-tmp.cx,tmp.y-tmp.cy,null);
            }

        }
        for(int i=0;i<memshot.size();i++)
        {
            EmShot tmp=memshot.get(i);
            canvas.drawCircle(tmp.x, tmp.y, tmp.r*2, tmp.emshotpaint);
        }
        for(int i = 0; i< mItem.size(); i++)
        {
            Item tmp = mItem.get(i);
            canvas.drawCircle(tmp.x, tmp.y, tmp.r*2, tmp.itempaint);
        }
        for(int i=mex.size()-1;i>-1;i--)
        {
            Ex tmp=mex.get(i);
            switch (tmp.where) {
                case 2:
                    canvas.drawCircle(tmp.x, tmp.y, (39+bluex)*rx, redpaint);
                    break;
                case 3:
                    canvas.drawText("+"+tmp.realbonus,tmp.x,tmp.y,whitepaint);
                    break;
                default:
                    canvas.drawBitmap(tmp.imgex[tmp.delay],tmp.x-tmp.imgex[tmp.delay].getWidth()/2,tmp.y-tmp.imgex[tmp.delay].getHeight()/2,null);
                    break;
            }
        }
        canvas.drawText(""+point, width*4/5, whitepaint.getTextSize(), whitepaint);
        canvas.drawText("level:"+(level),width*2/5,greenpaint.getTextSize(),greenpaint);
        canvas.drawRect(10*rx/6,20*ry/10,10*rx/6+imgbar.getWidth()*gauge/120,20*ry/10+imgbar.getHeight(),whitepaint);
        canvas.drawBitmap(imgbar,10*rx/6,20*ry/10,null);
        for(int i=superb>8 ? 8:superb;i>0;i--)
        {
            canvas.drawCircle((15+(i-1)*10+i)*rx/6,16*ry/10,5*rx/6,greenpaint);
        }
        for(int i=heart>4 ? 4:heart;i>0;i--)
        {
            canvas.drawBitmap(imgheart,10+(i-1)*imgheart.getWidth(),40*ry/10,null);
        }
        canvas.drawText(""+bonus, 10, 40*ry/10+20*rx/6+whitepaint.getTextSize(), whitepaint);
        if(continues)
        {
            canvas.drawRect(0, 0, width, height, blackpaint);
            canvas.drawBitmap(imgcontinues,width/2-imgcontinues.getWidth()/2,height/2-imgcontinues.getHeight()/2,null);
        }
    }//DrawAll/
    private void MoveRemove()//캐릭터 동작,삭제'
    {


            if(mhero.dead)
            {
                soundplay(2);
                mex.add().set(mhero.x,mhero.y,1);
                if(heart>0)
                {
                    herocomeback();
                }
                else
                {
                    MainActivity.gameplay=false;
                    soundplay(6);
                    continues=true;
                }
            }else{
                mhero.moveHero();
            }

        if(gauge>120)
        {
            gauge-=120;superb++;
        }
        for(int i=mshot.size()-1;i>-1;i--)
        {
            mshot.get(i).moveShot();
            if(mshot.get(i).dead)
            {
                mshot.remove(i);
            }
        }
        for(int i=msupershot.size()-1;i>-1;i--)
        {
            msupershot.get(i).moveSupershot();
            if(msupershot.get(i).dead)
            {
                msupershot.remove(i);
            }
        }
        for(int i=mempty.size()-1;i>-1;i--)
        {
            mempty.get(i).moveEmpty();
            if(mempty.get(i).dead)
            {
                mempty.remove(i);
            }
        }
        for(int i=memshot.size()-1;i>-1;i--)
        {
            memshot.get(i).moveEmshot();
            if(memshot.get(i).dead)
            {
                memshot.remove(i);
            }
        }
        for(int i = mItem.size()-1; i>-1; i--)
        {
            mItem.get(i).moveItem();
            if(mItem.get(i).dead)
            {
                mItem.remove(i);
            }
        }
        for(int i=mex.size()-1;i>-1;i--)
        {
            mex.get(i).moveEx();
            if(mex.get(i).dead)
            {
                mex.remove(i);
            }
        }
    }//MoveRemove/
    class Hero//플레이어 기체
    {
        int cx,cy;//중심점
        int x,y;//좌표
        boolean dead=false;//생사
        byte delay;//대기
        Bitmap imghero;
        public Hero(int _x,int _y) {
            // TODO Auto-generated constructor stub
            x=_x;y=_y;
            switch (select) {
                case 0:
                    imghero=BitmapFactory.decodeResource(getResources(), R.drawable.hblueberry);
                    break;
                case 1:
                    imghero=BitmapFactory.decodeResource(getResources(), R.drawable.hcrystal);
                    break;
                case 2:
                    imghero=BitmapFactory.decodeResource(getResources(), R.drawable.hblue);
                    break;
                default:
                    break;
            }
            imghero=Bitmap.createScaledBitmap(imghero, 80*rx/6, 80*ry/10, false);
            cx=(imghero.getWidth()/2);cy=(imghero.getHeight()/2);
        }
        public void moveHero()
        {
            delay++;
            if(y-cy<0)
            {
                y=cy;
            }
            if(y+cy>height)
            {
                y=height-cy;
            }
            if(x-cx<0)
            {
                x=cx;
            }
            if(x+cx>width)
            {
                x=width-cx;
            }
            if(supershot)
            {
                switch (select) {
                    case 0:

                        msupershot.add().set(x+(count%2==1?cx*rx/6:-cx*rx/6),y,30*ry/10,(delay<bonus?3:2));
                        //120(+14)
                        if(delay>59)
                        {
                            supershot=false;superb--;
                        }
                        break;
                    case 1:
                        msupershot.add().set(x,y,30*ry/10,(delay>40-bonus?2:1));
                        //80(+15)
                        if(delay>59)
                        {
                            supershot=false;superb--;
                        }
                        break;
                    case 2:
                        msupershot.add().set(x,y,-50*ry/10,26+bonus/3);
                        //26(+5)+14(+30)=75
                        supershot=false;superb--;
                        break;
                    default:
                        break;
                }
            }
            if(!supershot && delay%6==0)
            {
                mshot.add().set(x,y,true);//플레이어 총알 생성
                if(delay>29)
                {
                    if(select==2)
                    {
                        mshot.add().set(x-56*rx/6,y+ry,false);
                        mshot.add().set(x+56*rx/6,y+ry,false);
                    }
                    else
                    {
                        mshot.add().set(x,y-4*ry,false);
                    }
                    delay=0;
                }
            }
            if(unbeatable>0)
            {
                unbeatable--;
            }
        }
    }//플레이어 기체 끝
    class Shot//플레이어 총
    {
        int x,y;//좌표
        int cx,cy;//중심점
        int sx,sy;//속도
        boolean dead=false;//생사
        boolean one;
        Bitmap imgshot;
        public void set(int _x,int _y,boolean _one){
            // TODO Auto-generated constructor stub
            x=_x;y=_y;one=_one;dead=false;
            switch (select) {
                case 0:
                    if(one)
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shot2);
                        imgshot=Bitmap.createScaledBitmap(imgshot, 62*rx/6, 25*ry/10, false);
                        sx=0;
                        sy=(ry*7/2);
                    }
                    else
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shotblueberry);
                        int min=height+width;
                        for(int i=mempty.size()-1;i>-1;i--)
                        {
                            if(Math.abs(mhero.x-mempty.get(i).x)+Math.abs(mhero.y-mempty.get(i).y)<min)
                            {
                                min=Math.abs(mhero.x-mempty.get(i).x)+Math.abs(mhero.y-mempty.get(i).y);
                                sx=(ry*3)*(mhero.x-mempty.get(i).x)/min;
                                sy=(ry*3)*(mhero.y-mempty.get(i).y)/min;
                            }
                        }
                        if(min==height+width)
                        {
                            sy=(ry*3);
                        }
                        imgshot=Bitmap.createScaledBitmap(imgshot, 30*rx/6, 30*ry/10, false);
                    }
                    break;
                case 1:
                    if(one)
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shot1);
                        imgshot=Bitmap.createScaledBitmap(imgshot, 40*rx/6, 25*ry/10, false);
                        sy=(ry*7/2);
                    }
                    else
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shot6);
                        imgshot=Bitmap.createScaledBitmap(imgshot, 30*rx/6, 48*ry/10, false);
                        sy=(ry*3/2);
                    }
                    break;
                case 2:
                    if(one)
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shot3);
                        imgshot=Bitmap.createScaledBitmap(imgshot, 40*rx/6, 25*ry/10, false);
                        sy=(ry*7/2);
                    }
                    else
                    {
                        imgshot=BitmapFactory.decodeResource(getResources(), R.drawable.shot4);
                        imgshot=Bitmap.createScaledBitmap(imgshot, 30*rx/6, 48*ry/10, false);
                        sy=(ry*6);
                    }
                    break;
                default:
                    break;
            }
            cx=imgshot.getWidth()/2;cy=imgshot.getHeight()/2;
        }
        public void moveShot()
        {
            y-=sy;x-=sx;
            if(y<-2*40*ry/10)
            {
                dead=true;
                return;
            }
            for(int i=mempty.size()-1;i>-1;i--)//맞추기
            {
                if(dead){break;}
                if(Math.abs(mempty.get(i).x-x)<cx+mempty.get(i).cx && Math.abs(mempty.get(i).y-y)<cy+mempty.get(i).cy)//맞추기
                {
                    int critical=ran.nextInt(17-bonus);
                    soundplay(1);
                    if(one)
                    {
                        gauge+=(critical==0 ? 2:1);
                        unbeatable+=(critical==0 ? 2:0);
                        mempty.get(i).hp-=(critical==0 ? 2:1);
                    }
                    else
                    {
                        switch (select) {
                            case 0://blueberry
                                mempty.get(i).hp-=(critical==0 ? 4:2);
                                break;
                            case 1://yellow
                                mempty.get(i).hp-=(critical==0 ? 6:3);
                                break;
                            case 2://blue
                                mempty.get(i).hp-=(critical==0 ? 3:1+bluetwo);
                                bluetwo=(critical>0 ?(bluetwo<1 ? 1:0):bluetwo);
                                break;
                            default:
                                break;
                        }
                    }
                    dead=true;
                    mex.add().set(x,y,0);
                    return;
                }
            }
        }
    }//플레이어 총 끝
    class SuperShot//플레이어 슈퍼총
    {
        int x,y;//좌표
        int cx,cy;//중심점
        int sx,sy;//속도
        int force;
        boolean dead=false;//생사
        Bitmap imgsupershot;
        Paint supershotpaint;
        public SuperShot(){
            // TODO Auto-generated constructor stub
            switch (select) {
                case 2:
                    imgsupershot=BitmapFactory.decodeResource(getResources(), R.drawable.shotblue);
                    imgsupershot=Bitmap.createScaledBitmap(imgsupershot, 25*rx/6, 40*ry/10, false);
                    cx=imgsupershot.getWidth()/2;cy=imgsupershot.getHeight()/2;
                    break;
                case 1:
                    supershotpaint=new Paint();
                    supershotpaint.setColor(Color.argb(30, 250, 250, 0));
                    cx=rx*25/3;cy=ry*5;
                    break;
                case 0:
                    int k=ran.nextInt(4);
                    imgsupershot=BitmapFactory.decodeResource(getResources(), R.drawable.shotblueberry);
                    matrix.postRotate(20);
                    imgsupershot=Bitmap.createBitmap(imgsupershot, 0, 0, imgsupershot.getWidth(), imgsupershot.getHeight(), matrix, false);
                    imgsupershot=Bitmap.createScaledBitmap(imgsupershot, 26*rx/6, 26*ry/10, false);
                    sx=(3-k*2)*rx;
                    cx=imgsupershot.getWidth()/2;cy=imgsupershot.getHeight()/2;
                    break;
                default:
                    break;
            }
        }
        public void set(int _x, int _y, int _sy, int _force){
            // TODO Auto-generated constructor stub
            x=_x;y=_y;sy=_sy*ry/10;force=_force;dead=false;
        }
        public void moveSupershot()
        {
            y-=sy;
            switch (select) {
                case 2:
                    if(sy>0)
                    {
                        int tmpY=y+sy*ry/10;
                       mex.add().set(x,tmpY,0);
                        
                       mex.add().set(x,tmpY+25*ry/10,1);
                    }
                    if(sy<7*ry)
                    {
                        sy+=2*ry;
                    }
                    for(int i=memshot.size()-1;i>-1;i--)//적 총알 지우기
                    {
                        if(Math.abs(x-memshot.get(i).x)<4*rx && y<memshot.get(i).y)
                        {
                            memshot.remove(i);
                        }
                    }
                    if(y<24*ry){
                        for(int i=mempty.size()-1;i>-1;i--)//맞추기
                        {
                            if (Math.abs(mempty.get(i).x - x) < 2 * cx + mempty.get(i).cx) {
                                mempty.get(i).hp -= force;

                            }
                        }
                        mex.add().set(x,3*ry,0);
                        mex.add().set(x,9*ry,0);
                        mex.add().set(x,15*ry,0);
                        mex.add().set(x,21*ry,0);
                        mex.add().set(x, y, 2);
                        dead=true;
                        return;
                    }
                    break;
                case 1:
                    for(int i=memshot.size()-1;i>-1;i--)//적 총알 지우기
                    {

                            if(Math.abs(mhero.x-memshot.get(i).x)<mhero.cx && mhero.y+mhero.cy>memshot.get(i).y)
                            {
                               mex.add().set(memshot.get(i).x,memshot.get(i).y,0);
                                memshot.remove(i);
                            }
                    }
                    for(int i=mempty.size()-1;i>-1;i--)//맞추기
                    {
                        if(dead){break;}
                        if(Math.abs(mempty.get(i).x-x)<cx+mempty.get(i).cx && Math.abs(mempty.get(i).y-y)<cy+mempty.get(i).cy)//맞추기
                        {
                            dead=true;
                            mempty.get(i).hp-=force;
                            return;
                        }
                    }
                    break;
                case 0:
                    x-=sx;
                    if(x<0 || x>width)
                    {
                        sx*=-4;
                    }
                    if(y>height)
                    {
                        dead=true;
                        return;
                    }
                    for(int i=mempty.size()-1;i>-1;i--)//맞추기
                    {
                        if(dead){break;}
                        if(Math.abs(mempty.get(i).x-x)<cx+mempty.get(i).cx && Math.abs(mempty.get(i).y-y)<cy+mempty.get(i).cy)//맞추기
                        {
                            dead=true;
                            mempty.get(i).hp-=force;
                           mex.add().set(x,y,0);
                            return;
                        }
                    }
                    break;
                default:
                    break;
            }
            if(y<-2*40*ry/10)
            {
                switch (select) {
                    case 0:
                        sy*=-1;
                        break;
                    default:
                        dead=true;
                        break;
                }
            }
        }
    }//플레이어 슈퍼총 끝
    class Empty//적 기체
    {
        int x,y;//좌표
        int cx,cy;//중심점
        int dx,dy,distance,dxd,dyd;//플레이어와의 거리
        int hp,reward;//체력
        int who,reload,delay;//누구,장전,대기
        int sx,sy;//속도
        boolean dead,entry=false;//생사,입장
        Bitmap imgempty;
        public void set(int _x,int _y,int c,int i) {
            // TODO Auto-generated constructor stub
            x=_x;y=_y;who=c;delay=i;dead=false;entry=false;
            //reloadhp
            switch (who) {
                case 105:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em105);
                    reload=19;hp=288;
                    break;
                case 104:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em104);
                    reload=19;hp=216;
                    break;
                case 103:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em103);
                    reload=19;hp=168;
                    break;
                case 102:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em102);
                    reload=55;hp=74;
                    break;
                case 101:
                    imgempty=BitmapFactory.decodeResource(getResources(),R.drawable.em101);
                    reload=40;hp=74;
                    break;
                case 100:
                    imgempty=BitmapFactory.decodeResource(getResources(),R.drawable.em100);
                    reload=30;hp=50;
                    break;
                case 11:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em11);
                    reload=50;hp=16;
                    break;
                case 10:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em10);
                    reload=125;hp=21;
                    break;
                case 9:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em09);
                    reload=30;hp=11;
                    break;
                case 8:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em08);
                    reload=30;hp=4;
                    break;
                case 7:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em07);
                    reload=90;hp=15;
                    break;
                case 6:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em06);
                    reload=40;hp=12;
                    break;
                case 5:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em05);
                    reload=40;hp=11;
                    break;
                case 4:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em04);
                    reload=40;hp=5;
                    break;
                case 3:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em03);
                    reload=60;hp=7;
                    break;
                case 2:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em02);
                    reload=70;hp=7;
                    break;
                case 1:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em01);
                    reload=70;hp=5;
                    break;
                default:
                    imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em00);
                    reload=60;hp=3;
                    break;
            }
            //reloadhp/
            imgempty=Bitmap.createScaledBitmap(imgempty, (who>102 ? 200:80)*rx/6, (who>102 ? 128:80)*ry/10, false);
            cx=imgempty.getWidth()/2;cy=imgempty.getHeight()/2;
            sy=(ry*8);reward=hp*10;sx=0;
        }
        public void moveEmpty()
        {
            y+=sy;x+=sx;delay++;
            if(!entry)
            {
                if(sy<2*ry/10){sy=0;entry=true;}else{sy-=ry*2;}
            }
            //delayreload
            dx=mhero.x-x;dy=mhero.y-y;distance=Math.abs(dx)+Math.abs(dy)+1;
            if(delay==reload)
            {
                dxd=100*dx/distance;dyd=100*dy/distance;
            }
            if(delay>reload)
            {
                switch (who) {
                    case 105:
                        if(delay%10==0)
                        {
                            if(hp>143)
                            {
                                int pa=ran.nextInt(5)-2;
                                memshot.add().set(x,y,25*dx/distance+pa*2,25*dy/distance);
                                memshot.add().set(x,y,pa!=0?25*dx/distance-pa*2:22*dx/distance,pa!=0?25*dy/distance:22*dy/distance);
                                memshot.add().set(x,y,29*dx/distance,29*dy/distance);
                            }
                            else
                            {
                                for(int i=0;i<4;i++)
                                {
                                    for(int j=i;j>-1;j--)
                                    {
                                        memshot.add().set(x,y,(j*2-i)*4,i*4+9);
                                    }
                                }
                            }
                        }
                        if(delay>reload+29)
                        {
                            if(hp>192)
                            {
                                for(int i=0;i<4;i++)
                                {
                                    for(int j=i;j>-1;j--)
                                    {
                                        memshot.add().set(x,y,(j*2-i)*5,i*5+9);
                                    }
                                }
                            }
                            else if(hp>96)
                            {
                                memshot.add().set(x,y,41*dx/distance,41*dy/distance);
                                memshot.add().set(x,y,41*dx/distance+5,41*dy/distance-5);
                                memshot.add().set(x,y,41*dx/distance,41*dy/distance-5);
                                memshot.add().set(x,y,41*dx/distance-5,41*dy/distance-5);
                                memshot.add().set(x,y,41*dx/distance,41*dy/distance-10);
                            }
                            else
                            {
                                memshot.add().set(x,y,42*dx/distance,42*dy/distance);
                                memshot.add().set(x-100*rx/6,y,-4,6);
                                memshot.add().set(x-100*rx/6,y,2,9);
                                memshot.add().set(x-100*rx/6,y,8,16);
                                memshot.add().set(x+100*rx/6,y,4,6);
                                memshot.add().set(x+100*rx/6,y,-2,9);
                                memshot.add().set(x+100*rx/6,y,-8,16);
                            }
                            delay=0;
                        }
                        break;
                    case 104:
                        if(hp>72 && delay%6==0)
                        {
                            int pa=ran.nextInt(4);
                            memshot.add().set(x-(36+20*pa)*rx/6,y-20*ry/10,0,15);
                            memshot.add().set(x+(36+20*pa)*rx/6,y-20*ry/10,0,15);
                        }
                        else if(hp<73)
                        {
                            if(delay%2==1)
                            {
                                memshot.add().set(x,y,35*dxd/100,35*dyd/100);
                            }
                            if(delay%6==5)
                            {
                                memshot.add().set(x,y,30*dxd/100+9,30*dyd/100);
                                memshot.add().set(x,y,30*dxd/100-9,30*dyd/100);
                            }
                            if(delay%10==9)
                            {
                                memshot.add().set(x,y,30*dxd/100+12,30*dyd/100);
                                memshot.add().set(x,y,30*dxd/100-12,30*dyd/100);
                            }
                        }
                        if(delay>reload+29)
                        {
                            for(int i=0;i<6;i++)
                            {
                                memshot.add().set(x,y,(34-i)*dx/distance+i%2*8,(34-i)*dy/distance);
                                if(i%2==1)
                                {
                                    memshot.add().set(x,y,(34-i)*dx/distance-8,(34-i)*dy/distance);
                                }
                            }
                            delay=0;
                        }
                        break;
                    case 103:
                        if(delay%20==0)
                        {
                            memshot.add().set(x-50*rx/6,y-7*ry,+1,5);
                            memshot.add().set(x+50*rx/6,y-7*ry,-1,5);
                            memshot.add().set(x-50*rx/6,y,-2,6);
                            memshot.add().set(x+50*rx/6,y,2,6);
                        }
                        if(delay>reload+39)
                        {
                            if(hp<55)
                            {
                                memshot.add().set(x,y,36*dx/distance,36*dy/distance);
                                memshot.add().set(x-30*rx/6,y,32*dx/distance,32*dy/distance);
                                memshot.add().set(x+30*rx/6,y,32*dx/distance,32*dy/distance);
                                memshot.add().set(x-60*rx/6,y,30*dx/distance,30*dy/distance);
                                memshot.add().set(x+60*rx/6,y,30*dx/distance,30*dy/distance);
                                delay=0;
                            }
                            else if(hp<110)
                            {
                                memshot.add().set(x,y,3,7);
                                memshot.add().set(x,y,-3,7);
                                memshot.add().set(x,y-7*ry,36*dx/distance,36*dy/distance);
                                delay=0;
                            }
                            else
                            {
                                memshot.add().set(x,y,28*dx/distance,28*dy/distance);
                                delay=0;
                            }
                        }
                        break;
                    case 102:
                        if(delay%2==1)
                        {
                            memshot.add().set(x,y,30*dxd/100,30*dyd/100);
                        }
                        if(delay%6==5)
                        {
                            memshot.add().set(x,y,25*dxd/100+7,25*dyd/100);
                            memshot.add().set(x,y,25*dxd/100-7,25*dyd/100);
                        }
                        if(delay%9==8)
                        {
                            memshot.add().set(x,y,25*dxd/100+10,25*dyd/100);
                            memshot.add().set(x,y,25*dxd/100-10,25*dyd/100);
                        }
                        if(delay>reload+33)
                        {
                            delay=0;
                        }
                        break;
                    case 101:
                        if(delay!=reload+2)
                        {
                            memshot.add().set(x,y,27*dxd/100,27*dyd/100);
                        }
                        if(delay==reload+2)
                        {
                            memshot.add().set(x,y,27*dxd/100+1,27*dyd/100);
                            memshot.add().set(x,y,27*dxd/100-1,27*dyd/100);
                        }
                        if(delay>reload+2)
                        {
                            memshot.add().set(x,y,27*dxd/100+2,27*dyd/100);
                            memshot.add().set(x,y,27*dxd/100-2,27*dyd/100);
                            if(hp<26)
                            {
                                summon(4,1);
                                mempty.get(mempty.size()-1).hp=2;
                                mempty.get(mempty.size()-1).reward=0;
                            }
                            delay=0;
                        }
                        break;
                    case 100:
                        memshot.add().set(x,y,29*dx/distance,29*dy/distance);
                        delay=0;
                        break;
                    case 11:
                        memshot.add().set(x+20*rx/6,y+20*ry/10,31*dx/distance-2,31*dy/distance-2);
                        memshot.add().set(x-20*rx/6,y+20*ry/10,31*dx/distance+2,31*dy/distance-2);
                        memshot.add().set(x+20*rx/6,y,31*dx/distance-3,31*dy/distance);
                        memshot.add().set(x-20*rx/6,y,31*dx/distance+3,31*dy/distance);
                        memshot.add().set(x+20*rx/6,y-20*ry/10,31*dx/distance-4,31*dy/distance+4);
                        memshot.add().set(x-20*rx/6,y-20*ry/10,31*dx/distance+4,31*dy/distance+4);
                        delay=0;
                        break;
                    case 10:
                        delay=0;
                       mex.add().set(x,y,0);
                       mex.add().set(x-20*rx/6,y-10*ry/10,0);
                       mex.add().set(x+20*rx/6,y-10*ry/10,0);
                       mex.add().set(x-20*rx/6,y+10*ry/10,0);
                       mex.add().set(x+20*rx/6,y+10*ry/10,0);
                        who=11;hp=16;reload=45;
                        imgempty=BitmapFactory.decodeResource(getResources(), R.drawable.em11);
                        imgempty=Bitmap.createScaledBitmap(imgempty, 80*rx/6, 80*ry/10, false);
                        break;
                    case 9:
                        memshot.add().set(x,y,31*dx/distance,31*dy/distance);
                        memshot.add().set(x,y-15*ry/10,31*dx/distance,31*dy/distance);
                        memshot.add().set(x+15*rx/6,y-25*ry/10,31*dx/distance,31*dy/distance);
                        memshot.add().set(x-15*rx/6,y-25*ry/10,31*dx/distance,31*dy/distance);
                        delay=0;
                        break;
                    case 8:
                        if(y<height/2)
                        {
                            sx+=36*dx/distance*rx/6;sy+=36*dy/distance*ry/10;
                        }
                        delay=0;
                        break;
                    case 7:
                        if(delay%2==1)
                        {
                            memshot.add().set(x,y,21*dxd/100,21*dyd/100);
                        }
                        if(delay%4==3)
                        {
                            memshot.add().set(x,y,20*dxd/100+7,20*dyd/100);
                            memshot.add().set(x,y,20*dxd/100-7,20*dyd/100);
                        }
                        if(delay>reload+25)
                        {
                            delay=0;
                        }
                        break;
                    case 6:
                        memshot.add().set(x,y+36*ry/10,30*dx/distance,30*dy/distance);
                        memshot.add().set(x-36*rx/6,y,30*dx/distance,30*dy/distance);
                        memshot.add().set(x+36*rx/6,y,30*dx/distance,30*dy/distance);
                        memshot.add().set(x,y-36*ry/10,30*dx/distance,30*dy/distance);
                        delay=0;
                        break;
                    case 5:
                        memshot.add().set(x-36*rx/6,y+36*ry/10,20*dx/distance,20*dy/distance);
                        memshot.add().set(x+36*rx/6,y+36*ry/10,20*dx/distance,20*dy/distance);
                        memshot.add().set(x-36*rx/6,y-36*ry/10,20*dx/distance,20*dy/distance);
                        memshot.add().set(x+36*rx/6,y-36*ry/10,20*dx/distance,20*dy/distance);
                        delay=0;
                        break;
                    case 4:
                        if(y<height/2)
                        {
                            sx+=30*dx/distance*rx/6;sy+=30*dy/distance*ry/10;
                        }
                        delay=0;
                        break;
                    case 3:
                        memshot.add().set(x,y,21*dx/distance,21*dy/distance);
                        delay=0;
                        break;
                    case 2:
                        memshot.add().set(x-16*rx/6,y,-1,4);
                        memshot.add().set(x+16*rx/6,y,1,4);
                        memshot.add().set(x,y,0,9);
                        delay=0;
                        break;
                    case 1:
                        for(int i=-1;i<2;i++)
                        {
                            memshot.add().set(x+16*i*rx/6,y,i,7);
                        }
                        delay=0;
                        break;
                    default:
                        memshot.add().set(x,y,0,9);
                        delay=0;
                        break;
                }
            }
            //delayreload/

            //부딪히기
                if(unbeatable<1)
                {
                    if(Math.abs(mhero.x-x)<cx+mhero.cx*2/5 && Math.abs(mhero.y-y)<cy+mhero.cy*4/5)
                    {
                        mhero.dead=true;
                        return;
                    }
                }
            if(hp<1)
            {
                dead=true;
                soundplay(3);
                point+=reward;
                for(int i=0;i<3;i++)
                {
                    int j=(ran.nextInt(5)-2);
                    int k=(ran.nextInt(5)-2);
                   mex.add().set(x+15*rx/6*j,y+15*ry/10*k,1);
                }
                int item=0;
                if(who>102)
                {
                    mItem.add().set(x,y,0);
                }
                else
                {
                    item=ran.nextInt(70-lucky);
                    if(item<2)
                    {
                        mItem.add().set(x,y,1);lucky=0;
                    }
                    else if(item<10)
                    {
                        mItem.add().set(x,y,2);lucky=0;
                    }
                    else
                    {
                        lucky++;
                    }
                }

            }
            if(y>height || x<0 || x>width)
            {
                dead=true;
            }
        }
    }//적 기체 끝
    class EmShot//적 총
    {
        int r=ry/2;//반지름
        int x,y;//좌표
        int sx,sy;//속도
        boolean dead=false;//생사
        Paint emshotpaint;

        public EmShot() {
            // TODO Auto-generated constructor stub
            emshotpaint=new Paint();
        }
        public void set(int _x,int _y,int _sx,int _sy) {
            // TODO Auto-generated constructor stub
            x=_x;y=_y;sx=_sx*rx/6;sy=_sy*ry/10;dead=false;
            emshotpaint.setColor(Color.argb(240, 250, 255-2*(Math.abs(sx)+Math.abs(sy)), 0));
        }
        public void moveEmshot()
        {
            y+=sy;x+=sx;
            if(y>height || x>width || x<0)
            {
                dead=true;
                return;
            }
            if(Math.abs(mhero.x-x)<mhero.cx/2 && Math.abs(mhero.y-y)<mhero.cy*3/4)//맞추기
            {
                dead=true;
                if(unbeatable<1)
                {
                    mhero.dead=true;
                }
            }
        }
    }//적 총 끝
    class Item//아이템
    {
        int r=ry/3*2;//반지름
        int x,y;//좌표
        int sy=2*ry/10;//속도
        int what;//0은 폭탄,1은 무적
        boolean dead=false;//생사
        Paint itempaint;

        public Item() {
            // TODO Auto-generated constructor stub
            itempaint=new Paint();
        }
        public void set(int _x,int _y,int _what) {
        // TODO Auto-generated constructor stub
        x=_x;y=_y;what=_what;dead=false;
        switch (what) {
            case 0:
                itempaint.setColor(Color.GREEN);
                break;
            case 1:
                itempaint.setColor(Color.argb(255,255,125,255));
                break;
            case 2:
                itempaint.setColor(Color.CYAN);
                break;
            default:
                break;
        }
    }
        public void moveItem()
        {
            y+=sy;
            if(y>height || x>width || x<0)
            {
                dead=true;
                return;
            }
                if(Math.abs(mhero.x-x)<mhero.cx*3/4 && Math.abs(mhero.y-y)<mhero.cy)//먹기
                {
                    dead=true;
                    switch (what) {
                        case 0:
                            superb++;
                            break;
                        case 1:
                            unbeatable+=20;
                            gauge+=memshot.size()+mempty.size()+ mItem.size();
                            break;
                        case 2:
                            if(bonus<15)
                            {
                                bonus++;
                            }
                            point+=100*bonus;
                           mex.add().set(x,y,3);
                            break;
                        default:
                            break;
                    }
                }
        }
    }//아이템 끝
    class Ex//폭발
    {
        int x,y;//좌표 밑 중심
        byte delay;//대기
        int where;//어디서
        boolean dead=false;//끝났나?
        int realbonus;
        Bitmap imgex[]=new Bitmap[6];
        public void set(int _x,int _y,int j){
            x=_x;y=_y;where=j;dead=false;delay=0;
            switch (where) {
                case 0:
                    for(int i=0;i<6;i++)
                    {
                        imgex[i]=BitmapFactory.decodeResource(getResources(), R.drawable.ex01+i);
                        imgex[i]=Bitmap.createScaledBitmap(imgex[i], 50*rx/6, 50*ry/10, false);
                    }
                    break;
                case 1:
                    for(int i=0;i<6;i++)
                    {
                        imgex[i]=BitmapFactory.decodeResource(getResources(), R.drawable.ex11+i);
                        imgex[i]=Bitmap.createScaledBitmap(imgex[i], 50*rx/6, 50*ry/10, false);
                    }
                    break;
                case 2:
                    bluex=bonus;
                    break;
                default :
                    realbonus=bonus*100;
                    break;
            }
        }
        public void moveEx()
        {
            Log.d("View.java",this.toString()+":"+dead+where+"_"+(delay+1));
            delay++;
            if(where<2)
            {
                if(delay>5)
                {
                    dead=true;
                }
            }
            else if(where==3)
            {
                if(delay>11)
                {
                    dead=true;
                }
            }
            else//blue's supershot (7+bonus)
            {
                for(int j=memshot.size()-1;j>-1;j--)
                {
                    if(Math.abs(memshot.get(j).x-x)<(39+bluex)*rx && Math.abs(memshot.get(j).y-y)<(21+bluex/5)*ry)
                    {
                       mex.add().set(memshot.get(j).x,memshot.get(j).y,0);
                        memshot.remove(j);
                    }
                }
                for(int j=mempty.size()-1;j>-1;j--)
                {
                    if(Math.abs(mempty.get(j).x-x)<(39+bluex)*rx && Math.abs(mempty.get(j).y-y)<(21+bluex/5)*ry)
                    {
                        mempty.get(j).hp-=2;
                        if(delay>bluex+6)
                        {
                           mex.add().set(mempty.get(j).x,mempty.get(j).y,1);
                        }
                    }
                }
                if(delay>bluex+6)
                {
                    dead=true;
                }
            }
        }
    }//폭발 끝
    private void herocomeback()
    {
        heart--;
        bonus=(bonus>4? bonus-5:0);
        unbeatable+=60;
        mhero.x=width/2;mhero.y=height-height/10;
        mhero.dead=false;
       mex.add().set(width/2,height-height/10-25*ry/10,0);
       mex.add().set(width/2+25*rx/6,height-height/10,0);
       mex.add().set(width/2-25*rx/6,height-height/10,0);
       mex.add().set(width/2+12*rx/6,height-height/10+25*ry/10,0);
       mex.add().set(width/2-12*rx/6,height-height/10+25*ry/10,0);
       memshot.removeAll();
    }
    //summon
    private void summon(int c,int d)//a:0~5,b:0~2,d:0~4;
    {
        if(mapcount>map.length-1)mapcount=0;
        int a=(map[mapcount]%6);
        int b=(map[mapcount]/6);
        mempty.add().set(a*width/6+width/12,(b-2)*height/10,c,-d*30);
        mapcount++;
    }//summon/
    //playstage
    private void Playstage() {
        // TODO Auto-generated method stub
        if(mempty.size()==0)
        {
            if(level%10==9)
            {
                soundplay(5);
            }
            else
            {
                if(level!=0)
                {
                    soundplay(4);
                }
                if(point>(heartbonus+1)*50000)
                {
                    heartbonus++;
                    heart++;
                }
            }
            switch (level) {
                case 0:
                    summon(0,0);
                    summon(0,1);
                    summon(0,2);
                    summon(0,3);
                    break;
                case 1:
                    summon(1,0);
                    summon(1,1);
                    summon(1,2);
                    break;
                case 2:
                    summon(1,0);
                    summon(1,1);
                    summon(1,2);
                    summon(0,3);
                    summon(0,4);
                    break;
                case 3:
                    summon(0,0);
                    summon(0,1);
                    summon(2,2);
                    summon(2,3);
                    summon(2,4);
                    break;
                case 4:
                    summon(3,0);
                    summon(1,1);
                    summon(1,2);
                    summon(1,3);
                    summon(1,4);
                    break;
                case 5:
                    summon(3,0);
                    summon(2,1);
                    summon(2,2);
                    summon(1,3);
                    break;
                case 6:
                    summon(4,4);
                    summon(4,3);
                    summon(1,0);
                    summon(1,1);
                    break;
                case 7:
                    summon(2,0);
                    summon(2,1);
                    summon(2,2);
                    summon(2,3);
                    summon(2,4);
                    summon(2,4);
                    break;
                case 8:
                    summon(3,1);
                    summon(3,1);
                    summon(3,1);
                    summon(4,0);
                    summon(0,2);
                    summon(0,2);
                    summon(0,0);
                    break;
                case 9:
                    summon(100,0);
                    break;
                case 10:
                    summon(0,0);
                    summon(0,0);
                    summon(0,0);
                    summon(0,0);
                    break;
                case 11:
                    summon(1,0);
                    summon(1,0);
                    summon(1,0);
                    break;
                case 12:
                    summon(0,0);
                    summon(0,0);
                    summon(1,0);
                    summon(1,0);
                    summon(1,0);
                    break;
                case 13:
                    summon(0,0);
                    summon(0,0);
                    summon(2,0);
                    summon(2,0);
                    summon(2,0);
                    break;
                case 14:
                    summon(3,0);
                    summon(1,0);
                    summon(1,0);
                    summon(1,0);
                    summon(1,0);
                    break;
                case 15:
                    summon(3,0);
                    summon(2,0);
                    summon(2,0);
                    summon(1,0);
                    break;
                case 16:
                    summon(4,0);
                    summon(4,0);
                    summon(1,0);
                    summon(1,0);
                    break;
                case 17:
                    summon(2,0);
                    summon(2,1);
                    summon(2,2);
                    summon(2,3);
                    summon(2,4);
                    summon(2,4);
                    summon(2,4);
                    break;
                case 18:
                    summon(3,0);
                    summon(3,0);
                    summon(3,0);
                    summon(4,0);
                    summon(0,4);
                    summon(0,4);
                    summon(0,4);
                    break;
                case 19:
                    summon(101,0);
                    break;
                case 20:
                    summon(5,0);
                    summon(5,1);
                    summon(5,2);
                    summon(5,3);
                    summon(2,0);
                    summon(2,1);
                    summon(2,2);
                    summon(2,3);
                    break;
                case 21:
                    summon(5,0);
                    summon(5,1);
                    summon(5,2);
                    summon(5,3);
                    summon(5,4);
                    summon(5,2);
                    summon(5,3);
                    break;
                case 22:
                    summon(5,0);
                    summon(5,0);
                    summon(5,0);
                    summon(5,0);
                    summon(5,1);
                    summon(5,1);
                    summon(4,0);
                    summon(4,1);
                    summon(4,2);
                    break;
                case 23:
                    summon(6,0);
                    summon(6,1);
                    summon(6,2);
                    summon(6,3);
                    break;
                case 24:
                    summon(7,0);
                    summon(7,4);
                    break;
                case 25:
                    summon(4,0);
                    summon(4,1);
                    summon(4,2);
                    summon(4,3);
                    summon(4,4);
                    summon(3,0);
                    summon(3,0);
                    summon(0,0);
                    summon(0,0);
                    break;
                case 26:
                    summon(1,0);
                    summon(1,1);
                    summon(1,2);
                    summon(1,3);
                    summon(1,4);
                    summon(2,0);
                    summon(2,1);
                    summon(2,2);
                    summon(2,3);
                    summon(2,4);
                    break;
                case 27:
                    summon(5,0);
                    summon(5,1);
                    summon(5,2);
                    summon(6,1);
                    summon(6,2);
                    summon(6,3);
                    summon(7,4);
                    break;
                case 28:
                    summon(7,0);
                    summon(7,2);
                    summon(7,4);
                    break;
                case 29:
                    summon(102,0);
                    break;
                case 30:
                    summon(8,0);
                    summon(8,1);
                    summon(8,2);
                    summon(8,3);
                    summon(8,4);
                    break;
                case 31:
                    summon(9,0);
                    summon(9,1);
                    summon(9,2);
                    summon(8,3);
                    summon(8,4);
                    summon(8,3);
                    summon(8,4);
                    break;
                case 32:
                    summon(9,0);
                    summon(9,0);
                    summon(9,1);
                    summon(9,2);
                    summon(9,3);
                    summon(9,4);
                    summon(9,4);
                    break;
                case 33:
                    summon(10,4);
                    summon(8,0);
                    summon(8,1);
                    summon(8,2);
                    summon(9,1);
                    summon(9,2);
                    summon(9,3);
                    summon(9,4);
                    break;
                case 34:
                    summon(100,0);
                    summon(100,4);
                    break;
                case 35:
                    summon(101,0);
                    summon(101,4);
                    break;
                case 36:
                    summon(102,0);
                    summon(10,4);
                    break;
                case 37:
                    summon(10,0);
                    summon(10,2);
                    summon(10,4);
                    break;
                case 38:
                    summon(11,0);
                    summon(11,4);
                    summon(10,4);
                    summon(10,4);
                    summon(10,4);
                    break;
                case 39:
                    summon(103,0);
                    mempty.get(mempty.size()-1).x=width/2;
                    mempty.get(mempty.size()-1).y=-40*ry/10;
                    break;
                case 40:
                    summon(9,0);
                    summon(9,1);
                    summon(9,2);
                    summon(9,3);
                    summon(9,4);
                    summon(8,0);
                    summon(7,0);
                    break;
                case 41:
                    summon(7,0);
                    summon(7,1);
                    summon(7,2);
                    summon(7,3);
                    summon(7,4);
                    break;
                case 42:
                    summon(6,0);
                    summon(7,0);
                    summon(6,1);
                    summon(7,2);
                    summon(6,3);
                    summon(7,4);
                    summon(100,0);
                    break;
                case 43:
                    summon(2,0);
                    summon(2,1);
                    summon(2,2);
                    summon(2,3);
                    summon(2,4);
                    summon(10,1);
                    summon(11,2);
                    summon(10,3);
                    summon(10,4);
                    break;
                case 44:
                    summon(5,0);
                    summon(6,0);
                    summon(5,1);
                    summon(6,1);
                    summon(5,2);
                    summon(6,2);
                    summon(5,3);
                    summon(6,3);
                    summon(5,4);
                    summon(6,4);
                    summon(4,0);
                    break;
                case 45:
                    for(int i=0;i<12;i++)
                    {
                        summon(i,i%5);
                    }
                    break;
                case 46:
                    for(int i=0;i<12;i++)
                    {
                        summon(8+i%2,i%5);
                    }
                    summon(10,0);
                    summon(10,0);
                    break;
                case 47:
                    summon(7,0);
                    summon(7,1);
                    summon(7,2);
                    summon(102,2);
                    summon(102,3);
                    break;
                case 48:
                    summon(101,1);
                    summon(101,2);
                    summon(101,3);
                    break;
                case 49:
                    summon(104,0);
                    mempty.get(mempty.size()-1).x=width/2;
                    mempty.get(mempty.size()-1).y=-40*ry/10;
                    break;
                case 50:
                    for(int i=0;i<9;i++)
                    {
                        summon(i%2==1 ? 3:0,i%5);
                    }
                    break;
                case 51:
                    for(int i=0;i<9;i++)
                    {
                        summon(i%2==1 ? 5:1,i%5);
                    }
                    break;
                case 52:
                    for(int i=0;i<9;i++)
                    {
                        summon(i%2==1 ? 6:2,i%5);
                    }
                    break;
                case 53:
                    for(int i=0;i<18;i++)
                    {
                        summon(i%2==1 ? 8:4,i%5);
                    }
                    break;
                case 54:
                    for(int i=0;i<9;i++)
                    {
                        summon(i%2==1 ? 7:1,i%5);
                    }
                    break;
                case 55:
                    for(int i=0;i<9;i++)
                    {
                        summon(i>6 ? 100:3,i%5);
                    }
                    break;
                case 56:
                    summon(1,5);
                    summon(1,4);
                    summon(1,3);
                    summon(102,0);
                    summon(102,1);
                    summon(102,2);
                    break;
                case 57:
                    summon(100,0);
                    summon(101,1);
                    summon(102,2);
                    summon(100,3);
                    summon(7,4);
                    summon(7,4);
                    summon(7,4);
                    break;
                case 58:
                    for(int i=0;i<9;i++)
                    {
                        summon(11,i%5);
                    }
                    break;
                case 59:
                    summon(105,0);
                    mempty.get(mempty.size()-1).x=width/2;
                    mempty.get(mempty.size()-1).y=-40*ry/10;
                    break;
                default:
                    gauge+=ran.nextInt(31)+2*bonus;
                    if(level%10==9){
                        summon(ran.nextInt(3)+103,0);
                        mempty.get(mempty.size()-1).x=width/2;
                        mempty.get(mempty.size()-1).y=-40*ry/10;
                    }else{
                        int num=10+level%10;
                        for(int i=0;i<num;i++)
                        {
                            summon(ran.nextInt(12),i%5);
                        }
                    }
                    break;
            }
            for(int i=memshot.size()-1;i>-1;i--)
            {
                memshot.remove(i);
            }
            level++;
        }
    }//playstage/
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(!MainActivity.gameplay)
        {
            MainActivity.GameResume();
        }
        else
        {
            mThread.start();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
    class GameThread extends Thread
    {
        public GameThread(SurfaceHolder holder,Context context) {
            // TODO Auto-generated constructor stub
            mHolder=holder;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Canvas canvas=null;
            while (canrun)
            {
                canvas=mHolder.lockCanvas();
                try
                {
                    synchronized (mHolder) {
                        MoveRemove();
                        Playstage();
                        try
                        {
                            DrawAll(canvas);
                        }
                        catch(NullPointerException e)
                        {
                            MainActivity.gameplay=false;
                        }
                        if(MyGameView.point>StartGame.point2 && point%10<1)
                        {
                            StartGame.point2=MyGameView.point;
                            SharedPreferences.Editor edit=StartGame.pref.edit();//프리퍼런스.에딧
                            edit.putInt("BestPoint", StartGame.point2);//프리퍼런스 저장
                            edit.commit();
                        }
                        count++;
                        try {
                            sleep(17);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }finally
                {
                    if(canvas!=null)
                    {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                synchronized(this)
                {
                    if(!MainActivity.gameplay)
                    {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }
            }//while/
        }//run/
    }//GameThread/
    //ontouch
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_POINTER_UP:
                touchsec=true;
                if(superb>0 && !supershot)
                {
                    skill();
                }
                break;
            default:
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    touchsec=false;
                    x1=(int)event.getX();x2=0;
                    y1=(int)event.getY();y2=0;
                }
                if(event.getAction()==MotionEvent.ACTION_MOVE)
                {
                    x2=(int)event.getX();
                    y2=(int)event.getY();
                    if(touchsec)
                    {
                        x1=x2;y1=y2;touchsec=false;
                    }
                    mhero.x+=x2-x1;mhero.y+=y2-y1;
                    x1=x2;y1=y2;
                }
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    int x=(int)event.getX();
                    int y=(int)event.getY();
                    if(continues)
                    {
                        if(y>height/2+26 && y<height/2+83)
                        {
                            if(x>width/2-210 && x < width/2-71)
                            {
                                if(point%10<9)
                                {
                                    point++;
                                }
                                continues=false;
                                MainActivity.gameplay=true;
                                heart+=3;superb+=2;
                                herocomeback();
                                MainActivity.GameResume();
                            }
                            else if(x>width/2+116 && x < width/2+212)
                            {
                                continues=false;
                                System.exit(0);
                            }
                        }
                    }
                }
                break ;
        }
        return true;
    }//ontouchevent/
    private void skill()
    {
        mhero.delay=0;
        supershot=true;
        switch (select) {
            case 2:
                unbeatable+=20;
                break;
            default:
                unbeatable+=75;
                break;
        }
        soundplay(8);
    }
    private void soundplay(int i)
    {
        if(StartGame.soundon && audioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)
        {
            switch (i) {
                case 0://click
                    StartGame.mclick.start();
                    break;
                case 1://deadem
                    StartGame.mdeadem.start();
                    break;
                case 2:
                    StartGame.mdeadmy.start();
                    break;
                case 3:
                    StartGame.mdeadboss.start();
                    break;
                case 4:
                    StartGame.mfanfare.start();
                    break;
                case 5:
                    StartGame.mbossentry.start();
                    break;
                case 6:
                    StartGame.mlose.start();
                    break;
                case 8:
                    StartGame.mskill.start();
                    break;
                default:
                    break;
            }
        }
    }
}//surfaceView/

