package com.jcommerce.library.Controller;

import com.jcommerce.library.Entity.Book;
import com.jcommerce.library.Entity.Reader;
import com.jcommerce.library.Services.BookService;
import com.jcommerce.library.Services.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reader")
@RequiredArgsConstructor
public class ReaderController {

    @Autowired
    private ReaderService readerService;
    @Autowired
    private BookService bookService;


    @GetMapping("/all")
    public ResponseEntity<List<Reader>> getAllReaders() {
        return ResponseEntity.status(HttpStatus.OK).body(readerService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reader> findById(@PathVariable Long id) {
        Optional<Reader> reader = readerService.findById(id);
        if (!reader.isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(reader.get());
    }


    @PostMapping("/create")
    public ResponseEntity create(@Valid @RequestBody Reader reader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readerService.save(reader));
    }


    @PostMapping("{readerId}/borrow/book/{bookId}")
    public ResponseEntity borrowBook(@PathVariable Long bookId, @PathVariable Long readerId) {
        if(!bookService.findById(bookId).isPresent() || !readerService.findById(bookId).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ResponseEntity response;
        Book book = bookService.findById(bookId).get();

        if(book.getReader()!=null) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("Too late! Please wait until it is given back.");
        }
        else {
            book.setReader(readerId);
            bookService.save(book);
            response = ResponseEntity.status(HttpStatus.CREATED).body("Well done, you borrowed the book.");
        }
        return response;
    }


    @PostMapping("{readerId}/giveBack/book/{bookId}")
    public ResponseEntity giveBackBook(@PathVariable Long bookId, @PathVariable Long readerId) {
        if(!bookService.findById(bookId).isPresent() || !readerService.findById(bookId).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ResponseEntity response;
        Book book = bookService.findById(bookId).get();

        if(book.getReader()!=null && book.getReader().equals(readerId)) {
            book.setReader(null);
            bookService.save(book);
            response = ResponseEntity.status(HttpStatus.CREATED).body("Well done, you gave the book back.");
        }
        else {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("There is nothing to do.");
        }
        return response;
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<Reader> update(@PathVariable Long id, @Valid @RequestBody Reader reader) {
        if (!readerService.findById(id).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(readerService.save(reader));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        if (!readerService.findById(id).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        readerService.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}