package ru.ilyasmirnov.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    // Для записи в intent подсказки
    private static final String EXTRA_ANSWER = "ru.ilyasmirnov.android.quizquizapi16.answer_is_true";
    // Для записи в intent была ли показана подсказка
    private static final String EXTRA_IS_ANSWER_SHOWN = "ru.ilyasmirnov.android.quizquizapi16.answer_shown";
    // Для записи в bundle была ли показана подсказка
    private static final String KEY_IS_ANSWER_SHOWN = "is_answer_shown";

    private int mAnswer;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private Button mHelpActivityFinishButton;

    // Была ли использована подсказка
    private boolean mIsAnswerShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mAnswer = getIntent().getIntExtra(EXTRA_ANSWER, 0);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerTextView.setText(getResources().getText(mAnswer));
                mIsAnswerShown = true;
                setAnswerShownResult(mIsAnswerShown);

                // Анимация исчезновения кнопки вызова подсказки
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        mHelpActivityFinishButton = (Button) findViewById(R.id.help_activity_finish_button);
        mHelpActivityFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpActivity.this.finish();
            }
        });

        if (savedInstanceState != null) {
            mIsAnswerShown = savedInstanceState.getBoolean(KEY_IS_ANSWER_SHOWN);
            if (mIsAnswerShown) {
                mAnswerTextView.setText(getResources().getText(mAnswer));
                setAnswerShownResult(mIsAnswerShown);
                mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_IS_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_IS_ANSWER_SHOWN, false);
    }

    public static Intent newHelpIntent(Context packageContext, int answer) {
        Intent intent = new Intent(packageContext, HelpActivity.class);
        intent.putExtra(EXTRA_ANSWER, answer);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_IS_ANSWER_SHOWN, mIsAnswerShown);
    }
}
