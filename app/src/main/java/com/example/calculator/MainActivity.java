package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button[] digitButtons;
    private Button[] operatorButtons;
    private Button clearButton;
    private Button negateButton;
    private Button percentButton;
    private Button decimalButton;
    private TextView displayText;
    private double previousValue;
    private boolean isNewOperation = true;
    private String previousOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        int[] digitButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        int[] operatorButtonIds = {
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.buttonEqual,
        };

        displayText = findViewById(R.id.main_text);
        clearButton = findViewById(R.id.buttonAC);
        negateButton = findViewById(R.id.buttonPlusMinus);
        percentButton = findViewById(R.id.buttonPercent);
        decimalButton = findViewById(R.id.buttonComma);

        clearButton.setOnClickListener(v ->{
            displayText.setText("0");
            isNewOperation = true;
        });

        negateButton.setOnClickListener(v ->{
            displayText.setText(formatResult(parseDecimal(displayText.getText().toString()) * -1));
        });

        percentButton.setOnClickListener(v ->{
            displayText.setText(formatResult(parseDecimal(displayText.getText().toString()) / 100));
        });

        decimalButton.setOnClickListener(v ->{
            if (!displayText.getText().toString().contains(",")){
                displayText.append(",");
            }
        });

        digitButtons = new Button[digitButtonIds.length];

        operatorButtons = new Button[operatorButtonIds.length];

        for (int i = 0; i < digitButtonIds.length; i++) {
            digitButtons[i] = findViewById(digitButtonIds[i]);
            digitButtons[i].setOnClickListener(v -> {
                Button b = (Button)v;
                if (displayText.getText().charAt(0) == '0' && !displayText.getText().toString().contains("0,")) {
                    displayText.setText("");
                }
                if (displayText.getText().length() < 9){
                    displayText.append(b.getText());
                }
            });
        }

        for (int i = 0; i < operatorButtonIds.length; i++) {
            operatorButtons[i] = findViewById(operatorButtonIds[i]);
            operatorButtons[i].setOnClickListener(v ->{
                Button button = (Button)v;
                if (isNewOperation) {
                    previousValue = parseDecimal(displayText.getText().toString());
                    displayText.setText("0");
                    previousOperator = button.getText().toString();
                    isNewOperation = false;
                } else {
                    calculateResult(previousOperator, parseDecimal(displayText.getText().toString()));
                    isNewOperation = true;
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {
            displayText.setText(savedInstanceState.getString("mainText"));
            previousValue = savedInstanceState.getDouble("prevText");
            isNewOperation = savedInstanceState.getBoolean("step");
            previousOperator = savedInstanceState.getString("prevAction");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mainText", displayText.getText().toString());
        outState.putDouble("prevText", previousValue);
        outState.putBoolean("step", isNewOperation);
        outState.putString("prevAction", previousOperator);
    }

    private void calculateResult(String operator, double currentValue) {
        double result = 0;
        switch (operator) {
            case "+":
                result = previousValue + currentValue;
                break;
            case "-":
                result = previousValue - currentValue;
                break;
            case "×":
                result = previousValue * currentValue;
                Toast.makeText(this, String.valueOf(result), Toast.LENGTH_SHORT).show();
                break;
            case "÷":
                if (currentValue != 0) {
                    result = previousValue / currentValue;
                }
                break;
        }
        displayText.setText(formatResult(result));
    }

    private String formatResult(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(parseDecimal(String.valueOf(Math.round(value * 10000000) / 10000000.0)))
                    .replace(".", ",");
        }
    }

    private Double parseDecimal(String value){
        return Double.parseDouble(value.replace(",", "."));
    }
}