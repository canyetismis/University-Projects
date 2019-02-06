#include <stdio.h>

int mult(int x, int factor){
    int i = 0;
    int result = 0;
    for(i = 0; i<factor; i++){
        result = result + x;
    }
    return result;
}

int main(){
    int print = 0;
    int x = 3;
    int factor = 4;
    printf("Hello World\n");
    print = mult(x, factor);
    printf("Result = %d\n", print);
    return 0;
}