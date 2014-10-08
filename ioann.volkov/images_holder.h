#include <stdint.h>
#include <vector>
#include <string>

typedef std::vector<uint8_t> ByteVector;

class ImagesHolder {
public:
    ImagesHolder(std::string const& gzFileNameImages, std::string const& gzFileNameLabels);

    // Returns iterator to image's start
    ByteVector::const_iterator operator[](size_t imageNum) const;

    // Returns label
    uint8_t operator()(size_t imageNum) const;

    void debugPrint(size_t imageNum);

    size_t size() const   { return count_; }
    size_t width() const  { return width_; }
    size_t height() const { return height_; }
private:
    ByteVector images_;
    ByteVector labels_;
    size_t count_;
    size_t width_, height_;
};
