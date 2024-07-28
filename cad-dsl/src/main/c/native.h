

void print_hello(void);
int rectArea(int, int);
void* cMakeBottle(const double, const double, const double);
void cShowBottle(void*);
void testGlfw();

void* GlfwOcctView_initWindow(const int theWidth, const int theHeight, const char* theTitle);
void GlfwOcctView_initViewer(void* occtView);
void GlfwOcctView_initDemoScene(void* occtView);
void GlfwOcctView_initGui(void* occtView);
void GlfwOcctView_mainloop(void* occtView);
void GlfwOcctView_displayInContext(void* occtView, void* aShapePrs);
void GlfwOcctView_cleanup(void* occtView);