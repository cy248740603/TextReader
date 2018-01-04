package Data;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CY on 2018/1/3 0003.
 */

public class SetText implements Runnable{

    private Activity activity;
    private TextView mText;
    private int maxLineCountNum;
    private int maxLineTextNum;
    private int page = 0;
    private int nowPage = 0;

    private List<String> txtBuffer = new ArrayList<>();

    public SetText(Activity a,TextView tv ){
        activity = a;
        mText = tv;
    }
    @Override
    public void run() {
        getWindowsLineCounts();
        loadTxt();
    }
    private void getWindowsLineCounts(){
        DisplayMetrics wm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(wm);
        maxLineCountNum = wm.heightPixels/mText.getLineHeight();
        maxLineTextNum = (int)((float)wm.widthPixels/mText.getPaint().getTextSize());
        Log.w("getWindowsLineCounts","count:"+ maxLineCountNum + "text:" + maxLineTextNum);
    }

    private void loadTxt(){

        String txtFilePath = Environment.getExternalStorageDirectory() + "/TextReader/《残袍》.txt";
        int index = txtFilePath.lastIndexOf(File.separator);
        String name = txtFilePath.substring(index + 1,txtFilePath.length());
        activity.setTitle(name);
        try{
            FileInputStream fr = new FileInputStream(txtFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fr,"GB2312"));
            String line;//记录每一行数据
            int num = 0;
            StringBuffer content = new StringBuffer() ;
            while((line = br.readLine()) != null){//如果还有下一行数据
                if (line.length() == 0)
                    content.append("\t\t\t\t");
                else {
                    int tmp = (line.length() + 1)/ maxLineTextNum + num + 1;
                    if (tmp > maxLineCountNum){
                        int addLength = (maxLineCountNum - num) * maxLineTextNum;
                        content.append(line.substring(0,addLength - 1));
                        txtBuffer.add(content.toString());
                        content.setLength(0);//清空buffer
                        mHandler.sendEmptyMessage(page++);
                        content.append(line.substring((addLength),line.length()) + "\n");
                        num = tmp - maxLineCountNum;
                    }else if (tmp == maxLineCountNum){
                        content.append(line + "\n");
                        txtBuffer.add(content.toString());
                        content.setLength(0);//清空buffer
                        mHandler.sendEmptyMessage(page++);
                        num = 0;
                    }else {
                        content.append(line + "\n");
                        num = tmp;
                    }
                }
            }
            br.close();//关闭文件输出流
            fr.close();//关闭缓冲区
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                nowPage = 0;
                setPage();
            }else {

            }
        }
    };

    private void setPage(){
        mText.setText(txtBuffer.get(nowPage));
    }
}
