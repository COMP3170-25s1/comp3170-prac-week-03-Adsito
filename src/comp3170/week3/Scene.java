package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

    final private String VERTEX_SHADER = "vertex.glsl";
    final private String FRAGMENT_SHADER = "fragment.glsl";

    private Vector4f[] vertices;
    private int vertexBuffer;
    private int[] indices;
    private int indexBuffer;
    private Vector3f[] colours;
    private int colourBuffer;

    private long oldTime = System.currentTimeMillis();
    private float angle = 90f; // Angle of rotation (radians)
    private float speed = 0.1f; // Forward speed
    private float rotationSpeed = 1f; // Rotation speed (angle increment per frame)
    private Matrix4f transMatrix = new Matrix4f();
    private Matrix4f rotMatrix = new Matrix4f();
    private Matrix4f sclMatrix = new Matrix4f();
    private Matrix4f modelMatrix = new Matrix4f();
    private Vector3f position = new Vector3f(1.5f, 0f, 0.0f);
    private Shader shader;

    public Scene() {

        shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

        // Define the vertices of the shape (representing a simple triangle plane)
        vertices = new Vector4f[]{
            new Vector4f(0, 0, 0, 1),
            new Vector4f(0, 1, 0, 1),
            new Vector4f(-1, -1, 0, 1),
            new Vector4f(1, -1, 0, 1),
        };

        vertexBuffer = GLBuffers.createBuffer(vertices);

        // Define the colors of the vertices
        colours = new Vector3f[]{
            new Vector3f(1, 0, 1),    // MAGENTA
            new Vector3f(1, 0, 1),    // MAGENTA
            new Vector3f(1, 0, 0),    // RED
            new Vector3f(0, 0, 1),    // BLUE
        };

        colourBuffer = GLBuffers.createBuffer(colours);

        // Define the indices for drawing triangles
        indices = new int[]{
            0, 1, 2, // left triangle
            0, 1, 3, // right triangle
        };

        indexBuffer = GLBuffers.createIndexBuffer(indices);
    }

    public void update() {
        long time = System.currentTimeMillis();
        float deltaTime = (time - oldTime) / 1000f;
        oldTime = time;
        
        // Update the angle of rotation
        angle += rotationSpeed * deltaTime;
        
        // Calculate the new position based on the angle
        position.x = (float) Math.cos(angle) * speed;
        position.y = (float) Math.sin(angle) * speed;
    }

    public void draw() {
        shader.enable();
        update();
        shader.setAttribute("a_position", vertexBuffer);
        shader.setAttribute("a_colour", colourBuffer);
        modelMatrix.identity();
        
        translationMatrix(position.x, position.y, transMatrix);
        rotationMatrix(-angle, rotMatrix);
        scaleMatrix(0.1f, 0.1f, sclMatrix);
        modelMatrix.mul(transMatrix)
                   .mul(rotMatrix)
                   .mul(sclMatrix);
        shader.setUniform("u_modelMatrix", modelMatrix);

        // Bind the index buffer for drawing
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

        // Set polygon mode and draw the shape
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
        dest.identity();
    	dest.m30(tx);
        dest.m31(ty);
        return dest;
    }

    public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
    	dest.identity();
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        dest.m00(cos);
        dest.m01(-sin);
        dest.m10(sin);
        dest.m11(cos);

        return dest;
    }

    public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
    	dest.identity();
        dest.m00(sx);
        dest.m11(sy);
        return dest;
    }
}
