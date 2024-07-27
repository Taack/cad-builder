// MyRectangle.cpp
#include <iostream>

class Rectangle {
    int width, height;
  public:
    Rectangle(int, int);
    int area() const {return width*height;}
};

Rectangle::Rectangle(int w, int h) {
  this->width = w;
  this->height = h;
}

extern "C" int rectArea(int, int);

int rectArea(int w, int h) {
    std::cout << "Inside C++ Code " << std::endl;
    const Rectangle rect(w,h);
    return rect.area();
}
