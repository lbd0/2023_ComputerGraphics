package kr.ac.hallym.thelittleprince

import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import kr.ac.hallym.thelittleprince.databinding.ActivityMoonBinding

var scaleFactor = 1f

class Moon : AppCompatActivity() {

    val binding : ActivityMoonBinding by lazy {
        ActivityMoonBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_moon)

        supportActionBar?.hide()
        initSurfaceView()
        setContentView(binding.root)

        binding.eyeLeft.setOnClickListener {
            cameraRotate(0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            cameraRotate(-0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeForward.setOnClickListener {
            cameraMove(0.5f)
            binding.surfaceView.requestRender()
        }
        binding.eyeBackward.setOnClickListener {
            cameraMove(-0.5f)
            binding.surfaceView.requestRender()
        }

    }

    fun initSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)

        binding.surfaceView.setRenderer(MainGLRenderer(this))

        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

}