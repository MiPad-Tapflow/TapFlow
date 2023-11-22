package cn.ljlVink.Tapflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import es.dmoral.toasty.Toasty;

public class PremainActivity extends AppCompatActivity {
    static {
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish(); // 结束当前 Activity
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean result=Shell.isAppGrantedRoot();

        if(result==null||result==Boolean.TRUE){
            Shell.getShell(shell -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }else{
            Toasty.error(this, "请授予app root权限.即将退出程序.", Toast.LENGTH_SHORT, true).show();
            handler.sendEmptyMessageDelayed(0, 3000); // 设置延迟退出
        }

    }
}