#include <stdio.h>
#include <stdlib.h>
#include <GLFW/glfw3.h>

#include "native.h"

void* cCreate3DViewer();
int cCreateWindow();
void cShowBottle2(void*, void*);

void print_hello() {
    printf("Hello from C\n");
    printf("Rect %d\n", rectArea(2, 2));
    printf("MB +++\n");
    fflush(stdout);
    void* b = cMakeBottle(1.0, 1.0, 0.1);
    printf("MB ---\n");
    fflush(stdout);
    void* v = cCreate3DViewer();
    printf("CV ---\n");
    fflush(stdout);
//    cCreateWindow();
//    cShowBottle2(b, v);
    printf("SB ---\n");
}

//static void error_callback(int error, const char* description) {
//    printf("Error: %s\n", description);
//}
//
//static void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods) {
//    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
//        glfwSetWindowShouldClose(window, GLFW_TRUE);
//}

//void testGlfw() {
//    glfwSetErrorCallback(error_callback);
//
//    if (!glfwInit()) {
//        exit(EXIT_FAILURE);
//    }
//
//    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
//    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//
//    GLFWwindow* window = glfwCreateWindow(640, 480, "My Title", NULL, NULL);
//    if (!window) {
//        glfwTerminate();
//        exit(EXIT_FAILURE);
//    }
//    glfwSetKeyCallback(window, key_callback);
//
//    glfwMakeContextCurrent(window);
//    gladLoadGL(glfwGetProcAddress);
//    glfwSwapInterval(1);
//
//    while (!glfwWindowShouldClose(window)) {
//        // Keep running
//        int width, height;
//        glfwGetFramebufferSize(window, &width, &height);
//
//        double time = glfwGetTime();
//        glfwSwapBuffers(window);
//        glfwPollEvents();
//    }
//
//    glfwMakeContextCurrent(window);
//    glfwDestroyWindow(window);
//    glfwTerminate();
//}

