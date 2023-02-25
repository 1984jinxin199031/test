package com.boot.service.impl;

import com.boot.dao.BookDao;
import com.boot.domain.Book;
import com.boot.service.BookService;
import com.boot.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao bookDao;

    public boolean save(Book book) {
        bookDao.save(book);
        return true;
    }

    public boolean update(Book book) {
        bookDao.update(book);
        return true;
    }

    public boolean delete(Integer id) {
        bookDao.delete(id);
        return true;
    }

    public Book getById(Integer id) {
        return bookDao.getById(id);
    }

    public List<Book> getAll() {
        return bookDao.getAll();
    }

    @Override
    public List<Book> getPage(int pageNo, int pageNum) {
        int pageIndex = PageUtil.getPageIndexFromMysql(pageNo,pageNum);
        return bookDao.getPage(pageIndex,pageNum);
    }
}
