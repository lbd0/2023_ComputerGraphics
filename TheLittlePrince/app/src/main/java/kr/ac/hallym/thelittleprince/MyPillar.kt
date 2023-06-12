package kr.ac.hallym.thelittleprince

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyPillar(val myContext:Context) {
    private val vertexCoords = floatArrayOf(
        0.5f, 2.0f, 0.0f,
        0.25f, 2.0f, 0.4333f,
        -0.25f, 2.0f, 0.4333f,
        -0.5f, 2.0f, 0.0f,
        -0.25f, 2.0f, -0.4333f,
        0.25f, 2.0f, -0.4333f,
        0.5f, 2.0f, 0.0f,

        0.5f, -1.0f, 0.0f,
        0.25f, -1.0f, 0.4333f,
        -0.25f, -1.0f, 0.4333f,
        -0.5f, -1.0f, 0.0f,
        -0.25f, -1.0f, -0.4333f,
        0.25f, -1.0f, -0.4333f,
        0.5f, -1.0f, 0.0f
    )

    private val pillarIndex = shortArrayOf(
        0, 1, 8, 0, 8, 7,
        1, 2, 9, 1, 9, 8,
        2, 3, 10, 2, 10, 9,
        3, 4, 11, 3, 11, 10,
        4, 5, 12, 4, 12, 11,
        5, 6, 13, 5, 13, 12
    )

    private var vertexUVs = floatArrayOf(
        0.0f, 0.0f,
        0.1667f, 0.0f,
        0.3334f, 0.0f,
        0.5f, 0.0f,
        0.6667f, 0.0f,
        0.8334f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        0.1667f, 1.0f,
        0.3334f, 1.0f,
        0.5f, 1.0f,
        0.6667f, 1.0f,
        0.8334f, 1.0f,
        1.0f, 1.0f
    )

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }

    private var uvBuffer : FloatBuffer =
        ByteBuffer.allocateDirect(vertexUVs.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexUVs)
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(pillarIndex.size * 2). run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(pillarIndex)
                position(0)
            }
        }

    private var mProgram: Int = -1

    private var mvpMatrixHandle: Int = -1
    private var textureID = IntArray(1)

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, "pillar_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "pillar_frag.glsl", myContext)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(12)
        GLES30.glVertexAttribPointer(
            12,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(13)
        GLES30.glVertexAttribPointer(
            13,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")

        GLES30.glGenTextures(1, textureID, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, loadBitmap("rose.bmp", myContext), 0)
    }

    fun draw(mvpMatrix : FloatArray) {
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0])

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, pillarIndex.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}