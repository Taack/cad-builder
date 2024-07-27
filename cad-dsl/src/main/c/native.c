#include <stdio.h>
#include <stdlib.h>

#include "native.h"

void print_hello() {
    printf("Hello from C\n");
    printf("Rect %d\n", rectArea(2, 2));
    printf("MB +++\n");
    fflush(stdout);
    cMakeBottle(1.0, 1.0, 0.1);
    printf("MB ---\n");
}

