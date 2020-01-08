package co.avalinejad.iq.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import co.avalinejad.iq.R
import kotlinx.android.synthetic.main.select_language_layout.*
import co.avalinejad.iq.SpeedMeterApplication


class SelectLanguageDialogFragment(
    context: Context,
    val onResult: ((String) -> Unit)? = null
    ) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_language_layout)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.attributes.windowAnimations = R.style.selectLanStyle

        enBtn.setOnClickListener {
            onResult?.invoke("en")
            dismiss()
        }

        faBtn.setOnClickListener {
          onResult?.invoke("fa")
            dismiss()
        }
    }
}