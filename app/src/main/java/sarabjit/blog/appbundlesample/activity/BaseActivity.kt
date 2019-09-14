package sarabjit.blog.appbundlesample.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat
import sarabjit.blog.appbundlesample.application.LanguageHelper

/**
 * This base activity call to attachBaseContext:
 */
abstract class BaseSplitActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val ctx = newBase?.let { LanguageHelper.getLanguageConfigurationContext(it) }
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }
}