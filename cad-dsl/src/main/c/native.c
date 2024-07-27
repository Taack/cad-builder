#include <stdio.h>
#include "native.h"

void print_hello() {
    printf("Hello from C\n");
    printf("Rect %d", rectArea(2, 2));
}

