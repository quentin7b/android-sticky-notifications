package com.github.quentin7b.sn.ui

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.quentin7b.sn.R
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {


    private var licensesDialog: LicensesDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.action_about)

        var pInfo: PackageInfo? = null
        try {
            pInfo = packageManager.getPackageInfo(packageName, 0)
            versionTV.text = getString(R.string.about_version, pInfo!!.versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("Aa", "Cant get package manager", e)
        }


        licensesDialog = LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()

        versionTV.setOnClickListener { licensesDialog!!.showAppCompat() }
    }
}
