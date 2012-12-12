#include <cassert>
#include <iostream>

#include <boost/timer/timer.hpp>

#include "images_holder.h"

int f(ByteVector::const_iterator ib, std::vector<int> const& Q, int* result = 0) {
    int res = 0;
    for (size_t i = 0; i < Q.size(); ++i) {
        res += Q[i] * (*ib);
        ++ib;
    }
    if (result)
        *result = res;
    return (res >= 0) ? 1 : -1;
}

void adjustQ(std::vector<int>& Q, int correctRes, ByteVector::const_iterator ib) {
    for (size_t i = 0; i < Q.size(); ++i) {
        Q[i] += correctRes * (*ib);
        ++ib;
    }
}

int main() {
    std::cout << "Loading data from archives:" << std::flush;
    boost::timer::auto_cpu_timer tLoad;

    ImagesHolder trainIH("../../data/mnist/train-images-idx3-ubyte.gz", "../../data/mnist/train-labels-idx1-ubyte.gz");
    ImagesHolder testIH("../../data/mnist/t10k-images-idx3-ubyte.gz", "../../data/mnist/t10k-labels-idx1-ubyte.gz");

    tLoad.stop();
    tLoad.report();

    assert(trainIH.width() == testIH.width() && trainIH.height() == testIH.height());

    size_t N = trainIH.width() * trainIH.height();
    size_t steps = 100;

    std::vector<int> Q[10];
    for (size_t z = 0; z < 10; ++z) {
        Q[z].resize(N);
        Q[z].assign(N, 0);
    }

    for (size_t j = 0; j < steps; ++j) {
        {
            std::cout << "Step " << j + 1 << ":" << std::flush;
            boost::timer::auto_cpu_timer t;

            for (size_t i = 0; i < trainIH.size(); ++i) {
                for (size_t z = 0; z < 10; ++z) {
                    int correctRes = (trainIH(i) == z ? 1 : -1);
                    if (f(trainIH[i], Q[z]) != correctRes) {
                        adjustQ(Q[z], correctRes, trainIH[i]);
                    }
                }
            }
        }

        int errorsCount = 0;
        {
            std::cout << "Testing:" << std::flush;
            boost::timer::auto_cpu_timer t;

            for (size_t i = 0; i < testIH.size(); ++i) {
                int ans = -1;
                int maxRes = 0;
                for (size_t z = 0; z < 10; ++z) {
                    int res;
                    f(testIH[i], Q[z], &res);
                    if (res >= maxRes) {
                        maxRes = res;
                        ans = z;
                    }
                }
                if (ans != testIH(i))
                    ++errorsCount;
            }
        }

        std::cout << "Test error rate: " << static_cast<double>(errorsCount * 100) / testIH.size() << "%" << std::endl << std::flush;
    }

    return 0;
}
