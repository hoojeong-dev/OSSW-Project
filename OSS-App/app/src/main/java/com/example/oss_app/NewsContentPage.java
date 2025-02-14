package com.example.oss_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.List;

public class NewsContentPage extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    static int modelPosition;
    String category, key, title, content = null, keyword, summary, sttmsg = "null";
    static String[] newsContents;
    static int currentCount, count, contentcount, pageValue;
    TextView titleView, keywordView, summaryView;
    LayoutInflater setLayoutInflater, playLayoutInflater, sttLayoutInflater;
    LinearLayout settingLayout, playLayout, sttLayout;
    LinearLayout.LayoutParams setLayoutParams, playLayoutParams, sttLayoutParams;

    SoundPlay soundPlay = new SoundPlay();
    HttpConnection httpConnection = new HttpConnection();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.pagenum=2;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_page);
        MainActivity.viewPoint = findViewById(R.id.view_point);

        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        modelPosition = intent.getExtras().getInt("position");

        pageValue = 1;
        MainActivity.magnifier = false;

        title = models.get(modelPosition).getTitle();
        category = models.get(modelPosition).getCategory();
        key = models.get(modelPosition).getKey();
        content = models.get(modelPosition).getContent();
        keyword = models.get(modelPosition).getKeywords();
        summary = models.get(modelPosition).getSummary();

        setNewContents();

        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
        contentcount = 0;
        System.out.println("contentcount : " + contentcount);

        if(SpeechToText.resultType){
            sttmsg = SpeechToText.sttResult;
            loadTTS();
        }
    }

    // 뷰에 띄울 텍스트 설정
    public void setNewContents() {

        String[] sample = new String[1000];
        int maxLength = 208;
        int textLen = content.length();
        int loopCnt = textLen / maxLength + 1;
        String result = "";
        count = 1;
        currentCount = -2;

        for (int i = 0; i < loopCnt; i++) {
            int lastIndex = (i + 1) * maxLength;

            if (textLen > lastIndex) {
                result = content.substring(i * maxLength, lastIndex);
                System.out.println(result);
                sample[count] = result;
                count++;
            } else {
                result = content.substring(i * maxLength);
                sample[count] = result;
                count++;
            }
        }

        System.out.println("count : " + count);
        newsContents = new String[count];

        for(int i=1; i<count; i++){
            newsContents[i] = sample[i];
        }
    }

    // 이전 페이지로
    public void previous(View v){
        System.out.println(contentcount + "---------------" + count);
        contentcount--;

        if(contentcount > 0 && contentcount <= count - 1){
            setContentView(R.layout.activity_content_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);

            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);

            resetTextView();
            setContents(contentcount);

            pageValue = 1;
        }
        else if(contentcount == 0){
            setContentView(R.layout.activity_title_page);
            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);

            MainActivity.viewPoint = findViewById(R.id.view_point);
            titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);
            pageValue = 1;
        }
        else if(contentcount < 0){
            pageValue = 1;
            Toast.makeText(getApplicationContext(), "First Page", Toast.LENGTH_LONG).show();
        }
    }

    // 다음 페이지로
    public void next(View v){
        System.out.println(contentcount + "---------------" + count);
        contentcount++;

        if(contentcount > 0 && contentcount <= count - 1){
            setContentView(R.layout.activity_content_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);
            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);

            setContents(contentcount);

            pageValue = 1;
        }
        else if(contentcount == count){
            setContentView(R.layout.activity_summary_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);
            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);
            keywordView = (TextView) findViewById(R.id.keywords);
            keywordView.setText(keyword);
            summaryView = (TextView) findViewById(R.id.summary);
            summaryView.setText(summary);
            pageValue = 2;
        }
        else if(contentcount > count){
            pageValue = 2;
            Toast.makeText(getApplicationContext(), "Last Page", Toast.LENGTH_LONG).show();
        }
    }

    public void magnifierView(View v){
        LinearLayout magnifier_back = findViewById(R.id.magnifier_back);
        ImageButton magnifier = findViewById(R.id.magnifier);

        if(MainActivity.magnifier){
            MainActivity.magnifier = false;
            magnifier_back.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle));
            magnifier.setBackground(ContextCompat.getDrawable(this, R.drawable.black_magnifier));
        } else{
            MainActivity.magnifier = true;
            magnifier_back.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle_dark));
            magnifier.setBackground(ContextCompat.getDrawable(this, R.drawable.white_magnifier));
        }
    }

    public void clickText(View v){
        switch (v.getId()){
            case R.id.text1:
            case R.id.text2:
            case R.id.text3:
            case R.id.text4:
            case R.id.text5:
            case R.id.text6:
            case R.id.text7:
            case R.id.text8:
            case R.id.text9:
            case R.id.text10:
            case R.id.text11:
            case R.id.text12:
            case R.id.text13:
            case R.id.text14:
            case R.id.text15:
            case R.id.text16:
                if(!MainActivity.magnifier){
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable()  {
                        public void run() {
                            TextView text = findViewById(v.getId());
                            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
                            text.setPaintFlags(text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    //Toast.makeText(getApplicationContext(), "Change", Toast.LENGTH_LONG).show();
                                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                                    text.setPaintFlags(text.getPaintFlags() & (~Paint.FAKE_BOLD_TEXT_FLAG));
                                }
                            }, 3000);
                        }
                    }, 1000);
                }
        }
    }

    public void setting(View v) {
        setLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingLayout = (LinearLayout) setLayoutInflater.inflate(R.layout.activity_setting_view, null);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        settingLayout.setBackgroundColor(Color.parseColor("#99000000"));
        setLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(settingLayout, setLayoutParams);

        ImageButton mode = (ImageButton) findViewById(R.id.mode);
        if(MainActivity.mode == 0) {
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye_2));
        }
        else if(MainActivity.mode == 1) {
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye));
        }

        if(category.equals("My")) {
            LinearLayout removeBack = findViewById(R.id.removeBack);
            removeBack.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle));
            ImageButton remove = findViewById(R.id.remove);
            remove.setVisibility(View.VISIBLE);
        }

        LinearLayout magnifier_back = findViewById(R.id.magnifier_back);
        ImageButton magnifier = findViewById(R.id.magnifier);

        if(!MainActivity.magnifier){
            magnifier_back.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle));
            magnifier.setBackground(ContextCompat.getDrawable(this, R.drawable.black_magnifier));
        } else{
            magnifier_back.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle_dark));
            magnifier.setBackground(ContextCompat.getDrawable(this, R.drawable.white_magnifier));
        }
    }

    public void inVisibleSetting(View v) {
        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
        ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
    }

    //시선 or 터치 함수
    public void controlMode(View v){
        ImageButton mode = (ImageButton) findViewById(R.id.mode);
        if(MainActivity.mode == 0) {
            MainActivity.mode = 1;
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye));
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
        }
        else if(MainActivity.mode == 1) {
            MainActivity.mode = 0;
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye_2));
            MainActivity.viewPoint.setVisibility(View.GONE);
        }
    }

    public void pageView(View v){
        ImageButton pageView = findViewById(R.id.pageView);

        Intent intent = new Intent(v.getContext(), NewsContentScroll.class);
        intent.putExtra("position", modelPosition);
        v.getContext().startActivity(intent);

        MainActivity.pageType = 1;
        pageView.setBackground(ContextCompat.getDrawable(this, R.drawable.black_scroll));
    }

    public void tts(View v){
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (category.equals("My")) {
            String contentUrl = MainActivity.ipAddress + "/tts/" + content;
            soundPlay.setPlayUrl(contentUrl);
            ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
            addContentView(playLayout, playLayoutParams);
        } else {
            String msg = "", path = "category/" + category + "/" + key;

            if (pageValue == 1)
                msg = "읽어";
            else if (pageValue == 2)
                msg = "요약";

            System.out.println("=================" + msg);

            String soundUrl = httpConnection.sendTTS(msg, path);

            if (soundUrl.equals("failure")) {
                Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
                soundPlay.closePlayer();
            } else {
                soundPlay.setPlayUrl(soundUrl);
                ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
                addContentView(playLayout, playLayoutParams);
            }
        }
    }

    public void loadTTS(){
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        String msg = "", path = "category/" + category + "/" + key;

        if(SpeechToText.resultType && sttmsg.equals("읽어"))
            msg = "읽어";
        else if(SpeechToText.resultType && sttmsg.equals("요약"))
            msg = "요약";

        String soundUrl = httpConnection.sendTTS(msg, path);

        if (soundUrl.equals("failure")) {
            Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
            soundPlay.closePlayer();
        } else {
            soundPlay.setPlayUrl(soundUrl);
            addContentView(playLayout, playLayoutParams);

            sttmsg = "null";
            SpeechToText.resultType = false;
        }
    }

    public void playbtn(View v) {
        soundPlay.playAudio();
    }

    public void pausebtn(View v) {
        soundPlay.pauseAudio();
    }

    public void restartbtn(View v) {
        soundPlay.resumeAudio();
    }

    public void stopbtn(View v) {
        soundPlay.stopAudio();
    }

    public void inVisiblePlay(View v) {
        ((ViewManager) playLayout.getParent()).removeView(playLayout);
        soundPlay.closePlayer();

        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
    }

    public void stt(View v){
        Intent intent = new Intent(this, SpeechToText.class);
        intent.putExtra("pageName", "NewsContentPage");
        intent.putExtra("position", modelPosition);
        startActivity(intent);
    }

    public void pageback(View v){
        Intent intent = new Intent(this, NewsList.class);
        intent.putExtra("value", category);
        startActivity(intent);
    }

    public void removeContents(View v){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

        oDialog.setMessage("해당 글을 삭제하시겠습니까?")
                .setTitle("eye-world")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        System.out.println("취소");
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        NewsList.removeContents = title;
                        Toast.makeText(getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }

    public void setContents(int contentcount){
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        TextView text3 = findViewById(R.id.text3);
        TextView text4 = findViewById(R.id.text4);
        TextView text5 = findViewById(R.id.text5);
        TextView text6 = findViewById(R.id.text6);
        TextView text7 = findViewById(R.id.text7);
        TextView text8 = findViewById(R.id.text8);
        TextView text9 = findViewById(R.id.text9);
        TextView text10 = findViewById(R.id.text10);
        TextView text11 = findViewById(R.id.text11);
        TextView text12 = findViewById(R.id.text12);
        TextView text13 = findViewById(R.id.text13);
        TextView text14 = findViewById(R.id.text14);
        TextView text15 = findViewById(R.id.text15);
        TextView text16 = findViewById(R.id.text16);

        String[] sample = new String[1000];
        int maxLength = 13;
        int textLen = newsContents[contentcount].length();
        int loopCnt = textLen / maxLength + 1;
        String result = "";
        int countSet = 1;

        for (int i = 0; i < loopCnt; i++) {
            int lastIndex = (i + 1) * maxLength;

            if (textLen > lastIndex) {
                result = newsContents[contentcount].substring(i * maxLength, lastIndex);
                System.out.println(result);
                sample[countSet] = result;
                countSet++;
            } else {
                result = newsContents[contentcount].substring(i * maxLength);
                sample[countSet] = result;
                countSet++;
            }
        }

        System.out.println(sample.length);
        for(int i=0; i<sample.length; i++){
            System.out.println(i + " : " + sample[i]);
        }
        text1.setText(sample[1]);
        text2.setText(sample[2]);
        text3.setText(sample[3]);
        text4.setText(sample[4]);
        text5.setText(sample[5]);
        text6.setText(sample[6]);
        text7.setText(sample[7]);
        text8.setText(sample[8]);
        text9.setText(sample[9]);
        text10.setText(sample[10]);
        text11.setText(sample[11]);
        text12.setText(sample[12]);
        text13.setText(sample[13]);
        text14.setText(sample[14]);
        text15.setText(sample[15]);
        text16.setText(sample[16]);
    }

    public void resetTextView(){
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        TextView text3 = findViewById(R.id.text3);
        TextView text4 = findViewById(R.id.text4);
        TextView text5 = findViewById(R.id.text5);
        TextView text6 = findViewById(R.id.text6);
        TextView text7 = findViewById(R.id.text7);
        TextView text8 = findViewById(R.id.text8);
        TextView text9 = findViewById(R.id.text9);
        TextView text10 = findViewById(R.id.text10);
        TextView text11 = findViewById(R.id.text11);
        TextView text12 = findViewById(R.id.text12);
        TextView text13 = findViewById(R.id.text13);
        TextView text14 = findViewById(R.id.text14);
        TextView text15 = findViewById(R.id.text15);
        TextView text16 = findViewById(R.id.text16);

        text1.setText("");
        text2.setText("");
        text3.setText("");
        text4.setText("");
        text5.setText("");
        text6.setText("");
        text7.setText("");
        text8.setText("");
        text9.setText("");
        text10.setText("");
        text11.setText("");
        text12.setText("");
        text13.setText("");
        text14.setText("");
        text15.setText("");
        text16.setText("");
    }
}