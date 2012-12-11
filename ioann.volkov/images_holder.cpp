#include "images_holder.h"

#include <fstream>
#include <iostream>

#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iostreams/copy.hpp>
#include <boost/iostreams/filter/gzip.hpp>

namespace {

    class ByteVectorSink {
    public:
        typedef char char_type;
        typedef boost::iostreams::sink_tag category;

        ByteVectorSink(ByteVector& result)
            : result_(result) {
        }

        std::streamsize write(const char* s, std::streamsize n) {
            std::copy(s, s + n, std::back_inserter(result_));
        }
    private:
        ByteVector& result_;
    };

    void readGZFile(std::string const& gzFileName, ByteVector& result) {
        std::ifstream file(gzFileName.c_str(), std::ios_base::in | std::ios_base::binary);
        boost::iostreams::filtering_streambuf<boost::iostreams::input> in;
        in.push(boost::iostreams::gzip_decompressor());
        in.push(file);
        boost::iostreams::copy(in, ByteVectorSink(result));
    }

    uint32_t getUInt32_BE(ByteVector::const_iterator pos) {
        return (*pos) * 0x1000000 + (*(pos + 1)) * 0x10000 + (*(pos + 2)) * 0x100 + (*(pos + 3));
    }

}

ImagesHolder::ImagesHolder(std::string const& gzFileNameImages, std::string const& gzFileNameLabels) {
    readGZFile(gzFileNameImages, images_);
    readGZFile(gzFileNameLabels, labels_);

    assert(getUInt32_BE(images_.begin()) == 0x00000803);
    assert(getUInt32_BE(labels_.begin()) == 0x00000801);

    count_ = getUInt32_BE(images_.begin() + 4);

    assert(count_ == getUInt32_BE(labels_.begin() + 4));

    height_ = getUInt32_BE(images_.begin() + 8);
    width_ = getUInt32_BE(images_.begin() + 12);
}

ByteVector::const_iterator ImagesHolder::getImageBegin(size_t imageNum) const {
    return images_.begin() + 16 + width_ * height_ * imageNum;
}

ByteVector::const_iterator ImagesHolder::getImageEnd(size_t imageNum) const {
    return getImageBegin(imageNum + 1);
}

uint8_t ImagesHolder::getLabel(size_t imageNum) const {
    return labels_[8 + imageNum];
}
