#include <cassert>

#include "images_holder.h"

int main() {
    ImagesHolder trainIH("../../data/mnist/train-images-idx3-ubyte.gz", "../../data/mnist/train-labels-idx1-ubyte.gz");
    ImagesHolder testIH("../../data/mnist/t10k-images-idx3-ubyte.gz", "../../data/mnist/t10k-labels-idx1-ubyte.gz");

    assert(trainIH.width() == testIH.width() && trainIH.height() == testIH.height());

    //...

    return 0;
}
