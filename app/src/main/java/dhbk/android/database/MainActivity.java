package dhbk.android.database;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // : 6/3/2016 2 scale background image
        scaleBackgroundImage();

        setUpView();
    }

    private void scaleBackgroundImage() {
        //  3 code lấy kích thước màn hình - nhưng ko dùng nữa
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics); // metrics.widthPixels
        ImageView backgroundImageView = (ImageView) findViewById(R.id.image_login_bg);
        BitmapWorkerTask task = new BitmapWorkerTask(backgroundImageView, getApplicationContext(), 500, 500);
        task.execute(R.drawable.bg_apple);
    }

    private void setUpView() {
        Button registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

}
