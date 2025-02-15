package datn.service.parking.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Lấy URL của ảnh từ Cloudinary dựa trên public_id.
     *
     * @param publicId public_id của ảnh trên Cloudinary (vd: ocr_images/934a82d0-7869-46aa-9359-9ae0764540f7)
     * @return URL của ảnh hoặc null nếu xảy ra lỗi
     */
    public String getImageUrl(String publicId) {
        try {
            String targetPublicId = "ocr_images/"+publicId;
            Map result = cloudinary.api().resource(targetPublicId, ObjectUtils.emptyMap());
            return (String) result.get("url");
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy ảnh từ Cloudinary: " + e.getMessage());
            throw new RuntimeException("lỗi khi lấy ảnh ");
        }
    }
}
