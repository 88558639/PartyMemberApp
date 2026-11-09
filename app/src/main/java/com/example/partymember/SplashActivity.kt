package com.example.partymember

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator

class SplashActivity : AppCompatActivity() {

    private lateinit var cvLogoContainer: View
    private lateinit var tvAppName: View
    private lateinit var tvSlogan: View
    private lateinit var divider: View
    private lateinit var llFeatures: View
    private lateinit var ivGlow: View
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var tvVersion: View

    private val SPLASH_DURATION = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initViews()
        setupAnimations()
        scheduleSplashEnd()
    }

    private fun initViews() {
        cvLogoContainer = findViewById(R.id.cvLogoContainer)
        tvAppName = findViewById(R.id.tvAppName)
        tvSlogan = findViewById(R.id.tvSlogan)
        llFeatures = findViewById(R.id.llFeatures)
        ivGlow = findViewById(R.id.circle1)
        progressBar = findViewById(R.id.progressBar)
        tvVersion = findViewById(R.id.tvVersion)
    }

    private fun setupAnimations() {
        val scaleX = ObjectAnimator.ofFloat(cvLogoContainer, View.SCALE_X, 0f, 1f).apply {
            duration = 600
            interpolator = OvershootInterpolator(1.3f)
        }

        val scaleY = ObjectAnimator.ofFloat(cvLogoContainer, View.SCALE_Y, 0f, 1f).apply {
            duration = 600
            interpolator = OvershootInterpolator(1.3f)
        }

        val alpha = ObjectAnimator.ofFloat(cvLogoContainer, View.ALPHA, 0f, 1f).apply {
            duration = 600
        }

        val logoAnimatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
        }

        val appNameAlpha = ObjectAnimator.ofFloat(tvAppName, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 300
        }

        val appNameTranslationY = ObjectAnimator.ofFloat(tvAppName, View.TRANSLATION_Y, 40f, 0f).apply {
            duration = 500
            startDelay = 300
            interpolator = AccelerateDecelerateInterpolator()
        }

        val sloganAlpha = ObjectAnimator.ofFloat(tvSlogan, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 500
        }

        val sloganTranslationY = ObjectAnimator.ofFloat(tvSlogan, View.TRANSLATION_Y, 40f, 0f).apply {
            duration = 500
            startDelay = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        val featuresAlpha = ObjectAnimator.ofFloat(llFeatures, View.ALPHA, 0f, 1f).apply {
            duration = 600
            startDelay = 700
        }

        val featuresTranslationY = ObjectAnimator.ofFloat(llFeatures, View.TRANSLATION_Y, 40f, 0f).apply {
            duration = 600
            startDelay = 700
            interpolator = AccelerateDecelerateInterpolator()
        }

        val progressBarAlpha = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 900
        }

        val versionAlpha = ObjectAnimator.ofFloat(tvVersion, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 1100
        }

        val circle1Scale = ObjectAnimator.ofFloat(
            ivGlow, View.SCALE_X, 1f, 1.3f, 1f
        ).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
        }

        val circle1ScaleY = ObjectAnimator.ofFloat(
            ivGlow, View.SCALE_Y, 1f, 1.3f, 1f
        ).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
        }

        val circle1Alpha = ObjectAnimator.ofFloat(ivGlow, View.ALPHA, 0.3f, 0.5f, 0.3f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
        }

        val mainAnimatorSet = AnimatorSet().apply {
            playTogether(
                appNameAlpha, appNameTranslationY,
                sloganAlpha, sloganTranslationY,
                featuresAlpha, featuresTranslationY,
                progressBarAlpha, versionAlpha
            )
        }

        val circleAnimatorSet = AnimatorSet().apply {
            playTogether(circle1Scale, circle1ScaleY, circle1Alpha)
        }

        logoAnimatorSet.start()
        mainAnimatorSet.start()
        circleAnimatorSet.start()
    }

    private fun scheduleSplashEnd() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, SPLASH_DURATION)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
