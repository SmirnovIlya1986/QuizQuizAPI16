package ru.ilyasmirnov.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_INDEX = "index";
    private static final String KEY_INDEX_BANK = "index_bank";

    private static final String KEY_ANSWER_VAR1_BUTTON_IS_ENABLED = "answer_var1_button_is_enabled";
    private static final String KEY_ANSWER_VAR2_BUTTON_IS_ENABLED = "answer_var2_button_is_enabled";

    private static final String KEY_ANSWER_CORRECT = "answer_correct";
    private static final String KEY_ANSWER_CORRECT_PERCENT = "answer_correct_percent";
    private static final String KEY_ANSWER_CORRECT_PERCENT_TO_STRING = "answer_correct_percent_to_string";

    private static final String KEY_HELP_COUNT = "help_count";
    private static final String KEY_IS_HELP_USED = "is_help_used";
    private static final String KEY_HELP_BUTTON_IS_ENABLED = "help_button_is_enabled";

    private static final String KEY_IS_GAME_END = "is_game_end";

    private static final int REQUEST_CODE_HELP_ACTIVITY = 0;

    private Button mAnswerVar1Button;
    private Button mAnswerVar2Button;
    private Button mQuestionNextButton;

    private TextView mQuestionTextView;

    private Question[] mQuestionBank = {
            new Question(R.string.question_0, R.string.question_0_AnswerVar1, R.string.question_0_AnswerVar2, R.string.question_0_AnswerVar1), // 0
            new Question(R.string.question_1, R.string.question_1_AnswerVar1, R.string.question_1_AnswerVar2, R.string.question_1_AnswerVar2), // 1
            new Question(R.string.question_2, R.string.question_2_AnswerVar1, R.string.question_2_AnswerVar2, R.string.question_2_AnswerVar1), // 2
            new Question(R.string.question_3, R.string.question_3_AnswerVar1, R.string.question_3_AnswerVar2, R.string.question_3_AnswerVar2), // 3
            new Question(R.string.question_4, R.string.question_4_AnswerVar1, R.string.question_4_AnswerVar2, R.string.question_4_AnswerVar1), // 4
            new Question(R.string.question_5, R.string.question_5_AnswerVar1, R.string.question_5_AnswerVar2, R.string.question_5_AnswerVar2), // 5
            new Question(R.string.question_6, R.string.question_6_AnswerVar1, R.string.question_6_AnswerVar2, R.string.question_6_AnswerVar2), // 6
            new Question(R.string.question_7, R.string.question_7_AnswerVar1, R.string.question_7_AnswerVar2, R.string.question_7_AnswerVar2),  // 7
            // new Question(R.string.question_test, R.string.question_test_AnswerVar1, R.string.question_test_AnswerVar2, R.string.question_test_AnswerVar2),  // 8

    };

    // Лист для записи очерёдности вопросов.
    private ArrayList<Integer> mIndexBank;
    // Индекс текущего вопроса.
    private int mCurrentIndex;

    // Количество верных ответов.
    private double mAnswerCorrect = 0;
    // Процент верных ответов от общего числа вопросов в викторине.
    private double mAnswerCorrectPercent = 0;
    private String mAnswerCorrectPercentToString = null;

    // Вызов подсказки (HelpActivity).
    private Button mHelpButton;
    // Количество подсказок.
    private int mDefaultHelpCount = 3;
    private int mHelpCount = mDefaultHelpCount;
    // Вывод количества доступных подсказок.
    private TextView mHelpCountTextView;
    // Была ли использована подсказка на текущем вопросе.
    private boolean mIsHelpUsed;

    // Были ли пройдены все вопросы.
    private boolean mIsGameEnd = false;

    // Массив id Toasts, выводящихся при верном ответе.
    private int[] mCorrectToastResIdBank = {R.string.correct_toast,
            R.string.correct_toast3, R.string.correct_toast4, R.string.correct_toast5,
            R.string.correct_toast6, R.string.correct_toast7, R.string.correct_toast8};

    // Массив id Toasts, выводящихся при неверном ответе.
    private int[] mIncorrectToastResIdBank = {R.string.incorrect_toast, R.string.incorrect_toast3,
            R.string.incorrect_toast4, R.string.incorrect_toast5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndexBank = getIndexBank(); // Определение очерёдности вопросов.
        mCurrentIndex = getCurrentIndex(); // Индекс первого вопроса.

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mAnswerVar1Button = (Button) findViewById(R.id.answer_var1_button);
        mAnswerVar1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(mAnswerVar1Button.getText());
                buttonBlock();
            }
        });

        mAnswerVar2Button = (Button) findViewById(R.id.answer_var2_button);
        mAnswerVar2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(mAnswerVar2Button.getText());
                buttonBlock();
            }
        });

        mHelpCountTextView = (TextView) findViewById(R.id.help_count_text_view);

        mHelpButton = (Button) findViewById(R.id.help_button);
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int answer = mQuestionBank[mCurrentIndex].getAnswerResId(); // Ответ
                Intent intent = HelpActivity.newHelpIntent(MainActivity.this, answer);
                startActivityForResult(intent, REQUEST_CODE_HELP_ACTIVITY);
            }
        });

        mQuestionNextButton = (Button) findViewById(R.id.question_next_button);
        mQuestionNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsGameEnd) { // Не все вопросы пройдены.
                    if (mIndexBank.isEmpty()) { // Вопрос на экране последний =>
                                                // => На кнопку mQuestionNextButton устанавливается текст "Завершить"
                                                // (см. updateQuestion()).

                        // Подсчёт процента верных ответов
                        mAnswerCorrectPercent = (mAnswerCorrect * 100) / mQuestionBank.length;
                        mAnswerCorrectPercentToString = String.valueOf(Math.round(mAnswerCorrectPercent)) + getResources().getString(R.string.correct_percent);
                        mIsGameEnd = true;  // После нажатия на mQuestionNextButton ("Завершить") ответы на все вопросы даны  =>
                                            //  => кнопки mAnswerVar1Button, mAnswerVar1Button2, mHelpButton станут INVISIBLE;
                                            // в поле mQuestionTextView выводется текст mAnswerCorrectPercentToString;
                                            // на кнопку mQuestionNextButton устанавливается текст "Ещё раз"
                                            // (см. updateQuestion()).
                        }

                    if (!mIndexBank.isEmpty()) { // Вопрос на экране непоследний.
                        mCurrentIndex = getCurrentIndex();  // Индекс следующего вопроса.
                        buttonUnblock();
                    }
                } else {
                    // Подготовка новой игры.

                    mIndexBank = getIndexBank(); // Определение очерёдности вопросов.
                    mCurrentIndex = getCurrentIndex(); // Индекс первого вопроса.

                    // Сброс пройденной игры.

                    mAnswerCorrect = 0;

                    mHelpCount = mDefaultHelpCount;

                    mAnswerVar1Button.setVisibility(View.VISIBLE);
                    mAnswerVar2Button.setVisibility(View.VISIBLE);
                    mHelpButton.setVisibility(View.VISIBLE);
                    mHelpCountTextView.setVisibility(View.VISIBLE);
                    buttonUnblock();

                    mIsGameEnd = false;
                }
                mIsHelpUsed = false;
                updateQuestion();
            }
        });

        if (savedInstanceState != null) {

            mAnswerVar1Button.setEnabled(savedInstanceState.getBoolean(KEY_ANSWER_VAR1_BUTTON_IS_ENABLED, true));
            mAnswerVar2Button.setEnabled(savedInstanceState.getBoolean(KEY_ANSWER_VAR2_BUTTON_IS_ENABLED, true));
            mHelpButton.setEnabled(savedInstanceState.getBoolean(KEY_HELP_BUTTON_IS_ENABLED, true));

            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIndexBank = savedInstanceState.getIntegerArrayList(KEY_INDEX_BANK);

            mAnswerCorrect = savedInstanceState.getDouble(KEY_ANSWER_CORRECT, 0);
            mAnswerCorrectPercent = savedInstanceState.getDouble(KEY_ANSWER_CORRECT_PERCENT, 0);
            mAnswerCorrectPercentToString = savedInstanceState.getString(KEY_ANSWER_CORRECT_PERCENT_TO_STRING);

            mHelpCount = savedInstanceState.getInt(KEY_HELP_COUNT, mHelpCount);
            mIsHelpUsed = savedInstanceState.getBoolean(KEY_IS_HELP_USED, false);

            mIsGameEnd = savedInstanceState.getBoolean(KEY_IS_GAME_END, false);
        }
        updateQuestion();
    }

    private void updateQuestion() {

        int question;
        int answerVar1;
        int answerVar2;

        question = mQuestionBank[mCurrentIndex].getTextResId();
        answerVar1 = mQuestionBank[mCurrentIndex].getAnswerVar1ResId();
        answerVar2 = mQuestionBank[mCurrentIndex].getAnswerVar2ResId();

        mQuestionTextView.setText(question);
        mAnswerVar1Button.setText(answerVar1);
        mAnswerVar2Button.setText(answerVar2);

        mHelpCountTextView.setText(String.valueOf(mHelpCount));

        if (!mIsGameEnd) { // Не все вопросы пройдены.
            if (!mIndexBank.isEmpty()) { // Вопрос на экране непоследний.
                mQuestionNextButton.setText(R.string.next_button);
            }
            if (mIndexBank.isEmpty()) { // Вопрос на экране последний.
                mQuestionNextButton.setText(R.string.end_button);
            }
        } else { // Игрок находится на экране с процентом правильных ответов.
            mQuestionNextButton.setText(R.string.restart_button);
            mQuestionTextView.setText(mAnswerCorrectPercentToString);

            mAnswerVar1Button.setVisibility(View.INVISIBLE);
            mAnswerVar2Button.setVisibility(View.INVISIBLE);
            mHelpButton.setVisibility(View.INVISIBLE);
            mHelpCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    private int getCurrentIndex() {
            int currentIndex = mIndexBank.get(mIndexBank.size() - 1);
            mIndexBank.remove(mIndexBank.size() - 1);
            return currentIndex;
    }

    // Определение очерёдности вопросов.
    private ArrayList<Integer> getIndexBank() {
        ArrayList<Integer> indexBank = new ArrayList<>();
        for (int i = 0; i <= mQuestionBank.length - 1; i++) {
            indexBank.add(i);
        }
        Collections.shuffle(indexBank);
        return indexBank;
    }

    // Проверка ответа.
    private void checkAnswer(CharSequence userPressedTrue) {

        int answer = mQuestionBank[mCurrentIndex].getAnswerResId();

        String answerToString = getResources().getString(answer);

        int messageResId ;
        int messageResIdIndex;

        if (userPressedTrue.equals(answerToString)) {
            messageResIdIndex = (int) (Math.random() * mCorrectToastResIdBank.length);
            messageResId = mCorrectToastResIdBank[messageResIdIndex];
            mAnswerCorrect++;
        } else {
            messageResIdIndex = (int) (Math.random() * mIncorrectToastResIdBank.length);
            messageResId = mIncorrectToastResIdBank[messageResIdIndex];
        }
        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    private void buttonBlock() {
        mAnswerVar1Button.setEnabled(false);
        mAnswerVar2Button.setEnabled(false);
        mHelpButton.setEnabled(false);
    }

    private void buttonUnblock() {
        mAnswerVar1Button.setEnabled(true);
        mAnswerVar2Button.setEnabled(true);
        if (mHelpCount > 0) {
            mHelpButton.setEnabled(true);
        }
        // Для блокировки mHelpButton, если после использования последней подсказки,
        // игрок нажимает не на mAnswerVar1Button или mAnswerVar1Button2,
        // а сразу на mQuestionNextButton.
        if (mHelpCount <= 0) {
            mHelpButton.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_HELP_ACTIVITY) {
            if (data == null) {
                return;
            }
            if (!mIsHelpUsed) {
                mIsHelpUsed = HelpActivity.wasAnswerShown(data);
                mHelpCount--;
                mHelpCountTextView.setText(String.valueOf(mHelpCount));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_ANSWER_VAR1_BUTTON_IS_ENABLED, mAnswerVar1Button.isEnabled());
        savedInstanceState.putBoolean(KEY_ANSWER_VAR2_BUTTON_IS_ENABLED, mAnswerVar2Button.isEnabled());
        savedInstanceState.putBoolean(KEY_HELP_BUTTON_IS_ENABLED, mHelpButton.isEnabled());

        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntegerArrayList(KEY_INDEX_BANK, mIndexBank);

        savedInstanceState.putDouble(KEY_ANSWER_CORRECT, mAnswerCorrect);
        savedInstanceState.putDouble(KEY_ANSWER_CORRECT_PERCENT, mAnswerCorrectPercent);
        savedInstanceState.putString(KEY_ANSWER_CORRECT_PERCENT_TO_STRING, mAnswerCorrectPercentToString);

        savedInstanceState.putInt(KEY_HELP_COUNT, mHelpCount);
        savedInstanceState.putBoolean(KEY_IS_HELP_USED, mIsHelpUsed);

        savedInstanceState.putBoolean(KEY_IS_GAME_END, mIsGameEnd);
    }
}
