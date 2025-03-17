package com.otmj.otmjapp.Helper;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Provides live data validation for input fields.
 */
public abstract class TextValidator implements TextWatcher {
    private final TextInputEditText textInputEditText;
    private final TextInputLayout textInputLayout;

    public TextValidator(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
        this.textInputEditText = textInputEditText;
        this.textInputLayout = textInputLayout;
    }

    /**
     * Validates the input field while the user is typing.
     *
     * @param textInputEditText The {@link TextInputEditText} being validated.
     * @param textInputLayout   The {@link TextInputLayout} used to display validation errors.
     */
    public abstract void validateWhileTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout);

    /**
     * Validates the input field after the user has finished typing.
     *
     * @param textInputEditText The {@link TextInputEditText} being validated.
     * @param textInputLayout   The {@link TextInputLayout} used to display validation errors.
     */
    public abstract void validateAfterTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout);

    @Override
    final public void afterTextChanged(Editable s) {
        validateAfterTyping(textInputEditText, textInputLayout); // Trigger validation after user stops typing
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
        validateWhileTyping(textInputEditText, textInputLayout); // Trigger validation while user is typing
    }
}
