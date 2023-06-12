package kr.ac.hallym.thelittleprince

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
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

        val sinTheta = 0.17365f
        val cosTheta = 0.98481f
        binding.eyeLeft.setOnClickListener {
            var newVecZ = cosTheta * cameraVec[2] - sinTheta * cameraVec[0]
            var newVecX = sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecZ
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            var newVecZ = cosTheta * cameraVec[2] + sinTheta * cameraVec[0]
            var newVecX = -sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecZ
            binding.surfaceView.requestRender()
        }
        binding.eyeForward.setOnClickListener {
            /*var newPosX = eyePos[0] + 0.5f * cameraVec[0]
            var newPosZ = eyePos[2] + 0.5f * cameraVec[2]
            if(newPosX > -10 && newPosX < 10 && newPosZ > -10 && newPosZ < 10) {
                eyePos[0] = newPosX
                eyePos[2] = newPosZ
                binding.surfaceView.requestRender()
            }*/
            scaleFactor *= 0.9f
        }
        binding.eyeBackward.setOnClickListener {
            /*var newPosX = eyePos[0] - 0.5f * cameraVec[0]
            var newPosZ = eyePos[2] - 0.5f * cameraVec[2]
            if(newPosX > -10 && newPosX < 10 && newPosZ > -10 && newPosZ < 10) {
                eyePos[0] = newPosX
                eyePos[2] = newPosZ
                binding.surfaceView.requestRender()
            }*/
            scaleFactor *= 1.1f
        }
    }

    fun initSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)

        binding.surfaceView.setRenderer(MainGLRenderer(this))

        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

}