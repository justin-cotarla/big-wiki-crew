package org.wikipedia.descriptions

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_description_edit_license.view.*
import org.wikipedia.R
import org.wikipedia.util.StringUtil

class DescriptionEditLicenseView  @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.view_description_edit_license, this)
        licenseText!!.text = StringUtil.fromHtml(String
                .format(context.getString(R.string.description_edit_license_notice),
                        context.getString(R.string.terms_of_use_url),
                        context.getString(R.string.cc_0_url)))
        licenseText!!.movementMethod = LinkMovementMethod()
    }

}
