package com.ruriboshi.taskpriority

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private lateinit var reV: RecyclerView
    private lateinit var button: FloatingActionButton
    private lateinit var reloadBtn: Button
    private lateinit var realm: Realm
    private lateinit var noText: TextView

    //private lateinit var consentInformation: ConsentInformation
    //private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var lM: RecyclerView.LayoutManager

    lateinit var mAdView : AdView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reV = findViewById(R.id.rv)
        button = findViewById(R.id.Btn)
        reloadBtn = findViewById(R.id.reloadBtn)
        noText = findViewById(R.id.noTaskText)
        realm = Realm.getDefaultInstance()

        //↓GDPR対応↓

        /*val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("44BFF7B793722DF84C72205017FF4482")
            .build()

        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.reset()
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@MainActivity,
                    ConsentForm.OnConsentFormDismissedListener {

                        // Consent gathering failed.
                        /*Log.w(TAG, String.format("%s: %s",
                            loadAndShowError?.errorCode,
                            loadAndShowError?.message
                        ))*/
                        if (consentInformation.canRequestAds()) {
                            initializeMobileAdsSdk()
                        }
                        // Consent has been gathered.
                    }
                )
            },
            {

            // Consent gathering failed.
                /*Log.w(TAG, String.format("%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message
                ))*/
            })
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }*/
        //↑GDPR対応↑

        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adViewM)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.setBackgroundColor(Color.parseColor("#9ef3ec"))


        val model = realm.where<SampleModel>().equalTo("id",1L).findFirst()
        if (model == null){
            realm.executeTransaction {
                realm.createObject<SampleModel>(1)
            }
        }

        button.setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            val locale = Locale.getDefault()
            if (locale == Locale.JAPAN){
                actionBar.title = "タスク一覧"
            }else{
                actionBar.title = "Task list"
            }
        }

        reloadBtn.setOnClickListener {
            val model = realm.where<SampleModel>().equalTo("id",1L).findFirst()
            val lId = model?.layoutID
            realm.executeTransaction {
                if (model != null) {
                    if (lId == 4L){
                        model.layoutID = 0L
                    }else{
                        model.layoutID += 1L
                    }
                }
            }
            val realmResults = realm.where(MyModel::class.java)
                .findAll().sort("id", Sort.DESCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            recyclerAdapter.notifyDataSetChanged()
            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()

            overridePendingTransition(0, 0)
            startActivity(intent)
        }

    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }*/

    /*@SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val model = realm.where<SampleModel>().equalTo("id",1L).findFirst()
        val lId = model?.layoutID
        realm.executeTransaction {
            if (model != null) {
                if (lId == 4L){
                    model.layoutID = 0L
                }else{
                    model.layoutID += 1L
                }
            }
        }
        val realmResults = realm.where(MyModel::class.java)
            .findAll().sort("id",Sort.DESCENDING)
        recyclerAdapter = RecyclerAdapter(realmResults)
        recyclerAdapter.notifyDataSetChanged()
        return true
    }*/

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        val locale = Locale.getDefault()
        reloadBtn = findViewById(R.id.reloadBtn)
        val model = realm.where<SampleModel>().equalTo("id",1L).findFirst()
        val lId = model?.layoutID
        if (lId == 0L){
            val realmResults = realm.where(MyModel::class.java)
                .findAll().sort("id", Sort.DESCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            reV.adapter = recyclerAdapter
            lM = LinearLayoutManager(this)
            reV.layoutManager = lM
            if (locale == Locale.JAPAN){
                reloadBtn.text = "新しい順"
            }else{
                reloadBtn.text = "New"
            }
        }else if (lId == 1L){
            val realmResults = realm.where(MyModel::class.java).findAll().sort("difficultyLevel",
                Sort.DESCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            reV.adapter = recyclerAdapter
            lM = LinearLayoutManager(this)
            reV.layoutManager = lM
            if (locale == Locale.JAPAN){
                reloadBtn.text = "緊急度も重要度も高い"
            }else{
                reloadBtn.text = "High priority"
            }
        }else if (lId == 2L){
            val realmResults = realm.where(MyModel::class.java)
                .findAll().sort("priority", Sort.DESCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            reV.adapter = recyclerAdapter
            lM = LinearLayoutManager(this)
            reV.layoutManager = lM
            if (locale == Locale.JAPAN){
                reloadBtn.text = "緊急度が高い"
            }else{
                reloadBtn.text = "Emergency"
            }
        }else if (lId == 3L){
            val realmResults = realm.where(MyModel::class.java)
                .findAll().sort("importance", Sort.DESCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            reV.adapter = recyclerAdapter
            lM = LinearLayoutManager(this)
            reV.layoutManager = lM
            if (locale == Locale.JAPAN){
                reloadBtn.text = "重要度が高い"
            }else{
                reloadBtn.text = "Importance"
            }
        }else if (lId == 4L){
            val realmResults = realm.where(MyModel::class.java)
                .findAll().sort("difficultyLevel", Sort.ASCENDING)
            recyclerAdapter = RecyclerAdapter(realmResults)
            reV.adapter = recyclerAdapter
            lM = LinearLayoutManager(this)
            reV.layoutManager = lM
            if (locale == Locale.JAPAN){
                reloadBtn.text = "緊急度も重要度も低い"
            }else{
                reloadBtn.text = "Low priority"
            }
        }
        val realmResults = realm.where(MyModel::class.java)
            .findAll()
        if (realmResults.isEmpty()){
            noText.visibility = View.VISIBLE
            reloadBtn.visibility = View.GONE
        }else{
            noText.visibility = View.GONE
            reloadBtn.visibility = View.VISIBLE
        }
    }

    //GDPR対応
    /*private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)

        // InterstitialAd.load(...)
        mAdView = findViewById(R.id.adViewM)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.setBackgroundColor(Color.parseColor("#9ef3ec"))
    }*/

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}