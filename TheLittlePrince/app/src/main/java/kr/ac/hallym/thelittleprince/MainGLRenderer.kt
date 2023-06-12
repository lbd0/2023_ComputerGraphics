package kr.ac.hallym.thelittleprince

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

const val COORDS_PER_VERTEX = 3

var eyePos = floatArrayOf(0.0f, 2.0f, 2.0f)
var eyeAt = floatArrayOf(0.0f, 0.0f, 0.0f)
var cameraVec = floatArrayOf(0.0f, -0.7071f, -0.7071f)

val lightDir = floatArrayOf( 2.0f, 2.0f, 2.0f )
val lightAmbient = floatArrayOf( 0.1f, 0.1f, 0.1f )
val lightDiffuse = floatArrayOf( 1.0f, 1.0f, 1.0f )
val lightSpecular = floatArrayOf( 1.0f, 1.0f, 1.0f )

class MainGLRenderer (val context : Context) : GLSurfaceView.Renderer {

    private lateinit var mPyramid : MyPyramid
    private lateinit var mCube : MyCube
    private lateinit var mMoonGround : MyMoonGround
    private lateinit var mBox : MyBox
    private lateinit var mPillar : MyPillar
    private lateinit var mBoa : MyBoa
    private lateinit var mEarth : MyEarth


    private var modelMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var vpMatrix = FloatArray(16)
    private var mvpMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        Matrix.setIdentityM(mvpMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(vpMatrix, 0)

        mPyramid = MyPyramid(context)
        mCube = MyCube(context)
        mMoonGround = MyMoonGround(context)
        mBox = MyBox(context)
        mPillar = MyPillar(context)
        mBoa = MyBoa(context)
        mEarth = MyEarth(context)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)


        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 90f, ratio, 0.001f, 1000f)

        Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f,1f ,0f)

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // 카메라 움직이기
        eyeAt[0] = eyePos[0] + cameraVec[0]
        eyeAt[1] = eyePos[1] + cameraVec[1]
        eyeAt[2] = eyePos[2] + cameraVec[2]

        Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f, 1f, 0f)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        mMoonGround.draw(vpMatrix, modelMatrix)    // 바닥

        // 큐브 위치 조정
        var rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotMatrix, 0, 45f, 0f, 0f, 1f)

        var scaleMatrix = floatArrayOf(
            0.5f, 0f, 0f, 0f,
            0f, 0.5f, 0f, 0f,
            0f, 0f, 0.5f, 0f,
            0f, 0f, 0f, 1f
        )
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0.5f, 0f)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mCube.draw(mvpMatrix) // 어린왕자 머리

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mPyramid.draw(mvpMatrix)   // 어린왕자 몸



        // 행성 위치 및 크기 조정
        scaleMatrix = floatArrayOf(
            0.5f, 0f, 0f, 0f,
            0f, 0.5f, 0f, 0f,
            0f, 0f, 0.5f, 0f,
            0f, 0f, 0f, 1f
        )

        rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotMatrix, 0, 165f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -4.0f, 1.3f, 2.0f)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mPillar.draw(mvpMatrix)    // 행성 1 - 장미꽃


        scaleMatrix = floatArrayOf(
            0.5f, 0f, 0f, 0f,
            0f, 0.5f, 0f, 0f,
            0f, 0f, 0.5f, 0f,
            0f, 0f, 0f, 1f
        )
        rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotMatrix, 0, 100f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -3.0f, 1.3f, -0.5f)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mEarth.draw(mvpMatrix)    // 행성 2 - 바오밥(여우)


        scaleMatrix = floatArrayOf(
            0.3f, 0f, 0f, 0f,
            0f, 0.3f, 0f, 0f,
            0f, 0f, 0.3f, 0f,
            0f, 0f, 0f, 1f
        )
        rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotMatrix, 0, 90f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 1.5f, 1.3f, -1.0f)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mBoa.draw(mvpMatrix)    // 행성 3 - 보아뱀


        scaleMatrix = floatArrayOf(
            0.3f, 0f, 0f, 0f,
            0f, 0.3f, 0f, 0f,
            0f, 0f, 0.3f, 0f,
            0f, 0f, 0f, 1f
        )
        rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotMatrix, 0, 90f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 3.5f, 1.8f, 1.0f)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mBox.draw(mvpMatrix, modelMatrix)    // 행성 4 - 상자


    }

}

fun loadShader(type: Int, filename: String, myContext: Context): Int {

    return GLES30.glCreateShader(type).also{ shader ->

        val inputStream = myContext.assets.open(filename)
        val inputBuffer = ByteArray(inputStream.available())
        inputStream.read(inputBuffer)
        val shaderCode = String(inputBuffer)

        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)

        val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
        if(compiled.get(0) == 0) {
            GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
            if(compiled.get(0) > 1) {
                Log.e("Shader", "$type shader: " + GLES30.glGetShaderInfoLog(shader))
            }
            GLES30.glDeleteShader(shader)
            Log.e("Shader", "$type shader compile error.")
        }
    }
}

fun loadBitmap(filename: String, myContext: Context): Bitmap {
    val manager = myContext.assets
    val inputStream = BufferedInputStream(manager.open(filename))
    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
    return bitmap!!
}

fun cameraRotate(theta: Float) {
    val sinTheta = sin(theta)
    val cosTheta = cos(theta)
    val newVecZ = cosTheta * cameraVec[2] - sinTheta * cameraVec[0]
    val newVecX = sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
    cameraVec[0] = newVecX
    cameraVec[2] = newVecZ
}

fun cameraMove(distance: Float) {
    val newPosX = eyePos[0] + distance * cameraVec[0]
    val newPosZ = eyePos[2] + distance * cameraVec[2]
    if(newPosX > -10 && newPosX < 10 && newPosZ > -10 && newPosZ < 10) {
        eyePos[0] = newPosX
        eyePos[2] = newPosZ
    }
}