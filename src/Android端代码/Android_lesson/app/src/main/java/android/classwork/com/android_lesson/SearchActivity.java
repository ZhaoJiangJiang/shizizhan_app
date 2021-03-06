package android.classwork.com.android_lesson;

import android.classwork.com.android_lesson.util.HttpUtil;
import android.classwork.com.android_lesson.util.vocb_dialog.wordClick;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 赵江江 on 2018/11/20.
 */

public class SearchActivity extends AppCompatActivity{

    private final static String WEB_SEARCH_RES = HttpUtil.IP+":8080/android_lesson_work/bcz_vocb_get_word_by_English.jsp";
    private final static String WEB_IMG_ADDRESS = HttpUtil.IP+":8080/android_lesson_work/Images/";

    private EditText inputVoc;
    private TextView searchGo;

    private String voc;

    private RequestBody body;

    String English;
    String Chinese;
    String[] ChineseInfos;
    String vocbRoot;
    String sentence;
    String sentenceChinese;
    String Need;

    private TextView tv_Nothing;
    private TextView tv_English;
    private TextView tv_Chinese;
    private TextView tv_vocbRoot;
    private TextView tv_sentence;
    private TextView tv_sentenceChinese;
    private TextView tv_Need;
    private ImageView img_vocb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // 隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        }

        inputVoc = (EditText) findViewById(R.id.search_input_vocb);
        searchGo = (TextView) findViewById(R.id.search_go);

        tv_Nothing = (TextView) findViewById(R.id.search_Nothing);
        tv_English = (TextView) findViewById(R.id.search_English);
        tv_Chinese = (TextView) findViewById(R.id.search_Chinese);
        tv_vocbRoot = (TextView) findViewById(R.id.search_vocbRoot);
        tv_sentence = (TextView) findViewById(R.id.search_sentence);
        tv_sentenceChinese = (TextView) findViewById(R.id.search_sentenceChinese);
        tv_Need = (TextView) findViewById(R.id.search_Need);
        img_vocb = (ImageView) findViewById(R.id.search_vocbImg);

        searchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voc = inputVoc.getText().toString();
                body = new FormBody.Builder()
                        .add("English", voc)
                        .build();
                searchRes();
            }
        });
    }

    private void searchRes(){
        HttpUtil.sendOkHttpRequestWithRequestBody(WEB_SEARCH_RES, body, new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parseDataWithJson(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    /**
     * 解析JSON数据
     */
    private void parseDataWithJson(String jsonData){
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            if (jsonArray.length() == 0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_Nothing.setText("暂未收录该单词");
                        tv_English.setText("");
                        tv_Chinese.setText("");
                        tv_vocbRoot.setText("");
                        tv_sentence.setText("");
                        tv_sentenceChinese.setText("");
                        tv_Need.setText("");
                        img_vocb.setVisibility(View.INVISIBLE); // 隐藏图片
                    }
                });
                return;
            }
            for (int i = 0, len = jsonArray.length(); i < len; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                English = jsonObject.getString("English");
                ChineseInfos = jsonObject.getString("Chinese").split("；");
                Chinese = "";
                for (int index=0, _len=ChineseInfos.length; index<_len; index++){
                    Chinese = Chinese+ChineseInfos[index]+"\n";
                }
                vocbRoot = jsonObject.getString("vocbroot");
                sentence = jsonObject.getString("sentence");
                sentenceChinese = jsonObject.getString("sentenceChinese");
                Need = jsonObject.getString("need");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRes();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showRes(){
        tv_Nothing.setText("");
        tv_English.setText(English);
        tv_Chinese.setText(Chinese);
        tv_vocbRoot.setText(vocbRoot);
        tv_sentence.setText(sentence);
        tv_sentence.setText(sentence, TextView.BufferType.SPANNABLE);
        wordClick.getEachWord(tv_sentence);
        tv_sentence.setMovementMethod(LinkMovementMethod.getInstance());
        tv_sentenceChinese.setText(sentenceChinese);
        tv_Need.setText(Need);
        getImgAddress();
    }

    private void getImgAddress(){
        HttpUtil.sendOkHttpRequestWithOutRequestBody(WEB_IMG_ADDRESS+English+".jpg", new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 通过message，拿到字节数组
                final byte[] Image = (byte[]) response.body().bytes();
                // 使用BitmaFactory工厂，把字节数组转换为bitmap
                final Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_vocb.setVisibility(View.VISIBLE);
                        img_vocb.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}
