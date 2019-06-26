package com.github.qingyejiazhu.securitycore.validate.code.image;


import com.github.qingyejiazhu.securitycore.validate.code.ValidateCode;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图形验证码
 * @author : gxf
 * @version : V1.0
 * @date : 2018/8/3 22:44
 */
public class ImageCode extends ValidateCode implements Serializable{
    // 要放到 session里 并发中 集群 放到 radis 中需要 序列化
    private static final long serialVersionUID = -703011095085705839L;
    private BufferedImage image;// 这个属性 没有实现序列化  但是不需要放图片进 session

    public ImageCode(BufferedImage image, String code, int expireIn) {
        super(code, expireIn);
        this.image = image;
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
        super(code, expireTime);
        this.image = image;
    }


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
