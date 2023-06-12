package kr.ac.hallym.thelittleprince

import android.content.ContentResolver
import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyMoonGround(val myContext: Context) {
    private val vertexCoords = floatArrayOf(
        0.0f, -1.0f, 2.0f,
        4.0f, -1.0f, 2.0f,
        4.3f, -1.0f, 1.732f,
        4.0f, -1.0f, 0.732f,
        0.0f, -1.0f, 0.0f,
        -4.0f, -1.0f, 0.732f,
        -4.3f, -1.0f, 1.732f,
        -4.0f, -1.0f, 2.0f
    )

    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }


    private var mProgram: Int = -1
    private var mColorHandle: Int = -1
    private var mvpMatrixHandle: Int = -1

    private val vertexCount: Int = vertexCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init{
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, "moon_ground_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "moon_ground_frag.glsl", myContext)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(2) // enable하고

        GLES30.glVertexAttribPointer( // 넣음
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "fColor").also{
            // enable X
            GLES30.glUniform4fv(it, 1, color, 0)
        }

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
    }

}