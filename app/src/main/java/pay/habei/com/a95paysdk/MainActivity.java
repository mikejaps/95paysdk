package pay.habei.com.a95paysdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.channel.ef.PayManager;
import com.gandalf.daemon.utils.XL_log;

import k.m.PayListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.btn_start);
        final PayManager pay = PayManager.getInstance(this);
        pay.init("10003", "1");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pay != null) {

                    pay.pay("1001", 10, new PayListener() {
                        @Override
                        public void onPaySuccess() {
                            XL_log log = new XL_log(MainActivity.class);
                            log.debug("onPaySuccess");
                        }

                        @Override
                        public void onPayFailed(int errorCode, String errorMsg) {
                            XL_log log = new XL_log(MainActivity.class);
                            log.debug("onPayFailed code:" + errorCode + " msg:" + errorMsg);
                        }
                    });
                }
            }
        });
    }
}
