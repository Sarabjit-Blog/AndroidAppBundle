package sarabjit.blog.appbundlesample.application

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import com.google.android.play.core.splitcompat.SplitCompat
import java.util.*

/** We have to use a custom Application class, because we want to
 * initialize the selected language before SplitCompat#install() has a chance to run. */
class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        LanguageHelper.init(base)
        val ctx =
            LanguageHelper.getLanguageConfigurationContext(base)
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }
}

internal const val LANG_FR = "fr"
private const val PREFS_LANG = "language"

/**
 * A singleton helper for storing and retrieving the user selected language in a
 * SharedPreferences instance. It is required for persisting the user language choice between
 * application restarts.
 */
object LanguageHelper {
    lateinit var prefs: SharedPreferences
    var language: String
        get() {
            return prefs.getString(
                PREFS_LANG,
                LANG_FR
            )!!
        }
        set(value) {
            prefs.edit().putString(PREFS_LANG, value).apply()
        }

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(
            PREFS_LANG, Context.MODE_PRIVATE
        )
    }

    /**
     * Get a Context that overrides the language selection in the Configuration instance used by
     * getResources() and getAssets() by one that is stored in the LanguageHelper preferences.
     *
     * @param ctx a base context to base the new context on
     */
    fun getLanguageConfigurationContext(ctx: Context): Context {
        val conf = getLanguageConfiguration()
        return ctx.createConfigurationContext(conf)
    }

    /**
     * Get an empty Configuration instance that only sets the language that is
     * stored in the LanguageHelper preferences.
     * For use with Context#createConfigurationContext or Activity#applyOverrideConfiguration().
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getLanguageConfiguration(): Configuration {
        val conf = Configuration()
        println("Language Supported is $language")
        conf.setLocale(Locale.forLanguageTag(language))
        return conf
    }

}