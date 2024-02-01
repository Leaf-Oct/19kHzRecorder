package cn.leaf.record;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_actvity);
        var return_button=findViewById(R.id.back);
        return_button.setOnClickListener(v ->{
            finish();
        });
    }
}