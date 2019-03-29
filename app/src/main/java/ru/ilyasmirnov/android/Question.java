package ru.ilyasmirnov.android;

public class Question {

    private int mTextResId;         // Вопрос
    private int mAnswerVar1ResId;   // Вариант ответа 1
    private int mAnswerVar2ResId;   // Вариант ответа 2
    private int mAnswerResId;       // Ответ

    public Question(int textResId, int answerVar1ResId, int answerVar2ResId, int answerResId) {
        mTextResId = textResId;
        mAnswerVar1ResId = answerVar1ResId;
        mAnswerVar2ResId = answerVar2ResId;
        mAnswerResId = answerResId;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public int getAnswerVar1ResId() {
        return mAnswerVar1ResId;
    }

    public int getAnswerVar2ResId() {
        return mAnswerVar2ResId;
    }

    public int getAnswerResId() {
        return mAnswerResId;
    }
}
