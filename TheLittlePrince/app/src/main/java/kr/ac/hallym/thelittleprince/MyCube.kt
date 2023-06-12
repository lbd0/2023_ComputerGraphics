package kr.ac.hallym.thelittleprince

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyCube(val myContext: Context){
    private val vertexCoords = floatArrayOf(
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f

    )

    private val drawOrder = shortArrayOf(
        0, 3, 2, 0, 2, 1,    // back
        2, 3, 7, 2, 7, 6,    // right-side
        1, 2, 6, 1, 6, 5,    // bottom
        4, 0, 1, 4, 1, 5,    // left-side
        3, 0, 4, 3, 4, 7,    // top
        5, 6, 7, 5, 7, 4     // front

    )

    private var vertexBuffer: FloatBuffer =
        //
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            //
            order(ByteOrder.nativeOrder())

            //
            asFloatBuffer().apply {
                //
                put(vertexCoords)
                //
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        //
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val color = floatArrayOf( 1.0f, 1.0f, 0.0f, 1.0f )

    private var mProgram: Int = -1

    private var mColorHandle: Int = -1

    private var mvpMatrixHandle: Int = -1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, "cube_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "cube_frag.glsl", myContext)

        //
        mProgram = GLES30.glCreateProgram().also {

            //
            GLES30.glAttachShader(it, vertexShader)

            //
            GLES30.glAttachShader(it, fragmentShader)

            //
            GLES30.glLinkProgram(it)
        }

        //
        GLES30.glUseProgram(mProgram)

        //
        GLES30.glEnableVertexAttribArray(3)
        //
        GLES30.glVertexAttribPointer(
            3,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "fColor").also {
            //
            GLES30.glUniform4fv(it, 1, color, 0)
        }

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray) {

        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}