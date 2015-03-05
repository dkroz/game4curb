package com.dkroz.game;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String ACTION_SEND_MOVE = "com.dkroz.game.send_move";
    public static final String BROADCAST_GET_RESULT = "com.dkroz.game.get_result";

    private Context context;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private int humanChoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending your move to server...");
        dialog = new Dialog(context);

        findViewById(R.id.btn_rock).setOnClickListener(this);
        findViewById(R.id.btn_paper).setOnClickListener(this);
        findViewById(R.id.btn_scissors).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        progressDialog.show();
        switch (v.getId()) {
            case R.id.btn_rock:
                MyService.sendMove(context, 0);
                humanChoice = 0;
                break;
            case R.id.btn_paper:
                MyService.sendMove(context, 1);
                humanChoice = 1;
                break;
            case R.id.btn_scissors:
                MyService.sendMove(context, 2);
                humanChoice = 2;
                break;
        }
    }

    private BroadcastReceiver showResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();
            if (intent!=null && intent.hasExtra("result")) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Calendar calendar = Calendar.getInstance();
                String resultToSave = sdf.format(calendar.getTime()) + "|";

                dialog.setContentView(R.layout.dialog_result);
                dialog.setTitle("Result");

                TextView textResult = (TextView) dialog.findViewById(R.id.dialog_result);
                textResult.setText(intent.getStringExtra("result"));
                resultToSave = resultToSave + intent.getStringExtra("result")  + "|";

                ImageView iconYou = (ImageView) dialog.findViewById(R.id.dialog_icon_you);
                switch (humanChoice) {
                    case 0:
                        iconYou.setImageResource(R.drawable.ic_rock);
                        resultToSave = resultToSave + "You: ROCK|Computer: ";
                        break;
                    case 1:
                        iconYou.setImageResource(R.drawable.ic_paper);
                        resultToSave = resultToSave + "You: PAPER|Computer: ";
                        break;
                    case 2:
                        iconYou.setImageResource(R.drawable.ic_scissors);
                        resultToSave = resultToSave + "You: SCISSORS|Computer: ";
                        break;
                }

                ImageView iconComp = (ImageView) dialog.findViewById(R.id.dialog_icon_comp);
                String computerChoice = intent.getStringExtra("computer");
                if (computerChoice.equalsIgnoreCase("rock")) {
                    iconComp.setImageResource(R.drawable.ic_rock);
                    resultToSave = resultToSave + "ROCK||";
                } else if (computerChoice.equalsIgnoreCase("paper")) {
                    iconComp.setImageResource(R.drawable.ic_paper);
                    resultToSave = resultToSave + "PAPPER||";
                } else {
                    iconComp.setImageResource(R.drawable.ic_scissors);
                    resultToSave = resultToSave + "SCISSORS||";
                }

                dialog.findViewById(R.id.dialog_button_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                });

                dialog.show();

                GameHistory.saveResult(context, resultToSave);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                showResult, new IntentFilter(BROADCAST_GET_RESULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history_load) {

            String history = GameHistory.loadHistory(context);

            if (history.length()==0) {

                Toast.makeText(context, "History is empty!", Toast.LENGTH_SHORT).show();

            } else {
                dialog.setContentView(R.layout.dialog_history);
                dialog.setTitle("History");

                TextView textHistory = (TextView) dialog.findViewById(R.id.dialog_history);
                textHistory.setText(history);

                dialog.findViewById(R.id.dialog_button_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                dialog.show();
            }
            return true;

        } else if (id == R.id.action_history_clear) {

            if (GameHistory.clearHistory(context)) {
                Toast.makeText(context, "History is clear!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "History was not cleared!", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
