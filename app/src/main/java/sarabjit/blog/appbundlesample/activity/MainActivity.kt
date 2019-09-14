package sarabjit.blog.appbundlesample.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.*
import kotlinx.android.synthetic.main.activity_main.*
import sarabjit.blog.appbundlesample.BuildConfig
import sarabjit.blog.appbundlesample.R
import sarabjit.blog.appbundlesample.adapter.RecyclerViewAdapter
import sarabjit.blog.appbundlesample.listener.RecyclerviewClickHandler
import sarabjit.blog.appbundlesample.model.TourData
import sarabjit.blog.appbundlesample.model.getData

private const val PACKAGE_NAME = "sarabjit.blog.module1"
private const val CLASSNAME_MODULE = "${PACKAGE_NAME}.TripDetailActivity"
private const val CONFIRMATION_REQUEST_CODE = 1
const val TOUR_DATA = "tourData"

class MainActivity : BaseSplitActivity(), RecyclerviewClickHandler {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var listener: SplitInstallStateUpdatedListener
    private lateinit var splitInstallManager: SplitInstallManager
    private lateinit var mTour: TourData
    private var currentSessionId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        createSplitInstallationListener()
    }

    /**
     * Initialize the Split Install Manager and the Recyclerview
     */
    fun init() {
        splitInstallManager = SplitInstallManagerFactory.create(this)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter =
            RecyclerViewAdapter(getData(), this)
        uninstall_Button.setOnClickListener {
            requestUninstall()
        }
    }

    /** Request uninstall of all features. */
    private fun requestUninstall() {
        val installedModules = splitInstallManager.installedModules.toList()
        if (installedModules.isEmpty()) {
            showToastAndLog(getString(R.string.no_modules_downloaded))
            return
        } else {
            showToastAndLog(
                getString(R.string.uninstall_generic_message)
            )
        }

        splitInstallManager.deferredUninstall(installedModules).addOnSuccessListener {
            showToastAndLog("Uninstalling $installedModules")
        }.addOnFailureListener {
            showToastAndLog("Failed installation of $installedModules")
        }
    }

    /**
     * Create a listener to handle different states of the downloading of the dynamic module
     */
    private fun createSplitInstallationListener() {
        // Creates a listener for request status updates.
        listener = SplitInstallStateUpdatedListener { state ->
            if (state.sessionId() == currentSessionId) {
                when (state.status()) {
                    DOWNLOADING -> {
                        //show the progress of the downloading to the user
                        displayLoadingState(state)
                    }
                    INSTALLED -> {
                        //once the module is installed, do your stuff here
                        showHideProgressBar(false)
                        onSuccessfulLoad(mTour)
                    }
                    REQUIRES_USER_CONFIRMATION ->
                        // Displays a dialog for the user to either “Download” or “Cancel” the request.
                        splitInstallManager.startConfirmationDialogForResult(
                            state,
                            this,
                            CONFIRMATION_REQUEST_CODE
                        )
                    else -> {
                        //Log or handle any other scenario here
                    }
                }
            }
        }
    }

    /**
     * Persist the data to be sent to the next screen and create a request to install the
     * requested module and initiate it
     */
    override fun onClick(tour: TourData, position: Int) {
        mTour = tour
        if (splitInstallManager.installedModules.contains(getString(R.string.module_one))) {
            showToastAndLog(getString(R.string.module_already_installed))
            onSuccessfulLoad(tour)
            return
        }

        val request = SplitInstallRequest
            .newBuilder()
            .addModule(getString(R.string.module_one))
            .build()

        splitInstallManager.startInstall(request).addOnSuccessListener { currentSessionId = it }
            .addOnFailureListener { exception ->
                when ((exception as SplitInstallException).errorCode) {
                    SplitInstallErrorCode.NETWORK_ERROR -> {
                        showToastAndLog(getString(R.string.network_error))
                    }
                    SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED -> checkForActiveDownloads(
                        currentSessionId
                    )
                }
            }
    }

    /**
     * Checks for any existing Session and cancel if same session is found in Downloading state
     */
    fun checkForActiveDownloads(currentSession: Int) {
        splitInstallManager
            // Returns a SplitInstallSessionState object for each active session as a List.
            .sessionStates
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Check for active sessions.
                    for (state in task.result) {
                        if (state.status() == DOWNLOADING) {
                            // Cancel the request
                            showToastAndLog(getString(R.string.cancel_existing_installation))
                            splitInstallManager.cancelInstall(currentSession)
                        }
                    }
                }
            }

    }

    fun showToastAndLog(text: String) {
        // Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        Log.d(TAG, text)
    }


    /**
     * Load a feature by module name.
     * @param name The name of the feature module to load.
     */

    fun onSuccessfulLoad(tourData: TourData) {
        uninstall_Button.visibility = View.VISIBLE
        Intent().setClassName(BuildConfig.APPLICATION_ID, CLASSNAME_MODULE)
            .putExtra(TOUR_DATA, tourData)
            .also {
                startActivity(it)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            // Handle the user's decision. For example, if the user selects "Cancel",
            // you may want to disable certain functionality that depends on the module.
            if (resultCode == Activity.RESULT_CANCELED) {
                showToastAndLog(getString(R.string.user_cancelled))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        splitInstallManager.unregisterListener(listener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(listener);
    }


    /** Display a loading state to the user. */
    private fun displayLoadingState(state: SplitInstallSessionState) {
        showHideProgressBar(true)
        progress_bar.max = state.totalBytesToDownload().toInt()
        progress_bar.progress = state.bytesDownloaded().toInt()
    }

    /** Display progress bar and text. */
    private fun showHideProgressBar(showProgressBar: Boolean) {
        if (showProgressBar) progress.visibility = View.VISIBLE else progress.visibility =
            View.GONE

    }
}

