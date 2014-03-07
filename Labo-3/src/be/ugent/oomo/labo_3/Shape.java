package be.ugent.oomo.labo_3;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

public class Shape {

	private Buffer mVertexBuffer;
	private Buffer mColorBuffer;
	private Buffer mIndexBuffer;
	
    static float vertices[] = {   // in counterclockwise order:
	    	-1.0f, -1.0f, -1.0f,	// left  bottom back	0
	         1.0f, -1.0f, -1.0f,	// right bottom back	1
	         1.0f,  1.0f, -1.0f,	// right top    back	2
	        -1.0f,  1.0f, -1.0f,	// left  top    back	3
	        -1.0f, -1.0f,  1.0f,    // left  bottom front	4
	         1.0f, -1.0f,  1.0f,	// right bottom front	5
	         1.0f,  1.0f,  1.0f,	// right top    front	6
	        -1.0f,  1.0f,  1.0f,    // left  top    front	7
    };

    // Set color with red, green, blue and alpha (opacity) values
    static float colors[] = { 
	    	0.0f,  1.0f,  0.0f,  1.0f,
	        0.0f,  1.0f,  0.0f,  1.0f,
	        1.0f,  0.5f,  0.0f,  1.0f,
	        1.0f,  0.5f,  0.0f,  1.0f,
	        1.0f,  0.0f,  0.0f,  1.0f,
	        1.0f,  0.0f,  0.0f,  1.0f,
	        0.0f,  0.0f,  1.0f,  1.0f,
	        1.0f,  0.0f,  1.0f,  1.0f
    	};
    
    static byte indices[] = { 
	    	0, 4, 5, 0, 5, 1,
	        1, 5, 6, 1, 6, 2,
	        2, 6, 7, 2, 7, 3,
	        3, 7, 4, 3, 4, 0,
	        4, 7, 6, 4, 6, 5,
	        3, 0, 1, 3, 1, 2
    	};

    public Shape() {
        // initialize vertex byte buffer for shape coordinates
        mVertexBuffer = ByteBuffer
        		.allocateDirect(vertices.length * 4)
        		.order(ByteOrder.nativeOrder())
        		.asFloatBuffer()
        		.put(vertices)
        		.position(0);
        
        mColorBuffer = ByteBuffer
        		.allocateDirect(colors.length * 4)
        		.order(ByteOrder.nativeOrder())
        		.asFloatBuffer()
        		.put(colors)
        		.position(0);
        
        mIndexBuffer = ByteBuffer
        		.allocateDirect(indices.length)
        		.put(indices)
        		.position(0);
    }
    
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    	gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
    	gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
