package kr.ac.hallym.thelittleprince

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyBox(val myContext:Context) {

    private val drawOrder = intArrayOf(
        0, 3, 2, 0, 2, 1,    // back
        2, 3, 7, 2, 7, 6,    // right-side
        1, 2, 6, 1, 6, 5,    // bottom
        4, 0, 1, 4, 1, 5,    // left-side
        3, 0, 4, 3, 4, 7,    // top
        5, 6, 7, 5, 7, 4     // front
    )

    private val vertexCoords = FloatArray(108).apply {
        val vertex = arrayOf(
            floatArrayOf(-1.0f, 0.5f, -0.5f),
            floatArrayOf(-1.0f, -0.5f, -0.5f),
            floatArrayOf(1.0f, -0.5f, -0.5f),
            floatArrayOf(1.0f, 0.5f, -0.5f),
            floatArrayOf(-1.0f, 0.5f, 0.5f),
            floatArrayOf(-1.0f, -0.5f, 0.5f),
            floatArrayOf(1.0f, -0.5f, 0.5f),
            floatArrayOf(1.0f, 0.5f, 0.5f)
        )
        var index = 0
        for(i in 0..35) {
            this[index++] = vertex[drawOrder[i]][0]
            this[index++] = vertex[drawOrder[i]][1]
            this[index++] = vertex[drawOrder[i]][2]
        }
    }

    private val vertexUVs = FloatArray(72).apply {
        val UVs = arrayOf(
            floatArrayOf(0.0f, 0.0f),
            floatArrayOf(0.0f, 1.0f),
            floatArrayOf(1.0f, 1.0f),
            floatArrayOf(0.0f, 0.0f),
            floatArrayOf(1.0f, 1.0f),
            floatArrayOf(1.0f, 0.0f)
        )

        var index = 0
        for (i in 0..5) {
            for(j in 0..5) {
                this[index++] = UVs[j][0]
                this[index++] = UVs[j][1]
            }
        }
    }

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

    private var mProgram: Int = -1

    private var mvpMatrixHandle: Int = -1

    private var textureID = IntArray(1)

    private val vertexCount: Int = vertexCoords.size/ COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init{
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, "box_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "box_frag.glsl", myContext)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(4) // enable하고

        GLES30.glVertexAttribPointer( // 넣음
            4,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(5)
        GLES30.glVertexAttribPointer(
            5,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )

        //
        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")

        GLES30.glGenTextures(1, textureID, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, loadBitmap("crate.bmp", myContext), 0)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}