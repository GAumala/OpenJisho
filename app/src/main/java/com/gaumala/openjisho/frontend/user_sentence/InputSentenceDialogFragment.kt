package com.gaumala.openjisho.frontend.user_sentence

import android.os.Bundle
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.gaumala.openjisho.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout

class InputSentenceDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val INITIAL_VALUE_KEY = "initialValue"

        fun create(initialValue: String?): InputSentenceDialogFragment {
            return InputSentenceDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(INITIAL_VALUE_KEY, initialValue)
                }
            }
        }
    }

    private fun bindView(view: View, initialValue: String?) {
        val title = view.findViewById<TextView>(R.id.title)
        val textBody = view.findViewById<TextView>(R.id.text_body)
        val button = view.findViewById<View>(R.id.button)
        val textInput = view.findViewById<TextInputLayout>(R.id.text_field)
        val editText = textInput.editText!!

        val isEditing = initialValue != null
        title.text =
            if (isEditing) getString(R.string.edit_sentence)
            else getString(R.string.input_sentence)

        val rawBodyHtml = getString(R.string.please_input_sentence)
        textBody.text = HtmlCompat.fromHtml(
            rawBodyHtml,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        textBody.movementMethod = LinkMovementMethod.getInstance()

        editText.setText(initialValue)
        // multiline EditText with actionDone button
        // https://stackoverflow.com/a/41022589
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismiss()
                submitSentence(editText.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

        val closeButton =
            view.findViewById<View>(R.id.close_btn)
        closeButton.setOnClickListener {
            dismiss()
        }

        button.setOnClickListener {
            dismiss()
            submitSentence(editText.text.toString())
        }
    }

    private fun submitSentence(input: String) {
        if (input.isEmpty()) {
            return
        }

        val parent = (parentFragment as? InputSentenceDialogParent)
            ?: (activity as? InputSentenceDialogParent)
        parent?.onInputSentence(input)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.input_sentence_widget, null)
        val initialValue = requireArguments().getString(INITIAL_VALUE_KEY)
        bindView(view, initialValue)
        return view
    }
}