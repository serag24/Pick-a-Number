package com.example.android.pickanumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    int lowestNumber, highestNumber;
    String parity = "both";
    int computerGuess, guessTracker = 0;
    boolean gameStarted = false, success = false;

    TextView pickNumberTextView, isYourNumberTextView, lowestNumberTextView, highestNumberTextView;

    Button letsGoButton, tooLowButton, bingoButton, tooHighButton;

    final Random myRandom = new Random();
    HashMap<Integer, Integer> soundPoolMap;
    int sound1_id = 1, sound2_id = 2, sound3_id = 3, sound4_id = 4;
    boolean almostThereSoundPlayed = false;
    SoundPool soundPool;

    //private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    // final int sound1 = soundPool.load(this, R.raw.easy_sound_effect, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickNumberTextView = (TextView) findViewById(R.id.pick_number_text_view);
        lowestNumberTextView = (TextView) findViewById(R.id.lowest_number_text_view);
        highestNumberTextView = (TextView) findViewById(R.id.highest_number_text_view);
        isYourNumberTextView = (TextView) findViewById(R.id.is_your_number_text_view);
        letsGoButton = (Button) findViewById(R.id.lets_go);
        tooLowButton = (Button) findViewById(R.id.too_low_button);
        tooHighButton = (Button) findViewById(R.id.too_high_button);
        bingoButton = (Button) findViewById(R.id.bingo_button);
        Toast pressLetsGoMessage = Toast.makeText(MainActivity.this, "Press LET'S GO button to begin.", Toast.LENGTH_SHORT);
        Toast errorMessage2 = Toast.makeText(MainActivity.this, "The given parameters are wrongly placed.", Toast.LENGTH_SHORT);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(sound1_id, soundPool.load(this, R.raw.lets_guess_sound, 1));
        soundPoolMap.put(sound2_id, soundPool.load(this, R.raw.sorry_sound, 1));
        soundPoolMap.put(sound3_id, soundPool.load(this, R.raw.almost_there_sound, 1));
        soundPoolMap.put(sound4_id, soundPool.load(this, R.raw.easy_sound_effect, 1));
        //TODO Add checkBox preference that won't play any sound
        setUpSharedPreferences();

        letsGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guessTracker = 0;
                almostThereSoundPlayed = false;

                lowestNumber = Integer.parseInt(lowestNumberTextView.getText().toString());
                highestNumber = Integer.parseInt(highestNumberTextView.getText().toString());

                if (lowestNumber >= highestNumber)
                    showErrorToastMessage(errorMessage2);
                else {
                    gameStarted = true;
                    success = false;
                    soundPool.autoPause();
                    soundPool.play(sound1_id, 1, 1, 1, 0, 1);
                    calculateDisplayComputerGuess();

                }


            }
        });

        tooLowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameStarted || success)
                    showErrorToastMessage(pressLetsGoMessage);
                else {
                    lowestNumber = computerGuess + 1;
                    if (lowestNumber > highestNumber) showErrorDialog();
                    else {
                        calculateDisplayComputerGuess();
                    }

                }
            }
        });

        tooHighButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameStarted || success)
                    showErrorToastMessage(pressLetsGoMessage);
                else {
                    highestNumber = computerGuess - 1;
                    if (highestNumber < lowestNumber) showErrorDialog();
                    else {
                        calculateDisplayComputerGuess();
                    }

                }
            }
        });

        bingoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameStarted) {
                    success = true;
                    gameStarted = false;
                    guessTracker = 0;
                    almostThereSoundPlayed = true;
                    isYourNumberTextView.setText("I guessed your number!");
                    soundPool.autoPause();
                    soundPool.play(sound4_id, 1, 1, 1, 0, 1);
                } else
                    showErrorToastMessage(pressLetsGoMessage);
            }
        });

    }

    private void calculateDisplayComputerGuess() {
        guessTracker++;

        if (parity.equals("both")) {
            computerGuess = myRandom.nextInt(highestNumber - lowestNumber + 1) + lowestNumber;
            isYourNumberTextView.setText("Is your number " + computerGuess + " ?");
        } else if (parity.equals("even")) {
            if (highestNumber == lowestNumber) {
                showErrorDialog();
            } else {
                do {
                    computerGuess = myRandom.nextInt(highestNumber - lowestNumber + 1) + lowestNumber;
                } while ((computerGuess % 2 == 1));
                isYourNumberTextView.setText("Is your number " + computerGuess + " ?");
            }
        } else if (parity.equals("odd")) {
            if (highestNumber == lowestNumber) {
                showErrorDialog();
            } else {
                do {
                    computerGuess = myRandom.nextInt(highestNumber - lowestNumber + 1) + lowestNumber;
                } while ((computerGuess % 2 == 0));
                isYourNumberTextView.setText("Is your number " + computerGuess + " ?");
            }
        }

        if (guessTracker == 8) {
            soundPool.autoPause();
            soundPool.play(sound2_id, 1, 1, 1, 0, 1);
        }

        if ((highestNumber - lowestNumber < 10) && !almostThereSoundPlayed) {
            soundPool.autoPause();
            soundPool.play(sound3_id, 1, 1, 1, 0, 1);
            almostThereSoundPlayed = true;
        }
    }

    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadLowNumberFromPreferences(sharedPreferences);
        loadHighNumberFromPreferences(sharedPreferences);
        loadParityFromPreferences(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadLowNumberFromPreferences(SharedPreferences sharedPreferences) {
        lowestNumberTextView.setText(sharedPreferences.getString(getString(R.string.low_key),
                getString(R.string.low_default)));
    }

    private void loadHighNumberFromPreferences(SharedPreferences sharedPreferences) {
        highestNumberTextView.setText(sharedPreferences.getString(getString(R.string.high_key),
                getString(R.string.high_default)));
    }

    private void loadParityFromPreferences(SharedPreferences sharedPreferences) {
        parity = sharedPreferences.getString(getString(R.string.parity_list_key), getString(R.string.even_odd_value));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.low_key))) {
            loadLowNumberFromPreferences(sharedPreferences);
        } else if (key.equals(getString(R.string.high_key))) {
            loadHighNumberFromPreferences(sharedPreferences);
        } else if (key.equals(getString(R.string.parity_list_key))) {
            loadParityFromPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.autoPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void showErrorDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You must have made an error along the way. Click the LET'S GO button again for another round.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showErrorToastMessage(Toast message) {
        if (message != null) {
            message.cancel();
            message.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Getting the id of the item
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
//de adaugat la final cand se gaseste numarul un mic audio cu eaaasy