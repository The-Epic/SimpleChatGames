package xyz.epicebic.simplechatgames.utils;

import lombok.Getter;

import java.util.Random;

public class MathQuestionGenerator {

    private int lowerLimit;
    private int upperLimit;
    private Random random;
    @Getter
    private int result;

    public MathQuestionGenerator(int lowerLimit, int upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.random = new Random();
    }


    public String generateQuestion() {
        int num1 = random.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
        int num2 = random.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
        char operator = getRandomOperator();
        int result = calculateResult(num1, num2, operator);
        while (result % 10 != 0) {
            num1 = random.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
            num2 = random.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
            operator = getRandomOperator();
            result = calculateResult(num1, num2, operator);
        }
        this.result = result;
        return num1 + " " + operator + " " + num2 + " = ?";
    }

    private char getRandomOperator() {
        char[] operators = {'+', '-'};
        int index = random.nextInt(operators.length);
        return operators[index];
    }

    private int calculateResult(int num1, int num2, char operator) {
        int result = 0;
        switch (operator) {
            case '+'-> result = num1 + num2;
            case '-'-> result = num1 - num2;
        }
        return result;
    }
}
