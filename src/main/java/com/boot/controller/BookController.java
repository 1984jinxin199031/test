package com.boot.controller;

import com.boot.domain.Book;
import com.boot.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public Result save(@RequestBody Book book) {
        boolean flag = bookService.save(book);
        return new Result(flag ? Code.SAVE_OK:Code.SAVE_ERR,flag);
    }

    @PutMapping
    public Result update(@RequestBody Book book) {
        boolean flag = bookService.update(book);
        return new Result(flag ? Code.UPDATE_OK:Code.UPDATE_ERR,flag);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean flag = bookService.delete(id);
        return new Result(flag ? Code.DELETE_OK:Code.DELETE_ERR,flag);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Book book = bookService.getById(id);
        Integer code = book != null ? Code.GET_OK : Code.GET_ERR;
        String msg = book != null ? "" : "数据查询失败，请重试！";
        return new Result(code,book,msg);
    }

    @GetMapping
    public Result getAll() {
        List<Book> bookList = bookService.getAll();
        Integer code = bookList != null ? Code.GET_OK : Code.GET_ERR;
        String msg = bookList != null ? "" : "数据查询失败，请重试！";
        return new Result(code,bookList,msg);
    }

    @RequestMapping("page/{pageNo}&{pageNum}")
    public Result getPage(@PathVariable Integer pageNo, @PathVariable Integer pageNum) {
        List<Book> bookList = bookService.getPage(pageNo, pageNum);
        Integer code = bookList != null ? Code.GET_OK : Code.GET_ERR;
        String msg = bookList != null ? "" : "数据查询失败，请重试！";
        return new Result(code,bookList,msg);
    }


    @PostMapping("/upload")
    public Result insertDbMsPhoto(@RequestPart("photos") MultipartFile[] photos,
                                        @RequestParam("mid") String mid,
                                        HttpServletRequest request){
        try {
            if (photos.length > 0) {
                for (MultipartFile photo : photos) {
                    if (!photo.isEmpty()) {
                        String originalFilename = photo.getOriginalFilename();

                        String sufixFile = originalFilename.split("\\.")[1];
                        //拼接图片
                        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                        InputStream is = null;
                        OutputStream os = null;
                        //获得路径 本地路径
                        String relativePath = "/UploadFiles/"+date+"/";
                        String filePath = request.getSession().getServletContext()
                                .getRealPath(relativePath);
//                        String filePath = "C:\\Users\\jinxin\\AppData\\Local\\Temp" + relativePath;
                        File file = new File(filePath);
                        if(!file.exists()){
                            //先创建该文件的所有上级目录
                            if (file.getParentFile().mkdirs())
                            {
                                try{
                                    //如果不存在就创建
                                    file.mkdir();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        //获得图片名称
                        String fileName = date + System.currentTimeMillis()+ "." + sufixFile;

                        try {
                            is = photo.getInputStream();
                            os = new FileOutputStream(new File(filePath+fileName));
                            FileCopyUtils.copy(is, os);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                os.flush();
                                os.close();
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        String fileAddr = relativePath + fileName;
                        String fileAddrJson = "{'fileAddr':'"+ fileAddr + "'}";
                        return new Result(Code.FILE_OK,fileAddrJson,"文件保存成功");
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
            //日志，文件上传失败联系管理员
        }
        //日志，文件上传失败联系管理员
        return null;
    }

}
