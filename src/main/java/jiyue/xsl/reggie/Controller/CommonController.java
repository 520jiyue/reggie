package jiyue.xsl.reggie.Controller;


import jiyue.xsl.reggie.Common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传和下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basepath;

    /**
     * 文件上传      file参数名  必须和前端保保持一致
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("file upload   --------->  {}",file.toString());

        // 上传文件名
        String jpg_name_old = file.getOriginalFilename();
        String jpg_name_suffix = jpg_name_old.substring(jpg_name_old.lastIndexOf("."));

        //生成随机文件名
        String jpg_name_new = UUID.randomUUID().toString() + jpg_name_suffix;

        // 创建一个目录对象
        File dir = new File(basepath);

        // 判断目录是否存在
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        file.transferTo(new File( basepath + jpg_name_new));
        return R.success(jpg_name_new);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response) throws Exception {

        // 输入流 读取文件类容
        FileInputStream fileInputStream = new FileInputStream(new File(basepath + name));

        // 输出流 写回浏览器
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");

        byte[] bytes = new byte[1024];
        int lens = 0;
        while ((lens = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes,0,lens);
            outputStream.flush();
        }
        fileInputStream.close();
        outputStream.close();
    }
}
